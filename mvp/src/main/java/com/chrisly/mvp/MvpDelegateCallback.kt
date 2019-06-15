package com.chrisly.mvp

/**
 * @author big insect
 * @date 2019/6/15.
 */
interface MvpDelegateCallback<V: MvpView, P: MvpPresenter<V>> {

    fun createPresenter(): P?

    fun getPresenter(): P?

    fun setPresenter(presenter: P?)

    fun getMvpView(): V?
}