package com.black.imagesearcher.model.data

import androidx.annotation.DrawableRes
import com.black.imagesearcher.R
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val type: Type,
    val thumbnail: String,
    val title: String,
    val category: String,
    val contentUrl: String,
    val dateTime: Long,
) {
    enum class Type(@DrawableRes val iconResId: Int) {
        IMAGE(R.drawable.icon_image),
        VIDEO(R.drawable.icon_video)
    }
}