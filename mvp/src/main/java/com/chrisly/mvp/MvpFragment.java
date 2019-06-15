package com.chrisly.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * @author big insect
 * @date 2019/6/16.
 */
public abstract class MvpFragment<V extends  MvpView, P extends MvpPresenter<V>> extends Fragment
        implements MvpDelegateCallback<V, P>, MvpView {

    protected FragmentMvpDelegate<V, P> mvpDelegate;
    protected P presenter;

    public abstract P createPresenter();

    protected FragmentMvpDelegate<V, P> getMvpDelegate(){
        if (mvpDelegate == null){
            mvpDelegate = new FragmentMvpDelegateImpl<>(this, this, true, true);
        }

        return mvpDelegate;
    }

    @NonNull
    @Override
    public P getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public V getMvpView() {
        return (V)this;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMvpDelegate().onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getMvpDelegate().onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getMvpDelegate().onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        getMvpDelegate().onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        getMvpDelegate().onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        getMvpDelegate().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMvpDelegate().onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMvpDelegate().onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getMvpDelegate().onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getMvpDelegate().onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getMvpDelegate().onSaveInstanceState(outState);
    }
}
