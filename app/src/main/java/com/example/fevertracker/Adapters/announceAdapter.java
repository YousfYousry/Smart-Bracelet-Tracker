package com.example.fevertracker.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fevertracker.Classes.Announce;
import com.example.fevertracker.R;
import com.example.fevertracker.Activities.Admin.announcement;

import java.util.ArrayList;

public class announceAdapter extends ArrayAdapter<Announce> {

    announcement anouncementC;
    private final Context mContext;
    private final int mResource;

    public void setAnouncement(announcement anouncement) {
        this.anouncementC = anouncement;
    }

    private static class ViewHolder {
        TextView announce, dateText;
        ImageButton deletePost, ecitPost;
    }

    public announceAdapter(Context context, int resource, ArrayList<Announce> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("RtlHardcoded")
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Spanned announce = getItem(position).getAnnounce();
        Long time = getItem(position).getId();
        announceAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new announceAdapter.ViewHolder();
            holder.announce = (TextView) convertView.findViewById(R.id.announceText);
            holder.dateText = (TextView) convertView.findViewById(R.id.dateText);
            holder.deletePost = convertView.findViewById(R.id.deletePost);
            holder.ecitPost = convertView.findViewById(R.id.ecitPost);

            convertView.setTag(holder);
        } else {
            holder = (announceAdapter.ViewHolder) convertView.getTag();
        }
        if (holder.deletePost != null) {
            holder.deletePost.setOnClickListener(v -> anouncementC.deletePost(position));
        }
        if (holder.ecitPost != null) {
            holder.ecitPost.setOnClickListener(v -> anouncementC.editPost(position));
        }

        holder.announce.setText(announce);
        holder.announce.setGravity(Gravity.LEFT);
        holder.dateText.setText(getTime(time));
        return convertView;
    }

    public String getTime(Long time) {
        time = System.currentTimeMillis() - time;
        if (time < 60000) {
            return "less than a minute ago";
        }
        long years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0;
        time /= 1000;
        while (time >= 60) {
            if (time >= 31536000) {
                time -= 31536000;
                years += 1;
            } else if (time >= 2592000) {
                time -= 2592000;
                months += 1;
            } else if (time >= 604800) {
                time -= 604800;
                weeks += 1;
            } else if (time >= 86400) {
                time -= 86400;
                days += 1;
            } else if (time >= 3600) {
                time -= 3600;
                hours += 1;
            } else {
                time -= 60;
                minutes += 1;
            }
        }

        if (years > 0) {
            if (years > 1) {
                return years + " years ago";
            } else {
                return "a year ago";
            }
        } else if (months > 0) {
            if (months > 1) {
                return months + " months ago";
            } else {
                return "a month ago";
            }
        } else if (weeks > 0) {
            if (weeks > 1) {
                return weeks + " weeks ago";
            } else {
                return "a week ago";
            }
        } else if (days > 0) {
            if (days > 1) {
                return days + " days ago";
            } else {
                return "a day ago";
            }
        } else if (hours > 0) {
            if (hours > 1) {
                return hours + " hours ago";
            } else {
                return "an hour ago";
            }
        } else if (minutes > 0) {
            if (minutes > 1) {
                return minutes + " minutes ago";
            } else {
                return "a minute ago";
            }
        }
        return "";
    }
}
