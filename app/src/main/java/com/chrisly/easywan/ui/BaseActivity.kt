package com.chrisly.easywan.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.chrisly.easywan.util.ActivitiesManager
import com.chrisly.mvp.MvpActivity
import java.lang.ref.SoftReference

/**
 * @author big insect
 * @date 2019/6/16.
 */
abstract class BaseActivity<V: IBaseContract.IView, P: IBaseContract.IPresenter<V>>
    : MvpActivity<V, P>(), IBaseContract.IView {

    private lateinit var mSoftActivity: SoftReference<Activity>
    private var currentActivity: BaseActivity<V, P>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (0 != getLayoutId()){
            setContentView(getLayoutId())
        }

        initActivity()
        initView(savedInstanceState)
        mSoftActivity = SoftReference(this)
        ActivitiesManager.addActivity(mSoftActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this == currentActivity) {
            currentActivity = null
        }

        ActivitiesManager.finishActivity(mSoftActivity)
        mSoftActivity.clear()
    }

    override fun finish() {
        ActivitiesManager.finishActivity(mSoftActivity)
        mSoftActivity.clear()
        super.finish()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    internal fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    protected fun getActivity(): BaseActivity<V, P> {
        return this
    }

    @CallSuper
    protected open fun initView(savedInstanceState: Bundle?) {

    }

    @CallSuper
    protected open fun initActivity() {

    }

    abstract fun getLayoutId(): Int
}