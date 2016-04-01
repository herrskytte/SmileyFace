package no.skytte.smileyface;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

import no.skytte.smileyface.model.InspectionDto;
import no.skytte.smileyface.sync.SyncClient;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testSpreadSheatData() throws Exception {
        long timeStart = System.currentTimeMillis();
        List<InspectionDto> models = new SyncClient().getDataService().getAllInspections().clone().execute().body();
        long timeUsed = System.currentTimeMillis() - timeStart;
        Log.e("TEST", "Download and parse time (ms): " + timeUsed);
        InspectionDto first = models.get(0);
        assertNotNull(first);
    }
}