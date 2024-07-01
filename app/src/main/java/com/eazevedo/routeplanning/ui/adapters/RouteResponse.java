package com.eazevedo.routeplanning.ui.adapters;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RouteResponse {

    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        @SerializedName("optimizedIntermediateWaypointIndex")
        private List<Integer> optimizedIntermediateWaypointIndex;

        public List<Integer> getOptimizedIntermediateWaypointIndex() {
            return optimizedIntermediateWaypointIndex;
        }
    }
}
