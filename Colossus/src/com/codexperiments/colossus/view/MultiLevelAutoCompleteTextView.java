package com.codexperiments.colossus.view;


import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.codexperiments.colossus.R;
import com.codexperiments.colossus.utility.ResourceUtil;


/**
 * Slight variation over MultiAutoCompleteTextView which allows filtering on text view whole text
 * and tokens at the same time as soon as text or caret position is changed. Two filters are used
 * for that purpose: local filter is for tokens wheras global one is for whole text. Using two
 * filters results in two background threads being used while filtering Because of the way Filter
 * class is implemented. No simpler workaround seems possible.
 * 
 * Also, this component opens drop down view. QueryTokenizer taken from MultiAutoCompleteTextView is
 * too simple. Should implement something based on regular expressions to allow easy implementation
 * of more complex expressions.
 */
@Deprecated
public class MultiLevelAutoCompleteTextView extends AutoCompleteTextView
{
    private Tokenizer mTokenizer;
    private Filter mGlobalFilter;
    private boolean mGlobalReplace;
    private boolean mFilterOnCursorChange;



    public MultiLevelAutoCompleteTextView (Context pContext)
    {
        super(pContext, null);
        initialize(pContext, null, android.R.attr.autoCompleteTextViewStyle);
    }

    public MultiLevelAutoCompleteTextView (Context pContext, AttributeSet pAttrsSet)
    {
        super(pContext, pAttrsSet, android.R.attr.autoCompleteTextViewStyle);
        initialize(pContext, pAttrsSet, android.R.attr.autoCompleteTextViewStyle);
    }

    public MultiLevelAutoCompleteTextView (Context pContext, AttributeSet pAttrsSet, int pDefStyle)
    {
        super(pContext, pAttrsSet, pDefStyle);
        initialize(pContext, pAttrsSet, pDefStyle);
    }

    private void initialize (Context pContext, AttributeSet pAttrsSet, int pDefStyle)
    {
        ResourceUtil lResourceUtil = new ResourceUtil(pContext, pAttrsSet, R.styleable.SuggestMultiAutoCompleteTextView, pDefStyle);

        mTokenizer            = null;
        mGlobalFilter         = null;
        mFilterOnCursorChange = lResourceUtil.getBoolean(R.styleable.SuggestMultiAutoCompleteTextView_filterOnCursorChange, true);
    }

    /**
     * Sets the Tokenizer that will be used to determine the relevant range of the text where the
     * user is typing.
     */
    public void setTokenizer (Tokenizer pTokenizer)
    {
        mTokenizer = pTokenizer;
    }


    @Override
    protected void onSelectionChanged (int pSelStart, int pSelEnd)
    {
        super.onSelectionChanged(pSelStart, pSelEnd);
        if (mFilterOnCursorChange && (mTokenizer != null)) {
            performLocalFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
        }
    }

    @Override
    protected CharSequence convertSelectionToString (Object pSelectedItem)
    {
        if (mGlobalFilter != null) {
            mGlobalReplace = ((GlobalFilterable) getAdapter()).isItemGlobal(pSelectedItem);
        }
        return super.convertSelectionToString(pSelectedItem);
    }

    /**
     * Starts filtering the content of the drop down list. The filtering pattern is the specified
     * range of text from the edit box. Subclasses may override this method to filter with a
     * different pattern, for instance a smaller substring of <code>text</code>.
     */
    @Override
    protected void performFiltering (CharSequence pText, int pKeyCode)
    {
        if (mGlobalFilter != null && enoughToFilter ()) {
            mGlobalFilter.filter(pText, this);
        }

        performLocalFiltering(pText, pKeyCode);
    }

    /**
     * Instead of filtering on the entire contents of the edit box, this subclass method filters on
     * the range from {@link Tokenizer#findTokenStart} to {@link #getSelectionEnd} if the length of
     * that range meets or exceeds {@link #getThreshold}.
     */
    protected void performLocalFiltering (CharSequence pText, int pKeyCode)
    {
        int lCursor = getSelectionEnd();
        int lStart = mTokenizer.findTokenStart(pText, lCursor);
        int lEnd = mTokenizer.findTokenEnd(pText, lCursor);
        if (enoughToFilterLocal ()) {
            getFilter().filter(pText.subSequence(lStart, lEnd), this);
        } else {
            getFilter().filter(null, this);
        }
    }

    /**
     * Filters over tokens only when the length of the range from {@link Tokenizer#findTokenStart}
     * to {@link #getSelectionEnd} meets or exceeds {@link #getThreshold}.
     */
    protected boolean enoughToFilterLocal ()
    {
        Editable pText = getText();

        int lCursor = getSelectionEnd();
        if (lCursor < 0 || mTokenizer == null) {
            return false;
        }

        int lStart = mTokenizer.findTokenStart(pText, lCursor);
        int lEnd = mTokenizer.findTokenEnd(pText, lCursor);

        if ((lEnd - lStart) >= getThreshold()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Instead of validating the entire text, this subclass method validates each token of the text
     * individually. Empty tokens are removed.
     */
    @Override
    public void performValidation ()
    {
        Validator lValidator = getValidator();

        if (lValidator == null || mTokenizer == null) {
            return;
        }

        Editable lEditableText = getText();
        int i = getText().length();
        while (i > 0) {
            int lStart = mTokenizer.findTokenStart(lEditableText, i);
            int lEnd = mTokenizer.findTokenEnd(lEditableText, lStart);

            CharSequence sub = lEditableText.subSequence(lStart, lEnd);
            if (TextUtils.isEmpty(sub)) {
                lEditableText.replace(lStart, i, "");
            } else if (!lValidator.isValid(sub)) {
                lEditableText.replace(lStart, i, mTokenizer.terminateToken(lValidator.fixText(sub)));
            }

            i = lStart;
        }
    }

    /**
     * <p>
     * Performs the text completion by replacing the range from {@link Tokenizer#findTokenStart} to
     * {@link #getSelectionEnd} by the the result of passing <code>text</code> through
     * {@link Tokenizer#terminateToken}. In addition, the replaced region will be marked as an
     * AutoText substition so that if the user immediately presses DEL, the completion will be
     * undone. Subclasses may override this method to do some different insertion of the content
     * into the edit box.
     * </p>
     * 
     * @param pText the selected suggestion in the drop down list
     */
    @Override
    protected void replaceText (CharSequence pText)
    {
        if (mGlobalReplace) {
            super.replaceText(pText);
        } else {
            int lCursor = getSelectionEnd();
            int lStart = mTokenizer.findTokenStart(getText(), lCursor);
            int lEnd = mTokenizer.findTokenEnd(getText(), lCursor);
    
            Editable lEditable = getText();
            lEditable.replace(lStart, lEnd, mTokenizer.terminateToken(pText));
        }
    }


    @Override
    public <T extends ListAdapter & Filterable> void setAdapter (T pAdapter)
    {
        super.setAdapter(pAdapter);
        if (pAdapter instanceof GlobalFilterable) {
            mGlobalFilter = ((GlobalFilterable) pAdapter).getGlobalFilter();
        }
    }

    public boolean filterOnCursorChange ()
    {
        return mFilterOnCursorChange;
    }

    public void setFilterOnCursorChange (boolean pFilterOnCursorChange)
    {
        mFilterOnCursorChange = pFilterOnCursorChange;
    }



    public interface GlobalFilterable extends Filterable
    {
        Filter getGlobalFilter ();

        boolean isItemGlobal (Object pSelectedItem);
    }



    public static interface Tokenizer
    {
        /**
         * Returns the start of the token that ends at offset <code>cursor</code> within
         * <code>text</code>.
         */
        public int findTokenStart (CharSequence text, int cursor);

        /**
         * Returns the end of the token (minus trailing punctuation) that begins at offset
         * <code>cursor</code> within <code>text</code>.
         */
        public int findTokenEnd (CharSequence text, int cursor);

        /**
         * Returns <code>text</code>, modified, if necessary, to ensure that it ends with a token
         * terminator (for example a space or comma).
         */
        public CharSequence terminateToken (CharSequence text);
    }
}
