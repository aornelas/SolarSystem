package com.aornelas.android.wearable.solarsystem;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Resources res = getResources();

        // Obtain the DismissOverlayView element
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.long_press_intro);
        mDismissOverlay.showIntroIfNecessary();

        // Configure a gesture detector
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                mDismissOverlay.show();
            }
        });

        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(round ?
                        R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                pager.setPageMargins(rowMargin, colMargin);

                // GridViewPager relies on insets to properly handle
                // layout for round displays. They must be explicitly
                // applied since this listener has taken them over.
                pager.onApplyWindowInsets(insets);
                return insets;
            }
        });
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
        final SolarSystemGridPagerAdapter adapter = new SolarSystemGridPagerAdapter(this, getFragmentManager());
        pager.setAdapter(adapter);
        final DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setDotRadius(4);
        dotsPageIndicator.setDotRadiusSelected(7);
        dotsPageIndicator.setPager(pager);
        // We'll dismiss it ourselves, so prevent auto fade out
        dotsPageIndicator.setDotFadeOutDelay(999999);

        dotsPageIndicator.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
            private TextView label;

            @Override
            public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {
            }

            @Override
            public void onPageSelected(int row, int col) {
                final Fragment currentFragment = adapter.getFragment(row, col);
                label = (TextView) currentFragment.getView().findViewById(R.id.text);
                label.setVisibility(View.VISIBLE);
                dotsPageIndicator.setVisibility(View.VISIBLE);
                adapter.hideUIAfterDelay(row, col);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (label != null) {
                    label.setVisibility(View.VISIBLE);
                }
                if (dotsPageIndicator != null) {
                    dotsPageIndicator.setVisibility(View.VISIBLE);
                }
            }
        });

        // Prevent the screen timeout from stopping this application
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
