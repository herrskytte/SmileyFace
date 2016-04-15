package no.skytte.smileyface.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.smileyface.R;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;

/**
 * A fragment representing a single Inspection detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link InspectionDetailActivity}
 * on handsets.
 */
public class InspectionDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ARG_TO_ID = "tilsynsobjekt_id";

    private static final int LOCATIONS_LOADER = 0;

    private static final String[] LOCATION_COLUMNS = {
            LocationEntry.TABLE_NAME + "." + LocationEntry._ID,
            LocationEntry.COLUMN_TO_ID,
            LocationEntry.COLUMN_NAME,
            LocationEntry.COLUMN_ADDRESS,
            LocationEntry.COLUMN_POSTCODE,
            LocationEntry.COLUMN_CITY,
            InspectionEntry.COLUMN_DATE,
            InspectionEntry.COLUMN_GRADE
    };

    @Bind(R.id.detail_toolbar) Toolbar mToolbar;

    private String mCurrentToId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentToId = getArguments().getString(ARG_TO_ID);

//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_TO_ID));
//
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.content);
//            }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentToId = getArguments().getString(ARG_TO_ID);

        View rootView = inflater.inflate(R.layout.fragment_inspection_detail, container, false);
        ButterKnife.bind(this, rootView);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOCATIONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                LocationEntry.buildLocationUri(mCurrentToId),
                LOCATION_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(getActivity() == null){
            return;
        }

        if(data.moveToNext()){
            getActivity().setTitle(data.getString(data.getColumnIndex(LocationEntry.COLUMN_NAME)));
        }

        while (data.moveToNext()){
            Log.e("TEST date", data.getString(data.getColumnIndex(InspectionEntry.COLUMN_DATE)));
            Log.e("TEST grade", "grade: " + data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
