package com.chrisly.mvp

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.orhanobut.logger.Logger
import java.lang.NullPointerException
import java.util.*

/**
 * @author big insect
 * @date 2019/6/16.
 */
class FragmentMvpDelegateImpl<V: MvpView, P: MvpPresenter<V>>(fragment: Fragment?, delegateCallback: MvpDelegateCallback<V, P>?, keepPresenterDuringScreenOrientationChange: Boolean, keepPresenterOnBackStack: Boolean)
    : FragmentMvpDelegate<V, P>{

    companion object{
        private const val KEY_VIEW_ID = "com.hannesdorfmann.mosby3.fragment.mvp.id"
        var DEBUG = false
        private const val DEBUG_TAG = "FragmentMvpDelegateImpl"

        fun retainPresenterInstance(activity: Activity, fragment: Fragment,
                                    keepPresenterInstanceDuringScreenOrientationChanges: Boolean,
                                    keepPresenterOnBackStack: Boolean): Boolean{
            if (activity.isChangingConfigurations){
                return keepPresenterInstanceDuringScreenOrientationChanges
            }

            if (activity.isFinishing){
                return false
            }

            if (keepPresenterOnBackStack){
                return true
            }

            return !fragment.isRemoving
        }
    }

    private var viewId: String? = null
    private var fragment: Fragment
    private var keepPresenterOnBackStack = false
    private var keepPresenterDuringScreenOrientationChange = false
    private var delegateCallback: MvpDelegateCallback<V, P>? = null
    private var onViewCreatedCalled = false

    init {
        when{
            delegateCallback == null -> throw NullPointerException("MvpDelegateCallback is null!")
            fragment == null -> throw NullPointerException("Fragment is null!")
            !keepPresenterDuringScreenOrientationChange && keepPresenterOnBackStack -> throw IllegalArgumentException("It is not possible to keep the presenter on backstack, " +
                    "but NOT keep presenter through screen orientation changes. Keep presenter on backstack also requires keep presenter through screen orientation changes to be enabled")

            else -> {
                this.fragment = fragment
                this.delegateCallback = delegateCallback
                this.keepPresenterDuringScreenOrientationChange = keepPresenterDuringScreenOrientationChange
                this.keepPresenterOnBackStack = keepPresenterOnBackStack
            }
        }
    }

    private fun createViewIdAndCreatePresenter(): P {
        val presenter = this.delegateCallback?.createPresenter()
        if (null == presenter) {
            throw NullPointerException("Presenter returned from createPresenter() is null. Activity is " + this.getActivity())
        } else {
            if (keepPresenterDuringScreenOrientationChange) {
                this.viewId = UUID.randomUUID().toString()
                PresenterManager.putPresenter(getActivity(), viewId, presenter)
            }
        }
        return presenter
    }

    private fun getActivity(): Activity {
        /**a ?: b ------- a != null 返回a  否则返回b*/
        return fragment.activity
            ?: throw NullPointerException("Activity returned by Fragment.getActivity() is null. Fragment is $fragment")
    }

    private fun getMvpView(): V {
        return delegateCallback?.getMvpView()
            ?: throw NullPointerException("View returned from getMvpView() is null")
    }

    private fun getPresenter(): P {
        return delegateCallback?.getPresenter()
            ?: throw NullPointerException("Presenter returned from getPresenter() is null")
    }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        val presenter = getPresenter()
        presenter.attachView(getMvpView())
        if (DEBUG){
            Logger.d(DEBUG_TAG, "View ${getMvpView()} attached to presenter $presenter")
        }

        onViewCreatedCalled = true
    }

    override fun onDestroyView() {
        onViewCreatedCalled = false
        getPresenter().detachView()

        if (DEBUG){
            Logger.d(DEBUG_TAG, "detached MvpView from Presenter. MvpView ${delegateCallback?.getMvpView()} presenter: ${getPresenter()}")
        }
    }

    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun onStart() {
        if (!onViewCreatedCalled) {
            throw IllegalStateException("It seems that you are using ${delegateCallback?.javaClass?.canonicalName}  as headless (UI less) fragment (because onViewCreated() has not been called or maybe delegation misses that part). Having a Presenter without a View (UI) doesn't make sense. Simply use an usual fragment instead of an MvpFragment if you want to use a UI less Fragment")
        }
    }

    override fun onStop() {
    }

    override fun onActivityCreated(saveInstanceState: Bundle?) {
    }

    override fun onAttach(context: Context) {
    }

    override fun onDetach() {
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (null != outState && (keepPresenterDuringScreenOrientationChange || keepPresenterOnBackStack)) {
            outState.putString(KEY_VIEW_ID, viewId)
            if (DEBUG) {
                Log.d(DEBUG_TAG, "Saving MosbyViewId into Bundle. ViewId: $viewId")
            }
        }
    }

    override fun onCreate(savedState: Bundle?) {
        var presenter: P? = null
        if (null != savedState && keepPresenterDuringScreenOrientationChange){
            viewId = savedState.getString(KEY_VIEW_ID)
            if (DEBUG){
                Logger.d(DEBUG_TAG, "MosbyView ID $viewId for MvpView ${delegateCallback?.getMvpView()}")
            }

            if (null != viewId ){
                presenter = PresenterManager.getPresenter(getActivity(), viewId)
                if (presenter != null){
                    Logger.d(DEBUG_TAG, "Reused presenter $presenter for view ${delegateCallback?.getMvpView()}")
                }else{
                    presenter = createViewIdAndCreatePresenter()
                    if (DEBUG){
                        Logger.d(DEBUG_TAG, "No presenter found although view Id was here: $viewId ." +
                                "Most likely this was caused by a process death. New Presenter created $presenter for view ${getMvpView()}")
                    }
                }
            }
        }else{
            presenter = createViewIdAndCreatePresenter()
            if (DEBUG){
                Logger.d(DEBUG_TAG, "New presenter $presenter for view ${getMvpView()}")
            }
        }

        if (null == presenter){
            throw IllegalStateException("Presenter is null. This seems to be a Mosby internal bug.")
        }

        delegateCallback?.setPresenter(presenter)

    }

    override fun onDestroy() {
        val activity = getActivity()
        val retainPresenterInstance = retainPresenterInstance(activity, fragment, keepPresenterDuringScreenOrientationChange, keepPresenterOnBackStack)
        val presenter = getPresenter()
        if (!retainPresenterInstance){
            presenter.destroy()
            Logger.d(DEBUG_TAG, "Presenter destroyed. MvpView ${delegateCallback?.getMvpView()} presenter: $presenter")
        }

        if (!retainPresenterInstance && viewId != null){
            PresenterManager.remove(activity, viewId)
        }
    }
}