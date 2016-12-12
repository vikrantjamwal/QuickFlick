package com.vik.android.quickflick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String DETAILFRAGMENT_TAG = "DFTAG";

    public static boolean TWO_PANE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container) != null){
            TWO_PANE = true;
        }else {
            TWO_PANE = false;
        }
    }
}
