package com.widevision.quemvaita.util;



/**
 * Created by mercury-five on 21/01/16.
 */
public class CountryCodeHelper {
    /*private static final String DATABASE_TABLE_COUNTRYCODE = "country_code";

    public ArrayList<CountryBean> fetchCode() {
        ArrayList<CountryBean> list = new ArrayList<>();
        try {
            Cursor c = ActiveAndroid.getDatabase().rawQuery("select * from " + DATABASE_TABLE_COUNTRYCODE, null);
            if (c != null && c.moveToFirst()) {
                do {
                    CountryBean item = new CountryBean();
                    item.setCountryCode(c.getString(c.getColumnIndex("code")));
                    item.setCountryName(c.getString(c.getColumnIndex("country_name")));
                    list.add(item);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }*/
}
