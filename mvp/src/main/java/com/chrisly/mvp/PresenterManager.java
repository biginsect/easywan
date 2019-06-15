package com.chrisly.mvp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.util.ArrayMap;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * @author big insect
 * @date 2019/6/15.
 */
public final class PresenterManager {

    private static boolean DEBUG = false;
    private static final String DEBUG_TAG = "PresenterManager";
    private static final String KEY_ACTIVITY_ID = "com.hannesdorfmann.mosby3.MosbyPresenterManagerActivityId";
    private static final Map<Activity, String> MAP_ACTIVITY_ID = new ArrayMap<>();
    private static final Map<String, ActivityScopedCache> MAP_ACTIVITY_SCOPED_CACHE = new ArrayMap<>();

    private static final Application.ActivityLifecycleCallbacks ACTIVITY_LIFECYCLE_CALLBACKS = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (savedInstanceState != null){
                String activityId = savedInstanceState.getString(KEY_ACTIVITY_ID);
                if (null != activityId){
                    MAP_ACTIVITY_ID.put(activity, activityId);
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            String activityId = MAP_ACTIVITY_ID.get(activity);
            if (null != activityId){
                outState.putString(KEY_ACTIVITY_ID, activityId);
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (!activity.isChangingConfigurations()){
                String activityId = MAP_ACTIVITY_ID.get(activity);
                if (null != activityId){
                    ActivityScopedCache scopedCache = MAP_ACTIVITY_SCOPED_CACHE.get(activityId);
                    if (null != scopedCache){
                        scopedCache.clear();
                        MAP_ACTIVITY_SCOPED_CACHE.remove(activityId);
                    }

                    if (MAP_ACTIVITY_SCOPED_CACHE.isEmpty()){
                        activity.getApplication().unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE_CALLBACKS);
                        if (DEBUG){
                            Logger.d(DEBUG_TAG, "unregisterActivityLifecycleCallbacks");
                        }
                    }
                }
            }

            MAP_ACTIVITY_ID.remove(activity);
        }
    };

    private PresenterManager(){
    }

    @MainThread
    static ActivityScopedCache getOrCreateActivityScopedCache(Activity activity){
        String activityId = MAP_ACTIVITY_ID.get(activity);
        if (null == activityId){
            activityId = UUID.randomUUID().toString();
            MAP_ACTIVITY_ID.put(activity, activityId);
            if (MAP_ACTIVITY_ID.size() == 1){
                activity.getApplication().registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE_CALLBACKS);
                if (DEBUG){
                    Logger.d(DEBUG_TAG, "registerActivityLifecycleCallbacks");
                }
            }
        }

        ActivityScopedCache scopedCache = MAP_ACTIVITY_SCOPED_CACHE.get(activityId);
        if (null == scopedCache){
            scopedCache = new ActivityScopedCache();
            MAP_ACTIVITY_SCOPED_CACHE.put(activityId, scopedCache);
        }

        return scopedCache;
    }

    @MainThread
    static ActivityScopedCache getActivityScoped(Activity activity){
        String activityId = MAP_ACTIVITY_ID.get(activity);

        return MAP_ACTIVITY_SCOPED_CACHE.get(activityId);
    }

    public static <P> P getPresenter(Activity activity, String viewId){
        ActivityScopedCache scopedCache = getActivityScoped(activity);

        return scopedCache != null? (P)scopedCache.getPresenter(viewId): null;
    }

    public static <VS> VS getViewState(Activity activity, String viewId){
        ActivityScopedCache scopedCache = getActivityScoped(activity);

        return scopedCache != null? (VS) scopedCache.getViewState(viewId) : null;
    }

    public static Activity getActivity(Context context){

        if (context == null ){
            Logger.e(DEBUG_TAG, "context is null");
            return null;
        }

        if (context instanceof Activity){
            return (Activity) context;
        }

        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity)context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        throw new IllegalStateException("Could not find the surrounding activity");
    }

    static void reset(){
        MAP_ACTIVITY_ID.clear();
        for (ActivityScopedCache scopedCache: MAP_ACTIVITY_SCOPED_CACHE.values()){
            scopedCache.clear();
        }

        MAP_ACTIVITY_SCOPED_CACHE.clear();
    }

    public static void putPresenter(Activity activity, String viewId,
                                    MvpPresenter<? extends MvpView> presenter){
        if (activity == null){
            throw new NullPointerException("Activity is null");
        }

        ActivityScopedCache scopedCache = getOrCreateActivityScopedCache(activity);
        scopedCache.putPresenter(viewId, presenter);
    }

    public static void putViewState(Activity activity, String viewId, Object viewState){
        if (null == activity){
            throw new NullPointerException("Activity is null");
        }

        ActivityScopedCache scopedCache = getOrCreateActivityScopedCache(activity);
        scopedCache.putViewState(viewId, viewState);
    }

    public static void remove(Activity activity, String viewId){
        if (null == activity){
            throw new NullPointerException("Activity is null");
        }

        ActivityScopedCache scopedCache = getActivityScoped(activity);
        if (null != scopedCache){
            scopedCache.remove(viewId);
        }
    }
}
