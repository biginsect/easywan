package com.chrisly.easywan.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.lang.Exception
import java.lang.ref.SoftReference
import java.util.*

/**
 * Activity 管理
 * @author big insect
 * @date 2019/6/16.
 */
object ActivitiesManager {

    private val mActivityList: LinkedList<SoftReference<Activity>> =LinkedList()

    private fun finishAll(){
        var i = mActivityList.size - 1
        while (i >= 0){
            val activitySoft = mActivityList[i]
            activitySoft.get()?.finish()
            i--
        }

        mActivityList.clear()
    }

    fun addActivity(activitySoft: SoftReference<Activity>){
        mActivityList.add(activitySoft)
    }

    fun finishActivity(activitySoft: SoftReference<Activity>?){
        if (mActivityList.contains(activitySoft)){
            mActivityList.remove(activitySoft)
        }
    }

    fun appExit(context: Context){
        try {
            finishAll()
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            manager.killBackgroundProcesses(context.packageName)
            System.exit(0)
        }catch (e: Exception){

        }
    }
}