package com.widevision.quemvaita.activity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.CancelAccountDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.database.DbHelper;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Constant;
import com.widevision.quemvaita.util.GPSTracker;
import com.widevision.quemvaita.util.PreferenceConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    @Bind(R.id.home)
    LinearLayout home;
    @Bind(R.id.my_profile)
    LinearLayout my_profile;
    @Bind(R.id.event)
    LinearLayout event;
    @Bind(R.id.chat)
    LinearLayout chat;
    @Bind(R.id.invite_friends)
    LinearLayout invite_friends;
    /*@Bind(R.id.like_in_facebook)
    LinearLayout like_in_facebook;*/
    @Bind(R.id.like_in_playstore)
    LinearLayout like_in_playstore;
    @Bind(R.id.people_i_like)
    LinearLayout people_i_like;
    @Bind(R.id.people_i_block)
    LinearLayout people_i_block;
    @Bind(R.id.help)
    LinearLayout help;
    @Bind(R.id.help_submenu)
    LinearLayout help_submenu;
    @Bind(R.id.cancel_account)
    LinearLayout cancel_account;
    @Bind(R.id.tutorial)
    LinearLayout tutorial;
    @Bind(R.id.contact_us)
    LinearLayout contact_us;
    @Bind(R.id.faq)
    LinearLayout faq;
    @Bind(R.id.user_agreement)
    LinearLayout user_agreement;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    @Bind(R.id.menu_logo)
    ImageView mMenuLogo;
    @Bind(R.id.chat_status)
    ImageView chatStatus;
    android.support.v4.app.FragmentManager fragmentManager;
    Fragment fragment = null;
    FragmentTransaction ft;
    private Socket mSocket;
    private String user_id = "";
    private DbHelper dbHelper;
    int onChat = 0;
    private Animation animation;
    private CancelAccountREceiver cancelAccountREceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuLogo.setVisibility(View.GONE);
        chatStatus.setVisibility(View.GONE);
        user_id = PreferenceConnector.readString(MainActivity.this, PreferenceConnector.LOGIN_UserId, "");

        animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
                mMenuLogo.setVisibility(View.GONE);
                mMenuLogo.clearAnimation();
            }
        });

        home.setOnClickListener(this);
        my_profile.setOnClickListener(this);
        event.setOnClickListener(this);
        chat.setOnClickListener(this);
        invite_friends.setOnClickListener(this);
        // like_in_facebook.setOnClickListener(this);
        like_in_playstore.setOnClickListener(this);
        people_i_like.setOnClickListener(this);
        people_i_block.setOnClickListener(this);
        help.setOnClickListener(this);
        cancel_account.setOnClickListener(this);
        tutorial.setOnClickListener(this);
        contact_us.setOnClickListener(this);
        faq.setOnClickListener(this);
        user_agreement.setOnClickListener(this);
        mMenuLogo.setOnClickListener(this);
        addFragment(FragmentHome.class, "Home");
        toolbar_image.setVisibility(View.VISIBLE);
        help_submenu.setVisibility(View.GONE);

        Quemvaita app = (Quemvaita) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("MostRecentMessage", onNewMessage);
        mSocket.on("status", status);
        mSocket.connect();

        dbHelper = DbHelper.getInstance();

        if (!Constant.isMyServiceRunning(GPSTracker.class, MainActivity.this)) {
                startService(new Intent(MainActivity.this, GPSTracker.class));
        }

        cancelAccountREceiver = new CancelAccountREceiver();
        registerReceiver(cancelAccountREceiver, new IntentFilter(HelpActivity.cancel_receiver));
    }


    @Override
    public void onClick(View view) {
        onChat = 0;
        Bundle translateBundle = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
        switch (view.getId()) {

            case R.id.home:
                help_submenu.setVisibility(View.GONE);
                addFragment(FragmentHome.class, "Home");
                toolbar_image.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.my_profile:
                help_submenu.setVisibility(View.GONE);
                // addFragment(FragmentMyProfile.class, "My Profile");
                toolbar_image.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();

                Intent i1 = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(i1, translateBundle);

                break;
            case R.id.event:
                help_submenu.setVisibility(View.GONE);
                addFragment(FragmentEvents.class, "Events");
                toolbar_image.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();

                break;
            case R.id.menu_logo:
            case R.id.chat:
                mMenuLogo.setVisibility(View.GONE);
                mMenuLogo.clearAnimation();
                help_submenu.setVisibility(View.GONE);
                addFragment(FragmentChat.class, "Chats");
                toolbar_image.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                chatStatus.setVisibility(View.GONE);
                onChat = 1;
                break;
            case R.id.invite_friends:
                help_submenu.setVisibility(View.GONE);
                addFragment(InviteFriends.class, "Invite Friends");
                toolbar_image.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.like_in_playstore:
                help_submenu.setVisibility(View.GONE);
                toolbar_image.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                openPlayStore();
                break;
            case R.id.people_i_like:
                help_submenu.setVisibility(View.GONE);
                Intent in = new Intent(MainActivity.this, ActivityPeopleILiked.class);
                startActivity(in, translateBundle);
                /*finish();*/
                mDrawerLayout.closeDrawers();
                break;
            case R.id.people_i_block:
                help_submenu.setVisibility(View.GONE);
                Intent i = new Intent(MainActivity.this, ActivityPeopleIBlock.class);
                startActivity(i, translateBundle);
                /*finish();*/
                mDrawerLayout.closeDrawers();
                break;
            case R.id.help:
                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(helpIntent, translateBundle);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.cancel_account:
                mDrawerLayout.closeDrawers();
                cancelAccount();
                break;
        }
    }

    private void cancelAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you really want to close your account ?");
        builder.setTitle("Alert !");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                String user_id = PreferenceConnector.readString(MainActivity.this, PreferenceConnector.LOGIN_UserId, "");
                CancelAccountDao cancelAccountDao = new CancelAccountDao(user_id);
                cancelAccountDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        if (e == null && result != null) {
                            Toast.makeText(MainActivity.this, result.message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
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

    private void openPlayStore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            Bundle translateBundle = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
            startActivity(i, translateBundle);
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void addFragment(Class activity, String tag) {
        Class fragmentClass;
        fragmentClass = activity;
        toolbar_text.setText(tag);
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.add(R.id.flContent, fragment).addToBackStack(tag).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("MostRecentMessage", onNewMessage);
        try {
            unregisterReceiver(cancelAccountREceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private final Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, args[0].toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data1 = (JSONArray) args[0];
                    try {
                        JSONObject data = data1.getJSONObject(0);

                        Log.e("", "Message Json from server" + data.toString());

                        String message = data.getString("message");
                        String sentBy = data.getString("sentBy");
                        String sentTo = data.getString("sentTo");
                        String timing = data.getString("timing");
                        String SenderName = data.getString("senderName");
                        Calendar calendar = Calendar.getInstance();
                        int h = calendar.get(Calendar.HOUR_OF_DAY);
                        int m = calendar.get(Calendar.MINUTE);
                        String time = h + ":" + m;
//sender panel
                        if (sentBy.equals("" + user_id) && sentTo.equals("" + FragmentChat.friend_id)) {
                            dbHelper.insertNewMessage(FragmentChat.friend_id, Constant.SENDER, message, time, 1, "0", timing);
                            Intent i = new Intent(Constant.ACTION_RECEIVER);
                            i.putExtra("data", "" + data.toString());
                            i.putExtra("time", "" + time);
                            i.putExtra("type", Constant.SENDER);
                            sendBroadcast(i);
                            PreferenceConnector.writeString(MainActivity.this, PreferenceConnector.USER_ID + "" + sentTo, message);
                            Intent i1 = new Intent(Constant.ACTION_RECEIVER_FRAGMENT);
                            sendBroadcast(i1);
                        } else if (sentTo.equals("" + user_id) && sentBy.equals("" + FragmentChat.friend_id)) {
                            //receiver panel
                            dbHelper.insertNewMessage(FragmentChat.friend_id, Constant.RECEIVER, message, time, 1, "0", timing);
                            Intent i = new Intent(Constant.ACTION_RECEIVER);
                            i.putExtra("data", "" + data.toString());
                            i.putExtra("time", "" + time);
                            i.putExtra("type", Constant.RECEIVER);
                            sendBroadcast(i);
                            PreferenceConnector.writeString(MainActivity.this, PreferenceConnector.USER_ID + "" + sentBy, message);
                            Intent i2 = new Intent(Constant.ACTION_RECEIVER_FRAGMENT);
                            sendBroadcast(i2);
                        } else if (sentTo.equals("" + user_id)) {
                            //   showNotification(message, SenderName, sentBy);
                            PreferenceConnector.writeString(MainActivity.this, PreferenceConnector.USER_ID + "" + sentBy, message);
                            int prevCountValue = PreferenceConnector.readInteger(MainActivity.this, PreferenceConnector.UNREAD_COUNT + "" + sentBy, 0);
                            PreferenceConnector.writeInteger(MainActivity.this, PreferenceConnector.UNREAD_COUNT + "" + sentBy, prevCountValue + 1);
                            Intent i = new Intent(Constant.ACTION_RECEIVER_FRAGMENT);
                            sendBroadcast(i);
                            dbHelper.insertNewMessage(Integer.parseInt(sentBy), Constant.RECEIVER, message, time, 1, "0", timing);

                            if (onChat != 1) {
                                mMenuLogo.setVisibility(View.VISIBLE);
                                chatStatus.setVisibility(View.VISIBLE);
                                mMenuLogo.setAnimation(animation);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };


    private void showNotification(String message, String user_name, String id) {

        Intent intent = new Intent(this, UserConversActivity.class);
        intent.putExtra("friend_id", id);
        intent.putExtra("friend_name", user_name);
        intent.putExtra("friend_profile", "");

            /*intent.putExtra("name", notificationTitle);
            intent.putExtra("receiver_id", s_id);*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(user_name);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification myNotication = builder.getNotification();


        manager.notify(Integer.parseInt(id), myNotication);
    }

    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 1) {
            finish();
        } else {
            super.onBackPressed();
            String name = fragmentManager.getBackStackEntryAt(count - 2).getName();
            toolbar_text.setText(name);
        }
    }

    private class CancelAccountREceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

}