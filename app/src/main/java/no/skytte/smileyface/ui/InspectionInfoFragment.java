package no.skytte.smileyface.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.smileyface.R;
import no.skytte.smileyface.SmileyFaceApplication;
import no.skytte.smileyface.Utilities;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;

public class InspectionInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "InspectionInfoFragment";

    public static final String ARG_TO_ID = "tilsynsobjekt_id";

    private static final int LOCATION_LOADER = 10;
    private static final int PREV_INSPECTIONS_LOADER = 11;

    private static final String[] LOCATION_COLUMNS = {
            LocationEntry.TABLE_NAME + "." + LocationEntry._ID,
            LocationEntry.COLUMN_TO_ID,
            LocationEntry.COLUMN_NAME,
            LocationEntry.COLUMN_ADDRESS,
            LocationEntry.COLUMN_POSTCODE,
            LocationEntry.COLUMN_CITY,
            InspectionEntry.COLUMN_DATE,
            InspectionEntry.COLUMN_GRADE,
            InspectionEntry.COLUMN_GRADE1,
            InspectionEntry.COLUMN_GRADE2,
            InspectionEntry.COLUMN_GRADE3,
            InspectionEntry.COLUMN_GRADE4
    };

    private static final String[] INSPECTION_COLUMNS = {
            InspectionEntry.COLUMN_INSP_ID,
            InspectionEntry.COLUMN_DATE,
            InspectionEntry.COLUMN_GRADE
    };

    @Bind(R.id.header_text) TextView mHeader;
    @Bind(R.id.sub_text) TextView mAddress;
    @Bind(R.id.smiley_icon) ImageView mSmiley;
    @Bind(R.id.details1_icon) ImageView mSmileyDetails1;
    @Bind(R.id.details2_icon) ImageView mSmileyDetails2;
    @Bind(R.id.details3_icon) ImageView mSmileyDetails3;
    @Bind(R.id.details4_icon) ImageView mSmileyDetails4;
    @Bind(R.id.date_label) TextView mDate;
    @Bind(R.id.previous_view) View mPrevView;
    @Bind(R.id.previous1_icon) ImageView mSmileyPrev1;
    @Bind(R.id.previous2_icon) ImageView mSmileyPrev2;
    @Bind(R.id.previous3_icon) ImageView mSmileyPrev3;
    @Bind(R.id.previous1_date) TextView mPrev1Date;
    @Bind(R.id.previous2_date) TextView mPrev2Date;
    @Bind(R.id.previous3_date) TextView mPrev3Date;

    private String mCurrentToId;

    public static InspectionInfoFragment newInstance(String toId) {
        InspectionInfoFragment fragment = new InspectionInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TO_ID, toId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentToId = getArguments().getString(ARG_TO_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inspection_info, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().restartLoader(LOCATION_LOADER, null, this);
        getLoaderManager().restartLoader(PREV_INSPECTIONS_LOADER, null, this);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(i == LOCATION_LOADER){
            return new CursorLoader(getActivity(),
                    LocationEntry.CONTENT_URI,
                    LOCATION_COLUMNS,
                    LocationEntry.COLUMN_TO_ID + "=?",
                    new String[]{mCurrentToId},
                    null);
        }
        else{
            return new CursorLoader(getActivity(),
                    InspectionEntry.CONTENT_URI,
                    INSPECTION_COLUMNS,
                    InspectionEntry.COLUMN_TO_ID + "=?",
                    new String[]{mCurrentToId},
                    InspectionEntry.COLUMN_INSP_ID + " DESC");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(getActivity() == null){
            return;
        }

        if(loader.getId() == LOCATION_LOADER){
            updateLocationViews(data);
        }
        else{
            updatePreviousInspections(data);
        }
    }

    private void updatePreviousInspections(Cursor data) {
        Log.i("InpsectionInfo", "Total inspections: " + data.getCount());
        if(data.getCount() < 2){
            mPrevView.setVisibility(View.GONE);
            return;
        }
        data.moveToFirst();
        int inspectionNumber = 0;
        while (data.moveToNext()){
            inspectionNumber++;
            long date = data.getLong(data.getColumnIndex(InspectionEntry.COLUMN_DATE));
            int grade = data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE));

            if(inspectionNumber == 1){
                mPrev1Date.setVisibility(View.VISIBLE);
                mSmileyPrev1.setVisibility(View.VISIBLE);
                mPrev1Date.setText(Utilities.formatDateToShortDate(date));
                Utilities.setSmileyImage(mSmileyPrev1, grade);
            }
            else if(inspectionNumber == 2){
                mPrev2Date.setVisibility(View.VISIBLE);
                mSmileyPrev2.setVisibility(View.VISIBLE);
                mPrev2Date.setText(Utilities.formatDateToShortDate(date));
                Utilities.setSmileyImage(mSmileyPrev2, grade);
            }
            else if(inspectionNumber == 3){
                mPrev3Date.setVisibility(View.VISIBLE);
                mSmileyPrev3.setVisibility(View.VISIBLE);
                mPrev3Date.setText(Utilities.formatDateToShortDate(date));
                Utilities.setSmileyImage(mSmileyPrev3, grade);
            }



        }
    }

    private void updateLocationViews(Cursor data) {
        if(data.moveToFirst()){
            String name = data.getString(data.getColumnIndex(LocationEntry.COLUMN_NAME));
            String address = data.getString(data.getColumnIndex(LocationEntry.COLUMN_ADDRESS));
            String city = data.getString(data.getColumnIndex(LocationEntry.COLUMN_CITY));
            String postcode = data.getString(data.getColumnIndex(LocationEntry.COLUMN_POSTCODE));
            long date = data.getLong(data.getColumnIndex(InspectionEntry.COLUMN_DATE));
            int grade = data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE));
            int grade1 = data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE1));
            int grade2 = data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE2));
            int grade3 = data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE3));
            int grade4 = data.getInt(data.getColumnIndex(InspectionEntry.COLUMN_GRADE4));

            mHeader.setText(name);
            mAddress.setText(getString(R.string.list_address_long, address, postcode, city));
            mDate.setText(Utilities.formatDate(date));

            Utilities.setSmileyImage(mSmiley, grade);
            Utilities.setSmileyImage(mSmileyDetails1, grade1);
            Utilities.setSmileyImage(mSmileyDetails2, grade2);
            Utilities.setSmileyImage(mSmileyDetails3, grade3);
            Utilities.setSmileyImage(mSmileyDetails4, grade4);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
