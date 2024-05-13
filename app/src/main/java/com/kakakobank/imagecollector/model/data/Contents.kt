package com.kakakobank.imagecollector.model.data

import androidx.annotation.DrawableRes
import com.kakaobank.imagecollector.R
import kotlinx.serialization.Serializable

@Serializable
data class Contents(
    val type: Type,
    val thumbnail: String,
    val title: String,
    val category: String,
    val contentsUrl: String,
    val dateTime: Long,
) {
    enum class Type(@DrawableRes val iconResId: Int) {
        IMAGE(R.drawable.icon_image),
        VIDEO(R.drawable.icon_video)
    }
}