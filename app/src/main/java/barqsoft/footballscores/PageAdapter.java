/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package barqsoft.footballscores;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.annotation.Retention;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.LONG;

/**
 * Created by Igor Klimov on 1/31/2016.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = "PageAdapter";

    @Retention(SOURCE)
    @IntDef({TODAY, YESTERDAY, TOMORROW, DAY_BEFORE_YESTERDAY, DAY_AFTER_TOMORROW})
    public @interface Day {
    }

    public static final int TODAY = 2;
    public static final int YESTERDAY = 1;
    public static final int TOMORROW = 3;
    public static final int DAY_AFTER_TOMORROW = 4;
    public static final int DAY_BEFORE_YESTERDAY = 0;

    private static final int NUM_PAGES = 5;
    public static final long ONE_DAY_IN_MILLIS = 86400000;
    public static final SimpleDateFormat FULL_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private Context mContext;
    private MainScreenFragment[] mViewFragments = new MainScreenFragment[5];


    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
        Date d = new Date();
        for (int i = 0; i < NUM_PAGES; i++) {
            d.setTime(System.currentTimeMillis() + ((i - 2) * ONE_DAY_IN_MILLIS));
            mViewFragments[i] = new MainScreenFragment();
            mViewFragments[i].setFragmentDate(FULL_FORMAT.format(d));
        }
    }

    @Override
    public Fragment getItem(int i) {
        return mViewFragments[i];
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(@Day int position) {
        Calendar c = Calendar.getInstance();
        int d = c.get(DAY_OF_WEEK);
        String res = "";

        if (position == TODAY) {
            res =  mContext.getString(R.string.today);
        } else if (position == YESTERDAY) {
            res =  mContext.getString(R.string.yesterday);
        } else if (position == TOMORROW) {
            res = mContext.getString(R.string.tomorrow);
        } else if (position == DAY_BEFORE_YESTERDAY) {
            int n = adjustWeekDay(d, -2);
            c.set(DAY_OF_WEEK, n);
            res = c.getDisplayName(DAY_OF_WEEK, LONG, Locale.getDefault());
        } else if (position == DAY_AFTER_TOMORROW) {
            int n = adjustWeekDay(d, 2);
            c.set(DAY_OF_WEEK, n);
            res =  c.getDisplayName(DAY_OF_WEEK, LONG, Locale.getDefault());
        }

        return res.toUpperCase();
    }

    int adjustWeekDay(int weekDay, int adjustTo) {
        int res = weekDay + adjustTo;
        if (res < 1) {
            if (res == 0) res = 7;
            else if (res == -1) res = 6;
        } else if (res > 7) {
            res = res - 7;
        }

        return res;
    }
}

