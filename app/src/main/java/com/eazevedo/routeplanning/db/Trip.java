package com.eazevedo.routeplanning.db;

import java.util.List;

public class Trip {
    private String startLocation;
    private String destination;
    private boolean isRoundTrip;
    private List<Stop> stops; // Alterado para usar a classe Stop

    public static class Stop {
        private String address;

        public Stop(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String addresss) {
            this.address = addresss;
        }

        @Override
        public String toString() {
            return "Stop{" +
                    "Paradas='" + address + '\'' +
                    '}';
        }
    }

    // Construtor
    public Trip(String startLocation, String destination, boolean isRoundTrip, List<Stop> stops) {
        this.startLocation = startLocation;
        this.destination = destination;
        this.isRoundTrip = isRoundTrip;
        this.stops = stops;
    }

    // Getters e Setters
    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isRoundTrip() {
        return isRoundTrip;
    }

    public void setRoundTrip(boolean roundTrip) {
        isRoundTrip = roundTrip;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "startLocation='" + startLocation + '\'' +
                ", destination='" + destination + '\'' +
                ", isRoundTrip=" + isRoundTrip +
                ", stops=" + stops +
                '}';
    }
}
