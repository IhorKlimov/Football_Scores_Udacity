package barqsoft.footballscores;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    final ContentResolver contentResolver;

    public ScoresAdapter(Context context, Cursor cursor) {
        this.context = context;
        mCursor = cursor;
        contentResolver = context.getContentResolver();
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


        int homeCrest = Utility.getTeamCrestByTeamName(homeTeamName);
        holder.homeCrest.setImageResource(homeCrest);

        int awayCrest = Utility.getTeamCrestByTeamName(awayTeamName);
        holder.awayCrest.setImageResource(awayCrest);

        holder.leagueData = Utility.getLeague(context, mCursor.getInt(COL_LEAGUE));
        holder.matchDayData = Utility
                .getMatchDay(context, mCursor.getInt(COL_MATCHDAY), mCursor.getInt(COL_LEAGUE));

        if (holder.match_id == detailMatchId) {
            holder.detailContainer.setVisibility(VISIBLE);
            setupDetails(holder);
        } else {
            holder.detailContainer.setVisibility(GONE);
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

}
