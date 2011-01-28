package com.codexperiments.colossus.type;


/**
 * Small container that contains a modifiable int value which can be referenced from several places in the code.
 */
public final class IntHolder
{
    private int mValue;


    public IntHolder()
	{
		mValue = 0;
	}

    public IntHolder(int pValue)
	{
		mValue = pValue;
	}


	public int getValue()
	{
		return mValue;
	}

	public void setValue(int pValue)
	{
		mValue = pValue;
	}
}
