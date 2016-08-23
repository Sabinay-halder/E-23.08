package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.widevision.quemvaita.dao.GetFriendListDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.SetDislikeDao;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.ValidationTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 6/4/16.
 */
public class ActivityPeopleILiked extends HideKeyActivity {
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.text_content)
    TextView mTextContent;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    CustomAdapter adapter;
    private Extension extension;
    private String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_people);
        ButterKnife.bind(this);
        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("Liked People");
        extension = new Extension();

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityPeopleILiked.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        user_id = PreferenceConnector.readString(ActivityPeopleILiked.this, PreferenceConnector.LOGIN_UserId, "");
        if (extension.executeStrategy(ActivityPeopleILiked.this, "", ValidationTemplate.INTERNET)) {
            getFriendList();
        } else {
            Toast.makeText(ActivityPeopleILiked.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void getFriendList() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityPeopleILiked.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String user_id = PreferenceConnector.readString(ActivityPeopleILiked.this, PreferenceConnector.LOGIN_UserId, "");
        GetFriendListDao getFriendListDao = new GetFriendListDao(user_id);
        getFriendListDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    //  if (result.success.equals("1")) {
                    if (result.data != null && result.data.size() != 0) {
                        for (int i = 0; i < result.data.size(); i++) {
                            GsonClass.HomeData item = result.data.get(i);
                            if (!(item.receiver_status.equals("1") || item.sender_status.equals("1"))) {
                                result.data.remove(i);
                                i--;
                            }
                        }
                        mTextContent.setText(result.data.size() + " people Liked by you !");
                        CustomAdapter adapter = new CustomAdapter(ActivityPeopleILiked.this, result.data);
                        listview.setAdapter(adapter);
                    } else {
                        mTextContent.setText("0 people Liked by you !");
                    }
                    //  } else {
                    //       Toast.makeText(ActivityPeopleILiked.this, result.message, Toast.LENGTH_SHORT).show();
                    //   }
                } else {
                    Toast.makeText(ActivityPeopleILiked.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<GsonClass.HomeData> rowItems;
        private LayoutInflater mInflater;
        private ViewHolder holder;
        private AQuery aQuery;

        public CustomAdapter(Context context, ArrayList<GsonClass.HomeData> items) {
            this.context = context;
            this.rowItems = items;
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            aQuery = new AQuery(context);
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
                convertView = mInflater.inflate(R.layout.liked_user_list, parent, false);
                holder.label = (TextView) convertView.findViewById(R.id.user_name);
                holder.button = (ImageView) convertView.findViewById(R.id.button);
                holder.userImage = (ImageView) convertView.findViewById(R.id.user_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.label.setText(item.first_name);

            aQuery.id(holder.userImage).image(item.profile_pic, true, true, 100, R.drawable.com_facebook_profile_picture_blank_square);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDisLike(item.id);
                }
            });

            //  holder.label.setText(rowItems.get(position));
            return convertView;
        }

        private void setDisLike(String id) {

            SetDislikeDao likeDao = new SetDislikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    getFriendList();
                    Toast.makeText(ActivityPeopleILiked.this, result.message, Toast.LENGTH_SHORT).show();
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
