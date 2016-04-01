package no.skytte.smileyface.sync;

import java.util.List;

import no.skytte.smileyface.model.InspectionDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface OpenDataService {
    @GET("download/mattilsynet/smilefjes/tilsyn")
    Call<List<InspectionDto>> getAllInspections();
}
