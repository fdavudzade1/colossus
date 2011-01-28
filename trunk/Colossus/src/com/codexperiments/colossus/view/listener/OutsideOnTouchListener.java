package com.codexperiments.colossus.view.listener;


import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;


public class OutsideOnTouchListener implements OnTouchListener
{
	private PopupWindow mPopupWindow;
	
    public OutsideOnTouchListener(PopupWindow pPopupWindow) {
		mPopupWindow = pPopupWindow;
	}

	@Override
    public boolean onTouch (View pView, MotionEvent pEvent)
    {
        if (pEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
        	mPopupWindow.dismiss();
            return true;
        } else {
            return false;
        }
    }
}
