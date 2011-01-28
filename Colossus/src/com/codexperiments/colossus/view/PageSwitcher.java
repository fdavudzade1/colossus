package com.codexperiments.colossus.view;


import java.util.ArrayList;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

import com.codexperiments.colossus.R;
import com.codexperiments.colossus.utility.ResourceUtil;


/**
 * Layout which can accept several childs, each one representing a page. Only one page is displayed
 * at a time and user can scroll between pages using gesture recognition: one gesture to go the
 * right and one to go to the left. By default, a simple horizontal line gesture is needed. An
 * animation is played when switching from one page to another (by default, just a sliding effect).
 * 
 * Gestures can be customized using "previousGesture" and "nextGesture" properties. Animations for
 * in and out pages can be customized using "nextIn" and "nextOut" for right page, "previousIn" and
 * "previousOut" for left page.
 */
public class PageSwitcher extends FrameLayout
{
    private static final double PREDICTION_THRESHOLD = 1.0;
    
    // UI elements.
    private GestureOverlayView mUIGestureOverlay;
    private ViewFlipper        mUIViewFlipper;
    private GestureLibrary     mGestureLibrary;

    // Names of the gesture that will cause a switch to the previous or next view. 
    private String mPreviousGestureName; 
    private String mNextGestureName;

    // View switch transitions.
    private Animation mPreviousIn;
    private Animation mPreviousOut;
    private Animation mNextIn;
    private Animation mNextOut;



    public PageSwitcher (Context pContext)
    {
        super(pContext);
        initialize(pContext, null, 0);
    }

    public PageSwitcher (Context pContext, AttributeSet pAttrSet)
    {
        super(pContext, pAttrSet);
        initialize(pContext, pAttrSet, 0);
    }

    public PageSwitcher (Context pContext, AttributeSet pAttrSet, int pDefStyle)
    {
        super(pContext, pAttrSet, pDefStyle);
        initialize(pContext, pAttrSet, pDefStyle);
    }

    private void initialize(Context pContext, AttributeSet pAttrSet, int pDefStyle)
    {
        ResourceUtil lResourceUtil = new ResourceUtil(pContext, pAttrSet, R.styleable.PageSwitcher, pDefStyle);

        // Initializes the gesture library.
        int lGestureId = lResourceUtil.getResourceId(R.styleable.PageSwitcher_gestureLibrary, R.raw.pageswitcher_gesture);
        mGestureLibrary = GestureLibraries.fromRawResource(getContext(), lGestureId);
        if (!mGestureLibrary.load()) {
            throw new RuntimeException("Could not load gesture library");
        }
        
        // Retrieves gesture names and animations.
        mPreviousGestureName = lResourceUtil.getString(R.styleable.PageSwitcher_previousGesture, R.string.pageswitcher_previous_gesture);
        mNextGestureName     = lResourceUtil.getString(R.styleable.PageSwitcher_nextGesture, R.string.pageswitcher_next_gesture);

        mPreviousIn  = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.PageSwitcher_previousIn,  R.anim.pageswitcher_previous_in));
        mPreviousOut = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.PageSwitcher_previousOut, R.anim.pageswitcher_previous_out));
        mNextIn      = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.PageSwitcher_nextIn,      R.anim.pageswitcher_next_in));
        mNextOut     = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.PageSwitcher_nextOut,     R.anim.pageswitcher_next_out));

        // Initializes UI components.
        mUIViewFlipper = new ViewFlipper(pContext, pAttrSet);
        mUIViewFlipper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        mUIGestureOverlay = new GestureOverlayView(pContext, pAttrSet);
        mUIGestureOverlay.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        mUIGestureOverlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
        mUIGestureOverlay.setEventsInterceptionEnabled(true);
        mUIGestureOverlay.setGestureVisible(false);
        mUIGestureOverlay.addOnGesturePerformedListener(new GestureListener());
        
        mUIGestureOverlay.addView(mUIViewFlipper, -1, mUIViewFlipper.getLayoutParams());
        super.addView(mUIGestureOverlay, -1, mUIGestureOverlay.getLayoutParams());
    }



    /**
     * Listens for gestures and acts according to them (i.e. switch to next or previous screen).
     */
    private class GestureListener implements GestureOverlayView.OnGesturePerformedListener
    {
        @Override
        public void onGesturePerformed (GestureOverlayView pOverlay, Gesture pGesture)
        {
            ArrayList<Prediction> lPredictions = mGestureLibrary.recognize(pGesture);
            int lPredictionSize = lPredictions.size();
    
            // If a gesture is found with a good match...
            for (int i = 0; i < lPredictionSize; i++) {
                Prediction lPrediction = lPredictions.get(i);
                if (lPrediction.score > PREDICTION_THRESHOLD) {
                    // ...then executes the corresponding action, i.e. shows previous or next view. Scrolling is
                    // performed through the Android animation mechanism implemented in ViewFlipper component.
                    if (lPrediction.name.equals(mPreviousGestureName)) {
                        mUIViewFlipper.setInAnimation(mPreviousIn);
                        mUIViewFlipper.setOutAnimation(mPreviousOut);
                        mUIViewFlipper.showPrevious();
                    } else if (lPrediction.name.equals(mNextGestureName)) {
                        mUIViewFlipper.setInAnimation(mNextIn);
                        mUIViewFlipper.setOutAnimation(mNextOut);
                        mUIViewFlipper.showNext();
                    }
                    break;
                }
            }
        }
    }



    @Override
    public void addView (View pChild, int pWidth, int pHeight)
    {
        mUIViewFlipper.addView(pChild, pWidth, pHeight);
    }

    @Override
    public void addView (View pChild, int pIndex, android.view.ViewGroup.LayoutParams pParams)
    {
        mUIViewFlipper.addView(pChild, pIndex, pParams);
    }

    @Override
    public void addView (View pChild, int pIndex)
    {
        mUIViewFlipper.addView(pChild, pIndex);
    }

    @Override
    public void addView (View pChild, android.view.ViewGroup.LayoutParams pParams)
    {
        mUIViewFlipper.addView(pChild, pParams);
    }

    @Override
    public void addView (View pChild)
    {
        mUIViewFlipper.addView(pChild);
    }

    @Override
    public void removeAllViews ()
    {
        mUIViewFlipper.removeAllViews();
    }

    @Override
    public void removeAllViewsInLayout ()
    {
        mUIViewFlipper.removeAllViewsInLayout();
    }

    @Override
    public void removeView (View pView)
    {
        mUIViewFlipper.removeView(pView);
    }

    @Override
    public void removeViewAt (int pIndex)
    {
        mUIViewFlipper.removeViewAt(pIndex);
    }

    @Override
    public void removeViewInLayout (View pView)
    {
        mUIViewFlipper.removeViewInLayout(pView);
    }

    @Override
    public void removeViews (int pStart, int pCount)
    {
        mUIViewFlipper.removeViews(pStart, pCount);
    }

    @Override
    public void removeViewsInLayout (int pStart, int pCount)
    {
        mUIViewFlipper.removeViewsInLayout(pStart, pCount);
    }



    public void setGestureLibrary (GestureLibrary pGestureLibrary)
    {
        mGestureLibrary = pGestureLibrary;
    }

    public void setPreviousGestureName (String pPreviousGestureName)
    {
        mPreviousGestureName = pPreviousGestureName;
    }

    public void setNextGestureName (String pNextGestureName)
    {
        mNextGestureName = pNextGestureName;
    }

    public void setPreviousIn (Animation pPreviousIn)
    {
        mPreviousIn = pPreviousIn;
    }

    public void setPreviousOut (Animation pPreviousOut)
    {
        mPreviousOut = pPreviousOut;
    }

    public void setNextIn (Animation pNextIn)
    {
        mNextIn = pNextIn;
    }

    public void setNextOut (Animation pNextOut)
    {
        mNextOut = pNextOut;
    }
}
