package com.codexperiments.colossus.utility;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;


public final class ResourceUtil
{
    private Context mContext;
    private TypedArray mCustomAttributes;
    
    
    public ResourceUtil (Context pContext, AttributeSet pAttrSet, int[] pAttr, int pDefStyleAttr)
    {
        super();
        
        mContext = pContext;
        if (pAttrSet != null) {
            mCustomAttributes = pContext.obtainStyledAttributes(pAttrSet, pAttr, pDefStyleAttr, 0);
        }
    }
    
    public void close() {
        mCustomAttributes.recycle();
    }

    /**
     * Retrieves a resource Id stored in a custom attribute (see attr.xml files) if defined. In case
     * it is not, returns the default resource Id specified.
     * 
     * @param pCustomAttributeId Id of the custom attribute containing the searched resource.
     * @param pDefaultResourceId Default Id to return (not a custom attribute Id but a resource Id)
     * @return Resource Id defined in the specified custom attribute or pDefaultId if it is not.
     */
    public int getResourceId (int pCustomAttributeId, int pDefaultResourceId)
    {
        if (mCustomAttributes != null) {
            return mCustomAttributes.getResourceId(pCustomAttributeId, pDefaultResourceId);
        } else {
            return pDefaultResourceId;
        }
    }

    /**
     * Retrieves a string stored in a custom attribute (see attr.xml files) if defined. In case it
     * is not, returns the default string specified.
     * 
     * @param pCustomAttributeId Id of the custom attribute containing the searched string.
     * @param pDefaultId Default Id to return (not a custom attribute Id but a resource Id)
     * @return String defined in the specified custom attribute or pDefaultId if it is not.
     */
    public String getString (int pCustomAttributeId, String pDefaultString)
    {
        if (mCustomAttributes != null) {
            String lResult = mCustomAttributes.getString(pCustomAttributeId);
            if (lResult != null) {
                return lResult;
            } else {
                return pDefaultString;
            }
        } else {
            return pDefaultString;
        }
    }

    /**
     * Retrieves a string stored in a custom attribute (see attr.xml files) if defined. In case it
     * is not, returns the default string specified.
     * 
     * @param pCustomAttributeId Id of the custom attribute containing the searched string.
     * @param pDefaultId Default Id to return (not a custom attribute Id but a resource Id)
     * @return String defined in the specified custom attribute or pDefaultId if it is not.
     */
    public String getString (int pCustomAttributeId, int pDefaultStringId)
    {
        if (mCustomAttributes != null) {
            String lResult = mCustomAttributes.getString(pCustomAttributeId);
            if (lResult != null) {
                return lResult;
            } else {
                return mContext.getString(pDefaultStringId);
            }
        } else {
            return mContext.getString(pDefaultStringId);
        }
    }

    /**
     * Retrieves an integer stored in a custom attribute (see attr.xml files) if defined. In case it
     * is not, returns the default integer specified.
     * 
     * @param pCustomAttributeId Id of the custom attribute containing the searched integer.
     * @param pDefaultId Default Id to return (not a custom attribute Id but a resource Id)
     * @return String defined in the specified custom attribute or pDefaultId if it is not.
     */
    public int getInteger (int pCustomAttributeId, int pDefaultInteger)
    {
        if (mCustomAttributes != null) {
            return mCustomAttributes.getInt(pCustomAttributeId, pDefaultInteger);
        } else {
            return pDefaultInteger;
        }
    }

    /**
     * Retrieves a boolean stored in a custom attribute (see attr.xml files) if defined. In case it
     * is not, returns the default boolean specified.
     * 
     * @param pCustomAttributeId Id of the custom attribute containing the searched boolean.
     * @param pDefaultId Default Id to return (not a custom attribute Id but a resource Id)
     * @return String defined in the specified custom attribute or pDefaultId if it is not.
     */
    public boolean getBoolean (int pCustomAttributeId, boolean pDefaultBoolean)
    {
        if (mCustomAttributes != null) {
            return mCustomAttributes.getBoolean(pCustomAttributeId, pDefaultBoolean);
        } else {
            return pDefaultBoolean;
        }
    }
}
