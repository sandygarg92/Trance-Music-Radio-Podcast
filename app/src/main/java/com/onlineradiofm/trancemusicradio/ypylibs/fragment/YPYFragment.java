/*
 * Copyright (c) 2017. Radio Polska - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://radiopolska.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.onlineradiofm.trancemusicradio.ypylibs.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.onlineradiofm.trancemusicradio.MainActivity;
import com.onlineradiofm.trancemusicradio.ypylibs.activity.YPYFragmentActivity;


/**
 * @author:Radio Polska
 * @Skype: 
 * @Mobile : 
 * @Email: 
 * @Website: www.radiopolska.com
 * @Date:Oct 20, 2017
 */
public abstract class YPYFragment<T extends ViewBinding> extends Fragment implements IYPYFragmentConstants {

	private boolean isExtractData;
	public String mNameFragment;
	public int mIdFragment;
	public String mNameScreen;
	protected MainActivity mContext;
	private boolean isAllowFindViewContinuous;
	private boolean isCreated;
	private boolean isFirstInTab;
	private boolean isLoadingData;
	public Bundle mSavedInstanceState;

	protected T viewBinding;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.viewBinding = getViewBinding(inflater, container);
		return this.viewBinding.getRoot();
	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!isExtractData) {
			isExtractData = true;
			if (savedInstanceState == null) {
				onExtractData(getArguments());
			}
			else {
				this.mSavedInstanceState = savedInstanceState;
				onExtractData(savedInstanceState);
			}
			findView();
		}
		else {
			if (isAllowFindViewContinuous) {
				findView();
			}
		}
		isCreated = true;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(isAllowFindViewContinuous && isCreated){
			findView();
		}

	}
	@NonNull
	protected abstract T getViewBinding(@NonNull LayoutInflater inflater, ViewGroup container);

	public abstract void findView();

	public void onExtractData(Bundle savedInstance){
		if (savedInstance != null) {
			mNameFragment = savedInstance.getString(KEY_NAME_FRAGMENT);
			mIdFragment = savedInstance.getInt(KEY_ID_FRAGMENT);
			mNameScreen = savedInstance.getString(KEY_NAME_SCREEN);
			if (mSavedInstanceState != null) {
				if (getActivity() != null) {
					((YPYFragmentActivity) getActivity()).setActionBarTitle(mNameScreen);
				}
			}
		}
	}

	public void backToHome(YPYFragmentActivity mContext) {
		backToHome(mContext,true);
	}

	public void backToHome(YPYFragmentActivity mContext, boolean isNeedSetTitle) {
		try{
			FragmentTransaction mFragmentTransaction;
			FragmentManager mFragmentManager = mContext.getSupportFragmentManager();
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.remove(this);

			Fragment mFragmentHome = getFragmentHome(mContext);
			if(mFragmentHome!=null){
				if(isNeedSetTitle){
					String screenName=((YPYFragment<?>) mFragmentHome).getScreenName();
					if(!TextUtils.isEmpty(screenName)){
						mContext.setActionBarTitle(screenName);
					}
				}
				mFragmentTransaction.show(mFragmentHome);
			}
			mFragmentTransaction.commit();
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	public String getScreenName() {
		return mNameScreen;
	}

	public void setAllowFindViewContinuous(boolean isAllowFindViewContinuous) {
		this.isAllowFindViewContinuous = isAllowFindViewContinuous;
	}

	public Fragment getFragmentHome(FragmentActivity mContext){
		Fragment mFragmentHome=null;
		if(mIdFragment>0){
			mFragmentHome = mContext.getSupportFragmentManager().findFragmentById(mIdFragment);
		}
		else{
			if(!TextUtils.isEmpty(mNameFragment)){
				mFragmentHome = mContext.getSupportFragmentManager().findFragmentByTag(mNameFragment);
			}
		}
		return mFragmentHome;
	}

	public void notifyData(){

	}

	public void notifyData(int pos){

	}

	public void startLoadData(){

	}

	public boolean isLoadingData() {
		return isLoadingData;
	}

	public void setLoadingData(boolean loadingData) {
		isLoadingData = loadingData;
	}
	public boolean isFirstInTab() {
		return isFirstInTab;
	}

	public void setFirstInTab(boolean firstInTab) {
		isFirstInTab = firstInTab;
	}


	public boolean isCheckBack(){
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		viewBinding = null;
	}

	public void updateDarkMode(boolean isDark) {
		setLoadingData(false);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			outState.putInt(KEY_ID_FRAGMENT, mIdFragment);
			outState.putString(KEY_NAME_FRAGMENT, mNameFragment);
			outState.putString(KEY_NAME_SCREEN, mNameScreen);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
