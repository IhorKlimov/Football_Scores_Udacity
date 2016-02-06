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

package barqsoft.footballscores.helpers;

import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Igor Klimov on 2/3/2016.
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    private static final String LOG_TAG = "AppBarStateChange";
    @Retention(SOURCE)
    @IntDef({EXPANDED, COLLAPSED, IDLE})
    public @interface State {

    }

    public static final int EXPANDED = 0;
    public static final int COLLAPSED = 1;
    public static final int IDLE = 2;

    @State
    private int mCurrentState = IDLE;


    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        Log.d(LOG_TAG, "onOffsetChanged: ");
        if (i == 0) {
            if (mCurrentState != EXPANDED) {
                onStateChanged(appBarLayout, EXPANDED);
            }
            mCurrentState = EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != COLLAPSED) {
                onStateChanged(appBarLayout, COLLAPSED);
            }
            mCurrentState = COLLAPSED;
        } else {
            if (mCurrentState != IDLE) {
                onStateChanged(appBarLayout, IDLE);
            }
            mCurrentState = IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, @State int state);
}
