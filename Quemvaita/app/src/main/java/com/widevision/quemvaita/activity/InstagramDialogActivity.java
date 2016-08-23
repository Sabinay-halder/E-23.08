package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonClassGallery;
import com.widevision.quemvaita.dao.UploadGalleryImageDao;
import com.widevision.quemvaita.database.DbHelper;
import com.widevision.quemvaita.database.ImageStorage;
import com.widevision.quemvaita.instagram.InstagramDialog;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.PreferenceConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-five on 29/04/16.
 */
public class InstagramDialogActivity extends Activity {

    private GridView gridView;
    private ArrayList<String> imagesList = new ArrayList<>();
    private ArrayList<Boolean> selectedList = new ArrayList<>();
    int width;
    @Bind(R.id.toolbar_right_image)
    ImageView done;
    @Bind(R.id.icon_menu)
    ImageView menuIcon;
    @Bind(R.id.toolbar_text)
    TextView titleTxt;
    private DbHelper dbHelper;
    private int count = 0;
    private ProgressDialog progressDialog;
    private String pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instagram_dialog_activity);
        ButterKnife.bind(this);
        titleTxt.setText("Select Images");
        done.setVisibility(View.VISIBLE);
        menuIcon.setVisibility(View.GONE);
        done.setImageResource(R.drawable.select_icon);
        done.setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.gridview);
        width = this.getResources().getDisplayMetrics().widthPixels;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String images = b.getString("images");
            pos = b.getString("pos");
            try {

                JSONArray object = new JSONArray(images);
                for (int i = 0; i < object.length(); i++) {
                    JSONObject jo = (JSONObject) object.get(i);
                    JSONObject image = jo.getJSONObject("images");
                    JSONObject standard_resolution = image.getJSONObject("standard_resolution");
                    String url = standard_resolution.getString("url");
                    imagesList.add(url);
                    selectedList.add(false);
                }
                CustomAdapter customAdapter = new CustomAdapter(InstagramDialogActivity.this, imagesList);
                gridView.setAdapter(customAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String user_id = PreferenceConnector.readString(InstagramDialogActivity.this, PreferenceConnector.LOGIN_UserId, "");
                //dbHelper = DbHelper.getInstance();
                progressDialog = new ProgressDialog(InstagramDialogActivity.this);
                progressDialog.setMessage("Uploading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                for (int j = 0; j < selectedList.size(); j++) {
                    if (selectedList.get(j)) {
                        //dbHelper.insertImage(user_id, imagesList.get(j), ActivityAddMyPhoto.URL);
                        count = count + 1;
                    }
                }
                if (count == 0) {
                    progressDialog.dismiss();
                    finish();
                } else {
                    for (int j = 0; j < selectedList.size(); j++) {
                        if (selectedList.get(j)) {
                            //dbHelper.insertImage(user_id, imagesList.get(j), ActivityAddMyPhoto.URL);
                            uploadImage(imagesList.get(j));
                        }
                    }
                }
            }
        });
    }

    private void uploadImage(String imagePath) {
        final ProgressDialog progressDialog = new ProgressDialog(InstagramDialogActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String user_id = PreferenceConnector.readString(InstagramDialogActivity.this, PreferenceConnector.LOGIN_UserId, "");
        UploadGalleryImageDao imageDao = new UploadGalleryImageDao(user_id, imagePath, pos);
        imageDao.query(new AsyncCallback<GsonClassGallery>() {
            @Override
            public void onOperationCompleted(GsonClassGallery result, Exception e) {
                progressDialog.dismiss();
                finish();
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> rowItems;
        private AbsListView.LayoutParams layoutParams;
        private AQuery aQuery;
        private ViewHolder holder;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, ArrayList<String> items) {
            this.context = context;
            this.rowItems = items;
            layoutParams = new AbsListView.LayoutParams((width / 3) - 5, (width / 3));
            aQuery = new AQuery(InstagramDialogActivity.this);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {

            ImageView label;
            ImageView cross, check_image;
            RelativeLayout backgroud;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.insta_row, null);
                convertView.setLayoutParams(layoutParams);
                holder.label = (ImageView) convertView.findViewById(R.id.image);
                holder.backgroud = (RelativeLayout) convertView.findViewById(R.id.back);
                holder.check_image = (ImageView) convertView.findViewById(R.id.check_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //  holder.cross = (ImageView) convertView.findViewById(R.id.cross);

            aQuery.id(holder.label).image(rowItems.get(position));

            holder.label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* if (selectedList.get(position)) {
                        selectedList.set(position, false);
                        holder.check_image.setVisibility(View.GONE);
                    } else {
                        selectedList.set(position, true);
                        holder.check_image.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();*/
                    AlertDialog.Builder builder = new AlertDialog.Builder(InstagramDialogActivity.this);
                    builder.setMessage("Do you want to add this in gallery?");
                    builder.setCancelable(false);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            uploadImage(rowItems.get(position));
                        }
                    });
                    builder.create().show();
                }
            });

            if (selectedList.get(position)) {
                holder.check_image.setVisibility(View.VISIBLE);
            } else {
                holder.check_image.setVisibility(View.GONE);
            }
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
