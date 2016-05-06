package no.skytte.smileyface.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.smileyface.R;
import no.skytte.smileyface.Utilities;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;
import no.skytte.smileyface.ui.LocationsListFragment.InteractionListener;

public class LocationsRecyclerViewAdapter extends RecyclerView.Adapter<LocationsRecyclerViewAdapter.ViewHolder> {

    private final InteractionListener mListener;
    private Cursor mCursor;
    private Context mContext;

    public LocationsRecyclerViewAdapter(Context context, InteractionListener listener) {
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex(LocationEntry.COLUMN_NAME));
        String address = mCursor.getString(mCursor.getColumnIndex(LocationEntry.COLUMN_ADDRESS));
        String city = mCursor.getString(mCursor.getColumnIndex(LocationEntry.COLUMN_CITY));
        String toId = mCursor.getString(mCursor.getColumnIndex(LocationEntry.COLUMN_TO_ID));
        String date = mCursor.getString(mCursor.getColumnIndex(InspectionEntry.COLUMN_DATE));
        int grade = mCursor.getInt(mCursor.getColumnIndex(InspectionEntry.COLUMN_GRADE));

        holder.mHeaderView.setText(name);
        holder.mSubView.setText(mContext.getString(R.string.list_address, address, city));
        holder.mToName = name;
        holder.mToId = toId;
        holder.mDateView.setText(Utilities.formatDateToShortDate(date));

        Utilities.setSmileyImage(holder.mIconView, grade);

    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        //mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String mToName;
        String mToId;
        @Bind(R.id.header_text) TextView mHeaderView;
        @Bind(R.id.sub_text) TextView mSubView;
        @Bind(R.id.date_label) TextView mDateView;
        @Bind(R.id.smiley_icon) ImageView mIconView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onListClick(mToName, mToId);
        }
    }
}
