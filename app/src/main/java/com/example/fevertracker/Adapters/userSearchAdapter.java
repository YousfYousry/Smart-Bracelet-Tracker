package com.example.fevertracker.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.fevertracker.Classes.PlaceUsers;
import com.example.fevertracker.Classes.userSearch;
import com.example.fevertracker.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class userSearchAdapter extends ArrayAdapter<userSearch> {
    private Context mContext;
    private int mResource;


    private static class ViewHolder {
        TextView userName, userPassport, userId;
        FrameLayout back;
        ImageView UserPic;
    }

    public userSearchAdapter(Context context, int resource, ArrayList<userSearch> users) {
        super(context, resource, users);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName().trim(), passport = getItem(position).getPassport().trim(), id = getItem(position).getId().trim();
        int status = getItem(position).getStatus();

        userSearch user = new userSearch(name, passport, id, status);

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.userPassport = (TextView) convertView.findViewById(R.id.userPassport);
            holder.userId = (TextView) convertView.findViewById(R.id.userId);
            holder.back = (FrameLayout) convertView.findViewById(R.id.back);
            holder.UserPic = (ImageView) convertView.findViewById(R.id.UserPic);
            getUsersPic(user.getId(), holder.UserPic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userName.setText(user.getName());
        holder.userPassport.setText(user.getPassport());
        holder.userId.setText(user.getId());
        if(getItem(position)!=null) {
            if (getItem(position).getStatus() == 2) {
                holder.back.setBackgroundResource(R.drawable.yellow_circle);
            } else if (getItem(position).getStatus() == 3) {
                holder.back.setBackgroundResource(R.drawable.red_circle);
//            convertView.setBackgroundResource(R.drawable.users_search_ripple_red);
            }
        }

        return convertView;
    }

    public void getUsersPic(final String id, final ImageView UserPic) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads").child(id);
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if (UserPic != null) {
                        UserPic.setImageURI(Uri.fromFile(localFile));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 0;
        }
    }


}
