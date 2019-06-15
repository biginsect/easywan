package com.chrisly.mvp

import android.support.annotation.UiThread

/**
 * @author big insect
 * @date 2019/6/15.
 */
interface MvpPresenter<V: MvpView> {

    @UiThread
    fun attachView(v: V?)

    @UiThread
    fun detachView(isDetach: Boolean)

    @UiThread
    fun detachView()

    @UiThread
    fun destroy()
}