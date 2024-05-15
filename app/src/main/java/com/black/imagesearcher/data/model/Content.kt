package com.black.imagesearcher.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.black.imagesearcher.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Content(
    val type: Type,
    val thumbnail: String,
    val title: String,
    val category: String,
    val contentUrl: String,
    val dateTime: Long,
) : Parcelable {
    enum class Type(@DrawableRes val iconResId: Int) {
        IMAGE(R.drawable.icon_image),
        VIDEO(R.drawable.icon_video)
    }
}

data class PagingContent(val page: Int, val content: Content)
data class Contents(val contents: List<Content>, val isEnd: Boolean)
data class TypeContents(val type: Content.Type, val contents: List<Content>, val isEnd: Boolean)