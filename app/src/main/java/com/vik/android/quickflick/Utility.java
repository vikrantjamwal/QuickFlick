package com.vik.android.quickflick;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utility {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / context.getResources().getInteger(R.integer.item_width));
        return noOfColumns;
    }

}
