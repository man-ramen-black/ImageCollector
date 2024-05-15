package com.black.imagesearcher.model.preferences

import android.content.Context
import android.content.SharedPreferences
import com.black.imagesearcher.base.model.BasePreferences
import com.black.imagesearcher.base.model.preferences
import com.black.imagesearcher.model.data.Content
import com.black.imagesearcher.util.JsonUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchPreferences @Inject constructor(
    @ApplicationContext context: Context
): BasePreferences(context) {
    companion object {
        private const val KEY_FAVORITE = "favorite"

        val Context.searchPreferences by preferences("Search")
    }

    override fun getPreferences(context: Context): SharedPreferences {
        return context.searchPreferences
    }

    fun getFavorite(): Set<Content> {
        return get(KEY_FAVORITE, "[]")
            .let { JsonUtil.from(it, true) ?: emptySet() }
    }

    fun getFavoriteFlow(): Flow<Set<Content>> {
        return flow(KEY_FAVORITE, "[]")
            .map { JsonUtil.from(it, true) ?: emptySet() }
    }

    fun updateFavorite(favoriteSet: Set<Content>) {
        val updateData = favoriteSet
            .let { JsonUtil.to(it, true) ?: return }
        put(KEY_FAVORITE, updateData)
    }
}