package com.codexperiments.colossus.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewAnimator;

import com.codexperiments.colossus.R;
import com.codexperiments.colossus.utility.ResourceUtil;


/**
 * Layout which gives the illusion of an opening drawer from top to bottom over main view (with a
 * transparency effect). Background view is in fact cached into a bitmap and set as background
 * drawable of the top view. First child (index 0) is the main view and other childs are in the
 * drawer.
 * 
 * Custom attributes "openIn", "openOut", "closeIn" and "closeOut" allow drawer opening and closing
 * animation customization. Drawer transparency (to see cached main view when drawer is opened) can
 * be set using "transparent" attribute. Attribute "clampAnimation" indicates if childs smaller than
 * main view need their animation to be shifted (else a delay will appear as the unfilled part will
 * scroll first, giving the illusion of a delay in opening).
 * 
 * Component is not finished yet.
 */
public class PopupDrawer extends ViewAnimator
{
    private static final int ORIENTATION_HORIZONTAL = 1;
    private static final int ORIENTATION_VERTICAL = 2;

    private boolean mOpened;
    private boolean mTransparent;
    private int mOrientation;

    // Animations properties.
    private boolean mClamp;
    private Animation mOpenIn;
    private Animation mOpenOut;
    private Animation mCloseIn;
    private Animation mCloseOut;
    
    // Display cache.
    private Bitmap mMainDisplayBackground;



    public PopupDrawer (Context pContext, AttributeSet pAttrsSet)
    {
        super(pContext, pAttrsSet);
        initialize(pContext, pAttrsSet);
    }
    
    public PopupDrawer (Context pContext)
    {
        super(pContext);
        initialize(pContext, null);
    }

    private void initialize(Context pContext, AttributeSet pAttrsSet)
    {
        ResourceUtil lResourceUtil = new ResourceUtil(pContext, pAttrsSet, R.styleable.SwitchDrawer, 0);

        mOpened = false;
        mTransparent = lResourceUtil.getBoolean(R.styleable.SwitchDrawer_transparent, true);
        mOrientation = lResourceUtil.getInteger(R.styleable.SwitchDrawer_orientation, ORIENTATION_VERTICAL);

        // Retrieves animation properties.
        mClamp    = lResourceUtil.getBoolean(R.styleable.SwitchDrawer_clampAnimation, true);
        mOpenIn   = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.SwitchDrawer_openIn,   R.anim.switchdrawer_open_in));
        mOpenOut  = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.SwitchDrawer_openOut,  R.anim.switchdrawer_open_out));
        mCloseIn  = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.SwitchDrawer_closeIn,  R.anim.switchdrawer_close_in));
        mCloseOut = AnimationUtils.loadAnimation(pContext, lResourceUtil.getResourceId(R.styleable.SwitchDrawer_closeOut, R.anim.switchdrawer_close_out));

        // Keeps views in cache between animations.
        setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
    }


    
    /**
     * Opens the drawer with the selected child.
     * @param pChildIndex Between 1 and getChildCount(). Child at index 0 is displayed when drawer is closed.
     */
    public void open (int pChildIndex)
    {
        if (pChildIndex > 0 && pChildIndex < getChildCount() && (!mOpened || (pChildIndex != getDisplayedChild()))) {
            // Sets animation to use when opening the drawer.
            setInAnimation(mOpenIn);
            setOutAnimation(mOpenOut);

            View lChild = getChildAt(pChildIndex);
            // Clamps the animation if the incoming view is smaller than the whole surface. Else, it
            // can look like there is a delay before incoming view appears (e.g. if percentages are
            // used) as the container size is taken into account instead of the child size.
            if (mClamp) {
                if ((mOrientation == ORIENTATION_VERTICAL) && (lChild.getMeasuredHeight() < getMeasuredHeight())) {
                    double lShift = (double)lChild.getMeasuredHeight() / (double)getMeasuredHeight() - 1;
                    getInAnimation().setStartOffset((long) (getInAnimation().computeDurationHint() * lShift));
                } else if ((mOrientation == ORIENTATION_HORIZONTAL) && (lChild.getMeasuredWidth() < getMeasuredWidth())) {
                    double lShift = (double)lChild.getMeasuredWidth() / (double)getMeasuredWidth() - 1;
                    getInAnimation().setStartOffset((long) (getInAnimation().computeDurationHint() * lShift));
                }
            }
            
            // Sets the background behind the opening child (as a capture of the initial view).
            if (mTransparent) {
                View lMainDisplay = getChildAt(0);
                boolean lDrawingCacheValueBackup = lMainDisplay.isDrawingCacheEnabled();
                lMainDisplay.setDrawingCacheEnabled(true);
                lMainDisplay.buildDrawingCache(true);
                
                mMainDisplayBackground = Bitmap.createBitmap(lMainDisplay.getDrawingCache(true));
                BitmapDrawable lMainDisplayDrawable = new BitmapDrawable(mMainDisplayBackground);
                lMainDisplayDrawable.setTileModeY(TileMode.CLAMP);
                setBackgroundDrawable(lMainDisplayDrawable);
                
                lMainDisplay.destroyDrawingCache();
                lMainDisplay.setDrawingCacheEnabled(lDrawingCacheValueBackup);
            }

            // Opens the drawer with the selected child.
            setDisplayedChild(pChildIndex);
            mOpened = true;
        }
    }

    /**
     * Closes the drawer (displays child at index 0).
     */
    public void close ()
    {
        if (mOpened) {
            // Sets close animation and closes the drawer.
            setInAnimation(mCloseIn);
            setOutAnimation(mCloseOut);
            setDisplayedChild(0);

            // Allows "freeing" the background bitmap.
            mOpened = false;
        }
    }
}
