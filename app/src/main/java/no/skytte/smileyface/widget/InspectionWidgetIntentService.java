package no.skytte.smileyface.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import no.skytte.smileyface.R;
import no.skytte.smileyface.Utilities;
import no.skytte.smileyface.storage.SmileyContract;
import no.skytte.smileyface.ui.MainActivity;

public class InspectionWidgetIntentService extends IntentService {

    public InspectionWidgetIntentService() {
        super("InspectionWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the latest inspection widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                InspectionWidgetProvider.class));

        // Get latest data from the ContentProvider
        Cursor data = getContentResolver().query(
                SmileyContract.LocationEntry.CONTENT_URI,
                new String[]{ SmileyContract.LocationEntry.COLUMN_NAME,
                        SmileyContract.InspectionEntry.COLUMN_DATE,
                        SmileyContract.InspectionEntry.COLUMN_GRADE},
                null,
                null,
                SmileyContract.InspectionEntry.COLUMN_DATE + " DESC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the data from the Cursor
        String name = data.getString(data.getColumnIndex(SmileyContract.LocationEntry.COLUMN_NAME));
        int grade = data.getInt(data.getColumnIndex(SmileyContract.InspectionEntry.COLUMN_GRADE));
        int gradeImgResourceId = Utilities.getSmileyResource(grade);
        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.inspection_appwidget);

            // Add the data to the RemoteViews
            views.setImageViewResource(R.id.smiley_icon, gradeImgResourceId);
            views.setTextViewText(R.id.header_text, name);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
