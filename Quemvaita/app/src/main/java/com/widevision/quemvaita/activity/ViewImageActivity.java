package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.View.TouchImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-five on 20/05/16.
 */
public class ViewImageActivity extends Activity {

    @Bind(R.id.image)
    TouchImageView imageView;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.toolbar_image)
    LinearLayout toolbar_image;
    @Bind(R.id.icon_menu)
    ImageView icon_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image_activity);
        ButterKnife.bind(this);

        icon_menu.setImageResource(R.drawable.back_btn);
        toolbar_text.setText("View Photo");

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String image_url = b.getString("image_url");
            AQuery aQuery = new AQuery(ViewImageActivity.this);
            aQuery.id(imageView).image(image_url);
        }
    }
}