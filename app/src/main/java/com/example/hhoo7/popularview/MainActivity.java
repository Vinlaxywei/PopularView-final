package com.example.hhoo7.popularview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private String mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MovieFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String mode = Utility.getModeFromPreference(getBaseContext());
        MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
        if (mf != null && !mode.equals(mMode)) {
            mf.onModeChange();
        }
    }
}
