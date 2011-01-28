package com.codexperiments.colossus.view.tokenizer;


import android.widget.MultiAutoCompleteTextView;

import com.codexperiments.colossus.view.MultiLevelAutoCompleteTextView;


/**
 * Simple tokenizer for Web query (handles quoted expressions).
 * 
 * MultiLevelAutoCompleteTextView should provide a more complex approach because QueryTokenizer
 * because of quoted expressions gets a bit complex.
 */
public class SpaceAndQuoteTokenizer implements MultiLevelAutoCompleteTextView.Tokenizer, MultiAutoCompleteTextView.Tokenizer
{
    private static final char SEPARATION_SYMBOL = ' ';
    private static final char QUOTATION_SYMBOL  = '"';

    // State in a Tokenizer: although it works, this is a "smelly code"...
    // This is why Tokenizer is not satisfactory.
    private final String[] mSearchOptions;
    private int mTokenStart;
    private char mExpectedEndSymbol;


    public SpaceAndQuoteTokenizer (String[] pSearchOptions)
    {
        mSearchOptions = pSearchOptions;
        mTokenStart = -1;
        mExpectedEndSymbol = '\n';
    }


    @Override
    public int findTokenStart (CharSequence pText, int pCursor)
    {
        int lPreviousQuoteCount = 0;
        int lPreviousQuotePos = -1;
        for (int i = pCursor - 1; i >= 0; i--) {
            if (pText.charAt(i) == QUOTATION_SYMBOL) {
                if (lPreviousQuotePos == -1) {
                    // Saves position of the last quote (including seach option) before cursor.
                    lPreviousQuotePos = i;
                }
                lPreviousQuoteCount++;
            }
        }
        
        // Takes into account search option if part of the Token
        boolean lIsInQuotation = (lPreviousQuoteCount % 2) != 0;
        if (lIsInQuotation && lPreviousQuotePos != -1) {
            String lBeforeText = pText.subSequence(0, lPreviousQuotePos).toString();
            for (int iOption = 0; iOption < mSearchOptions.length; iOption++) {
                String lOption = mSearchOptions[iOption];
                // Should also check for a space or " or "string begin" right before search option.
                if (lBeforeText.endsWith(lOption)) {
                    lPreviousQuotePos = lPreviousQuotePos - lOption.length();
                }
            }
        }

        int lPreviousSpacePos = pCursor - 1;
        while (lPreviousSpacePos >= 0 && pText.charAt(lPreviousSpacePos) != SEPARATION_SYMBOL) {
            lPreviousSpacePos--;
        }

        if (!lIsInQuotation) {
            mExpectedEndSymbol = SEPARATION_SYMBOL;
            mTokenStart = Math.max(lPreviousQuotePos, lPreviousSpacePos) + 1;
            return mTokenStart;
        } else {
            mExpectedEndSymbol = QUOTATION_SYMBOL;
            mTokenStart = lPreviousQuotePos;
            return mTokenStart;
        }
    }

    @Override
    public int findTokenEnd (CharSequence pText, int pCursor)
    {
        int lLength = pText.length();

        if (mExpectedEndSymbol == QUOTATION_SYMBOL) {
            for (int i = pCursor; i < lLength; ++i) {
                if (pText.charAt(i) == QUOTATION_SYMBOL) {
                    return i;
                }
            }
        } else {
            for (int i = pCursor; i < lLength; ++i) {
                if (pText.charAt(i) == QUOTATION_SYMBOL) {
                    // Checks if found token is an option. If yes, we need to ignore the quote
                    // (which is an opening quote) and go to the second one.
                    boolean lWithinOption = false;
                    String lFoundSequence = pText.subSequence(mTokenStart, i).toString();

                    for (int iOption = 0; iOption < mSearchOptions.length; iOption++) {
                        String lOption = mSearchOptions[iOption];
                        // Ignores first quote character if cursor is on a search option.
                        // Should also check for a space or " or "string begin" right before search option.
                        if (lFoundSequence.equals(lOption)) {
                            lWithinOption = true;
                            break;
                        }
                    }

                    if (!lWithinOption) {
                        return i;
                    } else {
                        // Looks for closing quote.
                        for (++i; i < lLength; ++i) {
                            if (pText.charAt(i) == QUOTATION_SYMBOL) {
                                return i;
                            }
                        }
                    }
                } else if (pText.charAt(i) == SEPARATION_SYMBOL) {
                    return i;
                }
            }
        }

        return lLength;
    }

    @Override
    public CharSequence terminateToken (CharSequence pText)
    {
        return pText;
    }
}
