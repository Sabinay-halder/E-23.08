package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.CancelAccountDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.PreferenceConnector;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-five on 12/05/16.
 */
public class HelpActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.icon_menu)
    ImageView backBtn;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.cancel_account)
    TextView cancelBtn;
    @Bind(R.id.tutorial)
    TextView tutorialBtn;
    @Bind(R.id.contact_us)
    TextView contactUsBtn;
    @Bind(R.id.user_agreement)
    TextView userAgreementBtn;
    public static String cancel_receiver = "com.widevision.quemvaita.cancel_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        ButterKnife.bind(this);
        toolbar_text.setText("Help");

        backBtn.setImageResource(R.drawable.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cancelBtn.setOnClickListener(this);
        tutorialBtn.setOnClickListener(this);
        contactUsBtn.setOnClickListener(this);
        userAgreementBtn.setOnClickListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void cancelAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HelpActivity.this);
        builder.setMessage("Do you really want to close your account ?");
        builder.setTitle("Alert !");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                String user_id = PreferenceConnector.readString(HelpActivity.this, PreferenceConnector.LOGIN_UserId, "");

                final CancelAccountDao cancelAccountDao = new CancelAccountDao(user_id);
                cancelAccountDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        if (e == null && result != null) {
                            if (result.success.equals("1")) {
                                Toast.makeText(HelpActivity.this, getString(R.string.account_cancel), Toast.LENGTH_SHORT).show();
                                PreferenceConnector.clear(HelpActivity.this);
                                finish();
                                sendBroadcast(new Intent(cancel_receiver));
                                startActivity(new Intent(HelpActivity.this, ActivityFirst.class));
                            } else {
                                Toast.makeText(HelpActivity.this, result.message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HelpActivity.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cancel_account:
                cancelAccount();
                break;
            case R.id.tutorial:
                Toast.makeText(HelpActivity.this, "Coming soon.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_us:
                Toast.makeText(HelpActivity.this, "Coming soon.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_agreement:
                Toast.makeText(HelpActivity.this, "Coming soon.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
