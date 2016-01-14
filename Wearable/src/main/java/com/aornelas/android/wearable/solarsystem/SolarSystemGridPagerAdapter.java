package com.aornelas.android.wearable.solarsystem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs fragments as requested by the GridViewPager. For each row a different background is
 * provided.
 * <p>
 * Always avoid loading resources from the main thread. In this sample, the background images are
 * loaded from an background task and then updated using {@link #notifyRowBackgroundChanged(int)}
 * and {@link #notifyPageBackgroundChanged(int, int)}.
 */
public class SolarSystemGridPagerAdapter extends FragmentGridPagerAdapter {
    private static final int TRANSITION_DURATION_MILLIS = 500;

    private final Context mContext;
    private SolarSystem mSolarSystem;
    private ColorDrawable mDefaultBg;

    public SolarSystemGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;

        mSolarSystem = new SolarSystem(
                zoomFragment(R.string.sun),
                zoomFragment(R.string.mercury),
                zoomFragment(R.string.venus),
                zoomFragment(R.string.earth),
                zoomFragment(R.string.moon),
                zoomFragment(R.string.mars),
                zoomFragment(R.string.phobos),
                zoomFragment(R.string.deimos),
                zoomFragment(R.string.jupiter),
                zoomFragment(R.string.saturn),
                zoomFragment(R.string.uranus),
                zoomFragment(R.string.neptune),
                zoomFragment(R.string.pluto)
        );
        mDefaultBg = new ColorDrawable(ctx.getResources().getColor(R.color.black));
    }


    static final int[][] BG_IMAGES = new int[][] {
            {
                    R.drawable.sun,
                    R.drawable.mercury,
                    R.drawable.venus,
                    R.drawable.earth,
                    R.drawable.moon,
                    R.drawable.mars,
                    R.drawable.phobos,
                    R.drawable.deimos,
                    R.drawable.jupiter,
                    R.drawable.saturn,
                    R.drawable.uranus,
                    R.drawable.neptune,
                    R.drawable.pluto
            }
    };

    LruCache<Point, Drawable> mPageBackgrounds = new LruCache<Point, Drawable>(3) {
        @Override
        protected Drawable create(final Point page) {
            int resid = BG_IMAGES[page.y][page.x];
            new DrawableLoadingTask(mContext) {
                @Override
                protected void onPostExecute(Drawable result) {
                    TransitionDrawable background = new TransitionDrawable(new Drawable[] {
                            mDefaultBg,
                            result
                    });
                    mPageBackgrounds.put(page, background);
                    notifyPageBackgroundChanged(page.y, page.x);
                    background.startTransition(TRANSITION_DURATION_MILLIS);
                }
            }.execute(resid);
            return mDefaultBg;
        }
    };

    private Fragment zoomFragment(int textRes) {
        Resources res = mContext.getResources();
        Fragment fragment = new CustomFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CustomFragment.NAME_KEY, res.getText(textRes).toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    private class SolarSystem {
        final List<Fragment> columns = new ArrayList<Fragment>();

        public SolarSystem(Fragment... fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }

        public void add(Fragment f) {
            columns.add(f);
        }

        Fragment getPlanet(int i) {
            return columns.get(i);
        }

        public int getPlanetCount() {
            return columns.size();
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        // TODO: Consider moon rows
        return mSolarSystem.getPlanet(col);
    }

    @Override
    public Drawable getBackgroundForPage(final int row, final int column) {
        return mPageBackgrounds.get(new Point(column, row));
    }

    @Override
    public int getRowCount() {
        // TODO: Change row count based on number of moons on current planet
        return 1;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mSolarSystem.getPlanetCount();
    }

    class DrawableLoadingTask extends AsyncTask<Integer, Void, Drawable> {
        private static final String TAG = "Loader";
        private Context context;

        DrawableLoadingTask(Context context) {
            this.context = context;
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            Log.d(TAG, "Loading asset 0x" + Integer.toHexString(params[0]));
            return context.getResources().getDrawable(params[0]);
        }
    }
}
