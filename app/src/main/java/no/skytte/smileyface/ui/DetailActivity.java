package no.skytte.smileyface.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import no.skytte.smileyface.R;

/**
 * An activity representing a single Inspection detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            DetailFragment fragment = DetailFragment.newInstance(getIntent().getStringExtra(DetailFragment.ARG_TO_ID));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.inspection_detail_container, fragment)
                    .commit();
        }
    }
}
