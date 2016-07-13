package no.skytte.smileyface.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.smileyface.R;
import no.skytte.smileyface.SmileyFaceApplication;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;

public class LocationsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,  SearchView.OnQueryTextListener{
    private static final String TAG = "LocationsListFragment";

    private static final int LOCATIONS_LOADER = 0;

    private InteractionListener mListener;
    private LocationsRecyclerViewAdapter mAdapter;

    @Bind(R.id.recyclerview_locations) RecyclerView mRecyclerView;
    @Bind(R.id.recyclerview_locations_empty) TextView mEmptyView;
    @Bind(R.id.recyclerview_loading) View mLoadingView;
    SearchView mSearchView;

    private String mSearchQuery;

    private static final String[] LOCATION_COLUMNS = {
            LocationEntry.TABLE_NAME + "." + LocationEntry._ID,
            LocationEntry.COLUMN_TO_ID,
            LocationEntry.COLUMN_NAME,
            LocationEntry.COLUMN_ADDRESS,
            LocationEntry.COLUMN_POSTCODE,
            LocationEntry.COLUMN_CITY,
            InspectionEntry.COLUMN_INSP_ID,
            InspectionEntry.COLUMN_DATE,
            InspectionEntry.COLUMN_GRADE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locations_list, container, false);
        ButterKnife.bind(this, view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new LocationsRecyclerViewAdapter(getContext(), mListener);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchQuery = newText;
        getLoaderManager().restartLoader(LOCATIONS_LOADER, null, this);
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mSearchView = (SearchView) getActivity().findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);
        getLoaderManager().initLoader(LOCATIONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SmileyFaceApplication application = (SmileyFaceApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection = null;
        String[] selectionArgs = null;
        if(!TextUtils.isEmpty(mSearchQuery)){
            String[] queries = mSearchQuery.split(" ");
            selection = createSelection(queries);
            selectionArgs = createSelectionArgs(queries);
        }
        return new CursorLoader(getActivity(),
                LocationEntry.CONTENT_URI,
                LOCATION_COLUMNS,
                selection,
                selectionArgs,
                InspectionEntry.COLUMN_DATE + " DESC");
    }

    private String createSelection(String[] queries) {
        String s = LocationEntry.WHERE_SEARCH_QUERY;
        for(int i = 1; i < queries.length; i++){
            s += " AND " + LocationEntry.WHERE_SEARCH_QUERY;
        }
        return s;
    }

    @NonNull
    private String[] createSelectionArgs(String[] queries) {
        List<String> sa = new ArrayList<>();
        for(String s : queries){
            sa.add("%" + s + "%");
            sa.add("%" + s + "%");
            sa.add("%" + s + "%");
            sa.add("%" + s + "%");
        }

        return sa.toArray(new String[queries.length * 4]);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void updateEmptyView() {
        if(mAdapter.getItemCount() == 0){
            if(TextUtils.isEmpty(mSearchQuery)){
                mEmptyView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.VISIBLE);
            }
            else{
                mEmptyView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
            }
        }
        else {
            mEmptyView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
        }

//            TextView tv = (TextView) getView().findViewById(R.id.recyclerview_forecast_empty);
//            if ( null != tv ) {
//                // if cursor is empty, why? do we have an invalid location
//                int message = R.string.empty_forecast_list;
//                @SunshineSyncAdapter.LocationStatus int location = Utility.getLocationStatus(getActivity());
//                switch (location) {
//                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN:
//                        message = R.string.empty_forecast_list_server_down;
//                        break;
//                    case SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID:
//                        message = R.string.empty_forecast_list_server_error;
//                        break;
//                    case SunshineSyncAdapter.LOCATION_STATUS_INVALID:
//                        message = R.string.empty_forecast_list_invalid_location;
//                        break;
//                    default:
//                        if (!Utility.isNetworkAvailable(getActivity())) {
//                            message = R.string.empty_forecast_list_no_network;
//                        }
//                }
//                tv.setText(message);
//            }
//        }
    }

    public interface InteractionListener {
        void onListClick(String toName, String toId);
    }
}
