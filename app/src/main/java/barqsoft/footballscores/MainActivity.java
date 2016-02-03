package barqsoft.footballscores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.facebook.stetho.Stetho;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import barqsoft.footballscores.helpers.AppBarStateChangeListener;
import barqsoft.footballscores.sync.SyncAdapter;

import static android.view.View.VISIBLE;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    public static final String LOG_TAG = "MainActivity";
    public static final String REFRESH_FINISHED = "refresh finished";

    public static int sSelectedMatchId;
    public static int sCurrentFragment = 2;

    private ViewPager mPager;
    private AppBarLayout mAppBar;
    private AppBarStateChangeListener mAppBarListener;
    private SwipeRefreshLayout mRefresher;
    private LocalBroadcastManager mLocalBroadcastManager;
    private BroadcastReceiver mRefreshFinishedReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadBackdropImage();

        setupPager();

        TabLayout tab = (TabLayout) findViewById(R.id.tab);
        tab.setupWithViewPager(mPager);

        setupRefresher();

        setupRefreshFinishedReceiver();

        SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppBar.removeOnOffsetChangedListener(mAppBarListener);
        mRefresher.setOnRefreshListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mRefreshFinishedReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent about = new Intent(this, AboutActivity.class);
            startActivity(about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        sCurrentFragment = mPager.getCurrentItem();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        SyncAdapter.syncImmediately(this);
    }

    private void loadBackdropImage() {
        ImageView backdrop = (ImageView) findViewById(R.id.backdrop);
        final View scrim = findViewById(R.id.scrim);
        Picasso.with(this)
                .load("http://p1.pichost.me/i/63/1881032.jpg")
                .into(backdrop, new Callback() {
                    @Override
                    public void onSuccess() {
                        scrim.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void setupPager() {
        mPager = (ViewPager) findViewById(R.id.pager);
        PageAdapter adapter = new PageAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(sCurrentFragment);
    }

    private void setupRefresher() {
        mRefresher = (SwipeRefreshLayout) findViewById(R.id.refresher);
        mRefresher.setOnRefreshListener(this);



        mAppBarListener = new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, @State int state) {
                if (state == EXPANDED) {
                    mRefresher.setEnabled(true);
                } else {
                    mRefresher.setEnabled(false);
                }
            }
        };

        mAppBar = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBar.addOnOffsetChangedListener(mAppBarListener);
    }

    private void setupRefreshFinishedReceiver() {
        mRefreshFinishedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(REFRESH_FINISHED)) {
                    Log.d(LOG_TAG, "onReceive: Refresh finished");
                    mRefresher.setRefreshing(false);
                }
            }
        };

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        mLocalBroadcastManager
                .registerReceiver(
                        mRefreshFinishedReceiver, new IntentFilter(REFRESH_FINISHED));
    }

}
