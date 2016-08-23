package com.widevision.quemvaita.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetProfileDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonProfileClass;
import com.widevision.quemvaita.instagram.ApplicationData;
import com.widevision.quemvaita.instagram.InstagramApp;
import com.widevision.quemvaita.model.HideKeyFragment;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.PreferenceConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 29/3/16.
 */
public class FragmentMyProfile extends HideKeyFragment implements View.OnClickListener {
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
    @Bind(R.id.user_text)
    TextView user_text;
    @Bind(R.id.user_intrests)
    TextView user_intrests;
    @Bind(R.id.inactive_profile)
    LinearLayout inactive_profile;
    /*  @Bind(R.id.instagram_photo_layout)
      LinearLayout instagram_photo_layout;
      @Bind(R.id.edit_profile)
      Button mEditProfile;*/
    @Bind(R.id.man_radio)
    ImageView mManRadio;
    @Bind(R.id.woman_radio)
    ImageView mWomanRadio;
    @Bind(R.id.nothing_radio)
    ImageView mNothingRadio;


    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, view);
        camera_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivityAddMyPhoto.class);
                startActivity(i);

            }
        });
        mApp = new InstagramApp(getActivity(), ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                // tvSummary.setText("Connected as " + mApp.getUserName());

                // userInfoHashmap = mApp.
                Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT)
                        .show();
                new getInstagramPhotosTask().execute();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
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


// calling getProfile for view profile info
        getProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.man:
                mManRadio.setImageResource(R.drawable.radio_btn_selected);
                mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                break;
            case R.id.women:
                mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                mWomanRadio.setImageResource(R.drawable.radio_btn_selected);
                mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                break;
            case R.id.nothing:
                mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                mNothingRadio.setImageResource(R.drawable.radio_btn_selected);
                break;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
            } else if (msg.what == InstagramApp.WHAT_FINALIZE) {
                Toast.makeText(getActivity(), "Check your network.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });


    /*get profile data from sercer
    * @param*/
    private void getProfile() {
        String user_id = PreferenceConnector.readString(getActivity(), PreferenceConnector.LOGIN_UserId, "");
        GetProfileDao getProfileDao = new GetProfileDao(user_id);
        getProfileDao.query(new AsyncCallback<GsonProfileClass>() {
            @Override
            public void onOperationCompleted(GsonProfileClass result, Exception e) {
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.gender.equals("male")) {
                            mManRadio.setImageResource(R.drawable.radio_btn_selected);
                            mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                            mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                        } else if (result.gender.equals("female")) {
                            mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                            mWomanRadio.setImageResource(R.drawable.radio_btn_selected);
                            mNothingRadio.setImageResource(R.drawable.radio_btn_unselected);
                        } else if (result.gender.equals("nothing")) {
                            mManRadio.setImageResource(R.drawable.radio_btn_unselected);
                            mWomanRadio.setImageResource(R.drawable.radio_btn_unselected);
                            mNothingRadio.setImageResource(R.drawable.radio_btn_selected);
                        }
                    } else {

                    }
                } else {

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
            progressDialog = new ProgressDialog(getActivity());
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
                        JSONObject nja = jo.getJSONObject("photos");
                        JSONObject purl3 = nja.getJSONObject("thumbnail");
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

