package com.widevision.quemvaita.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.model.HideKeyFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 1/4/16.
 */
public class InviteFriends extends HideKeyFragment {
    @Bind(R.id.twitter_login_button)
    ImageView twitter_login_button;
    @Bind(R.id.loginFacebookButton)
    ImageView facebook;
    @Bind(R.id.message_btn)
    Button mMessageBtn;
    @Bind(R.id.email_btn)
    Button mEmailBtn;

    private ShareDialog shareDialog;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friend, container, false);
        ButterKnife.bind(this, view);
        shareDialog = new ShareDialog(this);

        twitter_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                        .text("Please find this amazing app to check the upcoming events in our Church. " + getString(R.string.app_url));
                builder.show();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Hello Facebook")
                            .setContentDescription(
                                    "The 'Hello Facebook' sample  showcases simple Facebook integration")
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .build();

                    shareDialog.show(linkContent);
                }*/
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentTitle("Quemvaita App")
                        .setContentDescription("Please find this amazing app to check the upcoming events in our Church. " + getString(R.string.app_url))
                        .build();
                ShareDialog shareDialog = new ShareDialog(InviteFriends.this);
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
            }
        });

        mEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        mMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return view;
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Quemvaita App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find this amazing app to check the upcoming events in our Church. " + getString(R.string.app_url));

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "Please find this amazing app to check the upcoming events in our Church. " + getString(R.string.app_url));
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }
}
