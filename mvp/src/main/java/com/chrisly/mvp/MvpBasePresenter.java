package com.chrisly.mvp;

import android.support.annotation.UiThread;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author big insect
 * @date 2019/6/16.
 */
public class MvpBasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private WeakReference<V> viewRef;
    private boolean presenterDestroyed = false;

    @UiThread
    @Override
    public void attachView(@Nullable V v) {
        viewRef = new WeakReference<>(v);
        presenterDestroyed = false;
    }

    @Override
    public void destroy() {
        presenterDestroyed = true;
    }

    @Override
    public void detachView(boolean isDetach) {

    }

    @Override
    public void detachView() {
        detachView(true);
        if (viewRef != null){
            viewRef .clear();
            viewRef = null;
        }
    }

    @UiThread
    public V getView(){
        return viewRef == null? null: viewRef.get();
    }

    @UiThread
    public boolean isViewAttached(){
        return viewRef != null && viewRef.get() != null;
    }

    /**
     * 可以选择不判断 isViewAttached()返回结果而直接回调
     * */
    protected final void ifViewAttached(boolean viewNotAttached, ViewAction<V> action){
        final V view = viewRef != null? viewRef.get(): null;
        if (view != null){
            action.run(view);
        }else if (viewNotAttached){
            throw new IllegalStateException(
                    "No View attached to Presenter. Presenter destroyed = " + presenterDestroyed);
        }
    }

    protected final void ifViewAttached(ViewAction<V> action){
        ifViewAttached(false, action);
    }

    public interface ViewAction<V>{
        void run(V view);
    }
}
