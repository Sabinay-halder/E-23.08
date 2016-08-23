package com.widevision.quemvaita.model;

import android.content.Context;
import android.util.AttributeSet;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.widevision.quemvaita.R;

/**
 * Created by mercury-one on 26/4/16.
 */
public class TwitterCustomButton extends TwitterLoginButton {
    public TwitterCustomButton(Context context) {
        super(context);
        init();
    }

    public TwitterCustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TwitterCustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()){
            return;
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        setBackgroundResource(R.drawable.twitter);
        setTextSize(20);
        setText("");
        setPadding(30, 0, 10, 0);
        setTextColor(getResources().getColor(R.color.tw__blue_default));

    }
}
