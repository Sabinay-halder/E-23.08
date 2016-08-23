package com.widevision.quemvaita.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Image storage database table..
 */
public class ImageStorage extends Model {


    @Column(name = "user_id")
    public String user_id;

    @Column(name = "profile_pic")
    public String profile_pic;

    @Column(name = "type")
    public String type;

    public ImageStorage() {
        super();
    }

    public ImageStorage(String user_id, String profile_pic, String type) {
        super();
        this.user_id = user_id;
        this.profile_pic = profile_pic;
        this.type = type;
    }
}
