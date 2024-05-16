package com.black.imagesearcher.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.black.imagesearcher.base.data.BasePreferences
import com.black.imagesearcher.base.data.preferences
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.util.JsonUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchPreferences @Inject constructor(
    @ApplicationContext context: Context
): BasePreferences(context) {
    companion object {
        private val Context.searchPreferences by preferences("Search")

        private const val KEY_FAVORITE = "favorite"
    }

    override fun getPreferences(context: Context): SharedPreferences {
        return context.searchPreferences
    }

    fun getFavorite(): Set<Content> {
        return get(KEY_FAVORITE, "[]", String::class.java)
            .let { JsonUtil.from(it, true) ?: emptySet() }
    }

    fun getFavoriteFlow(): Flow<Set<Content>> {
        return flow(KEY_FAVORITE, "[]", String::class.java)
            .map { JsonUtil.from(it, true) ?: emptySet() }
    }

    fun updateFavorite(favoriteSet: Set<Content>) {
        val updateData = favoriteSet
            .let { JsonUtil.to(it, true) ?: return }
        put(KEY_FAVORITE, updateData, String::class.java)
    }
}