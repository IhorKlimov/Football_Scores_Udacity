package barqsoft.footballscores.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MATCH_PATH = "scores";
    public static final String CREST_PATH = "crest";


    public static final class Match implements BaseColumns {
        public static final String TABLE_NAME = "match";
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";
        public static final String HOME_TEAM_URL = "home_team_url";
        public static final String AWAY_TEAM_URL = "away_team_url";

        //public static Uri SCORES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MATCH_PATH)
        //.build();
        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + Match._ID + " INTEGER PRIMARY KEY,"
                        + Match.DATE_COL + " TEXT NOT NULL,"
                        + Match.TIME_COL + " INTEGER NOT NULL,"
                        + Match.HOME_COL + " TEXT NOT NULL,"
                        + Match.AWAY_COL + " TEXT NOT NULL,"
                        + Match.LEAGUE_COL + " INTEGER NOT NULL,"
                        + Match.HOME_GOALS_COL + " TEXT NOT NULL,"
                        + Match.AWAY_GOALS_COL + " TEXT NOT NULL,"
                        + Match.MATCH_ID + " INTEGER NOT NULL,"
                        + Match.MATCH_DAY + " INTEGER NOT NULL,"
                        + Match.HOME_TEAM_URL + " TEXT NOT NULL,"
                        + Match.AWAY_TEAM_URL + " TEXT NOT NULL,"
                        + " UNIQUE (" + Match.MATCH_ID + ") ON CONFLICT REPLACE"
                        + " );";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MATCH_PATH).build();

        public static Uri buildScoreWithLeague() {
            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
        }

        public static Uri buildScoreWithId() {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }

        public static Uri buildScoreWithDate() {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }

    public static final class Crest implements BaseColumns {
        public static final String TABLE_NAME = "crest";
        public static final String COL_TEAM_NAME = "team_name";
        public static final String COL_CREST_URL = "crest_url";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_TEAM_NAME + " TEXT NOT NULL, "
                + COL_CREST_URL + " TEXT NOT NULL,"
                + " UNIQUE ("+COL_TEAM_NAME +") ON CONFLICT REPLACE);";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CREST_PATH).build();

    }

}
