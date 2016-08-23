package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetEventListDao;
import com.widevision.quemvaita.dao.GetMyEventListDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.JoinEventDao;
import com.widevision.quemvaita.model.HideKeyFragment;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.GPSTracker;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.Util;
import com.widevision.quemvaita.util.ValidationTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 1/4/16.
 */
public class FragmentEvents extends HideKeyFragment {
    private Extension extension;
    private String user_id = "";
    private ArrayList<GsonClass.HomeData> allEventList;
    private int isJoinVisible = View.GONE;
    private ArrayList<GsonClass.HomeData> myEventList;
    private GPSTracker gpsTracker;
    private Location location;

    @Bind(R.id.listview)
    ListView listview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_event, container, false);
        ButterKnife.bind(this, view);

       /* other_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                    other_events.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_backgroug));
                    my_event.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.layout_unselected_color));
                    other_events_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    my_event_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_backgroug));
                    select_events.setVisibility(View.VISIBLE);
                    below_mark_event.setVisibility(View.GONE);

                    all_img.setBackgroundResource(R.drawable.radio_btn_selected);
                    my_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                    choose_img.setBackgroundResource(R.drawable.radio_btn_unselected);

                    //get all event list
                    isJoinVisible = View.VISIBLE;
                    getEventListDao();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        my_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                    my_event.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_backgroug));
                    other_events.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.layout_unselected_color));
                    my_event_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    other_events_text.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_backgroug));
                    select_events.setVisibility(View.GONE);
                    below_mark_event.setVisibility(View.VISIBLE);
                    isJoinVisible = View.GONE;
                    getMyEventListDao();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                    if (location == null) {
                        new locationTask().execute();
                    }
                    new allEventTask().execute();
                    all_img.setBackgroundResource(R.drawable.radio_btn_selected);
                    my_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                    choose_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                    my_img.setBackgroundResource(R.drawable.radio_btn_selected);
                    all_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                    choose_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                    isJoinVisible = View.GONE;
                    getMyEventListDao();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                    choose_img.setBackgroundResource(R.drawable.radio_btn_selected);
                    my_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                    all_img.setBackgroundResource(R.drawable.radio_btn_unselected);
                    getEventListDao();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        extension = new Extension();
        user_id = PreferenceConnector.readString(getActivity(), PreferenceConnector.LOGIN_UserId, "");
        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
            getEventListDao();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        new locationTask().execute();
        return view;
    }

    private void getEventListDao() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetEventListDao listDao = new GetEventListDao(user_id);
        listDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        allEventList = result.data;
                        if (location == null) {
                            new locationTask().execute();
                        }
                        new allEventTask().execute();
                    } else {
                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*    private void getMyEventListDao() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetMyEventListDao listDao = new GetMyEventListDao(user_id);
        listDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            myEventList = result.data;
                            for (int i = 0; i < myEventList.size(); i++) {
                                GsonClass.HomeData item = myEventList.get(i);
                                if (item.published.equals("0")) {
                                    myEventList.remove(i);
                                    i--;
                                }
                            }
                            MyEventAdapter adapter = new MyEventAdapter(getActivity(), myEventList);
                            listview.setAdapter(adapter);
                        } else {
                            if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
                                //get all event list
                                isJoinVisible = View.VISIBLE;
                                getEventListDao();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    private class allEventTask extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... voids) {
            isJoinVisible = View.VISIBLE;
            if (allEventList != null && allEventList.size() != 0) {
                ArrayList<GsonClass.HomeData> joinList = new ArrayList();
                for (int i = 0; i < allEventList.size(); i++) {
                    if (allEventList.get(i).published.equals("0")) {
                        allEventList.remove(i);
                        i--;
                    } else if (allEventList.get(i).isjoin.equals("Going")) {
                        joinList.add(allEventList.get(i));
                        allEventList.remove(i);
                        i--;
                    }
                }

                if (location != null) {
                    Collections.sort(allEventList, new Comparator<GsonClass.HomeData>() {
                        @Override
                        public int compare(GsonClass.HomeData o1, GsonClass.HomeData o2) {
                            Location location1 = new Location("first");
                            Location location2 = new Location("second");
                            location1.setLatitude(Double.parseDouble(o1.lat));
                            location1.setLongitude(Double.parseDouble(o1.lng));
                            location2.setLatitude(Double.parseDouble(o2.lat));
                            location2.setLongitude(Double.parseDouble(o2.lng));
                            int aDist = Math.round(gpsTracker.getDistance(location1, location));
                            int bDist = Math.round(gpsTracker.getDistance(location2, location));
                            return aDist - bDist;
                        }
                    });
                }

                for (GsonClass.HomeData item : allEventList) {
                    joinList.add(item);
                }
                allEventList.clear();
                allEventList.addAll(joinList);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (allEventList != null && allEventList.size() != 0) {
                CustomAdapter adapter_all = new CustomAdapter(getActivity(), allEventList);
                listview.setAdapter(adapter_all);
            }
        }
    }

    private class CustomAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<GsonClass.HomeData> rowItems;
        private final LayoutInflater mInflater;
        private ViewHolder holder;
        private final String[] types = {"Going", "Interested", "Not Interested"};

        public CustomAdapter(Context context, ArrayList<GsonClass.HomeData> items) {
            this.context = context;
            this.rowItems = items;
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            private TextView label, event_date, event_description_txt;
            private TextView mJoinButton;
            private TextView type_txt;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonClass.HomeData item = rowItems.get(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.user_selected_events, null);
                holder.label = (TextView) convertView.findViewById(R.id.event_name);
                holder.event_date = (TextView) convertView.findViewById(R.id.event_date);
                holder.type_txt = (TextView) convertView.findViewById(R.id.type);
                holder.event_description_txt = (TextView) convertView.findViewById(R.id.event_description_txt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.label.setText(item.event_name);
            holder.event_date.setText(item.event_day);
            holder.type_txt.setText(item.isjoin);
            holder.event_description_txt.setText(item.event_description);

         /* holder.mJoinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mJoinButton.getText().toString().trim().equals("Join")) {
                        // join event functionality
                        item.isjoin = "1";
                        joinEvent(item.id);
                    }
                }
            });*/

            holder.type_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(item);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventUserListFragment eventUserListFragment = new EventUserListFragment();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    Bundle b = new Bundle();
                    b.putString("event_id", item.id);
                    b.putString("event_name", item.event_name);
                    eventUserListFragment.setArguments(b);
                    eventUserListFragment.show(fragmentManager, "EventUserListFragment");
                }
            });
            return convertView;
        }

        private void showPopup(final GsonClass.HomeData item) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_event_status);

            View.OnClickListener setTypeListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.action_cancle:
                            dialog.dismiss();
                            break;
                        case R.id.going:
                            dialog.dismiss();
                            joinEvent(item.id, "Going");
                            item.isjoin = "Going";
                            notifyDataSetChanged();
                            break;
                        case R.id.interested:
                            dialog.dismiss();
                            joinEvent(item.id, "Interested");
                            item.isjoin = "Interested";
                            notifyDataSetChanged();
                            break;
                        case R.id.not_interested:
                            dialog.dismiss();
                            joinEvent(item.id, "Not Interested");
                            item.isjoin = "Not Interested";
                            notifyDataSetChanged();
                            break;
                    }
                }
            };

            dialog.findViewById(R.id.action_cancle).setOnClickListener(setTypeListener);
            dialog.findViewById(R.id.going).setOnClickListener(setTypeListener);
            dialog.findViewById(R.id.interested).setOnClickListener(setTypeListener);
            dialog.findViewById(R.id.not_interested).setOnClickListener(setTypeListener);

            dialog.show();
        }


        private void joinEvent(String event_id, String status) {
            JoinEventDao joinEventDao = new JoinEventDao(user_id, event_id, status);
            joinEventDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    if (e == null && result != null) {
                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

/*    private class MyEventAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<GsonClass.HomeData> rowItems;
        private final LayoutInflater mInflater;
        private ViewHolder holder;

        public MyEventAdapter(Context context, ArrayList<GsonClass.HomeData> items) {
            this.context = context;
            this.rowItems = items;
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            private TextView label, event_date;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonClass.HomeData item = rowItems.get(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.user_selected_events_2, null);
                holder.label = (TextView) convertView.findViewById(R.id.event_name);
                holder.event_date = (TextView) convertView.findViewById(R.id.event_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            holder.label.setText(item.event_name);
            holder.event_date.setText(item.event_day);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventUserListFragment eventUserListFragment = new EventUserListFragment();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    Bundle b = new Bundle();
                    b.putString("event_id", item.id);
                    b.putString("event_name", item.event_name);
                    eventUserListFragment.setArguments(b);
                    eventUserListFragment.show(fragmentManager, "EventUserListFragment");
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }*/

    private class locationTask extends AsyncTask<Void, Void, Location> {
        @Override
        protected Location doInBackground(Void... voids) {
            gpsTracker = new GPSTracker();
            location = gpsTracker.getLocation(getActivity());
            return location;
        }
    }
}