package com.example.fevertracker.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.fevertracker.Adapters.announceAdapter;
import com.example.fevertracker.Classes.Announce;
import com.example.fevertracker.Fragments.Admin.StyleCallback;
import com.example.fevertracker.R;
import com.example.fevertracker.Classes.StyleSpanRemover;
import com.example.fevertracker.CustomWidgets.CustomEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import maes.tech.intentanim.CustomIntent;

public class announcement extends AppCompatActivity {
    FloatingActionButton floating;
    ArrayList<Announce> announce = new ArrayList<>();
    ImageView textColor, backgroundColor;
    ImageButton bold, italic, underLine, textC, textB;
    int posToEdit = 0;
    CustomEditText announcementE;
    ListView announcementList;
    ScrollView scrollViewE;
    LinearLayout style, textColorList, backgroundColorList;
    boolean editMode = false;
    StyleCallback styleCallback = new StyleCallback();
    announcement announcement = this;
    String ColorString = "#000000", bColorString = "#FFFFFF";
    boolean styleOpened = false, isBold = false, isItalic = false, isUnder = false, removeSelection = false, textCOpened = false, textBOpened = false, selected = true, editPost = false;
    Context context = this;


    public void deletePost(int pos) {
        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement").child(Long.toString(announce.get(pos).getId())).removeValue();
        Toast.makeText(this, "Post is deleted", Toast.LENGTH_SHORT).show();
    }

    public void editPost(int pos) {
        editPost = true;
        posToEdit = pos;
        EditPost(pos);
    }

    public void bold(View view) {
        if (isBold) {
            int startSelection = announcementE.getSelectionStart();
            int endSelection = announcementE.getSelectionEnd();
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveStyle(spannable, startSelection, endSelection, Typeface.BOLD);
            isBold = false;
            bold.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        } else {
            CharacterStyle cs;
            int start = announcementE.getSelectionStart();
            int end = announcementE.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());

            cs = new StyleSpan(Typeface.BOLD);
            ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            selected = false;
            announcementE.setText(ssb);
            announcementE.setSelection(start, end);
            isBold = true;
            selected = true;
            bold.setBackgroundResource(R.drawable.ripple_grey);
        }
    }

    public void italic(View view) {
        if (isItalic) {
            int startSelection = announcementE.getSelectionStart();
            int endSelection = announcementE.getSelectionEnd();
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveStyle(spannable, startSelection, endSelection, Typeface.ITALIC);
            isItalic = false;
            italic.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        } else {
            CharacterStyle cs;
            int start = announcementE.getSelectionStart();
            int end = announcementE.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());

            cs = new StyleSpan(Typeface.ITALIC);
            ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            selected = false;
            announcementE.setText(ssb);
            announcementE.setSelection(start, end);
            selected = true;
            isItalic = true;
            italic.setBackgroundResource(R.drawable.ripple_grey);
        }
    }

    public void underLine(View view) {
        if (isUnder) {
            int startSelection = announcementE.getSelectionStart();
            int endSelection = announcementE.getSelectionEnd();
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveOne(spannable, startSelection, endSelection, UnderlineSpan.class);
            isUnder = false;
            underLine.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        } else {
            CharacterStyle cs;
            int start = announcementE.getSelectionStart();
            int end = announcementE.getSelectionEnd();
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());

            cs = new UnderlineSpan();
            ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            selected = false;
            announcementE.setText(ssb);
            announcementE.setSelection(start, end);
            selected = true;
            isUnder = true;
            underLine.setBackgroundResource(R.drawable.ripple_grey);
        }
    }

    public void textColor(View view) {
        if (textBOpened) {
            slideViewWidth(backgroundColorList, dpToPx(310), 0, 300);
            textBOpened = false;
            textB.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }

        if (!textCOpened) {
            slideViewWidth(textColorList, 0, dpToPx(310), 300);
            textCOpened = true;
            textC.setBackgroundResource(R.drawable.ripple_grey);
        } else {
            slideViewWidth(textColorList, dpToPx(310), 0, 300);
            textCOpened = false;
            textC.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
    }

    public void black(View view) {
        colorChosen("#000000");
    }

    public void red(View view) {
        colorChosen("#f44235");
    }

    public void blue(View view) {
        colorChosen("#2296f3");
    }

    public void green(View view) {
        colorChosen("#4caf50");
    }

    public void yellow(View view) {
        colorChosen("#ffc100");
    }

    public void grey(View view) {
        colorChosen("#9e9e9e");
    }

    public void colorChosen(String colorString) {
        selected = false;
        CharacterStyle cs;
        ColorString = colorString;
        int start = announcementE.getSelectionStart();
        int end = announcementE.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveOne(ssb, start, end, ForegroundColorSpan.class);

        cs = new ForegroundColorSpan(Color.parseColor(colorString));
        ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        announcementE.setText(ssb);
        announcementE.setSelection(start, end);
        textColor.setBackgroundColor(Color.parseColor(colorString));
        closeColors();
        selected = true;
    }

    public void closeColors() {
        if (textBOpened) {
            slideViewWidth(backgroundColorList, dpToPx(310), 0, 300);
            textBOpened = false;
            textB.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
        if (textCOpened) {
            slideViewWidth(textColorList, dpToPx(310), 0, 300);
            textCOpened = false;
            textC.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
    }

    public void textBack(View view) {
        if (textCOpened) {
            slideViewWidth(textColorList, dpToPx(310), 0, 300);
            textCOpened = false;
            textC.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }

        if (!textBOpened) {
            slideViewWidth(backgroundColorList, 0, dpToPx(310), 300);
            textBOpened = true;
            textB.setBackgroundResource(R.drawable.ripple_grey);
        } else {
            slideViewWidth(backgroundColorList, dpToPx(310), 0, 300);
            textBOpened = false;
            textB.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
        }
    }


    public void blackB(View view) {
        selected = false;
        bColorString = "#FFFFFF";
        int start = announcementE.getSelectionStart();
        int end = announcementE.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveOne(ssb, start, end, BackgroundColorSpan.class);
        announcementE.setText(ssb);
        announcementE.setSelection(start, end);
        backgroundColor.setBackgroundColor(Color.parseColor(bColorString));
        closeColors();
        selected = true;
    }

    public void redB(View view) {
        bColorChosen("#ef9a9a");
    }

    public void blueB(View view) {
        bColorChosen("#90caf9");
    }

    public void greenB(View view) {
        bColorChosen("#a5d6a7");
    }

    public void yellowB(View view) {
        bColorChosen("#ffe082");
    }

    public void greyB(View view) {
        bColorChosen("#e0e0e0");
    }

    public void bColorChosen(String colorString) {
        selected = false;
        CharacterStyle cs;
        bColorString = colorString;
        int start = announcementE.getSelectionStart();
        int end = announcementE.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveOne(ssb, start, end, BackgroundColorSpan.class);

        cs = new BackgroundColorSpan(Color.parseColor(colorString));
        ssb.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        announcementE.setText(ssb);
        announcementE.setSelection(start, end);
        backgroundColor.setBackgroundColor(Color.parseColor(colorString));
        closeColors();
        selected = true;
    }

    public void removeAll(View view) {
        int startSelection = announcementE.getSelectionStart();
        int endSelection = announcementE.getSelectionEnd();
        Spannable spannable = announcementE.getText();
        StyleSpanRemover spanRemover = new StyleSpanRemover();
        spanRemover.RemoveAll(spannable, startSelection, endSelection);
        removeSelection = true;
        ColorString = "#000000";
        bColorString = "#FFFFFF";
        setButtons(startSelection, endSelection);
    }

    public void close(View view) {
        closeStyle();
    }

    public void closeStyle() {
        if (styleOpened) {
            closeColors();
            slideViewHeight(style, dpToPx(54), 0, 300);
            styleOpened = false;
            styleCallback.setStyleOpened(false);
            styleCallback.closeAction();
        }
    }

    public void paint(View view) {
        showView();
    }

    public void back(View view) {
        onBackPressed();
    }

    public void cancel(View view) {
        editMode = false;
        editPost = false;
        closeStyle();
        closeColors();
        hideKeyBoard(announcementE);
        floating.setImageResource(R.drawable.ic_baseline_add_24);
        announcementList.setVisibility(View.VISIBLE);
        scrollViewE.setVisibility(View.GONE);
    }

    public void Edit(View view) {
        if (!editMode) {
            editMode = true;
            announcementList.setVisibility(View.GONE);
            scrollViewE.setVisibility(View.VISIBLE);
            floating.setImageResource(R.drawable.ic_baseline_save_24);
            selected = false;
            showSoftKeyboard(announcementE);
            announcementE.setText("");
            selected = true;
        } else {
            hideKeyBoard(announcementE);
            closeStyle();
            closeColors();
            floating.setImageResource(R.drawable.ic_baseline_add_24);
            if (editPost) {

                FirebaseDatabase.getInstance().getReference().child("adminInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement").child(Long.toString(announce.get(posToEdit).getId())).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement").child(Long.toString(System.currentTimeMillis())).setValue(Html.toHtml(announcementE.getText()));
                        Toast.makeText(context, "Post is saved successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            } else {
                saveAnn();
            }
            editMode = false;
            editPost = false;
            announcementList.setVisibility(View.VISIBLE);
            scrollViewE.setVisibility(View.GONE);
        }
    }

    public void saveAnn() {
        FirebaseDatabase.getInstance().getReference().child("adminInfo").child("announcement").child(Long.toString(System.currentTimeMillis())).setValue(Html.toHtml(announcementE.getText()));
        Toast.makeText(this, "Post is saved successfully", Toast.LENGTH_SHORT).show();
    }

    public void EditPost(int pos) {
        if (!editMode) {
            showSoftKeyboard(announcementE);
            editMode = true;
            announcementList.setVisibility(View.GONE);
            scrollViewE.setVisibility(View.VISIBLE);
            selected = false;
            floating.setImageResource(R.drawable.ic_baseline_save_24);
            announcementE.setText(announce.get(pos).getAnnounce());
            selected = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_for_admin);

        floating = findViewById(R.id.edit);
        floating.setImageResource(R.drawable.ic_baseline_add_24);
        textColorList = findViewById(R.id.textColorlist);
        backgroundColorList = findViewById(R.id.backgroundColorlist);
        announcementList = findViewById(R.id.announcementList);
        textC = findViewById(R.id.textC);
        textB = findViewById(R.id.textB);
        bold = findViewById(R.id.bold);
        italic = findViewById(R.id.italic);
        underLine = findViewById(R.id.underLine);
        textColor = findViewById(R.id.textColor);
        backgroundColor = findViewById(R.id.backgroundColor);
        announcementE = findViewById(R.id.announceTextE);
        style = findViewById(R.id.style);
        styleCallback.setBodyView(announcementE);
        styleCallback.setAnouncement(announcement);
        announcementE.setCustomSelectionActionModeCallback(styleCallback);
        announcementE.setAnouncement(announcement);
        scrollViewE = findViewById(R.id.editMode);
        announcementE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTextSpan(start, count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.colorPrimary) & 0x00ffffff);

        announcementE.setHighlightColor(Color.parseColor("#782F5D87"));
        FirebaseDatabase.getInstance().getReference().child("adminInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (announce != null) {
                    announce.clear();
                }
                ArrayList<Long> posts = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.child("announcement").getChildren()) {
                    if (ds.getKey() != null) {
                        if (dataSnapshot.child("announcement").child(ds.getKey()).getValue() != null) {
                            posts.add(NumericOf(ds.getKey()));
                        }
                    }
                }
                Collections.sort(posts, Collections.reverseOrder());
                for (int i = 0; i < posts.size(); i++) {
                    announce.add(new Announce(Html.fromHtml(Objects.requireNonNull(dataSnapshot.child("announcement").child(Long.toString(posts.get(i))).getValue()).toString()), posts.get(i)));
                }

                announceAdapter arrayAdapter = new announceAdapter(context, R.layout.announce, announce);
                arrayAdapter.setAnouncement(announcement);
                announcementList.setAdapter(arrayAdapter);
//              Toast.makeText(context,"Announcement is edited",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setTextSpan(int start, int lengthAfter) {
        if (isBold && selected) {
            Objects.requireNonNull(announcementE.getText()).setSpan(new StyleSpan(Typeface.BOLD), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isItalic && selected) {
            Objects.requireNonNull(announcementE.getText()).setSpan(new StyleSpan(Typeface.ITALIC), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (isUnder && selected) {
            Objects.requireNonNull(announcementE.getText()).setSpan(new UnderlineSpan(), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!isUnder && selected) {
            Spannable spannable = announcementE.getText();
            StyleSpanRemover spanRemover = new StyleSpanRemover();
            spanRemover.RemoveOne(spannable, start, start + lengthAfter, UnderlineSpan.class);
        }
        if (selected) {
            Objects.requireNonNull(announcementE.getText()).setSpan(new ForegroundColorSpan(Color.parseColor(ColorString)), start, start + lengthAfter, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            if (bColorString.compareTo("#FFFFFF") != 0) {
                announcementE.getText().setSpan(new BackgroundColorSpan(Color.parseColor(bColorString)), start, start + lengthAfter, Spanned
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void setButtons(int start, int end) {
        if (styleOpened) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(announcementE.getText());
            if ((end - start) != 0 || removeSelection) {
                if (removeSelection) {
                    textColor.setBackgroundColor(Color.parseColor(ColorString));
                    backgroundColor.setBackgroundColor(Color.parseColor(bColorString));
                }
                if (!isBold(ssb, start, end) || removeSelection) {
                    isBold = false;
                    bold.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
                } else {
                    isBold = true;
                    bold.setBackgroundResource(R.drawable.ripple_grey);
                }

                if (!isItalic(ssb, start, end) || removeSelection) {
                    isItalic = false;
                    italic.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
                } else {
                    isItalic = true;
                    italic.setBackgroundResource(R.drawable.ripple_grey);
                }

                if (!isUnder(ssb, start, end) || removeSelection) {
                    isUnder = false;
                    underLine.setBackgroundResource(R.drawable.ripple_effect_rec_no_background);
                } else {
                    isUnder = true;
                    underLine.setBackgroundResource(R.drawable.ripple_grey);
                }
//                getColor(ssb, start, end);
                removeSelection = false;
            }
        }
    }

    public Boolean isBold(SpannableStringBuilder ssb, int start, int end) {
        int next;
        boolean continued = true;
        for (int i = start; i < end && continued; i = next) {
            continued = false;
            next = ssb.nextSpanTransition(i, ssb.length(), CharacterStyle.class);
            StyleSpan[] spans = ssb.getSpans(i, next, StyleSpan.class);
            for (StyleSpan span : spans) {
                if (span.getStyle() == Typeface.BOLD) {
                    continued = true;
                }
            }
        }
        return continued;
    }

    public Boolean isItalic(SpannableStringBuilder ssb, int start, int end) {
        int next;
        boolean continued = true;
        for (int i = start; i < end && continued; i = next) {
            continued = false;
            next = ssb.nextSpanTransition(i, ssb.length(), CharacterStyle.class);
            StyleSpan[] spans = ssb.getSpans(i, next, StyleSpan.class);
            for (StyleSpan span : spans) {
                if (span.getStyle() == Typeface.ITALIC) {
                    continued = true;
                }
            }
        }
        return continued;
    }

    public Boolean isUnder(SpannableStringBuilder ssb, int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = ssb.nextSpanTransition(i, ssb.length(), CharacterStyle.class);
            UnderlineSpan[] spans = ssb.getSpans(i, next, UnderlineSpan.class);
            if (spans.length == 0) {
                return false;
            }
        }
        return true;
    }

    public void showView() {
        if (!styleOpened) {
            slideViewHeight(style, 0, dpToPx(54), 300);
            styleOpened = true;
            styleCallback.setStyleOpened(true);
        } else {
            slideViewHeight(style, dpToPx(54), 0, 300);
            styleOpened = false;
            styleCallback.setStyleOpened(false);
            styleCallback.closeAction();
        }
    }

    public void slideViewHeight(final View view, int currentHeight, int newHeight,
                                long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().height = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public void slideViewWidth(final View view, int currentWidth, int newWidth, long duration) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(duration);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            view.getLayoutParams().width = (Integer) animation1.getAnimatedValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public long NumericOf(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {
            return 0;
        }
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public int dpToPx(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return (int) px;
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            editMode = false;
            editPost = false;
            hideKeyBoard(announcementE);
            floating.setImageResource(R.drawable.ic_baseline_add_24);
            announcementList.setVisibility(View.VISIBLE);
            scrollViewE.setVisibility(View.GONE);
            closeStyle();
            closeColors();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    }
}
