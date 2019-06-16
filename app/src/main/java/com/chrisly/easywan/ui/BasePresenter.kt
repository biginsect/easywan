package com.chrisly.easywan.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.chrisly.mvp.MvpBasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.NullPointerException

/**
 * @author big insect
 * @date 2019/6/16.
 */
abstract class BasePresenter<V: IBaseContract.IView>: MvpBasePresenter<V>(), IBaseContract.IPresenter<V> {

    private val mCompositeDisposable = CompositeDisposable()

    protected fun registerDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    private fun clearDisposables() {
        mCompositeDisposable.clear()
    }

    override fun detachView() {
        super.detachView()
        clearDisposables()
    }

    override fun getContext(): Context {
        if (view is Context){
            return view as Context
        }else if (view is Fragment){
            val fragment = view as Fragment
            val result = fragment.context
            if (null != result){
                return result
            }
        }

        throw NullPointerException("BasePresenter: getView is not instance of Context, cannot invoke getContext()")
    }

    override fun onRestoreInstanceState(outState: Bundle?) {
        TODO("not implemented")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        TODO("not implemented")
    }
}