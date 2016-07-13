package no.skytte.smileyface.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.danlew.android.joda.JodaTimeAndroid;

import no.skytte.smileyface.R;
import no.skytte.smileyface.SmileyFaceApplication;
import no.skytte.smileyface.sync.SyncDataIntentService;

public class MainActivity extends AppCompatActivity implements LocationsListFragment.InteractionListener {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JodaTimeAndroid.init(this);

        SyncDataIntentService.startFullSync(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public void onListClick(String toName, String toId) {
        logClickAnalytics(toName, toId);

        if(mTwoPane){
            DetailFragment fragment = DetailFragment.newInstance(toId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, "details_pane")
                    .commit();
        }
        else{
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(DetailFragment.ARG_TO_ID, toId);
            startActivity(i);
        }
    }

    private void logClickAnalytics(String toName, String toId) {
        SmileyFaceApplication application = (SmileyFaceApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Select location")
                .setLabel(toName + " (" + toId + ")")
                .build());
    }
}
