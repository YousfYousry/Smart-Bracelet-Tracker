package com.example.fevertracker.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fevertracker.Classes.PlaceUsers;
import com.example.fevertracker.R;

import java.util.ArrayList;


public class PlaceUsersAdapter extends ArrayAdapter<PlaceUsers> {

    private Context mContext;
    private int mResource;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        ImageView UserPic;
        TextView UserName;
        TextView UserId;
        TextView UserTime;
    }

    /**
     * Default constructor for the PersonListAdapter
     * //     *
     * //     * @param context
     * //     * @param resource
     * //     * @param objects
     * //
     */
    public PlaceUsersAdapter(Context context, int resource, ArrayList<PlaceUsers> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Uri pic = getItem(position).getPic();
        String UserName = getItem(position).getUserName();
        String UserId = getItem(position).getUserID();
        String UserTime = getItem(position).getUserTime();

        PlaceUsers user = new PlaceUsers(pic, UserName, UserId, UserTime);

        final View result;

        //ViewHolder object
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.UserPic = (ImageView) convertView.findViewById(R.id.profilePicture);
            holder.UserName = (TextView) convertView.findViewById(R.id.UserName);
            holder.UserId = (TextView) convertView.findViewById(R.id.UserID);
            holder.UserTime = (TextView) convertView.findViewById(R.id.Time);

            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        if (user.getPic() != null) {
            holder.UserPic.setImageURI(user.getPic());
        }
        holder.UserName.setText(user.getUserName());
        holder.UserId.setText(user.getUserID());
        holder.UserTime.setText(user.getUserTime());

        return convertView;
    }

}
