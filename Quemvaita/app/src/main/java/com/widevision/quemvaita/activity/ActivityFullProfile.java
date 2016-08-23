package com.widevision.quemvaita.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetMyEventListDao;
import com.widevision.quemvaita.dao.GetUserGalleryDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonClassGallery;
import com.widevision.quemvaita.model.CustomTextView;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 2/4/16.
 */
public class ActivityFullProfile extends HideKeyActivity {

    @Bind(R.id.user_name)
    TextView user_name;
    @Bind(R.id.about_user)
    TextView about_user;
    @Bind(R.id.about_user_detail)
    TextView about_user_detail;
    @Bind(R.id.user_detail)
    TextView userDetailText;
    @Bind(R.id.gallery)
    ImageView gallery_btn;
    @Bind(R.id.dislike_btn)
    ImageView dislike_btn;
    /*@Bind(R.id.profile)
    ImageView mProfile;
    */
    @Bind(R.id.back_btn)
    ImageView back;
    @Bind(R.id.event_container)
    LinearLayout eventContainer;
    @Bind(R.id.slider)
    SliderLayout mSlider;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_profile);
        ButterKnife.bind(this);
        aQuery = new AQuery(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDataFromIntent();
    }

    private void getMyEventListDao(String user_id) {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityFullProfile.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetMyEventListDao listDao = new GetMyEventListDao(user_id);
        listDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            for (int i = 0; i < result.data.size(); i++) {
                                GsonClass.HomeData item = result.data.get(i);
                                if (item.published.equals("0")) {
                                    result.data.remove(i);
                                    i--;
                                }
                            }
                            for (int j = 0; j < result.data.size(); j++) {
                                CustomTextView textView = new CustomTextView(ActivityFullProfile.this);
                                textView.setText(result.data.get(j).event_name);
                                textView.setTextSize(14);
                                textView.setTextColor(ContextCompat.getColor(ActivityFullProfile.this, R.color.app_backgroug));
                                textView.setPadding(5, 5, 5, 5);
                                eventContainer.addView(textView);
                            }
                        } else {
                            CustomTextView textView = new CustomTextView(ActivityFullProfile.this);
                            textView.setText("Nothing to show.");
                            textView.setTextSize(14);
                            textView.setTextColor(ContextCompat.getColor(ActivityFullProfile.this, R.color.app_backgroug));
                            textView.setPadding(5, 5, 5, 5);
                            eventContainer.addView(textView);
                        }
                    } else {
                        Toast.makeText(ActivityFullProfile.this, result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityFullProfile.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private void getDataFromIntent() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String profile = b.getString("profile");
            String about = b.getString("about");
            String interest = b.getString("interest");
            String name = b.getString("name");
            final String friend_id = b.getString("id");
            if (about != null && about.equals("")) {
                userDetailText.setText("Nothing to show.");
            } else {
                userDetailText.setText(about);
            }
            if (interest != null && interest.equals("")) {
                about_user_detail.setText("Nothing to show.");
            } else {
                about_user_detail.setText(interest);
            }
            user_name.setText(name);
            //   aQuery.id(mProfile).image(profile);
            getMyEventListDao(friend_id);
            getGalleryImages(friend_id);

            gallery_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityFullProfile.this, ViewGalleryActivity.class);
                    intent.putExtra("id", friend_id);
                    startActivity(intent);
                }
            });
        }
    }

    private void getGalleryImages(String user_id) {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityFullProfile.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetUserGalleryDao galleryDao = new GetUserGalleryDao(user_id);
        galleryDao.query(new AsyncCallback<GsonClassGallery>() {
            @Override
            public void onOperationCompleted(GsonClassGallery result, Exception e) {
                if (result != null && e == null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {

                            for (int z = 0; z < result.data.size(); z++) {
                                if (result.data.get(z).imgurl == null || result.data.get(z).imgurl.equals("")) {
                                    result.data.remove(z);
                                    z--;
                                }
                            }

                            for (GsonClassGallery.Data name : result.data) {
                                TextSliderView textSliderView = new TextSliderView(ActivityFullProfile.this);
                                // initialize a SliderLayout
                                textSliderView.image(name.imgurl)
                                        .setScaleType(BaseSliderView.ScaleType.CenterInside);
/*
                                //add your extra information
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra", name.imgurl);*/

                                mSlider.addSlider(textSliderView);
                            }
                            mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            mSlider.setCustomAnimation(new DescriptionAnimation());
                            mSlider.setDuration(5000);
                        } else {
                            Toast.makeText(ActivityFullProfile.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityFullProfile.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityFullProfile.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}