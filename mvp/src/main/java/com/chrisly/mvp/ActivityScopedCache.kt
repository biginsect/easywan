package com.chrisly.mvp

import android.util.ArrayMap
import com.orhanobut.logger.Logger

/**
 * @author big insect
 * @date 2019/6/15.
 */
class ActivityScopedCache {
    private val presenterMap = ArrayMap<String, PresenterHolder>()
    private val TAG = "ActivityScopedCache"

    private fun logViewIdNull(){
        Logger.e(TAG, "viewId is null")
    }

    private fun logPresenterNull(){
        Logger.e(TAG, "presenter is null")
    }

    private fun logViewStateNull(){
        Logger.e(TAG, "ViewState is null")
    }

    fun clear(){
        presenterMap.clear()
    }

    @Suppress("UNCHECKED_CAST")
    fun <P> getPresenter(viewId: String): P{
        val holder = presenterMap[viewId]
        return holder?.presenter as P
    }

    @Suppress("UNCHECKED_CAST")
    fun <VS> getViewState(viewId: String): VS{
        val  holder = presenterMap[viewId]
        return holder?.viewState as VS
    }

    fun putPresenter(viewId: String?, presenter: MvpPresenter<*>?){
        when{
            viewId == null -> logViewIdNull()
            presenter == null -> logPresenterNull()

            else -> {
                var holder = presenterMap[viewId]
                if (null == holder){
                    holder = PresenterHolder()
                    holder.presenter = presenter
                    presenterMap[viewId] = holder
                }else{
                    holder.presenter = presenter
                }
            }
        }
    }

    fun putViewState(viewId: String?, viewState: Any?){
        when{
            viewId == null -> logViewIdNull()
            viewState == null -> logViewStateNull()

            else -> {
                var holder = presenterMap[viewId]
                if (null == holder){
                    holder = PresenterHolder()
                    presenterMap[viewId] = holder
                }
                holder.viewState = viewState
            }
        }
    }

    fun remove(viewId: String?){
        if (null == viewId){
            logViewIdNull()
        }else{
            presenterMap.remove(viewId)
        }

    }

    internal class PresenterHolder{
        var presenter: MvpPresenter<*>? = null
        internal var viewState: Any? = null
    }
}