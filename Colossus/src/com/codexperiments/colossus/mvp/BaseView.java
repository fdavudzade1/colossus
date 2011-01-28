package com.codexperiments.colossus.mvp;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;


public abstract class BaseView<TContext extends Context<TContext, TView>, TView>
extends Activity
implements OnSharedPreferenceChangeListener
{
    private Class<TView> mViewClass;
    private SharedPreferences mPreferences;
    private Map<String, OnPreferenceChangeListener> mPreferenceChangeListeners;

    protected Presenter<TContext, TView> mPresenter;
    protected TContext mContext;


	@Override
    @SuppressWarnings("unchecked")
    public void onCreate (Bundle pSavedInstanceState)
    {
        super.onCreate(pSavedInstanceState);
        mViewClass = (Class<TView>) getClass();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mPreferenceChangeListeners = new HashMap<String, OnPreferenceChangeListener>();

        // Restores configuration
        mContext = (TContext) getLastNonConfigurationInstance();
        if (mContext == null) {
            mContext = onCreateContext(pSavedInstanceState);
        }

        // Initializes context and presenter.
        mContext.bindTo((TView) this);
        mPresenter = new Presenter<TContext, TView>();

        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * After view creation, and if application is not restoring (e.g. after screen is switching to
     * landscape), then creates the activity context.
     * 
     * @param pSavedInstanceState Previous activity state.
     * @return New activity context.
     */
    protected abstract TContext onCreateContext (Bundle pSavedInstanceState);

    @Override
    public Object onRetainNonConfigurationInstance ()
    {
        TView lView = ViewProxy.newInstance(mViewClass);
        mContext.bindTo(lView);
        return mContext;
    }



    @Override
	public final void onSharedPreferenceChanged(SharedPreferences pPreferences, String pKey)
	{
		OnPreferenceChangeListener lPreferenceChangeListener = mPreferenceChangeListeners.get(pKey);
		if (lPreferenceChangeListener != null) {
			lPreferenceChangeListener.onChange(pPreferences);
		}
	}

    /**
     * Registers a new listener triggered when a specific preference is changed. Note that listener is triggered
     * once during this call (to allow listener to be always triggered at least once).
     * @param pKeyResId Resource ID of the string value used to identify preference.
     * @param pOnPreferenceChange Listener to trigger when preference changes. Is triggered once during this call.
     */
    protected void addPreferenceChangeListener(int pKeyResId, OnPreferenceChangeListener pOnPreferenceChange)
    {
    	mPreferenceChangeListeners.put(getResources().getString(pKeyResId), pOnPreferenceChange);
    	pOnPreferenceChange.onChange(mPreferences);
    }

    /**
     * @param pPreferences Preference store from which preferences are retrieved.
     * @param pKeyResId Resource ID of the string value used to identify preferenc.
     * @param pDefaultValueResId Resource ID pointing to a string containing default value.
     * @return Preference value or default value if it does not exist.
     */
    protected final String getStringPreference(SharedPreferences pPreferences, int pKeyResId, int pDefaultValueResId)
    {
    	String key = getResources().getString(pKeyResId);
    	String defaultValue = getResources().getString(pDefaultValueResId);
    	return pPreferences.getString(key, defaultValue);
    }

    /**
     * @param pPreferences Preference store from which preferences are retrieved.
     * @param pKeyResId Resource ID of the string value used to identify preferenc.
     * @param pDefaultValueResId Resource ID pointing to a string containing default value.
     * @return Integer preference stored as a String (e.g. when using Android ListPreference) or
     *         default value if it does not exist.
     */
    protected final int getIntPreferenceFromString(SharedPreferences pPreferences, int pKeyResId, int pDefaultValueResId)
    {
    	String key = getResources().getString(pKeyResId);
    	String defaultValue = getResources().getString(pDefaultValueResId);
    	return Integer.parseInt(pPreferences.getString(key, defaultValue));
    }



    public static class ViewProxy implements InvocationHandler
    {
        @SuppressWarnings("unchecked")
        public static <TView> TView newInstance (Class<?> pProxiedClass)
        {
            return (TView) Proxy.newProxyInstance(ViewProxy.class.getClassLoader(),
                                                  pProxiedClass.getInterfaces(),
                                                  new ViewProxy());
        }

        public Object invoke (Object pProxy, Method mMethod, Object[] pArgs) throws Throwable
        {
            // Does nothing.
            return null;
        }
    }
}
