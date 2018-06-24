package com.foodprotect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/** Customizing AutoCompleteTextView to return Place Description
 * corresponding to the selected item
 */
public class CustomAutoCompleteTextview extends AutoCompleteTextView {

    public CustomAutoCompleteTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Returns the place description corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }
}