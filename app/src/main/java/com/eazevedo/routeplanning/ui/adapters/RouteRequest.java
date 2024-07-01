package com.eazevedo.routeplanning.ui.adapters;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RouteRequest {

    @SerializedName("origin")
    private Location origin;

    @SerializedName("destination")
    private Location destination;

    @SerializedName("intermediates")
    private List<Location> intermediates;

    @SerializedName("travelMode")
    private String travelMode;

    @SerializedName("optimizeWaypointOrder")
    private boolean optimizeWaypointOrder;

    public RouteRequest(Location origin, Location destination, List<Location> intermediates) {
        this.origin = origin;
        this.destination = destination;
        this.intermediates = intermediates;
        this.travelMode = "DRIVE";
        this.optimizeWaypointOrder = true;
    }

    public static class Location {
        @SerializedName("address")
        private String address;

        public Location(String address) {
            this.address = address;
        }
    }
}
