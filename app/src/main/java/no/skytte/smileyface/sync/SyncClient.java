package no.skytte.smileyface.sync;

import retrofit2.Retrofit;

// http://hotell.difi.no/download/mattilsynet/smilefjes/tilsyn
public class SyncClient {

    private static final String BASE_URL = "http://hotell.difi.no/";
    private OpenDataService dataService;

    public SyncClient()
    {
        Retrofit retrofitClient = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new CsvConverterFactory())
                .build();

        dataService = retrofitClient.create(OpenDataService.class);
    }

    public OpenDataService getDataService()
    {
        return dataService;
    }
}
