package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.gson.Gson;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetEventUserListDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Constant;
import com.widevision.quemvaita.util.PreferenceConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mercury-five on 13/05/16.
 */
public class EventUserListFragment extends DialogFragment implements View.OnClickListener {

    @Bind(R.id.action_cancle)
    ImageView mCancleBtn;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.event_name)
    TextView event_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_user_list, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancleBtn.setOnClickListener(this);
        new task().execute();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.action_cancle:
                dismiss();
                break;
        }
    }

    private class task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Bundle b = getArguments();
            if (b != null) {
                String event_id = b.getString("event_id");
                final String name = b.getString("event_name");
                String user_id = PreferenceConnector.readString(getActivity(), PreferenceConnector.LOGIN_UserId, "");
                GetEventUserListDao getEventUserListDao = new GetEventUserListDao(user_id, event_id);
                getEventUserListDao.query(new AsyncCallback<GsonClass>() {
                    @Override
                    public void onOperationCompleted(GsonClass result, Exception e) {
                        if (e == null && result != null) {
                            if (result.success.equals("1")) {
                                if (result.data != null && result.data.size() != 0) {
                                    try {
                                        event_name.setText(name);
                                        MyEventAdapter adapter = new MyEventAdapter(getActivity(), result.data);
                                        mList.setAdapter(adapter);
                                        getView().findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    dismiss();
                                    Toast.makeText(getActivity(), "No member in this event.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                dismiss();
                                Toast.makeText(getActivity(), "No member in this event.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            dismiss();
                            Toast.makeText(getActivity(), "No member in this event.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            return null;
        }
    }

    private class MyEventAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<GsonClass.HomeData> rowItems;
        private LayoutInflater mInflater;
        private ViewHolder holder;
        private AQuery aQuery;

        public MyEventAdapter(Context context, ArrayList<GsonClass.HomeData> items) {

            this.context = context;
            this.rowItems = items;
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            aQuery = new AQuery(getActivity());

        }

        private class ViewHolder {
            private TextView user_name;
            private ImageView user_image;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonClass.HomeData item = rowItems.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.event_user_row, null);
                holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                holder.user_image = (ImageView) convertView.findViewById(R.id.user_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.user_name.setText(item.first_name + " " + item.last_name);
            aQuery.id(holder.user_image).image(item.profile_pic, true, true, 100, R.drawable.com_facebook_profile_picture_blank_square);

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
    }
}

