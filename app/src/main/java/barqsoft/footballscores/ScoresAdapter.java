package barqsoft.footballscores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import barqsoft.footballscores.data.DatabaseContract;
import barqsoft.footballscores.svg.SvgDecoder;
import barqsoft.footballscores.svg.SvgDrawableTranscoder;
import barqsoft.footballscores.svg.SvgSoftwareLayerSetter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends RecyclerView.Adapter<Holder> {
    private static final String LOG_TAG = "ScoresAdapter";

    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_HOME_TEAM_URL = 10;
    public static final int COL_AWAY_TEAM_URL = 11;

     Context context;
    private Cursor mCursor;
    public double detailMatchId = 0;
    private ArrayList<Holder> mHolders = new ArrayList<>();
    private final GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> mRequestBuilder;

    public ScoresAdapter(Context context, Cursor cursor) {
        this.context = context;
        mCursor = cursor;

        mRequestBuilder = Glide.with(this.context)
                .using(Glide.buildStreamModelLoader(Uri.class, this.context), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .diskCacheStrategy(SOURCE)
                .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .listener(new SvgSoftwareLayerSetter<Uri>());
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context)
                .inflate(R.layout.scores_list_item, parent, false);

        Holder holder = new Holder(context, inflate, new Callback() {
            @Override
            public void OnItemClick(Holder holder) {
                for (Holder h : mHolders) {
                    h.detailContainer.setVisibility(GONE);
                    h.shareButton.setOnClickListener(null);
                }
                detailMatchId = holder.match_id;
                MainActivity.sSelectedMatchId = (int) holder.match_id;
                holder.detailContainer.setVisibility(VISIBLE);
                setupDetails(holder);
            }
        });

        mHolders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        if (!mCursor.moveToPosition(position)) return;

        String homeTeamName = mCursor.getString(COL_HOME);
        holder.homeName.setText(homeTeamName);
        String awayTeamName = mCursor.getString(COL_AWAY);
        holder.awayName.setText(awayTeamName);
        holder.date.setText(mCursor.getString(COL_MATCHTIME));
        holder.score.setText(
                Utility.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS)));
        holder.match_id = mCursor.getDouble(COL_ID);


        String homeTeamUrl = mCursor.getString(COL_HOME_TEAM_URL);
        String homeCrest = Utility.getTeamCrestByTeamName(
                context, homeTeamUrl, homeTeamName);
        loadTeamCrest(homeCrest, holder.homeCrest, homeTeamUrl, homeTeamName);

//        LoadCrest loadHomeCrest = new LoadCrest(holder.homeCrest);
//        loadHomeCrest.execute(mCursor.getString(COL_HOME_TEAM_URL), mCursor.getString(COL_HOME));

        String awayTeamUrl = mCursor.getString(COL_AWAY_TEAM_URL);
        String awayCrest = Utility.getTeamCrestByTeamName(
                context, awayTeamUrl, awayTeamName);
        loadTeamCrest(awayCrest, holder.awayCrest, awayTeamUrl, awayTeamName);
//        LoadCrest loadAwayCrest = new LoadCrest(holder.awayCrest);
//        loadAwayCrest.execute(mCursor.getString(COL_AWAY_TEAM_URL), mCursor.getString(COL_AWAY));


        holder.leagueData = Utility.getLeague(context, mCursor.getInt(COL_LEAGUE));
        holder.matchDayData = Utility
                .getMatchDay(context, mCursor.getInt(COL_MATCHDAY), mCursor.getInt(COL_LEAGUE));

//        String s = mCursor.getString(COL_HOME_CREST);
//        if (!s.equals("")) {
//            Picasso.with(context).load(s).into(holder.homeCrest);
//        }
//        String a = mCursor.getString(COL_AWAY_CREST);
//        if (!a.equals("")) {
//            Picasso.with(context).load(a).into(holder.awayCrest);
//        }

        if (holder.match_id == detailMatchId) {
            holder.detailContainer.setVisibility(VISIBLE);
            setupDetails(holder);
        } else {
            holder.detailContainer.setVisibility(GONE);
        }
    }

    private void loadTeamCrest(String crest, ImageView view, String teamUrl, String teamName) {
        if (!crest.equals("")) {
            setImage(crest, view);
        } else {
            Log.d(LOG_TAG, "loadTeamCrest: GETTING CREST URL FROM WEB");
            LoadCrest loadHomeCrest = new LoadCrest(view);
            loadHomeCrest.execute(teamUrl, teamName);
//            Picasso.with(context).load(R.drawable.no_icon).into(view);
        }
    }

    private void setImage(String crest, ImageView view) {
        if (crest.endsWith("svg")) {
            mRequestBuilder.load(Uri.parse(crest)).into(view);
        } else {
            Glide.with(context).load(crest).into(view);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    private void setupDetails(final Holder holder) {
        holder.matchDay.setText(holder.matchDayData);
        holder.league.setText(holder.leagueData);
        holder.shareButton.setOnClickListener(holder);
    }

    public void swapCursor(Cursor cursor) {
        Log.d(LOG_TAG, "swapCursor: ");
        mCursor = cursor;
        notifyDataSetChanged();
    }

    private class LoadCrest extends AsyncTask<String, Void, String> {
        private final WeakReference<ImageView> reference;

        public LoadCrest(ImageView view) {
            reference = new WeakReference<>(view);
        }

        @Override
        protected String doInBackground(String... params) {
            String res = Utility.getCrestUrl(params[0]);
            if (res != null && !res.equals("")) {
                ContentValues cv = new ContentValues();
                cv.put(DatabaseContract.Crest.COL_TEAM_NAME, params[1]);
                cv.put(DatabaseContract.Crest.COL_CREST_URL, res);
                context.getContentResolver().insert(DatabaseContract.Crest.CONTENT_URI, cv);
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ImageView view = reference.get();
            setImage(s, view);
        }
    }

}
