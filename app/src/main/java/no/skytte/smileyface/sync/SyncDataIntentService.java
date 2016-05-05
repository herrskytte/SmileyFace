package no.skytte.smileyface.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.skytte.smileyface.model.InspectionDto;
import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;
import retrofit2.Call;
import retrofit2.Response;

public class SyncDataIntentService extends IntentService {

    private static final String TAG = "SyncDataIntentService";

    private static final String ACTION_FULL_SYNC = "no.skytte.smileyface.storage.action.FULL_SYNC";

    public SyncDataIntentService() {
        super("SyncDataIntentService");
    }

    public static void startFullSync(Context context) {
        Intent intent = new Intent(context, SyncDataIntentService.class);
        intent.setAction(ACTION_FULL_SYNC);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FULL_SYNC.equals(action)) {
                handleFullSync();
            }
        }
    }

    private void handleFullSync() {
        Call<List<InspectionDto>> call = new SyncClient().getDataService().getAllInspections();
        try {
            long timeStart = System.currentTimeMillis();
            Response<List<InspectionDto>> response = call.execute();
            List<InspectionDto> models = response.body();
            long step1 = System.currentTimeMillis();
            Log.e(TAG, "Download and parse time (ms): " + (step1 - timeStart));

            // Get newest inspection
            String newestInspectionId = null;
            Cursor c = getContentResolver().query(InspectionEntry.CONTENT_URI, new String[]{"MAX(" +InspectionEntry.COLUMN_INSP_ID +")"}, null, null, null);
            if(c.moveToNext()){
                newestInspectionId = c.getString(0);
            }
            c.close();


            Map<String, ContentValues> locations = new HashMap<>(models.size());
            List<ContentValues> inspections = new ArrayList<>(models.size());
            for(InspectionDto dto : models){
                if(newestInspectionId == null || newestInspectionId.compareTo(dto.tilsynid) < 0){
                    if(!locations.containsKey(dto.tilsynsobjektid)){
                        locations.put(dto.tilsynsobjektid, dto.getLocationValues());
                    }
                    inspections.add(dto.getInspectionValues());
                }

            }
            ContentValues[] vals1 = locations.values().toArray(new ContentValues[locations.size()]);
            ContentValues[] vals2 = inspections.toArray(new ContentValues[locations.size()]);
            long step2 = System.currentTimeMillis();
            Log.e(TAG, "Create content values time (ms): " + (step2 - step1));


            int count1 = getContentResolver().bulkInsert(LocationEntry.CONTENT_URI, vals1);
            Log.e(TAG, "Stored locations: " + count1);
            long step3 = System.currentTimeMillis();
            Log.e(TAG, "Store locations time (ms): " + (step3 - step2));


            int count2 = getContentResolver().bulkInsert(InspectionEntry.CONTENT_URI, vals2);
            Log.e(TAG, "Stored inspections: " + count2);
            long step4 = System.currentTimeMillis();
            Log.e(TAG, "Store inspections time (ms): " + (step4 - step3));


            Log.e(TAG, "Total time (ms): " + (step4 - timeStart));
            getContentResolver().notifyChange(LocationEntry.CONTENT_URI, null);
            getContentResolver().notifyChange(InspectionEntry.CONTENT_URI, null);
        } catch (IOException e) {
            Log.e(TAG, "Error syncing and saving data", e);
        }
    }
}
