package no.skytte.smileyface.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import no.skytte.smileyface.R;
import no.skytte.smileyface.sync.SyncDataIntentService;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SyncDataIntentService.startFullSync(this);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.inspection_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
}
