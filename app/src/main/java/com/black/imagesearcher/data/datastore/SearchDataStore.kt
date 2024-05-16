package com.black.imagesearcher.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.black.imagesearcher.base.data.BaseDataStore
import com.black.imagesearcher.data.SearchRepository
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [SearchRepository]
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

    suspend fun getFavorite(): Set<Content> {
        return get(KEY_FAVORITE, "[]")
            .let { JsonUtil.from(it, true) ?: emptySet() }
    }

    fun getFavoriteFlow(): Flow<Set<Content>> {
        return flow(KEY_FAVORITE, "[]")
            .map { JsonUtil.from(it, true) ?: emptySet() }
    }

    suspend fun updateFavorite(favoriteSet: Set<Content>) {
        val updateData = favoriteSet
            .let { JsonUtil.to(it, true) ?: return }
        Log.d(updateData)
        update(KEY_FAVORITE, updateData)
    }
}