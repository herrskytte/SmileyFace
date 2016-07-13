package no.skytte.smileyface.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import no.skytte.smileyface.model.InspectionDto;
import no.skytte.smileyface.storage.SmileyContract;
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
        long timeStart = System.currentTimeMillis();
        SharedPreferences prefs = getSharedPreferences("SMILEY", MODE_PRIVATE);
        long lastSync = prefs.getLong("LAST_SYNC", 0);
        long timeSinceLastSync = timeStart - lastSync;
        if(timeSinceLastSync < 86400000){
            Log.e(TAG, "Synced last 24h. All good.");
            return;
        }

        try {
            Call<List<InspectionDto>> call = new SyncClient().getDataService().getAllInspections();
            Response<List<InspectionDto>> response = call.execute();
            List<InspectionDto> models = response.body();
            long step1 = System.currentTimeMillis();
            Log.e(TAG, "Inspections: " + models.size());
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
            ContentValues[] vals2 = inspections.toArray(new ContentValues[inspections.size()]);
            long step2 = System.currentTimeMillis();
            Log.e(TAG, "Create content values time (ms): " + (step2 - step1));

            long step3 = storeLocations(vals1, step2);
            long step4 = storeInspections(vals2, step3);

            Log.e(TAG, "Total time (ms): " + (step4 - timeStart));

            prefs.edit().putLong("LAST_SYNC", timeStart).commit();
        } catch (IOException e) {
            Log.e(TAG, "Error syncing and saving data", e);
        }
    }

    private long storeInspections(ContentValues[] vals2, long step3) {
        // Store inspections
        List<ContentValues[]> inspectionValuesList = splitArray(vals2, 500);
        int count2 = 0;
        for(ContentValues[] inspectionValues : inspectionValuesList){
            long step4a = System.currentTimeMillis();
            count2 += getContentResolver().bulkInsert(InspectionEntry.CONTENT_URI, inspectionValues);
            Log.e(TAG, "Stored " + inspectionValues.length + " inspections in (ms): " + (System.currentTimeMillis() - step4a));
            //getContentResolver().notifyChange(InspectionEntry.CONTENT_URI, null);
        }
        Log.e(TAG, "Stored inspections: " + count2);
        long step4 = System.currentTimeMillis();
        Log.e(TAG, "Store inspections time (ms): " + (step4 - step3));
        return step4;
    }

    private long storeLocations(ContentValues[] vals1, long step2) {
        // Store locations
        List<ContentValues[]> locationValuesList = splitArray(vals1, 500);
        int count1 = 0;
        for(ContentValues[] locationValues : locationValuesList){
            long step3a = System.currentTimeMillis();
            count1 += getContentResolver().bulkInsert(LocationEntry.CONTENT_URI, locationValues);
            Log.e(TAG, "Stored " + locationValues.length + " locations in (ms): " + (System.currentTimeMillis() - step3a));
            //getContentResolver().notifyChange(LocationEntry.CONTENT_URI, null);
        }
        Log.e(TAG, "Stored locations: " + count1);
        long step3 = System.currentTimeMillis();
        Log.e(TAG, "Store locations time (ms): " + (step3 - step2));
        return step3;
    }

    public static <T extends Object> List<T[]> splitArray(T[] array, int max){

        int x = array.length / max;
        int r = (array.length % max); // remainder

        int lower = 0;
        int upper = 0;

        List<T[]> list = new ArrayList<T[]>();

        int i=0;

        for(i=0; i<x; i++){

            upper += max;

            list.add(Arrays.copyOfRange(array, lower, upper));

            lower = upper;
        }

        if(r > 0){

            list.add(Arrays.copyOfRange(array, lower, (lower + r)));

        }

        return list;
    }
}
