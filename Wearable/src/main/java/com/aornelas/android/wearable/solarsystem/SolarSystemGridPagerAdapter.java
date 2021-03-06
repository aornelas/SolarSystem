package com.aornelas.android.wearable.solarsystem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

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
    private BitmapDrawable mDefaultBg;

    public SolarSystemGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;

        mSolarSystem = new SolarSystem(
                new PlanetarySystem(
                        objectFragment(R.string.sun)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.mercury)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.venus)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.earth),
                        objectFragment(R.string.international_space_station),
                        objectFragment(R.string.moon),
                        objectFragment(R.string.apollo_lunar_rover)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.mars),
                        objectFragment(R.string.phobos),
                        objectFragment(R.string.deimos)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.main_asteroid_belt),
                        objectFragment(R.string.ceres),
                        objectFragment(R.string.vesta),
                        objectFragment(R.string.pallas),
                        objectFragment(R.string.hygiea)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.jupiter),
                        objectFragment(R.string.io),
                        objectFragment(R.string.europa),
                        objectFragment(R.string.ganymede),
                        objectFragment(R.string.callisto)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.saturn),
                        objectFragment(R.string.mimas),
                        objectFragment(R.string.enceladus),
                        objectFragment(R.string.tethys),
                        objectFragment(R.string.dione),
                        objectFragment(R.string.rhea),
                        objectFragment(R.string.titan),
                        objectFragment(R.string.hyperion),
                        objectFragment(R.string.iapetus),
                        objectFragment(R.string.phoebe)

                ),
                new PlanetarySystem(
                        objectFragment(R.string.uranus),
                        objectFragment(R.string.miranda),
                        objectFragment(R.string.ariel),
                        objectFragment(R.string.umbriel),
                        objectFragment(R.string.titania),
                        objectFragment(R.string.oberon)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.neptune),
                        objectFragment(R.string.triton),
                        objectFragment(R.string.larissa)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.pluto),
                        objectFragment(R.string.charon),
                        objectFragment(R.string.nix),
                        objectFragment(R.string.hydra),
                        objectFragment(R.string.kerberos),
                        objectFragment(R.string.styx)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.kuiper_belt),
                        objectFragment(R.string.haumea),
                        objectFragment(R.string.makemake),
                        objectFragment(R.string.eris)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.sedna)
                ),
                new PlanetarySystem(
                        objectFragment(R.string.oort_cloud)
                )
        );
        mDefaultBg = new BitmapDrawable(ctx.getResources(),
                BitmapFactory.decodeResource(ctx.getResources(), R.drawable.loading));
    }


    static final int[][] BG_IMAGES = new int[][] {
            {
                    R.drawable.sun
            },
            {
                    R.drawable.mercury
            },
            {
                    R.drawable.venus
            },
            {
                    R.drawable.earth,
                    R.drawable.international_space_station,
                    R.drawable.moon,
                    R.drawable.apollo_lunar_rover
            },
            {
                    R.drawable.mars,
                    R.drawable.phobos,
                    R.drawable.deimos
            },
            {
                    R.drawable.main_asteroid_belt,
                    R.drawable.ceres,
                    R.drawable.vesta,
                    R.drawable.pallas,
                    R.drawable.hygiea
            },
            {
                    R.drawable.jupiter,
                    R.drawable.io,
                    R.drawable.europa,
                    R.drawable.ganymede,
                    R.drawable.callisto
            },
            {
                    R.drawable.saturn,
                    R.drawable.mimas,
                    R.drawable.enceladus,
                    R.drawable.tethys,
                    R.drawable.dione,
                    R.drawable.rhea,
                    R.drawable.titan,
                    R.drawable.hyperion,
                    R.drawable.iapetus,
                    R.drawable.phoebe
            },
            {
                    R.drawable.uranus,
                    R.drawable.miranda,
                    R.drawable.ariel,
                    R.drawable.umbriel,
                    R.drawable.titania,
                    R.drawable.oberon
            },
            {
                    R.drawable.neptune,
                    R.drawable.triton,
                    R.drawable.larissa,
                    R.drawable.eris
            },
            {
                    R.drawable.pluto,
                    R.drawable.charon,
                    R.drawable.nix,
                    R.drawable.hydra,
                    R.drawable.kerberos,
                    R.drawable.styx
            },
            {
                    R.drawable.kuiper_belt,
                    R.drawable.haumea,
                    R.drawable.makemake,
                    R.drawable.eris,
            },
            {
                    R.drawable.sedna
            },
            {
                    R.drawable.oort_cloud
            }
    };

    LruCache<Point, Drawable> mPageBackgrounds = new LruCache<Point, Drawable>(3) {
        @Override
        protected Drawable create(final Point page) {
            final int row = page.y;
            final int column = page.x;
            final int resid = BG_IMAGES[row][column];
            new DrawableLoadingTask(mContext) {
                @Override
                protected void onPostExecute(Drawable result) {
                    TransitionDrawable background = new TransitionDrawable(new Drawable[] {
                            mDefaultBg,
                            rotateDrawable(result)
                    });
                    mPageBackgrounds.put(page, background);
                    notifyPageBackgroundChanged(row, column);
                    background.startTransition(TRANSITION_DURATION_MILLIS);

                    hideUIAfterDelay(row, column);
                }
            }.execute(resid);

            return mDefaultBg;
        }

        /**
         * Rotates the given drawable by 90 degrees
         */
        private Drawable rotateDrawable(@NonNull final Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            canvas.rotate(90, canvas.getWidth() / 2, canvas.getHeight() / 2);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return new BitmapDrawable(mContext.getResources(), bitmap);
        }
    };

    public void hideUIAfterDelay(int row, int column) {
        // Only hide label if background was already loaded; otherwise, the postExecute of load will
        if (getBackgroundForPage(row, column) != mDefaultBg) {
            final Activity activity = getFragment(row, column).getActivity();
            final View view = getFragment(row, column).getView();
            if (activity == null || view == null) {
                return;
            }
            final TextView label = (TextView) view.findViewById(R.id.text);
            final DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) activity
                    .findViewById(R.id.page_indicator);
            final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (label != null) {
                        label.setVisibility(View.INVISIBLE);
                    }
                    if (dotsPageIndicator != null) {
                        dotsPageIndicator.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    label.startAnimation(fadeOut);
                    dotsPageIndicator.startAnimation(fadeOut);
                }
            }, 1000);
        }
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

    private Fragment objectFragment(int textRes) {
        Resources res = mContext.getResources();
        Fragment fragment = new CustomFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CustomFragment.NAME_KEY, res.getText(textRes).toString());
        fragment.setArguments(bundle);
        return fragment;
    }

    private class SolarSystem {
        final List<PlanetarySystem> planetarySystems = new ArrayList<>();

        public SolarSystem(PlanetarySystem... planetarySystems) {
            for (PlanetarySystem pS : planetarySystems) {
                this.planetarySystems.add(pS);
            }
        }

        PlanetarySystem getPlanetarySystem(int i) {
            return planetarySystems.get(i);
        }

        public int getPlanetarySystemCount() {
            return planetarySystems.size();
        }
    }

    private class PlanetarySystem {
        final List<Fragment> rows = new ArrayList<>();

        public PlanetarySystem(Fragment... fragments) {
            for (Fragment f : fragments) {
                add(f);
            }
        }

        public void add(Fragment f) {
            rows.add(f);
        }

        Fragment getFragment(int i) {
            return rows.get(i);
        }

        public int getFragmentCount() {
            return rows.size();
        }
    }

    @Override
    public Fragment getFragment(int row, int col) {
        return mSolarSystem.getPlanetarySystem(row).getFragment(col);
    }

    @Override
    public Drawable getBackgroundForPage(final int row, final int column) {
        return mPageBackgrounds.get(new Point(column, row));
    }

    @Override
    public int getRowCount() {
        return mSolarSystem.getPlanetarySystemCount();
    }

    @Override
    public int getColumnCount(int rowNum) {
        return mSolarSystem.getPlanetarySystem(rowNum).getFragmentCount();
    }
}
