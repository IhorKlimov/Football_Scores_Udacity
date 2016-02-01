package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

import static barqsoft.footballscores.PageAdapter.FULL_FORMAT;
import static barqsoft.footballscores.PageAdapter.ONE_DAY_IN_MILLIS;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FetchService extends IntentService {
    public static final String LOG_TAG = "FetchService";

    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    private static final String BUNDESLIGA1 = "394";
    private static final String BUNDESLIGA2 = "395";
    private static final String LIGUE1 = "396";
    private static final String LIGUE2 = "397";
    private static final String PREMIER_LEAGUE = "398";
    private static final String PRIMERA_DIVISION = "399";
    private static final String SEGUNDA_DIVISION = "400";
    private static final String SERIE_A = "401";
    private static final String PRIMERA_LIGA = "402";
    private static final String Bundesliga3 = "403";
    private static final String EREDIVISIE = "404";

    private static final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
    private static final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
    private static final String FIXTURES = "fixtures";
    private static final String LINKS = "_links";
    private static final String SOCCER_SEASON = "soccerseason";
    private static final String SELF = "self";
    private static final String MATCH_DATE = "date";
    private static final String HOME_TEAM = "homeTeamName";
    private static final String AWAY_TEAM = "awayTeamName";
    private static final String RESULT = "result";
    private static final String HOME_GOALS = "goalsHomeTeam";
    private static final String AWAY_GOALS = "goalsAwayTeam";
    private static final String MATCH_DAY = "matchday";

    private static final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
    private static final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
    private static final SimpleDateFormat MATCH_DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-ddHH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat NEW_DATE =
            new SimpleDateFormat("yyyy-MM-dd:HH:mm", Locale.getDefault());


    public FetchService() {
        super("FetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getData("n2");
        getData("p2");
    }

    private void getData(String timeFrame) {
        //final String QUERY_MATCH_DAY = "matchday";

        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+uri.toString()); //log spam
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String jsonData = null;
        //Opening Connection
        try {
            URL fetch = new URL(uri.toString());
            connection = (HttpURLConnection) fetch.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-Auth-Token", BuildConfig.FOOTBALL_DATA_API_KEY);
            connection.connect();

            // Read the input stream into a String
            InputStream inputStream = connection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) return;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
            if (buffer.length() == 0) return;
            jsonData = buffer.toString();
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
        try {
            if (jsonData != null) {
                //This bit is to check if the data contains any matches.
                // If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJsonData(getString(R.string.dummy_data),
                            getApplicationContext(), false);
                } else {
                    processJsonData(jsonData, getApplicationContext(), true);
                }
            } else {
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void processJsonData(String JSONdata, Context mContext, boolean isReal) {
        //Match data
        String league;
        String date;
        String time;
        String home;
        String away;
        String homeGoals;
        String awayGoals;
        String matchId;
        String matchDay;

        try {
            Log.d(LOG_TAG, "processJsonData: " + JSONdata);
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);

            //ContentValues to be inserted
            int length = matches.length();
            Vector<ContentValues> values = new Vector<>(length);
            for (int i = 0; i < length; i++) {
                JSONObject matchData = matches.getJSONObject(i);
                league = matchData.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");
                league = league.replace(SEASON_LINK, "");

                if (!isInterestedLeague(league)) continue;

                matchId = matchData.getJSONObject(LINKS)
                        .getJSONObject(SELF)
                        .getString("href");
                matchId = matchId.replace(MATCH_LINK, "");
                if (!isReal) {
                    //This if statement changes the match ID of the dummy data
                    // so that it all goes into the database
                    matchId = matchId + Integer.toString(i);
                }

                date = matchData.getString(MATCH_DATE);
                time = date.substring(date.indexOf("T") + 1, date.indexOf("Z"));
                date = date.substring(0, date.indexOf("T"));
                FetchService.MATCH_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
                try {
                    Date parsedDate = FetchService.MATCH_DATE_FORMAT.parse(date + time);
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
                home = matchData.getString(HOME_TEAM);
                away = matchData.getString(AWAY_TEAM);
                homeGoals = matchData.getJSONObject(RESULT).getString(HOME_GOALS);
                awayGoals = matchData.getJSONObject(RESULT).getString(AWAY_GOALS);
                matchDay = matchData.getString(MATCH_DAY);

                //Log.v(LOG_TAG,matchId);
                //Log.v(LOG_TAG,date);
                //Log.v(LOG_TAG,time);
                //Log.v(LOG_TAG,home);
                //Log.v(LOG_TAG,away);
                //Log.v(LOG_TAG,homeGoals);
                //Log.v(LOG_TAG,awayGoals);

                ContentValues matchValues = createContentValues(league, date, time, home, away,
                        homeGoals, awayGoals, matchId, matchDay);

                values.add(matchValues);

            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insert_data);

            //Log.v(LOG_TAG,"Succesfully Inserted : " + String.valueOf(inserted_data));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @NonNull
    private ContentValues createContentValues(String league, String date, String time,
                                              String home, String away, String homeGoals,
                                              String awayGoals, String matchId,
                                              String matchDay) {
        ContentValues matchValues = new ContentValues();
        matchValues.put(DatabaseContract.scores_table.MATCH_ID, matchId);
        matchValues.put(DatabaseContract.scores_table.DATE_COL, date);
        matchValues.put(DatabaseContract.scores_table.TIME_COL, time);
        matchValues.put(DatabaseContract.scores_table.HOME_COL, home);
        matchValues.put(DatabaseContract.scores_table.AWAY_COL, away);
        matchValues.put(DatabaseContract.scores_table.HOME_GOALS_COL, homeGoals);
        matchValues.put(DatabaseContract.scores_table.AWAY_GOALS_COL, awayGoals);
        matchValues.put(DatabaseContract.scores_table.LEAGUE_COL, league);
        matchValues.put(DatabaseContract.scores_table.MATCH_DAY, matchDay);

        return matchValues;
    }

    private boolean isInterestedLeague(String league) {
        //This if statement controls which leagues we're interested in the data from.
        //add leagues here in order to have them be added to the DB.
        // If you are finding no data in the app, check that this contains all the leagues.
        // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
        return league.equals(PREMIER_LEAGUE) ||
                league.equals(SERIE_A) ||
                league.equals(BUNDESLIGA1) ||
                league.equals(BUNDESLIGA2) ||
                league.equals(PRIMERA_DIVISION);
    }
}

