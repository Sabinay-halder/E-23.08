package com.widevision.quemvaita.dao;

import java.util.ArrayList;

/**
 * Created by mercury-one on 7/4/16.
 */
public class GsonEventMemberClass {
    public String success;
    public String user_id;
    public String message;
    public String first_name;
    public String last_name;
    public String email;
    public String gender;
    public String profile_pic;
    public String facebook_id;
    public String date;

    public ArrayList<HomeData> data;


    /*"message": "Success",
    "user_id": "3",
    "first_name": "Akshay",
    "last_name": "Gupta",
    "email": "akshaygupta1323@hotmail.com",
    "gender": "male",
    "profile_pic": "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xpa1/v/t1.0-1/p50x50/13091884_242035879486861_717466266723947536_n.jpg?oh=cf97b1ca6669f6082edd77ec4f2bdebe&oe=57A39C97&__gda__=1471048516_3a632118ce7a06305f6a689bd2fc71d1",
    "facebook_id": "242646852759097",
    "date": "2016-04-28 19:16:49"*/
/* "about": "",
            "interest"*/

    public class HomeData {

        public String id;
        public String event_name;
        public String event_day;
        public String event_date;
        public String location;
        public String published;
        public String isjoin;
        public String lat;
        public String lng;
        public boolean location_status;

        public ArrayList<UserList> user_list;
    }

    public class UserList {
/*  "id": "11",
                    "first_name": "Sabinay",
                    "last_name": "Halder",
                    "email": "sabinay.halder@widevision.co.in",
                    "gender": "male",
                    "profile_pic": "http://103.231.44.154/newquemvaita/images/quemvaita/prc1464251847_profile_pic.png",
                    "facebook_id": "118857265189777",
                    "about": "testing update 12-05-2016 x\nxkd\nCJC\nCJC\ncj\ncmc\nckc\ncmc\nck",
                    "interest": "test ingbzs\ndjd\ndid\nDJ\ndid\ndid\nxjd\njx\nxmf\nckf\nck",
                    "device_type": "0",
                    "device_token": "",
                    "status": "active",
                    "date": "2016-05-26 14:07:27"
                    */

        public String id;
        public String first_name;
        public String last_name;
        public String email;
        public String gender;
        public String profile_pic;
        public String facebook_id;
        public String about;
        public String interest;
        public String device_type;
        public String device_token;
        public String status;
        public String date;
        public String receiver_status;
        public String sender_status;
        public String event_name;
        public String event_day;
        public String event_date;
        public String location;
        public String issent;
        public String published;
        public String isjoin;
        public String lat;
        public String lng;
        public boolean location_status;
    }

}