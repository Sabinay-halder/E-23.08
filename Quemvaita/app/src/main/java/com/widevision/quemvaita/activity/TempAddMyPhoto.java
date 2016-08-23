package com.widevision.quemvaita.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widevision.quemvaita.R;
import com.widevision.quemvaita.model.HideKeyActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 12/4/16.
 */
public class TempAddMyPhoto extends HideKeyActivity {
    @Bind(R.id.icon_menu) ImageView icon_menu;
    @Bind(R.id.toolbar_right_image) ImageView toolbar_right_image;
    @Bind(R.id.image) ImageView image;
    @Bind(R.id.cross) ImageView cross;
    @Bind(R.id.toolbar_text)TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    private final int PICK_FROM_CAMERA = 2;
    private final int PICK_FROM_FILE = 1;
    private String mPhotoPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_activity_add_photo);
        ButterKnife.bind(this);
        toolbar_right_image.setVisibility(View.VISIBLE);
        toolbar_right_image.setImageResource(R.drawable.select_icon);
        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("Add my photo");
        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TempAddMyPhoto.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(TempAddMyPhoto.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_photo_dialog);
                dialog.setCanceledOnTouchOutside(true);

                TextView choose_gallery = (TextView) dialog.findViewById(R.id.choose_gallery);
                TextView choose_camera = (TextView) dialog.findViewById(R.id.choose_camera);
                TextView choose_facebook = (TextView) dialog.findViewById(R.id.choose_facebook);
                TextView choose_instagram = (TextView) dialog.findViewById(R.id.choose_instagram);
                ImageView cross = (ImageView) dialog.findViewById(R.id.cross);
                choose_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(Intent.createChooser(intent, "Complete action using..."), PICK_FROM_FILE);
                    }
                });
                choose_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(Environment.getExternalStorageDirectory(), "profilepic.png");
                        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }
                });
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


                dialog.show();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case PICK_FROM_FILE:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Uri uri = data.getData();
                            try {
                                mPhotoPath = getRealPathFromURI(uri);
                                try {
                                    File fs = new File(mPhotoPath);
                                    FileInputStream fileInputStream = new FileInputStream(fs);
                                    Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);

                                     image.setImageBitmap(realImage);
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
                        mPhotoPath = getRealPathFromURI2(tempUri);
                        try {
                            File fs = new File(mPhotoPath);
                            FileInputStream fileInputStream = new FileInputStream(fs);
                           Bitmap realImage = BitmapFactory.decodeStream(fileInputStream);
                             image.setImageBitmap(realImage);
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
}
