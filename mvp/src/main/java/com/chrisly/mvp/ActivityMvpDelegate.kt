package com.chrisly.mvp

import android.os.Bundle

/**
 * @author big insect
 * @date 2019/6/15.
 */
interface ActivityMvpDelegate<V : MvpView, P: MvpPresenter<V>> {

    fun onCreate(bundle: Bundle?)

    fun onDestroy()

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onRestart()

    fun onContentChanged()

    fun onSaveInstanceState(outState: Bundle?)

    fun onPostCreate(bundle: Bundle?)
}