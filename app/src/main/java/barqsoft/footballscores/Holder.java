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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Igor Klimov on 2/1/2016.
 */
public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ScoresAdapter mAdapter;
    public TextView home_name;
    public TextView away_name;
    public TextView score;
    public TextView date;
    public ImageView home_crest;
    public ImageView away_crest;
    public double match_id;
    public ViewGroup container;

    public Holder(View view, ScoresAdapter adapter) {
        super(view);
        home_name = (TextView) view.findViewById(R.id.home_name);
        away_name = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
        home_crest = (ImageView) view.findViewById(R.id.home_crest);
        away_crest = (ImageView) view.findViewById(R.id.away_crest);
        container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        this.mAdapter = adapter;

        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mAdapter.detailMatchId = match_id;
        MainActivity.sSelectedMatchId = (int) match_id;
        mAdapter.notifyDataSetChanged();
    }

}
