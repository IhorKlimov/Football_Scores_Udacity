/*
 * Copyright (C) 2015 The Android Open Source Project
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

package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.PageAdapter;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.data.DatabaseContract.Match;
import barqsoft.footballscores.sync.SyncAdapter;

import static barqsoft.footballscores.ScoresAdapter.COL_AWAY;
import static barqsoft.footballscores.ScoresAdapter.COL_AWAY_GOALS;
import static barqsoft.footballscores.ScoresAdapter.COL_HOME;
import static barqsoft.footballscores.ScoresAdapter.COL_HOME_GOALS;
import static barqsoft.footballscores.sync.SyncAdapter.MATCH_PROJECTION;

/**
 * Created by Igor Klimov on 12/30/2015.
 */
public class Factory implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = "Factory";
    private static final String TAG = "Factory";
    private Context mContext;
    private int mWidgetId;
    private Cursor mCursor;

    public Factory(Context context, Intent intent) {
        Log.d(LOG_TAG, "Factory: ");
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        Calendar c = Calendar.getInstance();
        String today =
                PageAdapter.FULL_FORMAT.format(new Date(c.getTimeInMillis()));
        mCursor = mContext.getContentResolver().query(
                Match.CONTENT_URI, MATCH_PROJECTION, null, new String[]{today}, null);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        int count = mCursor.getCount();
        Log.d(LOG_TAG, "getCount() returned: " + count);
        return count;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews v = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        String homeTeamName = "";
        String awayTeamName = "";
        String score = "";
        String time = "";

        if (mCursor != null && mCursor.moveToPosition(position)) {
            Log.d(LOG_TAG, "getViewAt: ---------------------------------------------------");
            homeTeamName = mCursor.getString(COL_HOME);
            awayTeamName = mCursor.getString(COL_AWAY);
            score = Utility.getScores(
                    mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS));
            time = mCursor.getString(2);


//            String homeCrest = Utility.getTeamCrestByTeamName(
//                    context, homeTeamName);
//            loadTeamCrest(homeCrest, holder.homeCrest, homeTeamUrl, homeTeamName);

//            String awayCrest = Utility.getTeamCrestByTeamName(
//                    context, awayTeamName);
//            loadTeamCrest(awayCrest, holder.awayCrest, awayTeamUrl, awayTeamName);

        }

        v.setTextViewText(R.id.home_name, homeTeamName);
        v.setTextViewText(R.id.away_name, awayTeamName);
        v.setTextViewText(R.id.score_textview, score);
        v.setTextViewText(R.id.data_textview, time);


        v.setOnClickFillInIntent(R.id.widget_list_item, new Intent());

        return v;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        Log.v(TAG, "getItemId");
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
