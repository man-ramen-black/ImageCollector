package com.black.imagesearcher.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

object ImageViewBindingAdapter {
    @BindingAdapter("glideUrl", "glideCircle", "glideErrorDrawable", "glidePlaceholder", "glideErrorUrl", requireAll = false)
    @JvmStatic
    fun setGlideImage(view: ImageView, url: String?, isCircle: Boolean?, errorDrawable: Drawable?, placeholder: Drawable?, errorUrl: String?) {
        if (url == null) {
            return
        }

        Glide.with(view)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .override(Target.SIZE_ORIGINAL) // 이미지 원본 사이즈 사용
            .run { if (isCircle == true) circleCrop() else this }
            .run {
                errorUrl ?: return@run this
                error(Glide.with(view).load(errorUrl))
            }
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

    @BindingAdapter("offscreenPageLimit")
    @JvmStatic
    fun setOffscreenPageLimit(view: ViewPager2, limit: Int) {
        view.offscreenPageLimit = limit.takeIf { it != 0 } ?: ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
    }
}

object ViewBindingAdapter {
    private const val CLICK_DELAY = 350L
    private val clickTime = AtomicLong(0L)

    /**
     * 전역 멀티 터치 방지 onClick
     * 해당 onClick이 설정된 뷰들은 특정 뷰 onClick 시 CLICK_DELAY 동안 모든 뷰의 onClick 차단
     */
    @BindingAdapter("onClick")
    @JvmStatic
    fun setOnClickListener(view: View, listener: View.OnClickListener?) {
        view.setOnClickListener {
            if (System.currentTimeMillis() - clickTime.get() < CLICK_DELAY) {
                Log.v("Cannot click")
                @Suppress("LABEL_NAME_CLASH")
                return@setOnClickListener
            }
            clickTime.set(System.currentTimeMillis())
            listener?.onClick(view)
        }
    }
}