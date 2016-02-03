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

package barqsoft.footballscores.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.data.DatabaseContract;

import static barqsoft.footballscores.MainActivity.REFRESH_FINISHED;
import static barqsoft.footballscores.PageAdapter.FULL_FORMAT;
import static barqsoft.footballscores.PageAdapter.ONE_DAY_IN_MILLIS;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.AWAY_COL;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.AWAY_GOALS_COL;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.DATE_COL;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.HOME_COL;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.HOME_GOALS_COL;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.LEAGUE_COL;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.MATCH_ID;
import static barqsoft.footballscores.data.DatabaseContract.scores_table.TIME_COL;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "SyncAdapter";
    private static final String FROM_REFRESHER = "from refresher";
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    Context mContext;

    private static final int ONE_DAYS_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    private static final int SYNC_INTERVAL = 24 * 60 * 60;
    private static final int FLEX_TIME = SYNC_INTERVAL / 3;

    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int Bundesliga3 = 403;
    public static final int EREDIVISIE = 404;

    private static final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
    private static final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
    private static final String FIXTURES = "fixtures";
    private static final String LINKS = "_links";
    private static final String SOCCER_SEASON = "soccerseason";
    private static final String SELF = "self";
    private static final String MATCH_DATE = "date";
    private static final String HOME_TEAM_NAME = "homeTeamName";
    private static final String AWAY_TEAM_NAME = "awayTeamName";
    private static final String HOME_TEAM = "homeTeam";
    private static final String AWAY_TEAM = "awayTeam";
    private static final String RESULT = "result";
    private static final String HOME_GOALS = "goalsHomeTeam";
    private static final String AWAY_GOALS = "goalsAwayTeam";
    private static final String MATCH_DAY = "matchday";
    private static final String HREF = "href";
    private static final String CREST_URL = "crestUrl";

    private static final String BASE_URL = "http://api.football-data.org/alpha/fixtures";
    private static final String QUERY_TIME_FRAME = "timeFrame";
    private static final SimpleDateFormat MATCH_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat NEW_DATE =
            new SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.getDefault());

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "barqsoft.footballscores";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "example.com";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;


    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
//        long lastUpdate = prefs.getLong(
//                mContext.getString(R.string.pref_last_update), System.currentTimeMillis());
//        Log.d("TAG", "onPerformSync: IF " + (System.currentTimeMillis() - lastUpdate));
//        if (System.currentTimeMillis() - lastUpdate >= ONE_DAYS_IN_MILLISECONDS) {
//            int delete1 = mContentResolver.delete(MovieByPopularity.CONTENT_URI, null, null);
//            int delete2 = mContentResolver.delete(MovieByReleaseDate.CONTENT_URI, null, null);
//            int delete3 = mContentResolver.delete(MovieByVotes.CONTENT_URI, null, null);
//            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete1);
//            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete2);
//            Log.d("TAG", "onPerformSync: ERASE THE DATABASE -------" + delete3);
//            prefs.edit().putLong(mContext.getString(R.string.last_update), System.currentTimeMillis()).apply();
//            Utility.initializePagePreference(mContext);
//            if (MoviesGridFragment.sListener != null) MoviesGridFragment.sListener.refresh();
//            syncImmediately(mContext);
//        } else {
//            getData();
//        }

        boolean fromRefresher = extras.getBoolean(FROM_REFRESHER, false);

        getData("n2", false);
        getData("p2", fromRefresher);
    }

    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "syncImmediately: ");
//        ConnectivityManager systemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
//        if (activeNetworkInfo == null) {
//            new NoInternet().show(((MainActivity) context).getSupportFragmentManager(), "1");
//        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(FROM_REFRESHER, true);
        ContentResolver.requestSync(
                getSyncAccount(context), context.getString(R.string.authority), bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account getSyncAccount(Context context) {
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.account_type));
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) return null;

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d(LOG_TAG, "CREATED NEW ACCOUNT");
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, FLEX_TIME);

        ContentResolver.setSyncAutomatically(
                newAccount, context.getString(R.string.authority), true);

//        SharedPreferences preferences =
//                PreferenceManager.getDefaultSharedPreferences(context);
//        preferences.edit().putLong(
//                context.getString(R.string.pref_last_update), System.currentTimeMillis())
//                .apply();

//        SyncAdapter.syncImmediately(context);
    }

    private static void configurePeriodicSync(
            Context context, int syncInterval, int flexTime) {
        String authority = context.getString(R.string.authority);
        Account account = getSyncAccount(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private void getData(String timeFrame, boolean fromRefresher) {
        Log.d(LOG_TAG, "getData: ");
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();

        String jsonData = sendGetRequestAndGetResponse(uri.toString());

        try {
            if (jsonData != null) {
                //This bit is to check if the data contains any matches.
                // If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJsonData(
                            mContext.getString(R.string.dummy_data), mContext, false, fromRefresher);
                } else {
                    processJsonData(jsonData, mContext, true, fromRefresher);
                }
            } else {
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private String sendGetRequestAndGetResponse(String url) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String result = "";
        try {
            URL fetch = new URL(url);
            connection = (HttpURLConnection) fetch.openConnection();
            connection.addRequestProperty("X-Auth-Token", BuildConfig.FOOTBALL_DATA_API_KEY);
            connection.connect();

            Log.d(LOG_TAG, "getData: " + fetch.toString());

            InputStream inputStream = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) return "";

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            if (buffer.length() == 0) return "";
            result = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception here" + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream");
                }
            }
        }
        return result;
    }

    private void processJsonData(String JSONdata, Context mContext, boolean isReal, boolean fromRefresher) {
        int league;
        String date;
        String time;
        String home;
        String away;
        String homeGoals;
        String awayGoals;
        String matchId;
        String matchDay;
//        String homeCrest;
//        String awayCrest;

        try {
            Log.d(LOG_TAG, "processJsonData: " + JSONdata);
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

            //ContentValues to be inserted
            int length = matches.length();
            Vector<ContentValues> values = new Vector<>(length);
            for (int i = 0; i < length; i++) {
                JSONObject matchData = matches.getJSONObject(i);

                JSONObject links = matchData.getJSONObject(LINKS);
                league = Integer.valueOf(
                        links.getJSONObject(SOCCER_SEASON)
                                .getString(HREF).replace(SEASON_LINK, ""));

                if (!isInterestedLeague(league)) continue;

                matchId = links.getJSONObject(SELF)
                        .getString(HREF);
                matchId = matchId.replace(MATCH_LINK, "");

//                homeCrest = getCrestUrl(
//                        links.getJSONObject(HOME_TEAM)
//                                .getString(HREF));

//                awayCrest = getCrestUrl(
//                        links.getJSONObject(AWAY_TEAM)
//                                .getString(HREF));

                if (!isReal) {
                    //This if statement changes the match ID of the dummy data
                    // so that it all goes into the database
                    matchId = matchId + Integer.toString(i);
                }

                date = matchData.getString(MATCH_DATE);
                time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
                date = date.substring(0, date.indexOf("T"));
                MATCH_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date parsedDate = MATCH_DATE_FORMAT.parse(date + time);
                    NEW_DATE.setTimeZone(TimeZone.getDefault());
                    date = NEW_DATE.format(parsedDate);
                    time = date.substring(date.indexOf(":") + 1);
                    date = date.substring(0, date.indexOf(":"));

                    if (!isReal) {
                        //This if statement changes the dummy data's date to match our current date range.
                        Date fragmentDate = new Date(System.currentTimeMillis() +
                                ((i - 2) * ONE_DAY_IN_MILLIS));
                        date = FULL_FORMAT.format(fragmentDate);
                    }
                } catch (Exception e) {
                    Log.d(LOG_TAG, "error here!");
                    Log.e(LOG_TAG, e.getMessage());
                }
                home = matchData.getString(HOME_TEAM_NAME);
                away = matchData.getString(AWAY_TEAM_NAME);
                homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                matchDay = matchData.getString(MATCH_DAY);

                ContentValues matchValues = createContentValues(league, date, time, home, away,
                        homeGoals, awayGoals, matchId, matchDay);

                values.add(matchValues);

            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insert_data);

            if (fromRefresher) {
                notifyMainActivityRefreshFinished();
            }

            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private String getCrestUrl(String teamUrl) {
        String crest = "";

        String response = sendGetRequestAndGetResponse(teamUrl);
        try {
            crest = new JSONObject(response).getString(CREST_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return crest;
    }

    @NonNull
    private ContentValues createContentValues(int league, String date, String time,
                                              String home, String away, String homeGoals,
                                              String awayGoals, String matchId,
                                              String matchDay) {
        ContentValues matchValues = new ContentValues();
        matchValues.put(MATCH_ID, matchId);
        matchValues.put(DATE_COL, date);
        matchValues.put(TIME_COL, time);
        matchValues.put(HOME_COL, home);
        matchValues.put(AWAY_COL, away);
        matchValues.put(HOME_GOALS_COL, homeGoals);
        matchValues.put(AWAY_GOALS_COL, awayGoals);
        matchValues.put(LEAGUE_COL, league);
//        matchValues.put(HOME_CREST, homeCrest);
//        matchValues.put(AWAY_CREST, awayCrest);
        matchValues.put(DatabaseContract.scores_table.MATCH_DAY, matchDay);

        return matchValues;
    }

    private boolean isInterestedLeague(int league) {
        //This if statement controls which leagues we're interested in the data from.
        //add leagues here in order to have them be added to the DB.
        // If you are finding no data in the app, check that this contains all the leagues.
        // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
        return league == PREMIER_LEAGUE ||
                league == SERIE_A ||
                league == BUNDESLIGA1 ||
                league == BUNDESLIGA2 ||
                league == PRIMERA_DIVISION;
    }

    private void notifyMainActivityRefreshFinished() {
        LocalBroadcastManager
                .getInstance(mContext)
                .sendBroadcast(
                        new Intent(REFRESH_FINISHED));
    }

}