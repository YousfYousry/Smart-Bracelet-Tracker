package com.example.fevertracker.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.fevertracker.Classes.PlacesClass;
import com.example.fevertracker.R;
import com.example.fevertracker.Activities.LocationHistory;
import java.util.ArrayList;
import static com.example.fevertracker.Activities.LocationHistory.colour;

public class PlacesAdapter extends ArrayAdapter<PlacesClass> {
    private Context mContext;
    private int mResource;
    private ArrayList<View> all = new ArrayList<>(20);

    public void setViewColor(int pos) {
        try {
            if (all.get(pos) != null) {
                all.get(pos).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }
        }catch (Exception ignored){

        }
    }

    public void setLocationHistory(LocationHistory locationHistory) {
        this.locationHistory = locationHistory;
    }

    LocationHistory locationHistory = new LocationHistory();

    private static class ViewHolder {
        TextView Placename, TimeInter, PlaceDetails, TimeInRoad, Distance, LatLon;
        LinearLayout PlacesLayout, DistanceLayout;
    }

    public PlacesAdapter(Context context, int resource, ArrayList<PlacesClass> items,int size) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
        if (all != null) {
            all.clear();
        }
        for (int i = 0; i < size; i++) {
            if (all != null) {
                all.add(null);
            }
        }
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        try {
            all.set(position, convertView);
            String Placename = getItem(position).getPlacename().trim(), TimeInter = getItem(position).getTimeInter().trim(), PlaceDetails = getItem(position).getPlaceDetails().trim(), TimeInRoad = getItem(position).getTimeInRoad().trim(), Distance = getItem(position).getDistance().trim(), LatLon = getItem(position).getLatLon().trim();
            if (Placename.length() > 20) {
                Placename = Placename.substring(0, 19) + "...";
            }

            PlacesClass place = new PlacesClass(Placename, TimeInter, PlaceDetails, TimeInRoad, Distance, LatLon);

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder = new ViewHolder();
                holder.Placename = convertView.findViewById(R.id.Placename);
                holder.TimeInter = convertView.findViewById(R.id.TimeInter);
                holder.PlaceDetails = convertView.findViewById(R.id.PlaceDetails);
                holder.TimeInRoad = convertView.findViewById(R.id.TimeInRoad);
                holder.Distance = convertView.findViewById(R.id.Distance);
                holder.LatLon = convertView.findViewById(R.id.LatLon);
                holder.PlacesLayout = convertView.findViewById(R.id.PlacesLayout);
                holder.DistanceLayout = convertView.findViewById(R.id.DistanceLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (!place.getLatLon().isEmpty()) {
                holder.PlacesLayout.setVisibility(View.VISIBLE);
                holder.DistanceLayout.setVisibility(View.GONE);
                holder.Placename.setText(place.getPlacename());
                holder.TimeInter.setText(place.getTimeInter());
                holder.PlaceDetails.setText(place.getPlaceDetails());
                holder.LatLon.setText(place.getLatLon());
            } else {
                holder.PlacesLayout.setVisibility(View.GONE);
                holder.DistanceLayout.setVisibility(View.VISIBLE);
                holder.TimeInRoad.setText(place.getTimeInRoad());
                holder.Distance.setText(place.getDistance());
            }

            if (colour[position] == 0) {
                convertView.setBackgroundColor(Color.parseColor("#E3E3E9"));
            } else if (colour[position] == 1) {
                convertView.setBackgroundColor(Color.LTGRAY);
            }
//        } catch (Exception ignored) {
//
//        }
        return convertView;
    }

}
