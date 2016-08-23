package com.widevision.quemvaita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.model.HideKeyActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 7/4/16.
 */
public class ActivityLogin extends HideKeyActivity {
    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.login)
    Button login;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
   // @Bind(R.id.facebook_login) LoginButton facebook_login;
    String uemail = "", upass = "";
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        icon_menu.setImageResource(R.drawable.back_btn);
       /* callbackManager = CallbackManager.Factory.create();
        facebook_login.setReadPermissions("user_friends");*/
        toolbar_text.setText("Login");
        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityLogin.this, ActivityFirst.class);
                startActivity(i);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* uemail=email.getText().toString();
                upass=password.getText().toString();
                LoginDao loginDao = new LoginDao(uemail, upass);
                loginDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {

                        if (e == null && result != null) {
                            if (result.success.equals("1")) {
                                finish();
                                startActivity(new Intent(ActivityLogin.this, MainActivity.class));

                            } else {
                                Toast.makeText(ActivityLogin.this,"login fail",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ActivityLogin.this,"login fail",Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
                startActivity(new Intent(ActivityLogin.this, MainActivity.class));
            }
        });

        /*facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });*/
    }

  /*  @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }*/
}