package com.widevision.quemvaita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widevision.quemvaita.R;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.Extension;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 7/4/16.
 */
public class ActivityRegister extends HideKeyActivity {
    @Bind(R.id.user_name) EditText user_name;
    @Bind(R.id.email) EditText email;
    @Bind(R.id.gender) EditText gender;
    @Bind(R.id.about_user) EditText about_user;
    @Bind(R.id.job_description) EditText job_description;
    @Bind(R.id.address) EditText address;
    @Bind(R.id.password) EditText password;
    @Bind(R.id.register) Button register;
    @Bind(R.id.icon_menu) ImageView icon_menu;
    @Bind(R.id.toolbar_text) TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    String uname="",uemail="",ugender="",uabout="",ujobdesc="",uaddress="",upassword="";
    public Extension extension;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("Register");
        extension = new Extension();
        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityRegister.this, ActivityFirst.class);
                startActivity(i);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*uname=user_name.getText().toString();
                uemail=email.getText().toString();
                ugender=gender.getText().toString();
                uabout=about_user.getText().toString();
                ujobdesc=job_description.getText().toString();
                uaddress=address.getText().toString();
                upassword=password.getText().toString();
                if (extension.executeStrategy(ActivityRegister.this, "", ValidationTemplate.INTERNET)) {
                    UserRegistrationDao register = new UserRegistrationDao(uname, uemail, ugender, uabout, ujobdesc, uaddress, upassword);
                    register.query(new AsyncCallback<GsonClass>() {

                        @Override
                        public void onOperationCompleted(GsonClass result, Exception e) {

                            if (result != null) {
                                if (result.success != null && result.success.equals("1")) {
                                    Toast.makeText(ActivityRegister.this, "register success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ActivityRegister.this, "register fail", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ActivityRegister.this, "register fail", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }
                else{
                    Toast.makeText(ActivityRegister.this, "no internet", Toast.LENGTH_SHORT).show();
                }*/
                startActivity(new Intent(ActivityRegister.this, MainActivity.class));
            }
        });
    }
}
