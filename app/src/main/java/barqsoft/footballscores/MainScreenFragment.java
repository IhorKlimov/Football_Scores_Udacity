package barqsoft.footballscores;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import barqsoft.footballscores.data.DatabaseContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = "MainScreenFragment";
    public static final int SCORES_LOADER = 0;
    private ScoresAdapter mAdapter;
    private String[] fragmentDate = new String[1];
    private int last_selected_item = -1;
    private RecyclerView mScoreList;

    public MainScreenFragment() {
    }

    public void setFragmentDate(String date) {
        fragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
//        updateScores();
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        mScoreList = (RecyclerView) rootView.findViewById(R.id.scores_list);
        mScoreList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ScoresAdapter(getActivity(), null);
        mScoreList.setAdapter(mAdapter);
//        mScoreList.addOnScrollListener(new ScrollListener(fragmentDate[0], mScoreList));
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        mAdapter.detailMatchId = MainActivity.sSelectedMatchId;

        return rootView;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mScoreList.clearOnScrollListeners();
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), DatabaseContract.Match.CONTENT_URI,
                null, null, fragmentDate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


}
