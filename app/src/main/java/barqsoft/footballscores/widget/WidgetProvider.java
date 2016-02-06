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

package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresAdapter;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.sync.SyncAdapter;

import static barqsoft.footballscores.sync.SyncAdapter.ACTION_DATA_UPDATED;

/**
 * Created by Igor Klimov on 2/5/2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            rv.setRemoteAdapter(R.id.widget_list, intent);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            rv.setOnClickPendingIntent(R.id.widget, pendingIntent);

//            Intent clickIntent = new Intent(context,MainActivity.class);
//            PendingIntent pi = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntent)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setPendingIntentTemplate(R.id.widget_list, pi);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_DATA_UPDATED)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.notifyAppWidgetViewDataChanged(
                    manager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class)),
                    R.id.widget_list);
        }
    }
}
