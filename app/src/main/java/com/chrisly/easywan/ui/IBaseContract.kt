package com.chrisly.easywan.ui

import android.content.Context
import android.os.Bundle
import com.chrisly.mvp.MvpPresenter
import com.chrisly.mvp.MvpView

/**
 * @author big insect
 * @date 2019/6/16.
 */
interface IBaseContract {

    interface IView: MvpView{

        fun showLoading()

        fun hideLoading()
    }

    interface IPresenter<V: IView>: MvpPresenter<V>{

        fun getContext(): Context

        fun onSaveInstanceState(outState: Bundle?)

        fun onRestoreInstanceState(outState: Bundle?)
    }
}