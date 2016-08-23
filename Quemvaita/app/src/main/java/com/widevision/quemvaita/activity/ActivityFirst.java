package com.widevision.quemvaita.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonClassGallery;
import com.widevision.quemvaita.dao.SetProfileDao;
import com.widevision.quemvaita.dao.UploadGalleryImageDao;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.GPSTracker;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.ValidationTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 7/4/16.
 */
public class ActivityFirst extends HideKeyActivity {
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    @Bind(R.id.before_btn)
    TextView mBeforeBtn;
    @Bind(R.id.after_btn)
    TextView mAfterBtn;
    @Bind(R.id.during_btn)
    TextView mDuringBtn;
    @Bind(R.id.status_txt)
    TextView mStatusTxt;
    @Bind(R.id.login_button)
    TextView mLoginBtn;
    @Bind(R.id.info_btn)
    ImageView mInfoBtn;
    @Bind(R.id.tutorial_images)
    ImageView mTutorialImg;
    @Bind(R.id.dot1)
    ImageView dot1;
    @Bind(R.id.dot2)
    ImageView dot2;
    @Bind(R.id.dot3)
    ImageView dot3;

    private Extension extension;
    CallbackManager callbackManager;
    AccessToken access_token;
    // GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;
    private GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // For auto login feature....
        if (PreferenceConnector.readString(ActivityFirst.this, PreferenceConnector.IS_LOGIN, "").equals("Yes")) {
            Intent i = new Intent(ActivityFirst.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.activity_first);
        ButterKnife.bind(this);
        toolbar_text.setText("Quemvaita");
        toolbar_image.setVisibility(View.INVISIBLE);
        extension = new Extension();
        callbackManager = CallbackManager.Factory.create();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (extension.executeStrategy(ActivityFirst.this, "", ValidationTemplate.INTERNET)) {

                    LoginManager.getInstance().logInWithReadPermissions(ActivityFirst.this, Arrays.asList("public_profile", "user_friends", "email", "user_events"));
                    LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            access_token = AccessToken.getCurrentAccessToken();

                            progressDialog = new ProgressDialog(ActivityFirst.this);
                            progressDialog.setMessage("Loading...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            GraphRequest request = GraphRequest.newMeRequest(access_token,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            progressDialog.dismiss();
                                            // Application code
                                            JSONObject jsonObject = response.getJSONObject();
                                            Log.e("string value-----", jsonObject.toString());
                                            try {
                                                attamptToSend(jsonObject);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "first_name,last_name,picture,email,gender,locale,updated_time,link,timezone,verified,age_range,likes,events");

                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            if (exception instanceof FacebookAuthorizationException) {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    LoginManager.getInstance().logOut();
                                }
                            }
                            Toast.makeText(ActivityFirst.this, "" + exception, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ActivityFirst.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }


        });

        mStatusTxt.setText("Are you enjoying to see who wil be there? Like them! If they like you too you can start a chat.");
        mBeforeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatusTxt.setText("Are you enjoying to see who wil be there? Like them! If they like you too you can start a chat.");

                mBeforeBtn.setBackgroundResource(R.drawable.button1);
                mAfterBtn.setBackgroundResource(R.drawable.button2);
                mDuringBtn.setBackgroundResource(R.drawable.button2);
                mTutorialImg.setImageResource(R.drawable.image_1);

                dot1.setImageResource(R.drawable.slider_dot2);
                dot2.setImageResource(R.drawable.slider_dot1);
                dot3.setImageResource(R.drawable.slider_dot1);
            }
        });

        mAfterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatusTxt.setText("During the event get noticed when people you liked are around.");
                mTutorialImg.setImageResource(R.drawable.image_2);
                mBeforeBtn.setBackgroundResource(R.drawable.button2);
                mAfterBtn.setBackgroundResource(R.drawable.button1);
                mDuringBtn.setBackgroundResource(R.drawable.button2);

                dot1.setImageResource(R.drawable.slider_dot1);
                dot2.setImageResource(R.drawable.slider_dot2);
                dot3.setImageResource(R.drawable.slider_dot1);
            }
        });

        mDuringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStatusTxt.setText("Did you meet during the event? Meet here later!\n" +
                        "Did you meet here? Meet in the event later!");
                mTutorialImg.setImageResource(R.drawable.image_3);
                mBeforeBtn.setBackgroundResource(R.drawable.button2);
                mAfterBtn.setBackgroundResource(R.drawable.button2);
                mDuringBtn.setBackgroundResource(R.drawable.button1);

                dot1.setImageResource(R.drawable.slider_dot1);
                dot2.setImageResource(R.drawable.slider_dot1);
                dot3.setImageResource(R.drawable.slider_dot2);
            }
        });

        gpsTracker = new GPSTracker();

    }

    private void attamptToSend(JSONObject jsonObject) {
/*(first_name,last_name,email,gender,profile_pic,facebook_id)*/
        try {
            String first_name = jsonObject.getString("first_name");
            String last_name = jsonObject.getString("last_name");
            String email = jsonObject.getString("email");
            String gender = jsonObject.getString("gender");
            String facebook_id = jsonObject.getString("id");
            final String photo_url = "https://graph.facebook.com/" + facebook_id + "/picture?type=large";

            progressDialog = new ProgressDialog(ActivityFirst.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Location location = gpsTracker.getLocation(ActivityFirst.this);
            if (location == null) {
                location = new Location("no location");
                location.setLatitude(0.0f);
                location.setLongitude(0.0f);
            }

            SetProfileDao profileDao = new SetProfileDao(first_name, last_name, email, gender, photo_url, facebook_id, "", location);
            profileDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    progressDialog.dismiss();
                    if (e == null) {
                        if (result != null) {
                            if (result.success.equals("1")) {
                                Intent i = new Intent(ActivityFirst.this, MainActivity.class);
                                startActivity(i);
                                finish();
                                PreferenceConnector.writeString(ActivityFirst.this, PreferenceConnector.LOGIN_UserId, result.user_id);
                                PreferenceConnector.writeString(ActivityFirst.this, PreferenceConnector.IS_LOGIN, "Yes");
                                uploadImage(result.user_id, photo_url);
                            } else {
                                if (result.message.trim().equals("Email Already Registered")) {
                                    uploadImage(result.user_id, photo_url);
                                    Intent i = new Intent(ActivityFirst.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    PreferenceConnector.writeString(ActivityFirst.this, PreferenceConnector.LOGIN_UserId, result.user_id);
                                    PreferenceConnector.writeString(ActivityFirst.this, PreferenceConnector.IS_LOGIN, "Yes");
                                } else {
                                    Toast.makeText(ActivityFirst.this, result.message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ActivityFirst.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityFirst.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //used for uploading profile image in galley...
    private void uploadImage(String user_id, String image_url) {
        UploadGalleryImageDao imageDao = new UploadGalleryImageDao(user_id, image_url, "1");
        imageDao.query(new AsyncCallback<GsonClassGallery>() {
            @Override
            public void onOperationCompleted(GsonClassGallery result, Exception e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
