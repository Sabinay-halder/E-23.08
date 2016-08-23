package com.widevision.quemvaita.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * database helper class
 */
public class DbHelper {

    private static volatile DbHelper instance = null;

    // private constructor
    private DbHelper() {
    }

    public static DbHelper getInstance() {
        if (instance == null) {
            synchronized (DbHelper.class) {
                // Double check
                if (instance == null) {
                    instance = new DbHelper();
                }
            }
        }
        return instance;
    }

    // insert image uri in table
    public void insertImage(String user_id, String profile_pic, String type) {
        ImageStorage imageStorage = new ImageStorage(user_id, profile_pic, type);
        imageStorage.save();
    }

    // get all stored images
    public List<ImageStorage> getImages(String user_id) {
        Select select = new Select();
        List<ImageStorage> imageStorages = select.from(ImageStorage.class).where("user_id = ?", user_id).execute();
        return imageStorages;
    }

    //remove image from database
    public void removeImage(String url) {
        List<ImageStorage> list = new Select().from(ImageStorage.class).where("profile_pic = ?", url).execute();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.get(i).delete();
        }
    }


    // insert every new message..
    public void insertNewMessage(int user_id, String message_type, String message_text, String message_time, int app_version, String unread_status, String message_date) {
        MessageListTable table = new MessageListTable(user_id, message_type, message_text, message_time, app_version, unread_status, message_date);
        table.save();
    }

    // for getting conversation of any friend
    public List<MessageListTable> getAllMessage(int user_id) {
        Select select = new Select();
        List<MessageListTable> list = select.from(MessageListTable.class).where("user_id = ?", user_id).execute();
        return list;
    }

    public List<MessageListTable> getMessages(int user_id, int limit) {
        ArrayList<MessageListTable> list = new ArrayList<>();
        try {
            SQLiteDatabase db = ActiveAndroid.getDatabase();
            //  String q = "select * from MessageListTable where user_id = " + user_id + " DESC limit 10";
            String q = "SELECT * FROM MessageListTable where user_id = " + user_id + " ORDER BY id DESC LIMIT " + limit;
            Cursor c = db.rawQuery(q, null);

            if (c != null && c.moveToFirst()) {
                do {
                    int id = c.getInt(c.getColumnIndex("user_id"));
                    String message_type = c.getString(c.getColumnIndex("message_type"));
                    String message_text = c.getString(c.getColumnIndex("message_text"));
                    String message_time = c.getString(c.getColumnIndex("message_time"));
                    int app_version = c.getInt(c.getColumnIndex("app_version"));
                    String unread_status = c.getString(c.getColumnIndex("unread_status"));
                    String message_date = c.getString(c.getColumnIndex("message_date"));
                    MessageListTable table = new MessageListTable(id, message_type, message_text, message_time, app_version, unread_status, message_date);
                    list.add(table);
                } while (c.moveToNext());
            }
            return list;
        } catch (Exception e) {
            return list;
        }
    }

}