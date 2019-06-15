package com.chrisly.mvp

import android.content.Context
import android.os.Bundle
import android.view.View

/**
 * @author big insect
 * @date 2019/6/16.
 */
interface FragmentMvpDelegate<V: MvpView, P: MvpPresenter<V>> {

    fun onCreate(savedState: Bundle?)

    fun onDestroy()

    fun onViewCreated(view: View, savedState: Bundle?)

    fun onDestroyView()

    fun onPause()

    fun onResume()

    fun onStart()

    fun onStop()

    fun onActivityCreated(saveInstanceState: Bundle?)

    fun onAttach(context: Context)

    fun onDetach()

    fun onSaveInstanceState(outState: Bundle?)
}