package com.kakakobank.imagecollector.base.component

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.kakakobank.imagecollector.base.viewmodel.EventObserver
import com.kakakobank.imagecollector.util.Log

abstract class BaseFragment<T : ViewDataBinding> : Fragment(), EventObserver {
    private var _binding: T? = null
    // onCreateView ~ onDestroyView까지 유효
    protected val binding get() = _binding!!

    private val onDestroyViewQueue = ArrayList<() -> Unit>()

    @get:LayoutRes
    protected abstract val layoutResId : Int

    abstract fun onBindVariable(binding: T)

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 자식 클래스 이름 출력을 위해 javaClass.simpleName 출력
        Log.v(javaClass.simpleName)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(javaClass.simpleName)
    }

    /*
    #Fragment #databinding
    https://developer.android.com/topic/libraries/view-binding?hl=ko#fragments
     */
    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(javaClass.simpleName)
        _binding = DataBindingUtil.inflate<T>(inflater, layoutResId, container, false).apply {
            // lifecycleOwner를 적용하지 않으면 liveData 변경 시 뷰에 반영되지 않음
            // https://stackoverflow.com/questions/59545195/mutablelivedata-not-updating-in-ui
            lifecycleOwner = viewLifecycleOwner
        }
        return _binding!!.root
    }

    override fun onReceivedEvent(action: String, data: Any?) { }

    /**
     * View 셋팅은 onViewCreated에서 하는 것을 권장
     */
    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v(javaClass.simpleName)
        super.onViewCreated(view, savedInstanceState)
        onBindVariable(_binding!!)
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

    @CallSuper
    override fun onPause() {
        super.onPause()
        Log.v(javaClass.simpleName)
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        Log.v(javaClass.simpleName)
    }

    /*
    참고: 프래그먼트는 뷰보다 오래 지속됩니다.
    프래그먼트의 onDestroyView() 메서드에서 결합 클래스 인스턴스 참조를 정리해야 합니다.
    #fragment #onDestroyView #destroy
    https://developer.android.com/topic/libraries/view-binding?hl=ko#fragments
     */
    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        Log.v(javaClass.simpleName)
        onDestroyViewQueue.removeIf {
            it.invoke()
            true
        }
        _binding = null
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        Log.v(javaClass.simpleName)
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        Log.v(javaClass.simpleName)
    }

    fun BaseFragment<*>.doOnDestroyView(block: () -> Unit) {
        onDestroyViewQueue += block
    }
}