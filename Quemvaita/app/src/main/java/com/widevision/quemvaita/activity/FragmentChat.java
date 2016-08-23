package com.widevision.quemvaita.activity;


import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetFriendListDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.model.HideKeyFragment;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Constant;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.GPSTracker;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.ValidationTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 1/4/16.
 */
public class FragmentChat extends HideKeyFragment {
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.text_no_friend)
    TextView noFriendTxt;
    private CustomAdapter adapter;
    public static int friend_id;
    private ChatMessageReceiver receiver;
    private LocationReceiver locationReceiver;
    private Extension extension;
    private Location location;
    private GPSTracker gpsTracker;
    private String user_id = "";
    private ArrayList<GsonClass.HomeData> friendList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);
        ButterKnife.bind(this, view);
        extension = new Extension();
        user_id = PreferenceConnector.readString(getActivity(), PreferenceConnector.LOGIN_UserId, "");
        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
            getFriendList();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gpsTracker = new GPSTracker();
        location = gpsTracker.getLocation(getActivity());
    }

    private void getFriendList() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        GetFriendListDao getFriendListDao = new GetFriendListDao(user_id);
        getFriendListDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            new setAdapterTask().execute(result.data);
                        } else {
                            listview.setVisibility(View.GONE);
                            noFriendTxt.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "You have no friend. Please send request by click on like button on home screen.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        listview.setVisibility(View.GONE);
                        noFriendTxt.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    listview.setVisibility(View.GONE);
                    noFriendTxt.setVisibility(View.VISIBLE);
                    noFriendTxt.setText(getString(R.string.wrong));
                    Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private class setAdapterTask extends AsyncTask<ArrayList<GsonClass.HomeData>, Boolean, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(ArrayList<GsonClass.HomeData>... arrayLists) {
            ArrayList<GsonClass.HomeData> list = arrayLists[0];
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).sender_status.equals("0") || list.get(i).receiver_status.equals("0") || list.get(i).sender_status.equals("2") || list.get(i).receiver_status.equals("2")) {
                    list.remove(i);
                    i--;
                }
            }
            try {
                if (location != null) {
                    for (int j = 0; j < list.size(); j++) {
                        GsonClass.HomeData item = list.get(j);
                        String lat = item.lat;
                        String lng = item.lng;
                        if (lat != null && lng != null && !lat.isEmpty() && !lng.isEmpty()) {
                            Location location_friend = new Location("friend_location");
                            location_friend.setLatitude(Double.parseDouble(lat));
                            location_friend.setLongitude(Double.parseDouble(lng));
                            float distance = gpsTracker.getDistance(location, location_friend);
                            if (distance > 100) {
                                item.location_status = false;
                            } else {
                                item.location_status = true;
                            }
                        } else {
                            item.location_status = false;
                        }
                    }
                }
                friendList = list;
            } catch (Exception e1) {
                e1.printStackTrace();
                friendList = list;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            adapter = new CustomAdapter(getActivity());
            listview.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }

    private void updateList() {
        GetFriendListDao getFriendListDao = new GetFriendListDao(user_id);
        getFriendListDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                if (result != null && e == null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            new calculateDistance().execute(result.data);


                        }
                    }
                }
            }
        });
    }

    private class calculateDistance extends AsyncTask<ArrayList<GsonClass.HomeData>, ArrayList<GsonClass.HomeData>, Boolean> {
        @Override
        protected Boolean doInBackground(ArrayList<GsonClass.HomeData>... voids) {
            ArrayList<GsonClass.HomeData> list = voids[0];
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).sender_status.equals("0") || list.get(i).receiver_status.equals("0") || list.get(i).sender_status.equals("2") || list.get(i).receiver_status.equals("2")) {
                    list.remove(i);
                    i--;
                }
            }
            try {
                for (int j = 0; j < list.size(); j++) {
                    GsonClass.HomeData item = list.get(j);
                    String lat = item.lat;
                    String lng = item.lng;
                    if (lat != null && lng != null && !lat.isEmpty() && !lng.isEmpty()) {
                        Location location_friend = new Location("friend_location");
                        location_friend.setLatitude(Double.parseDouble(lat));
                        location_friend.setLongitude(Double.parseDouble(lng));
                        float distance = gpsTracker.getDistance(location, location_friend);
                        if (distance > 100) {
                            item.location_status = false;
                        } else {
                            item.location_status = true;
                        }
                    } else {
                        item.location_status = false;
                    }
                }
                friendList = list;

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class CustomAdapter extends BaseAdapter {
        private Context context;

        private AQuery aq;
        private LayoutInflater mInflater;
        private ViewHolder holder;

        public CustomAdapter(Context context) {
            this.context = context;

            aq = new AQuery(context);
            mInflater = getLayoutInflater(null);
        }

        private class ViewHolder {
            TextView user_name;
            ImageView message_image, status_image;
            TextView message_date;
            TextView message_content, unread_textView;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonClass.HomeData item = friendList.get(position);

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.chat_list, null);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.message_image = (ImageView) convertView.findViewById(R.id.message_image);
            holder.status_image = (ImageView) convertView.findViewById(R.id.status_img);
            holder.message_date = (TextView) convertView.findViewById(R.id.message_date);
            holder.message_content = (TextView) convertView.findViewById(R.id.message_content);
            holder.unread_textView = (TextView) convertView.findViewById(R.id.unread_textview);
            holder.message_date.setVisibility(View.GONE);
            convertView.setTag(holder);


            // holder.label.setSelected(true);
            holder.user_name.setText(item.first_name + " " + item.last_name);
            /*holder.message_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ActivityFullProfile.class);
                    startActivity(i);
                }
            });*/
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, UserConversActivity.class);
                    friend_id = Integer.parseInt(item.id);
                    i.putExtra("friend_id", "" + item.id);
                    i.putExtra("friend_name", "" + item.first_name + " " + item.last_name);
                    i.putExtra("friend_profile", "" + item.profile_pic);
                    Bundle translateBundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                    startActivity(i, translateBundle);
                }
            });

            aq.id(holder.message_image).image(item.profile_pic, true, true, 100, R.drawable.com_facebook_profile_picture_blank_square);

            String message = PreferenceConnector.readString(getActivity(), PreferenceConnector.USER_ID + "" + item.id, "");
            int count = PreferenceConnector.readInteger(getActivity(), PreferenceConnector.UNREAD_COUNT + "" + item.id, 0);
            if (message.isEmpty()) {
                holder.message_content.setVisibility(View.GONE);
            } else {
                holder.message_content.setText(message);
            }
            if (count != 0) {
                holder.unread_textView.setVisibility(View.VISIBLE);
                holder.unread_textView.setText("" + count);
            } else {
                holder.unread_textView.setVisibility(View.GONE);
            }
            if (item.location_status) {
                holder.status_image.setImageResource(R.drawable.circle_fill);
            } else {
                holder.status_image.setImageResource(R.drawable.circle_blank);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public Object getItem(int position) {
            return friendList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        friend_id = -1;
        receiver = new ChatMessageReceiver();
        locationReceiver = new LocationReceiver();
        //getActivity().registerReceiver(locationReceiver, new IntentFilter(Constant.ACTION_RECEIVER_LOCATION));
        getActivity().registerReceiver(receiver, new IntentFilter(Constant.ACTION_RECEIVER_FRAGMENT));

        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_RECEIVER_LOCATION);
        bManager.registerReceiver(locationReceiver, intentFilter);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(receiver);
            //  getActivity().unregisterReceiver(locationReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ChatMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if (b != null) {
                String lat = b.getString("lat");
                String lng = b.getString("lng");
                location.setLatitude(Double.parseDouble(lat));
                location.setLongitude(Double.parseDouble(lng));
                updateList();
            }
        }
    }
}