package com.example.fevertracker.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fevertracker.Classes.Dates;
import com.example.fevertracker.R;

import java.util.ArrayList;


public class DatesAdapter extends ArrayAdapter<Dates> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView Date;
    }

    /**
     * Default constructor for the PersonListAdapter
     //     *
     //     * @param context
     //     * @param resource
     //     * @param objects
     //     */
    public DatesAdapter(Context context, int resource, ArrayList<Dates> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String Date = getItem(position).getDate();

        Dates date = new Dates(Date);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.Date = (TextView) convertView.findViewById(R.id.Title);

            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
//        result.startAnimation(animation);
        lastPosition = position;

        holder.Date.setText(date.getDate());
        holder.Date.setGravity(Gravity.CENTER);

        return convertView;
    }

}
