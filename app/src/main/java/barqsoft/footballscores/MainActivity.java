package barqsoft.footballscores;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.facebook.stetho.Stetho;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.view.Gravity.CENTER;
import static android.view.View.VISIBLE;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MainActivity";

    public static int sSelectedMatchId;
    public static int sCurrentFragment = 2;

    private ViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        mPager = (ViewPager) findViewById(R.id.pager);
        PageAdapter adapter = new PageAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(sCurrentFragment);

        TabLayout tab = (TabLayout) findViewById(R.id.tab);
        tab.setupWithViewPager(mPager);

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
        Log.v(LOG_TAG, "will save");
        Log.v(LOG_TAG, "fragment: " + valueOf(mPager.getCurrentItem()));
        Log.v(LOG_TAG, "selected id: " + sSelectedMatchId);

        sCurrentFragment = mPager.getCurrentItem();
        super.onSaveInstanceState(outState);
    }

}
