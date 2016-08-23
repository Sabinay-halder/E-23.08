package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetDisLikedListDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.UnblockDao;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.ValidationTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 1/4/16.
 */
public class ActivityPeopleIBlock extends HideKeyActivity {
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    @Bind(R.id.text_content)
    TextView mContentText;
    ArrayList<String> aa;
    CustomAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_people);
        ButterKnife.bind(this);
        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("Blocked People");
        aa = new ArrayList<>();
        aa.add("Amanda");
        aa.add("Pamela");
        aa.add("Harry");


        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityPeopleIBlock.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Extension extension = new Extension();
        if (extension.executeStrategy(ActivityPeopleIBlock.this, "", ValidationTemplate.INTERNET)) {
            getBlockedPeopleList();
        } else {
            Toast.makeText(ActivityPeopleIBlock.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void getBlockedPeopleList() {
        String user_id = PreferenceConnector.readString(ActivityPeopleIBlock.this, PreferenceConnector.LOGIN_UserId, "");
        GetDisLikedListDao dao = new GetDisLikedListDao(user_id);
        dao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            mContentText.setText(result.data.size() + " people are Blocked by you !");
                            CustomAdapter adapter = new CustomAdapter(ActivityPeopleIBlock.this, result.data);
                            listview.setAdapter(adapter);
                        } else {
                            mContentText.setText("0 people are Blocked by you !");
                            listview.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(ActivityPeopleIBlock.this, result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityPeopleIBlock.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<GsonClass.HomeData> rowItems;
        private LayoutInflater mInflater;
        private ViewHolder holder;
        private AQuery aQuery;

        public CustomAdapter(Context context, ArrayList<GsonClass.HomeData> items) {
            this.context = context;
            this.rowItems = items;
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            aQuery = new AQuery(ActivityPeopleIBlock.this);
        }

        private class ViewHolder {
            TextView label;
            TextView paidBtn;
            ImageView button, userImage;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonClass.HomeData item = rowItems.get(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.blocked_user_list, parent, false);
                holder.label = (TextView) convertView.findViewById(R.id.user_name);
                holder.button = (ImageView) convertView.findViewById(R.id.button);
                holder.userImage = (ImageView) convertView.findViewById(R.id.user_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // holder.label.setSelected(true);

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLike(item.id);
                }
            });

            holder.label.setText(item.first_name);
            aQuery.id(holder.userImage).image(item.profile_pic, true, true, 100, R.drawable.com_facebook_profile_picture_blank_square);

            return convertView;
        }

        private void setLike(String id) {
            String user_id = PreferenceConnector.readString(ActivityPeopleIBlock.this, PreferenceConnector.LOGIN_UserId, "");
            UnblockDao likeDao = new UnblockDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    getBlockedPeopleList();
                    Toast.makeText(ActivityPeopleIBlock.this, result.message, Toast.LENGTH_SHORT).show();
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
}
