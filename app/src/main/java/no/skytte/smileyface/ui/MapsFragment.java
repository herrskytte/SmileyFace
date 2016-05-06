package no.skytte.smileyface.ui;


import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.skytte.smileyface.R;
import no.skytte.smileyface.SmileyFaceApplication;
import no.skytte.smileyface.storage.SmileyContract;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "MapsFragment";

    private static final String ARG_TO_ID = "tilsynsobjekt_id";

    private static final int LOCATIONS_LOADER = 0;

    private static final String[] LOCATION_COLUMNS = {
            SmileyContract.LocationEntry.COLUMN_NAME,
            SmileyContract.LocationEntry.COLUMN_ADDRESS,
            SmileyContract.LocationEntry.COLUMN_CITY,
            SmileyContract.LocationEntry.COLUMN_POSTCODE
    };

    @Bind(R.id.map) View mMapView;
    @Bind(R.id.map_empty) TextView mEmptyText;
    @Bind(R.id.map_fab) FloatingActionButton mFab;

    private String mCurrentToId;
    private GoogleMap mMap;
    private String mCurrentLocation;
    private String mCurrentLocationAddress;
    private Address mCurrentAddress;

    public static MapsFragment newInstance(String toId) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TO_ID, toId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentToId = getArguments().getString(ARG_TO_ID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, rootView);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOCATIONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        updateMapWithAddress();
    }

    @OnClick(R.id.map_fab)
    public void clickNavigate(){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mCurrentLocationAddress);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                SmileyContract.LocationEntry.CONTENT_URI,
                LOCATION_COLUMNS,
                SmileyContract.LocationEntry.COLUMN_TO_ID + "=?",
                new String[]{mCurrentToId},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(getActivity() == null){
            return;
        }

        if(data.moveToNext()){
            mCurrentLocation = data.getString(data.getColumnIndex(SmileyContract.LocationEntry.COLUMN_NAME));
            String address = data.getString(data.getColumnIndex(SmileyContract.LocationEntry.COLUMN_ADDRESS));
            String city = data.getString(data.getColumnIndex(SmileyContract.LocationEntry.COLUMN_CITY));
            String postcode = data.getString(data.getColumnIndex(SmileyContract.LocationEntry.COLUMN_POSTCODE));
            mCurrentLocationAddress = address + ", " + postcode + " " + city + ", Norway";

            Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses =
                        geoCoder.getFromLocationName(mCurrentLocationAddress, 1);

                if (addresses.size() > 0) {
                    mCurrentAddress = addresses.get(0);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            updateMapWithAddress();
        }
    }

    private void updateMapWithAddress() {
        if(mCurrentAddress != null && mMap != null){
            mMapView.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);

            // Add a marker and move the camera
            LatLng marker = new LatLng(mCurrentAddress.getLatitude(), mCurrentAddress.getLongitude());
            mMap.addMarker(new MarkerOptions().position(marker).title(mCurrentLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 17));
        }
        else{
            mMapView.setVisibility(View.GONE);
            mFab.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
