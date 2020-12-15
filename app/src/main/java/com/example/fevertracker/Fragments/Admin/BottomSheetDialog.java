package com.example.fevertracker.Fragments.Admin;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fevertracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    com.example.fevertracker.Fragments.Admin.profileForAdminFragment profileForAdminFragment;
    TextView Title;
    EditText val;
    String dataName="",title="",Content="",id="";
    Button save,cancel;

    public void setId(String id) {
        this.id = id;
    }

    public void setValString(String Content) {
        this.Content = Content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setProfileForAdminFragment(com.example.fevertracker.Fragments.Admin.profileForAdminFragment profileForAdminFragment) {
        this.profileForAdminFragment = profileForAdminFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) {
            Title = getView().findViewById(R.id.Title);
            Title.setText(title);
            val = getView().findViewById(R.id.Val);
            if(dataName.compareTo("phone")==0){
                val.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            val.setText(Content);
            val.setSelectAllOnFocus(true);
            val.selectAll();
            val.requestFocus();
            InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            save = getView().findViewById(R.id.save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!id.isEmpty() && !dataName.isEmpty() && !val.getText().toString().isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("Member").child(id).child(dataName).setValue(val.getText().toString());
                        closeKeyBoard();
                        profileForAdminFragment.cancel();
                    }
                }
            });
            cancel = getView().findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeKeyBoard();
                    profileForAdminFragment.cancel();
                }
            });
        }
    }

    private void closeKeyBoard(){
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}