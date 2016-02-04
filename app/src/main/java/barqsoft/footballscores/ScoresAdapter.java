package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends RecyclerView.Adapter<Holder> {
    private static final String LOG_TAG = "ScoresAdapter";

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_HOME_CREST = 10;
    public static final int COL_AWAY_CREST = 11;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    private Context mContext;
    private Cursor mCursor;
    public double detailMatchId = 0;
    private ArrayList<Holder> mHolders = new ArrayList<>();

    public ScoresAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        mCursor = cursor;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext)
                .inflate(R.layout.scores_list_item, parent, false);

        Holder holder = new Holder(mContext, inflate, new Callback() {
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

        holder.homeName.setText(mCursor.getString(COL_HOME));
        holder.awayName.setText(mCursor.getString(COL_AWAY));
        holder.date.setText(mCursor.getString(COL_MATCHTIME));
        holder.score.setText(
                Utility.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS)));
        holder.match_id = mCursor.getDouble(COL_ID);


        String homeCrest = Utility.getTeamCrestByTeamName(mContext,mCursor.getString(COL_HOME));
        if (!homeCrest.equals("")) {
            Picasso.with(mContext).load(homeCrest).into(holder.homeCrest);
        } else {
            Picasso.with(mContext).load(R.drawable.no_icon).into(holder.homeCrest);
        }

        String awayCrest = Utility.getTeamCrestByTeamName(mContext,mCursor.getString(COL_AWAY));
        if (!awayCrest.equals("")) {
            Picasso.with(mContext).load(awayCrest).into(holder.awayCrest);
        } else {
            Picasso.with(mContext).load(R.drawable.no_icon).into(holder.awayCrest);
        }

        holder.leagueData = Utility.getLeague(mContext, mCursor.getInt(COL_LEAGUE));
        holder.matchDayData = Utility
                .getMatchDay(mContext, mCursor.getInt(COL_MATCHDAY), mCursor.getInt(COL_LEAGUE));

//        String s = mCursor.getString(COL_HOME_CREST);
//        if (!s.equals("")) {
//            Picasso.with(mContext).load(s).into(holder.homeCrest);
//        }
//        String a = mCursor.getString(COL_AWAY_CREST);
//        if (!a.equals("")) {
//            Picasso.with(mContext).load(a).into(holder.awayCrest);
//        }

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
