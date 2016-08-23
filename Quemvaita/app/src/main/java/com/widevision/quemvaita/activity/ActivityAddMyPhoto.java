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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.dao.GetUserGalleryDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonClassGallery;
import com.widevision.quemvaita.dao.UpdateGalleryDao;
import com.widevision.quemvaita.dao.UploadGalleryImageDao;
import com.widevision.quemvaita.database.DbHelper;
import com.widevision.quemvaita.database.ImageStorage;
import com.widevision.quemvaita.dndGrid.DNDAdapter;
import com.widevision.quemvaita.dndGrid.DNDGridView;
import com.widevision.quemvaita.dndGrid.DNDViewHolder;
import com.widevision.quemvaita.instagram.ApplicationData;
import com.widevision.quemvaita.instagram.InstagramApp;
import com.widevision.quemvaita.loader.ImageLoader;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Constant;
import com.widevision.quemvaita.util.PreferenceConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 6/4/16.
 */
public class ActivityAddMyPhoto extends HideKeyActivity {
    @Bind(R.id.gridview)
    DNDGridView gridview;
    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_right_image)
    ImageView toolbar_right_image;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    @Bind(R.id.update_gallery)
    Button update_gallery;
    private final int PICK_FROM_CAMERA = 2;
    private final int PICK_FROM_FILE = 1;

    private Bitmap realImage;
    private int c;
    private int width;
    private InstagramApp mApp;
    private DbHelper dbHelper;
    private ArrayList<ImageStorage> imagesList;
    public static final String URL = "url";
    public static final String FILE = "file";
    private String pos = "";
    private ArrayList<GsonClassGallery.Data> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_photo);
        ButterKnife.bind(this);

        dbHelper = DbHelper.getInstance();
        width = this.getResources().getDisplayMetrics().widthPixels;

        toolbar_right_image.setVisibility(View.GONE);
        toolbar_right_image.setImageResource(R.drawable.add_btn);
        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("Add my photo");

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar_right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ActivityAddMyPhoto.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.add_photo_dialog);
                dialog.setCanceledOnTouchOutside(true);
                TextView choose_gallery = (TextView) dialog.findViewById(R.id.choose_gallery);
                TextView choose_camera = (TextView) dialog.findViewById(R.id.choose_camera);
                TextView choose_facebook = (TextView) dialog.findViewById(R.id.choose_facebook);
                TextView choose_instagram = (TextView) dialog.findViewById(R.id.choose_instagram);
                ImageView cross1 = (ImageView) dialog.findViewById(R.id.cross);
                choose_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent, PICK_FROM_FILE);
                    }
                });

                choose_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(Environment.getExternalStorageDirectory(), "profilepic.png");
                        // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }
                });

                cross1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                choose_instagram.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        mApp = new InstagramApp(ActivityAddMyPhoto.this, ApplicationData.CLIENT_ID,
                                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
                        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

                            @Override
                            public void onSuccess() {
                                // tvSummary.setText("Connected as " + mApp.getUserName());

                                // userInfoHashmap = mApp.
                                Toast.makeText(ActivityAddMyPhoto.this, "success", Toast.LENGTH_SHORT).show();
                                new getInstagramPhotosTask().execute();
                            }

                            @Override
                            public void onFail(String error) {
                                Toast.makeText(ActivityAddMyPhoto.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        /*if (mApp.hasAccessToken()) {
                            new getInstagramPhotosTask().execute();
                        } else {*/
                        mApp.authorize();
                       /* }*/
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });

        update_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final ProgressDialog progressDialog = new ProgressDialog(ActivityAddMyPhoto.this);
                    progressDialog.setMessage("Updating...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < rowItems.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("position", "" + (i + 1));
                        jsonObject.put("imgurl", rowItems.get(i).imgurl);
                        jsonArray.put(jsonObject);
                    }
                    String user_id = PreferenceConnector.readString(ActivityAddMyPhoto.this, PreferenceConnector.LOGIN_UserId, "");
                    UpdateGalleryDao updateGalleryDao = new UpdateGalleryDao(user_id, jsonArray.toString());
                    updateGalleryDao.query(new AsyncCallback<GsonClass>() {
                        @Override
                        public void onOperationCompleted(GsonClass result, Exception e) {
                            progressDialog.dismiss();
                            if (result != null && e == null) {
                                if (result.success.equals("1")) {
                                    Toast.makeText(ActivityAddMyPhoto.this, "Update successful.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ActivityAddMyPhoto.this, "Failed to update gallery.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String user_id = PreferenceConnector.readString(ActivityAddMyPhoto.this, PreferenceConnector.LOGIN_UserId, "");
        //  imagesList = (ArrayList<ImageStorage>) dbHelper.getImages(user_id);
       /* adapter = new CustomAdapter(this, imagesList);
        gridview.setAdapter(adapter);*/
        getGalleryImages();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    /*Take photos from instagram*/
    class getInstagramPhotosTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        JSONArray object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityAddMyPhoto.this);
            progressDialog.setMessage("Loading photos...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL example = new URL("https://api.instagram.com/v1/users/self/media/recent?access_token=" + mApp.getTOken());

                URLConnection tc = example.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    JSONObject ob = new JSONObject(line);

                    object = ob.getJSONArray("data");
                    /*Log.e("", "data" + object.toString());
                    for (int i = 0; i < object.length(); i++) {
                        JSONObject jo = (JSONObject) object.get(i);
                        JSONObject images = jo.getJSONObject("images");
                        JSONObject standard_resolution = images.getJSONObject("standard_resolution");
                        String url = standard_resolution.getString("url");

                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            startActivity(new Intent(ActivityAddMyPhoto.this, InstagramDialogActivity.class).putExtra("images", object.toString()).putExtra("pos", pos));
        }
    }

    private class CustomAdapter extends DNDAdapter {

        Context context;

        private AQuery aQuery;
        LayoutInflater mInflater;
        ViewHolder holder;
        private final GridView.LayoutParams layoutParams;
        private ImageLoader imageLoader;

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public CustomAdapter(Context context) {
            super(context, R.layout.add_my_photo_list);
            this.context = context;

            aQuery = new AQuery(ActivityAddMyPhoto.this);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            layoutParams = new GridView.LayoutParams((Constant.width / 3) - 5, (Constant.width / 3) + 10);
            setCustomArray(rowItems);
            imageLoader = new ImageLoader(ActivityAddMyPhoto.this);
        }

        private class ViewHolder extends DNDViewHolder {
            public ViewHolder(int posistion) {
                super(posistion);
            }

            ImageView label;
            ImageView addBtn;
            RelativeLayout buttomLayout;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            holder = new ViewHolder(position);
            convertView = mInflater.inflate(R.layout.add_my_photo_list, null);

            holder.label = (ImageView) convertView.findViewById(R.id.image);
            holder.addBtn = (ImageView) convertView.findViewById(R.id.add_image);
            holder.buttomLayout = (RelativeLayout) convertView.findViewById(R.id.btm);
            convertView.setLayoutParams(layoutParams);
            holder.addBtn.setVisibility(View.VISIBLE);
            holder.buttomLayout.setVisibility(View.VISIBLE);
            aQuery.id(holder.label).image(rowItems.get(position).imgurl, true, true, (Constant.width / 3), R.drawable.cdefault);
            //aQuery.id(holder.label).image(rowItems.get(position).imgurl, true, true, 0, 0, null, AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
            // imageLoader.DisplayImage(rowItems.get(position).imgurl, holder.label);
            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = "" + (position + 1);
                    viewDialog();
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!rowItems.get(position).imgurl.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddMyPhoto.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Do you want to set as a profile picture ?");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                EditProfileActivity.mPhotoPath = rowItems.get(position).imgurl;
                                EditProfileActivity.FILE_TYPE = URL;
                                finish();
                                Toast.makeText(ActivityAddMyPhoto.this, "You need to click on 'Update Profile' button for upload profile picture.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.create().show();
                    }
                }
            });

            setUpDragNDrop(position, convertView);

            return convertView;
        }


        private void viewDialog() {
            final Dialog dialog = new Dialog(ActivityAddMyPhoto.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.add_photo_dialog);
            dialog.setCanceledOnTouchOutside(true);
            TextView choose_gallery = (TextView) dialog.findViewById(R.id.choose_gallery);
            TextView choose_camera = (TextView) dialog.findViewById(R.id.choose_camera);
            TextView choose_facebook = (TextView) dialog.findViewById(R.id.choose_facebook);
            TextView choose_instagram = (TextView) dialog.findViewById(R.id.choose_instagram);
            ImageView cross1 = (ImageView) dialog.findViewById(R.id.cross);
            choose_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(intent, PICK_FROM_FILE);
                }
            });

            choose_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "profilepic.png");
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, PICK_FROM_CAMERA);
                }
            });

            cross1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            choose_instagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    mApp = new InstagramApp(ActivityAddMyPhoto.this, ApplicationData.CLIENT_ID,
                            ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
                    mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

                        @Override
                        public void onSuccess() {
                            // tvSummary.setText("Connected as " + mApp.getUserName());

                            // userInfoHashmap = mApp.
                            Toast.makeText(ActivityAddMyPhoto.this, "success", Toast.LENGTH_SHORT).show();
                            new getInstagramPhotosTask().execute();
                        }

                        @Override
                        public void onFail(String error) {
                            Toast.makeText(ActivityAddMyPhoto.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                        /*if (mApp.hasAccessToken()) {
                            new getInstagramPhotosTask().execute();
                        } else {*/
                    mApp.authorize();
                       /* }*/
                }
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void notifyData() {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String user_id = PreferenceConnector.readString(ActivityAddMyPhoto.this, PreferenceConnector.LOGIN_UserId, "");
            switch (requestCode) {
                case PICK_FROM_FILE:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Uri uri = data.getData();
                            try {
                                EditProfileActivity.mPhotoPath = getRealPathFromURI(uri);
                                try {
                                    EditProfileActivity.FILE_TYPE = FILE;
                                  /*  dbHelper.insertImage(user_id, EditProfileActivity.mPhotoPath, FILE);
                                    ImageStorage imageStorage = new ImageStorage(user_id, EditProfileActivity.mPhotoPath, FILE);
                                    imagesList.add(imageStorage);
                                    adapter.notifyDataSetChanged();*/
                                    //finish();

                                    uploadImage(EditProfileActivity.mPhotoPath);

                                    // label.setImageBitmap(realImage);
                                    // mProfileImage.setImageBitmap(realImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    break;

                case PICK_FROM_CAMERA:

                    if (resultCode == Activity.RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        //    mProfieImage.setImageBitmap(photo);

                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Uri tempUri = getImageUri(getApplicationContext(), photo);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        EditProfileActivity.mPhotoPath = getRealPathFromURI2(tempUri);
                        try {
                            /*dbHelper.insertImage(user_id, EditProfileActivity.mPhotoPath, FILE);
                            ImageStorage imageStorage = new ImageStorage(user_id, EditProfileActivity.mPhotoPath, FILE);
                            imagesList.add(imageStorage);
                            adapter.notifyDataSetChanged();*/
                            uploadImage(EditProfileActivity.mPhotoPath);
                            EditProfileActivity.FILE_TYPE = FILE;
                            //  finish();
                           /* File fs = new File(EditProfileActivity.mPhotoPath);
                            FileInputStream fileInputStream = new FileInputStream(fs);
                            realImage = BitmapFactory.decodeStream(fileInputStream);*/
                            // mProfileImage.setImageBitmap(realImage);
                            // label.setImageBitmap(realImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI2(Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void uploadImage(String imagePath) {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityAddMyPhoto.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String user_id = PreferenceConnector.readString(ActivityAddMyPhoto.this, PreferenceConnector.LOGIN_UserId, "");
        UploadGalleryImageDao imageDao = new UploadGalleryImageDao(user_id, new File(imagePath), pos);
        imageDao.query(new AsyncCallback<GsonClassGallery>() {
            @Override
            public void onOperationCompleted(GsonClassGallery result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        getGalleryImages();
                        Toast.makeText(ActivityAddMyPhoto.this, result.message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ActivityAddMyPhoto.this, result.message, Toast.LENGTH_SHORT).show();
                    }

                    if (result.data != null && result.data.size() != 0) {
                        rowItems = result.data;
                        CustomAdapter adapter = new CustomAdapter(ActivityAddMyPhoto.this);
                        gridview.setAdapter(adapter);
                    } else {
                        Toast.makeText(ActivityAddMyPhoto.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void getGalleryImages() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityAddMyPhoto.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String user_id = PreferenceConnector.readString(ActivityAddMyPhoto.this, PreferenceConnector.LOGIN_UserId, "");
        GetUserGalleryDao galleryDao = new GetUserGalleryDao(user_id);
        galleryDao.query(new AsyncCallback<GsonClassGallery>() {
            @Override
            public void onOperationCompleted(GsonClassGallery result, Exception e) {
                if (result != null && e == null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {

                            int margin = getResources().getDimensionPixelSize(R.dimen.margin);
                            // set the GridView margin
                            gridview.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
                            rowItems = result.data;
                            CustomAdapter adapter = new CustomAdapter(ActivityAddMyPhoto.this);
                            gridview.setAdapter(adapter);
                        } else {
                            Toast.makeText(ActivityAddMyPhoto.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityAddMyPhoto.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityAddMyPhoto.this, getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}