package com.kakakobank.imagecollector.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat
import java.util.Locale

object ImageViewBindingAdapter {
    @BindingAdapter("glideUrl", "glideCircle", "glideError", "glidePlaceholder", requireAll = false)
    @JvmStatic
    fun setGlideImage(view: ImageView, url: String?, isCircle: Boolean?, errorDrawable: Drawable?, placeholder: Drawable?) {
        if (url == null) {
            return
        }

        Glide.with(view)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .override(Target.SIZE_ORIGINAL) // 이미지 원본 사이즈 사용
            .run { if (isCircle == true) circleCrop() else this }
            .run { error(errorDrawable ?: return@run this) }
            .run { placeholder(placeholder ?: return@run this) }
            .into(view)
    }

    @BindingAdapter("android:src")
    @JvmStatic
    fun setImageResource(view: ImageView, resId: Int?) {
        if (resId == null || resId == 0) {
            return
        }
        view.setImageResource(resId)
    }
}

object TextViewBindingAdapter {
    @BindingAdapter("time", "dateFormat")
    @JvmStatic
    fun setDateTime(view: TextView, timeMs: Long, dateFormat: String?) {
        if (timeMs == 0L || dateFormat.isNullOrEmpty()) {
            return
        }

        val text = try {
            SimpleDateFormat(dateFormat, Locale.getDefault())
                .format(timeMs)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return
        }

        view.text = text
    }
}

object ViewPagerBindingAdapter {
    @BindingAdapter("userInputEnabled")
    @JvmStatic
    fun setUserInputEnabled(view: ViewPager2, enabled: Boolean) {
        view.isUserInputEnabled = enabled
    }
}