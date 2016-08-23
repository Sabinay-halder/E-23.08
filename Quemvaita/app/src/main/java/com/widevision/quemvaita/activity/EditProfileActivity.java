package com.widevision.quemvaita.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetProfileDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonProfileClass;
import com.widevision.quemvaita.dao.InterruptProfileDao;
import com.widevision.quemvaita.dao.UpdateProfileDao;
import com.widevision.quemvaita.database.DbHelper;
import com.widevision.quemvaita.instagram.ApplicationData;
import com.widevision.quemvaita.instagram.InstagramApp;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.ValidationTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditProfileActivity extends HideKeyActivity implements View.OnClickListener {
    private InstagramApp mApp;
    public static String mCallbackUrl = "";
    private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String API_URL = "https://api.instagram.com/v1";
    @Bind(R.id.user_name)
    TextView user_name;
    @Bind(R.id.camera_icon)
    ImageView camera_icon;
    @Bind(R.id.man)
    LinearLayout man;
    @Bind(R.id.women)
    LinearLayout women;
    @Bind(R.id.nothing)
    LinearLayout nothing;
    @Bind(R.id.inactive_profile)
    LinearLayout inactive_profile;
    @Bind(R.id.update_profile)
    TextView mUpdateProfile;
    @Bind(R.id.man_radio)
    ImageView mManRadio;
    @Bind(R.id.woman_radio)
    ImageView mWomanRadio;
    @Bind(R.id.nothing_radio)
    ImageView mNothingRadio;
    @Bind(R.id.user_about)
    EditText mAboutEdt;
    @Bind(R.id.user_intrest)
    EditText mInterestEdt;
    @Bind(R.id.user_name_txt)
    EditText userNameTxt;

    @Bind(R.id.icon_menu)
    ImageView backBtn;
    @Bind(R.id.toolbar_right_image)
    ImageView editBtn;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.profile_pic)
    ImageView profile_pic;
    private boolean enable = false;
    public static String mPhotoPath = "";
    private GsonProfileClass updatedData;
    private AQuery aQuery;
    public static String FILE_TYPE = "";

    private void enable() {
        mAboutEdt.setClickable(true);
        mAboutEdt.setFocusable(true);
        mAboutEdt.setEnabled(true);
        mAboutEdt.setFocusableInTouchMode(true);

        mInterestEdt.setEnabled(true);
        mInterestEdt.setFocusable(true);
        mInterestEdt.setFocusableInTouchMode(true);
        mInterestEdt.setClickable(true);

        userNameTxt.setEnabled(true);
        userNameTxt.setFocusable(true);
        userNameTxt.setFocusableInTouchMode(true);
        userNameTxt.setClickable(true);

        camera_icon.setVisibility(View.VISIBLE);
        mUpdateProfile.setVisibility(View.VISIBLE);
    }

    private void disable() {
        mAboutEdt.setClickable(false);
        mAboutEdt.setFocusable(false);
        mAboutEdt.setEnabled(false);

        mInterestEdt.setClickable(false);
        mInterestEdt.setFocusable(false);
        mInterestEdt.setEnabled(false);

        userNameTxt.setClickable(false);
        userNameTxt.setFocusable(false);
        userNameTxt.setEnabled(false);

        camera_icon.setVisibility(View.GONE);
        mUpdateProfile.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_profile);
        ButterKnife.bind(this);
        toolbar_text.setText("My Profile");
        disable();
        editBtn.setVisibility(View.VISIBLE);
        editBtn.setImageResource(R.drawable.edit_icon);
        backBtn.setImageResource(R.drawable.back_btn);
        aQuery = new AQuery(EditProfileActivity.this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        camera_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditProfileActivity.this, ActivityAddMyPhoto.class);
                Bundle translateBundle = ActivityOptions.makeCustomAnimation(EditProfileActivity.this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                startActivity(i, translateBundle);
            }
        });

        Extension extension = new Extension();

        mApp = new InstagramApp(EditProfileActivity.this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                // tvSummary.setText("Connected as " + mApp.getUserName());

                // userInfoHashmap = mApp.
                Toast.makeText(EditProfileActivity.this, "success", Toast.LENGTH_SHORT).show();
                new getInstagramPhotosTask().execute();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
       /* instagram_photo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApp.hasAccessToken()) {
                    mApp.fetchUserName(handler);
                    new getInstagramPhotosTask().execute();
                } else {
                    mApp.authorize();
                }
            }
        });*/

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enable();
                enable = true;
            }
        });

        mUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disable();
                enable = false;
                updateProfile();
            }
        });

        man.setOnClickListener(this);
        women.setOnClickListener(this);
        nothing.setOnClickListener(this);

        inactive_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intrruptProfile();
            }
        });

// calling getProfile for view profile info
        if (extension.executeStrategy(EditProfileActivity.this, "", ValidationTemplate.INTERNET)) {
            getProfile();
        } else {
            Toast.makeText(EditProfileActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mPhotoPath == null || mPhotoPath.isEmpty()) {
                aQuery.id(profile_pic).image(updatedData.profile_pic, true, true, 400, R.drawable.com_facebook_profile_picture_blank_square);
            } else {
                if (FILE_TYPE.equals(ActivityAddMyPhoto.FILE)) {
                    aQuery.id(profile_pic).image(new File(mPhotoPath), 400);
                } else {
                    aQuery.id(profile_pic).image(mPhotoPath, true, true, 400, R.drawable.com_facebook_profile_picture_blank_square);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = this.getResources().getDisplayMetrics().widthPixels;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (width / 2) + (width / 6));
        profile_pic.setLayoutParams(layoutParams);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view) {
        if (enable) {
            int id = view.getId();
            switch (id) {
                case R.id.man:
                    mManRadio.setImageResource(R.drawable.radio_btn_selected);
                    mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                    mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                    updatedData.gender = "male";
                    break;
                case R.id.women:
                    mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                    mWomanRadio.setImageResource(R.drawable.radio_btn_selected);
                    mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                    updatedData.gender = "female";
                    break;
                case R.id.nothing:
                    mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                    mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                    mNothingRadio.setImageResource(R.drawable.radio_btn_selected);
                    updatedData.gender = "nothing";
                    break;
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                HashMap<String, String> userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(EditProfileActivity.this, "Check your network.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    /*Intrrupte profile function*/
    private void intrruptProfile() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setMessage("Do you really want to inactive your account ?");
        builder.setTitle("Alert !");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                String user_id = PreferenceConnector.readString(EditProfileActivity.this, PreferenceConnector.LOGIN_UserId, "");
                InterruptProfileDao profileDao = new InterruptProfileDao(user_id);
                profileDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        progressDialog.dismiss();
                        if (e == null && result != null) {
                            Toast.makeText(EditProfileActivity.this, result.message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    /*get profile data from sercer
    * @param*/
    private void getProfile() {
        String user_id = PreferenceConnector.readString(EditProfileActivity.this, PreferenceConnector.LOGIN_UserId, "");
        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetProfileDao getProfileDao = new GetProfileDao(user_id);
        getProfileDao.query(new AsyncCallback<GsonProfileClass>() {
            @Override
            public void onOperationCompleted(GsonProfileClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    updatedData = result;
                    if (result.success.equals("1")) {
                        userNameTxt.setText(result.first_name);
                        switch (result.gender) {
                            case "male":
                                mManRadio.setImageResource(R.drawable.radio_btn_selected);
                                mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                                mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                                break;
                            case "female":
                                mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                                mWomanRadio.setImageResource(R.drawable.radio_btn_selected);
                                mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                                break;
                            case "nothing":
                                mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                                mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                                mNothingRadio.setImageResource(R.drawable.radio_btn_selected);
                                break;
                        }
                        FILE_TYPE = ActivityAddMyPhoto.URL;
                        aQuery.id(profile_pic).image(updatedData.profile_pic, true, true, 400, R.drawable.com_facebook_profile_picture_blank_square);
                        mPhotoPath = updatedData.profile_pic;
                        mAboutEdt.setText(updatedData.about);
                        mInterestEdt.setText(updatedData.interest);
                        /*String status = PreferenceConnector.readString(EditProfileActivity.this, PreferenceConnector.IS_EDIT_FIRST_TIME, "Yes");
                        if (status.equals("Yes")) {
                            PreferenceConnector.writeString(EditProfileActivity.this, PreferenceConnector.IS_EDIT_FIRST_TIME, "No");
                            DbHelper dbHelper = DbHelper.getInstance();
                            String user_id = PreferenceConnector.readString(EditProfileActivity.this, PreferenceConnector.LOGIN_UserId, "");
                            dbHelper.insertImage(user_id, updatedData.profile_pic, ActivityAddMyPhoto.URL);
                        }*/
                    } else {
                        Toast.makeText(EditProfileActivity.this, result.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateProfile() {

        String about = mAboutEdt.getText().toString();
        String interest = mInterestEdt.getText().toString();
        String name = userNameTxt.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please enter your name.", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
       /*String user_id, String first_name, String last_name, String email, String gender, File profile_pic, String facebook_id, String device_token, String about, String interest*/
        String user_id = PreferenceConnector.readString(EditProfileActivity.this, PreferenceConnector.LOGIN_UserId, "");
        UpdateProfileDao updateProfileDao;
        if (FILE_TYPE.equals(ActivityAddMyPhoto.FILE)) {
            updateProfileDao = new UpdateProfileDao(user_id, name, updatedData.last_name, updatedData.email, updatedData.gender, new File(mPhotoPath), "", "", about, interest);
        } else {
            updateProfileDao = new UpdateProfileDao(user_id, name, updatedData.last_name, updatedData.email, updatedData.gender, mPhotoPath, "", "", about, interest);
        }

        updateProfileDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    Toast.makeText(EditProfileActivity.this, result.message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*Take photos from instagram*/
    class getInstagramPhotosTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditProfileActivity.this);
            progressDialog.setMessage("Getting photos...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token=" + mApp.getTOken());

                URLConnection tc = example.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                    JSONArray object = ob.getJSONArray("data");

                    for (int i = 0; i < object.length(); i++) {
                        JSONObject jo = (JSONObject) object.get(i);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }
}