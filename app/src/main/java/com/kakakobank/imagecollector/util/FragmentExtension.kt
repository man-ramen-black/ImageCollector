package com.kakakobank.imagecollector.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

object FragmentExtension {

    /**
     * 현재 위치가 더 이상 pop되면 안되는 최상단 상태인지 반환
     */
    fun NavController.isRootDestination() : Boolean {
        return previousBackStackEntry == null
    }

    fun NavController.clearBackStack() {
        while (previousBackStackEntry != null) {
            popBackStack()
        }
    }

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
}