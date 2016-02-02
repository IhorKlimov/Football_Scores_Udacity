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

package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Igor Klimov on 2/1/2016.
 */
public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Callback mCallback;
    private Context mContext;
    TextView homeName;
    TextView awayName;
    TextView score;
    TextView date;
    ImageView homeCrest;
    ImageView awayCrest;
    double match_id;

    LinearLayout detailContainer;
    TextView matchDay;
    TextView league;
    Button shareButton;

    String matchDayData;
    String leagueData;


    public Holder(Context context, View view, Callback callback) {
        super(view);
        homeName = (TextView) view.findViewById(R.id.home_name);
        awayName = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
        homeCrest = (ImageView) view.findViewById(R.id.home_crest);
        awayCrest = (ImageView) view.findViewById(R.id.away_crest);

        detailContainer = (LinearLayout) view.findViewById(R.id.detail_container);
        matchDay = (TextView) view.findViewById(R.id.matchday_textview);
        league = (TextView) view.findViewById(R.id.league_textview);
        shareButton = (Button) view.findViewById(R.id.share_button);

        this.mCallback = callback;
        this.mContext = context;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == shareButton.getId()) {
            mContext.startActivity(createShareForecastIntent(homeName.getText() + " "
                    + score.getText() + " " + awayName.getText() + " "));
        } else {
            mCallback.OnItemClick(this);
        }
    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String FOOTBALL_SCORES_HASHTAG = "#Football Scores app";
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
