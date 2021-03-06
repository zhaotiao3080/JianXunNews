package com.gyf.jianxunnews.mvp.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.apkfuns.logutils.LogUtils;
import com.gyf.jianxunnews.R;
import com.gyf.jianxunnews.dagger.component.DaggerFragmentComponent;
import com.gyf.jianxunnews.dagger.component.FragmentComponent;
import com.gyf.jianxunnews.dagger.module.FragmentModule;

import butterknife.ButterKnife;

/**
 * Fragment的基类
 * Created by geyifeng on 2017/4/7.
 */
public abstract class BaseFragment extends Fragment {
    /**
     * The M activity.
     */
    protected FragmentActivity mActivity;

    /**
     * 是否对用户可见
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     */
    protected boolean mIsPrepare;

    /**
     * The M root view.
     */
    protected View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //if (mRootView == null) {
            mRootView = inflater.inflate(setLayoutId(), container, false);
            ButterKnife.bind(this, mRootView);
        //}
        initView();
        if (isLazyLoad()) {
            mIsPrepare = true;
            onLazyLoad();
        } else {
            initData();
        }
        setListener();
        return mRootView;
    }


    /**
     * Gets fragment component.
     *
     * @return the fragment component
     */
    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .fragmentModule(getFragmentModule())
                .build();
    }

    /**
     * Gets fragment module.
     *
     * @return the fragment module
     */
    protected FragmentModule getFragmentModule() {
        return new FragmentModule(this);
    }


    /**
     * 是否懒加载
     *
     * @return the boolean
     */
    protected boolean isLazyLoad() {
        return true;
    }

    /**
     * 用户可见时执行的操作
     */
    protected void onVisible() {
        onLazyLoad();
    }

    private void onLazyLoad() {

        if (mIsVisible & mIsPrepare) {
            mIsPrepare = false;
            initData();
        }

    }

    /**
     * 找到activity的控件
     *
     * @param <T> the type parameter
     * @param id  the id
     * @return the t
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T findActivityViewById(@IdRes int id) {

        return (T) mActivity.findViewById(id);
    }

    /**
     * Sets layout id.
     *
     * @return the layout id
     */
    protected abstract int setLayoutId();

    protected void initView() {
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 设置监听
     */
    protected void setListener() {
    }

    /**
     * 用户不可见执行
     */
    protected void onInvisible() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbindDrawables(mRootView);
    }

    /**
     * 解决内存溢出
     *
     * @param view the view
     */
    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
