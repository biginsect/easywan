package com.chrisly.mvp

import android.app.Activity
import android.os.Bundle
import com.orhanobut.logger.Logger
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.util.*

/**
 * @author big insect
 * @date 2019/6/15.
 */
class ActivityMvpDelegateImpl<V: MvpView, P: MvpPresenter<V>>(delegateCallback: MvpDelegateCallback<V, P>?, activity: Activity?, keepPresenterInstance: Boolean)
    :ActivityMvpDelegate<V, P>{

    companion object{
        var DEBUG = false
        internal const val KEY_VIEW_ID = "com.hannesdorfmann.mosby3.activity.mvp.id"
        internal const val TAG = "ActivityMvpDelegateImpl"
        fun retainPresenterInstance(keepPresenterInstance: Boolean, activity: Activity?): Boolean{
            return keepPresenterInstance && (activity?.isChangingConfigurations!! || activity.isFinishing)
        }
    }

    private var delegateCallback: MvpDelegateCallback<V, P>? = null
    private var keepPresenterInstance = false
    private var activity: Activity? = null
    private var viewId: String? = null

    init {
        when{
            null == activity -> throw NullPointerException("activity is null")
            null == delegateCallback -> throw NullPointerException("delegateCallback is null")

            else -> {
                this.delegateCallback = delegateCallback
                this.activity = activity
                this.keepPresenterInstance = keepPresenterInstance
            }
        }
    }

    private fun createViewAndCreatePresenter(): P{
        val presenter = delegateCallback?.createPresenter()
        when(presenter){
            null -> {
                throw NullPointerException("\"Presenter returned from createPresenter() is null. Activity is ${this.activity}\"")
            }
            else -> {
                if (this.keepPresenterInstance){
                    viewId = UUID.randomUUID().toString()
                    PresenterManager.putPresenter(this.activity, this.viewId, presenter)
                }
            }
        }

        return presenter
    }

    override fun onCreate(bundle: Bundle?) {
        var presenter: P? = null
        if (null != bundle && keepPresenterInstance){
            viewId = bundle.getString(KEY_VIEW_ID)
            if (DEBUG){
                Logger.d(TAG, "MosbyView ID = $viewId for MvpView ${delegateCallback?.getMvpView()}")
            }

            if (null != viewId ){
                presenter = PresenterManager.getPresenter(activity, viewId)
                if (presenter != null){
                    Logger.d(TAG, "Reused presenter $presenter for view ${delegateCallback?.getMvpView()}")
                }else{
                    presenter = createViewAndCreatePresenter()
                    if (DEBUG){
                        Logger.d(TAG, "No presenter found although view Id was here: $viewId ." +
                                "Most likely this was caused by a process death. New Presenter created $presenter for view ${getMvpView()}")
                    }
                }
            }
        }else{
            presenter = createViewAndCreatePresenter()
            if (DEBUG){
                Logger.d(TAG, "New presenter $presenter for view ${getMvpView()}")
            }
        }

        if (null == presenter){
            throw IllegalStateException("Presenter is null. This seems to be a Mosby internal bug.")
        }

        delegateCallback?.setPresenter(presenter)
        getPresenter().attachView(getMvpView())

        if (DEBUG){
            Logger.d(TAG, "View ${getMvpView()} attached to presenter $presenter")
        }
    }

    private fun getPresenter(): P{
        return delegateCallback?.createPresenter() ?: throw NullPointerException("Presenter returned from getPresenter() is null")
    }

    private fun getMvpView(): V{
        return delegateCallback?.getMvpView() ?: throw NullPointerException("View returned from getMvpView() is null")
    }

    override fun onDestroy() {
        val retainPresenterInstance = retainPresenterInstance(keepPresenterInstance, activity)
        getPresenter().detachView()

        if (!retainPresenterInstance){
            getPresenter().destroy()
        }

        if (!retainPresenterInstance && viewId != null){
            PresenterManager.remove(activity, viewId)
        }

        if (DEBUG){
            if (retainPresenterInstance){
                Logger.d(TAG, "View ${getMvpView()} destroy temporarily. View detached from presenter ${getPresenter()}")
            }else{
                Logger.d(TAG, "View ${getMvpView()} destroy permanently. View detached permanently from presenter ${getPresenter()}")
            }
        }
    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onRestart() {

    }

    override fun onContentChanged() {

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (keepPresenterInstance && outState != null){
            outState.putString(KEY_VIEW_ID, viewId)
            if (DEBUG){
                Logger.d(TAG, "Saving MosbyViewId into Bundle. ViewId: $viewId for view ${getMvpView()}")
            }
        }
    }

    override fun onPostCreate(bundle: Bundle?) {

    }
}