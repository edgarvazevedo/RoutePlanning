package com.eazevedo.routeplanning.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eazevedo.routeplanning.R;
import com.eazevedo.routeplanning.db.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> trips;

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        public TextView startLocation;
        public TextView destination;
        public TextView isRoundTrip;
        public TextView stops;

        public TripViewHolder(View itemView) {
            super(itemView);
            startLocation = itemView.findViewById(R.id.start_location);
            destination = itemView.findViewById(R.id.destination);
            isRoundTrip = itemView.findViewById(R.id.is_round_trip);
            stops = itemView.findViewById(R.id.stops);
        }
    }

    public TripAdapter(List<Trip> trips) {
        this.trips = trips;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.startLocation.setText(trip.getStartLocation());
        holder.destination.setText(trip.getDestination());
        holder.isRoundTrip.setText(trip.isRoundTrip() ? "Round Trip" : "One Way");

        StringBuilder stopsBuilder = new StringBuilder();
        for (Trip.Stop stop : trip.getStops()) {
            stopsBuilder.append(stop.getAddress()).append("\n");
        }
        holder.stops.setText(stopsBuilder.toString().trim());
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }
}
