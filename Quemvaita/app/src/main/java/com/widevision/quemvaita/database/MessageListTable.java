package com.widevision.quemvaita.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-five on 22/04/16.
 */
@Table(name = "MessageListTable")
public class MessageListTable extends Model {

    /*id(message id) auto increment primary key,
user_id(Chat with user id),
message_type (Receive/Send),
message_text (message),
message_time (sending time),
app_version (application version),
unread_status integer (0/1),
message_date*/

    @Column(name = "user_id")
    public int user_id;

    @Column(name = "message_type")
    public String message_type;

    @Column(name = "message_text")
    public String message_text;

    @Column(name = "message_time")
    public String message_time;

    @Column(name = "app_version")
    public int app_version;

    @Column(name = "unread_status")
    public String unread_status;

    @Column(name = "message_date")
    public String message_date;

    public MessageListTable() {
        super();
    }

    public MessageListTable(int user_id, String message_type, String message_text, String message_time, int app_version, String unread_status, String message_date) {
        super();
        this.user_id = user_id;
        this.message_type = message_type;
        this.message_text = message_text;
        this.message_time = message_time;
        this.app_version = app_version;
        this.unread_status = unread_status;
        this.message_date = message_date;
    }

}
