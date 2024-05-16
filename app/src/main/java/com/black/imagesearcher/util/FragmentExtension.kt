package com.black.imagesearcher.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

object FragmentExtension {
    /**
     * 현재 destination에서 navigate할 수 없는 directions이면 navigate하지 않음
     */
    fun NavController.navigateSafety(directions: NavDirections) {
        if (currentDestination?.getAction(directions.actionId) == null) {
            Log.v(currentDestination?.navigatorName + " cannot navigate to " + directions.actionId)
            return
        }
        navigate(directions)
    }

    fun Fragment.navigate(directions: NavDirections, navController: NavController? = null) {
        // java.lang.IllegalStateExeption: Can not perform this action after onSaveInstatnceState
        // Exception 방지를 위해 onResume 상태에서만 navigateSafety
        Util.launchWhenState(viewLifecycleOwner, Lifecycle.State.RESUMED) {
            (navController ?: findNavController())
                .navigateSafety(directions)
        }
    }

    /**
     * 부모 프래그먼트의 ViewModel 획득
     */
    @MainThread
    inline fun <reified VM : ViewModel> Fragment.parentViewModels(
        parentCls: Class<out Fragment>? = null
    ): Lazy<VM> {
        return viewModels({
            val parentClassName = parentCls?.name
                ?: requireParentFragment().javaClass.name

            var current = this
            var parent: Fragment? = null

            // 부모 Fragment 중 parentCls와 동일한 클래스를 찾는다.
            while (true) {
                val currentParent = current.parentFragment
                if (currentParent == null) {
                    break
                } else if (currentParent.javaClass.name == parentClassName) {
                    parent = currentParent
                    break
                }

                current = currentParent
            }

            parent ?: throw IllegalStateException("parentFragment not found : $parentCls")
        })
    }
}