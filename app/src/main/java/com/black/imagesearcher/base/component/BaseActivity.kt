package com.black.imagesearcher.base.component

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.black.imagesearcher.util.Log

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    companion object {
        fun BaseActivity<*>.doOnDestroy(block: () -> Unit) {
            onDestroyQueue += block
        }
    }

    protected lateinit var binding: T
    private val onDestroyQueue = ArrayDeque<() -> Unit>()

    @get:LayoutRes
    protected abstract val layoutResId : Int

    abstract fun onBindVariable(binding: T)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 자식 클래스 이름 출력을 위해 javaClass.simpleName 출력
        Log.v(javaClass.simpleName)
        binding = DataBindingUtil.setContentView<T>(this, layoutResId).apply {
            lifecycleOwner = this@BaseActivity
        }
        onBindVariable(binding)
        onReceivedIntent(intent ?: return)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        Log.v(javaClass.simpleName)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        Log.v(javaClass.simpleName)
    }

    /**
     * 현재 액티비티가 최상위가 아닌 경우 호출
     * ex. 반투명(또는 투명) 액티비티가 위에 노출된 경우 onPause만 호출 (그 외 멀티 윈도우 등등)
     */
    @CallSuper
    override fun onPause() {
        super.onPause()
        Log.v(javaClass.simpleName)
    }

    /**
     * 현재 액티비티가 숨겨진 경우 호출
     * ex. 반투명(또는 투명) 액티비티가 위에 노출된 경우 onPause만 호출 (그 외 멀티 윈도우 등등)
     */
    @CallSuper
    override fun onStop() {
        super.onStop()
        Log.v(javaClass.simpleName)
    }

    /**
     * targetSdkVersion : Build.VERSION_CODES.P 이상에서는 onStop() 이후에 호출
     * targetSdkVersion : Build.VERSION_CODES.P 미만인 경우 onStop() 이전에 발생하며 onPause() 이전 또는 이후에 발생할지 여부에 대한 보장은 없습니다.
     */
    // https://developer.android.com/reference/android/app/Activity#onSaveInstanceState(android.os.Bundle)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.v(javaClass.simpleName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.v(javaClass.simpleName)
    }

    @CallSuper
    override fun onDestroy() {
        Log.v(javaClass.simpleName)
        onDestroyQueue.removeAll {
            it.invoke()
            true
        }
        super.onDestroy()
    }

    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.v(javaClass.simpleName)
    }

    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.v("${javaClass.simpleName} : requestCode : $requestCode, permissions : $permissions, grantResults : $grantResults")
    }

    /**
     * launchMode가 singleTop, singleTask인 경우,
     * Intent.FLAG_ACTIVITY_SINGLE_TOP로 Activity를 실행한 경우
     * onPause -> onNewIntent -> onResume 순서로 실행됨
     * https://developer.android.com/reference/android/app/Activity#onNewIntent(android.content.Intent)
     */
    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.v("${javaClass.simpleName} : intent : $intent")
        onReceivedIntent(intent ?: return)
    }

    /**
     * onCreate, onNewIntent 인텐트 공통 처리용 콜백
     */
    protected open fun onReceivedIntent(intent: Intent) { }
}