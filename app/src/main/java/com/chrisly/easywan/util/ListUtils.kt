package com.chrisly.easywan.util

/**
 * @author big insect
 * @date 2019/6/16.
 */
object ListUtils {

    fun <T> getSize(list: List<T>?): Int{
        if (null == list){
            return 0
        }

        return list.size
    }

    fun <T> isEmpty(list: List<T>?): Boolean {
        return list == null || list.isEmpty()
    }
}