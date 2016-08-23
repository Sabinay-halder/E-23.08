package com.widevision.quemvaita.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.View.StaggeredGridView;
import com.widevision.quemvaita.dao.GetUserGalleryDao;
import com.widevision.quemvaita.dao.GsonClassGallery;
import com.widevision.quemvaita.dao.UploadGalleryImageDao;
import com.widevision.quemvaita.database.DbHelper;
import com.widevision.quemvaita.database.ImageStorage;
import com.widevision.quemvaita.instagram.ApplicationData;
import com.widevision.quemvaita.instagram.InstagramApp;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.PreferenceConnector;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 6/4/16.
 */
public class ViewGalleryActivity extends HideKeyActivity {
    @Bind(R.id.gridview)
    StaggeredGridView gridview;
    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_photo);
        ButterKnife.bind(this);

        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("Gallery");

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String id = b.getString("id");
            getGalleryImages(id);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String user_id = PreferenceConnector.readString(ViewGalleryActivity.this, PreferenceConnector.LOGIN_UserId, "");
        //  imagesList = (ArrayList<ImageStorage>) dbHelper.getImages(user_id);
       /* adapter = new CustomAdapter(this, imagesList);
        gridview.setAdapter(adapter);*/
    }

    private class CustomAdapter extends BaseAdapter {

        Context context;
        ArrayList<GsonClassGallery.Data> rowItems;
        private AQuery aQuery;
        LayoutInflater mInflater;
        ViewHolder holder;

        public CustomAdapter(Context context, ArrayList<GsonClassGallery.Data> items) {
            this.context = context;
            this.rowItems = items;
            aQuery = new AQuery(ViewGalleryActivity.this);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {
            ImageView label;
            ImageView cross;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.add_my_photo_list, null);
            holder.label = (ImageView) convertView.findViewById(R.id.image);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
            });

            aQuery.id(holder.label).image(rowItems.get(position).imgurl);


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

    private void getGalleryImages(String user_id) {
        final ProgressDialog progressDialog = new ProgressDialog(ViewGalleryActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetUserGalleryDao galleryDao = new GetUserGalleryDao(user_id);
        galleryDao.query(new AsyncCallback<GsonClassGallery>() {
            @Override
            public void onOperationCompleted(GsonClassGallery result, Exception e) {
                if (result != null && e == null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {

                            int margin = getResources().getDimensionPixelSize(R.dimen.margin);
                            gridview.setItemMargin(margin); // set the GridView margin
                            gridview.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
                            CustomAdapter adapter = new CustomAdapter(ViewGalleryActivity.this, result.data);
                            gridview.setAdapter(adapter);
                        } else {
                            Toast.makeText(ViewGalleryActivity.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ViewGalleryActivity.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewGalleryActivity.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}