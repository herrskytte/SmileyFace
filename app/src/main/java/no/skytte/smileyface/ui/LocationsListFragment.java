package no.skytte.smileyface.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.smileyface.R;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;

public class LocationsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOCATIONS_LOADER = 0;

    private InteractionListener mListener;
    private LocationsRecyclerViewAdapter mAdapter;

    @Bind(R.id.recyclerview_locations) RecyclerView mRecyclerView;
    @Bind(R.id.recyclerview_locations_empty) TextView mEmptyView;

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
    public void onActivityCreated(Bundle savedInstanceState) {
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
        //String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        return new CursorLoader(getActivity(),
                LocationEntry.CONTENT_URI,
                LOCATION_COLUMNS,
                null,
                null,
                null);
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
        mEmptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface InteractionListener {
        void onListClick(String toId);
    }
}
