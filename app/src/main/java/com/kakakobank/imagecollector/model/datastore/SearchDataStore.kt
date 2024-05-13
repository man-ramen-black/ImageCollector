package com.kakakobank.imagecollector.model.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kakakobank.imagecollector.base.datastore.BaseDataStore
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
import com.kakakobank.imagecollector.util.KBJson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [SearchModel]
 */
class SearchDataStore @Inject constructor(
    @ApplicationContext context: Context
): BaseDataStore(context) {
    companion object {
        private val Context.searchDataStore: DataStore<Preferences> by preferencesDataStore(name = "ImageSearcher.Search")

        private val KEY_FAVORITE = stringPreferencesKey("favorite")
    }

    override fun getDataStore(context: Context): DataStore<Preferences> {
        return context.searchDataStore
    }

    suspend fun getFavorite(): Set<Contents> {
        return get(KEY_FAVORITE, "[]")
            .let { KBJson.from(it, true) ?: emptySet() }
    }

    fun getFavoriteFlow(): Flow<Set<Contents>> {
        return flow(KEY_FAVORITE, "[]")
            .map { KBJson.from(it, true) ?: emptySet() }
    }

    suspend fun updateFavorite(favoriteSet: Set<Contents>) {
        val updateData = favoriteSet
            .let { KBJson.to(it) ?: "" }
        update(KEY_FAVORITE, updateData)
    }
}