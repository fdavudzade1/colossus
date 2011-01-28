package com.codexperiments.colossus.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;


/**
 * Layout which can accept several childs, each one representing a page. Only one page is displayed
 * at a time and user can scroll between pages thanks to an horizontal scroll (like Android home
 * page).
 */
public class PageSlider extends HorizontalScrollView
{
    // Required as an HorizontalScrollView can contain only one child.
    private ViewGroup mWrapper;



    public PageSlider (Context pContext, AttributeSet pAttrSet, int pDefStyle)
    {
        super(pContext, pAttrSet, pDefStyle);
        initialize(pContext, pAttrSet);
    }

    public PageSlider (Context pContext, AttributeSet pAttrSet)
    {
        super(pContext, pAttrSet);
        initialize(pContext, pAttrSet);
    }

    public PageSlider (Context pContext)
    {
        super(pContext);
        initialize(pContext, null);
    }

    private void initialize (Context pContext, AttributeSet pAttrSet)
    {
        // Initializes UI components.
        mWrapper = new PageSliderLayout(pContext);
        super.addView(mWrapper, -1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }


    /**
     * Behaves like HorizontalScrollView except that when user releases his finger, aligns with the
     * current page (i.e. the most visible one).
     */
    @Override
    public boolean onTouchEvent (MotionEvent pMotionEvent)
    {
        int lEventAction = pMotionEvent.getAction();
        // If user has finished scrolling and put is finger up.
        if (lEventAction == MotionEvent.ACTION_UP || lEventAction == MotionEvent.ACTION_CANCEL) {
            int lScrollX = getScrollX();
            int lPageWidth = getMeasuredWidth();
            
            // Move to the current page. 
            int lActivePage = (lScrollX + (lPageWidth / 2)) / lPageWidth;
            smoothScrollTo(lActivePage * lPageWidth, 0);
            return true;
        }
        return super.onTouchEvent(pMotionEvent);
    }



    /**
     * Internal PageSlider layout which considers each child as a page and scale each of them so
     * that it fills in width the parent.
     */
    private class PageSliderLayout extends ViewGroup
    {
        public PageSliderLayout (Context pContext, AttributeSet pAttrs, int pDefStyle)
        {
            super(pContext, pAttrs, pDefStyle);
        }

        public PageSliderLayout (Context pContext, AttributeSet pAttrs)
        {
            super(pContext, pAttrs);
        }

        public PageSliderLayout (Context pContext)
        {
            super(pContext);
        }



        @Override
        protected void onMeasure(int pWidthMeasureSpec, int pHeightMeasureSpec)
        {
            int lWidth = 0;
            int lHeight = 0;
            int lPageWidth = PageSlider.this.getMeasuredWidth();
            int lChildCount = getChildCount();
            
            // Finds the width (page width * page number) and height (tallest child) of the wrapper.
            for (int i = 0; i < lChildCount; i++) {
                View lChild = getChildAt(i);
                if (lChild.getVisibility() != GONE) {
                    // Measure the child, specifying its width must be equal to a page size (or unspecified if page size
                    // is still unknown during first pass).
                    int lMeasureSpec = (lPageWidth == 0) ? MeasureSpec.UNSPECIFIED : MeasureSpec.EXACTLY;
                    int lChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lPageWidth, lMeasureSpec);
                    measureChildWithMargins(lChild, lChildWidthMeasureSpec, 0, pHeightMeasureSpec, 0);
                    
                    lHeight = Math.max(lHeight, lChild.getMeasuredHeight());
                    lWidth += lChild.getMeasuredWidth();
                }
            }

            // Takes into account padding.
            lWidth  += getPaddingLeft() + getPaddingRight();
            lWidth  = Math.max(lWidth, getSuggestedMinimumWidth());
            lHeight += getPaddingTop()  + getPaddingBottom();
            lHeight = Math.max(lHeight + getPaddingTop()  + getPaddingBottom(), getSuggestedMinimumHeight());

            // Sets dimensions.
            setMeasuredDimension(resolveSize(lWidth, MeasureSpec.UNSPECIFIED),
                                 resolveSize(lHeight, MeasureSpec.UNSPECIFIED));
        }

        @Override
        protected void onLayout (boolean pChanged, int pL, int pT, int pR, int pB)
        {
            int lChildTop = getPaddingTop();
            int lChildBottom = getBottom() - getTop() - getPaddingBottom();
            int lChildLeft = getPaddingLeft();
            int lChildCount = getChildCount();
            
            for (int i = 0; i < lChildCount; i++) {
                View lChild = getChildAt(i);
                
                if (lChild.getVisibility() != GONE) {
                    int lChildLeftTmp = lChildLeft;
                    lChildLeft += lChild.getMeasuredWidth();

                    lChild.layout(lChildLeftTmp, lChildTop, lChildLeft, lChildBottom);
                }
            }
        }
    }


    /**
     * Automatically scrolls to the specified page.
     * 
     * @param pPage Page index in the range [0, getPageCount() - 1]. No check is currently performed
     *        and thus no exception is raised if outside this range.
     */
    public void goToPage(int pPage) {
        smoothScrollTo(pPage * getMeasuredWidth(), 0);
    }

    /**
     * @return Current visible page.
     */
    public int getCurrentPage() {
        return getScrollX() / getMeasuredWidth();
    }

    /**
     * This operation can be expensive as each child must be checked to make sure it is not gone.
     * 
     * @return Number of available pages.
     */
    public int getPageCount() {
        int lChildCount = mWrapper.getChildCount();
        int lResult = 0;

        for (int i = 0; i < lChildCount; i++) {
            View lChild = mWrapper.getChildAt(i);
            if (lChild.getVisibility() != GONE) {
                lResult++;
            }
        }
        return lResult;
    }



    @Override
    public void addView (View pChild, int pIndex, android.view.ViewGroup.LayoutParams pParams)
    {
        mWrapper.addView(pChild, pIndex, pParams);
    }

    @Override
    public void addView (View pChild, int pIndex)
    {
        mWrapper.addView(pChild, pIndex);
    }

    @Override
    public void addView (View pChild, android.view.ViewGroup.LayoutParams pParams)
    {
        mWrapper.addView(pChild, pParams);
    }

    @Override
    public void addView (View pChild)
    {
        mWrapper.addView(pChild);
    }

    @Override
    public void addView (View pChild, int pWidth, int pHeight)
    {
        mWrapper.addView(pChild, pWidth, pHeight);
    }

    @Override
    public void removeAllViews ()
    {
        mWrapper.removeAllViews();
    }

    @Override
    public void removeAllViewsInLayout ()
    {
        mWrapper.removeAllViewsInLayout();
    }

    @Override
    public void removeView (View pView)
    {
        mWrapper.removeView(pView);
    }

    @Override
    public void removeViewAt (int pIndex)
    {
        mWrapper.removeViewAt(pIndex);
    }

    @Override
    public void removeViewInLayout (View pView)
    {
        mWrapper.removeViewInLayout(pView);
    }

    @Override
    public void removeViews (int pStart, int pCount)
    {
        mWrapper.removeViews(pStart, pCount);
    }

    @Override
    public void removeViewsInLayout (int pStart, int pCount)
    {
        mWrapper.removeViewsInLayout(pStart, pCount);
    }
}
