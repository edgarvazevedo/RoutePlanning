package com.eazevedo.routeplanning.services;

import com.eazevedo.routeplanning.ui.adapters.RouteRequest;
import com.eazevedo.routeplanning.ui.adapters.RouteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
            "X-Goog-FieldMask:routes.optimizedIntermediateWaypointIndex",
            "Content-Type:application/json",
            "X-Goog-Api-Key:AIzaSyBqhK8FHYGewXFjy7fmBCyJvui_A3Nl4Hw"
    })
    @POST("directions/v2:computeRoutes")
    Call<RouteResponse> computeRoutes(@Body RouteRequest request);
}
