package barqsoft.footballscores.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import barqsoft.footballscores.data.DatabaseContract.Crest;
import barqsoft.footballscores.data.DatabaseContract.Match;

import static barqsoft.footballscores.data.DatabaseContract.CONTENT_AUTHORITY;
import static barqsoft.footballscores.data.DatabaseContract.CREST_PATH;
import static barqsoft.footballscores.data.DatabaseContract.MATCH_PATH;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {
    private ScoresDBHelper mOpenHelper;
    private final UriMatcher mUriMatcher = buildUriMatcher();
    private ContentResolver mContextResolver;

    private static final int MATCH = 100;
    private static final int MATCH_WITH_LEAGUE = 101;
    private static final int MATCH_WITH_ID = 102;
//    private static final int MATCH_WITH_DATE = 103;
    private static final int CREST = 200;

    private static final String SCORES_BY_LEAGUE = Match.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE =
            Match.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID =
            Match.MATCH_ID + " = ?";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(CONTENT_AUTHORITY, MATCH_PATH, MATCH);
        matcher.addURI(CONTENT_AUTHORITY, "league", MATCH_WITH_LEAGUE);
        matcher.addURI(CONTENT_AUTHORITY, "id", MATCH_WITH_ID);
//        matcher.addURI(CONTENT_AUTHORITY, "date", MATCH_WITH_DATE);
        matcher.addURI(CONTENT_AUTHORITY, CREST_PATH, CREST);
        return matcher;
    }

//    private int match_uri(Uri uri) {
//        String link = uri.toString();
//
//        if (link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString())) {
//            return MATCH;
//        } else if (link.contentEquals(DatabaseContract.Match.buildScoreWithDate().toString())) {
//            return MATCH_WITH_DATE;
//        } else if (link.contentEquals(DatabaseContract.Match.buildScoreWithId().toString())) {
//            return MATCH_WITH_ID;
//        } else if (link.contentEquals(DatabaseContract.Match.buildScoreWithLeague().toString())) {
//            return MATCH_WITH_LEAGUE;
//        }
//
//        return -1;
//    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ScoresDBHelper(getContext());
        if (getContext() != null) {
            mContextResolver = getContext().getContentResolver();
        }
        return false;
    }

    @Override
    public int update(
            @NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        switch (mUriMatcher.match(uri)) {
//            case MATCH:
//                retCursor = db.query(
//                        Match.TABLE_NAME,
//                        projection, null, null, null, null, sortOrder);
//                break;
            case MATCH:
                retCursor = db.query(
                        Match.TABLE_NAME,
                        projection, SCORES_BY_DATE, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_WITH_ID:
                retCursor = db.query(
                        Match.TABLE_NAME,
                        projection, SCORES_BY_ID, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_WITH_LEAGUE:
                retCursor = db.query(
                        Match.TABLE_NAME,
                        projection, SCORES_BY_LEAGUE, selectionArgs, null, null, sortOrder);
                break;
            case CREST:
                retCursor = db.query(
                        Crest.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }

        retCursor.setNotificationUri(mContextResolver, uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long insert = -1;

        switch (mUriMatcher.match(uri)) {
            case CREST:
                insert = db.insert(Crest.TABLE_NAME, null, values);
                break;
        }
        if (insert != -1) {
            mContextResolver.notifyChange(uri,null,false);
        }

        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int returnCount = 0;

        switch (mUriMatcher.match(uri)) {
            case MATCH:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(Match.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                mContextResolver.notifyChange(uri, null, false);
                return returnCount;
            case CREST:
                db.beginTransaction();
                try{
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(Crest.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                mContextResolver.notifyChange(uri, null, false);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
