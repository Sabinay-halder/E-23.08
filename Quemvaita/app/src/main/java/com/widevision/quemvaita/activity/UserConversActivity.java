package com.widevision.quemvaita.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.twitter.sdk.android.core.models.User;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.database.DbHelper;
import com.widevision.quemvaita.database.MessageListTable;
import com.widevision.quemvaita.model.HideKeyActivity;
import com.widevision.quemvaita.util.Constant;
import com.widevision.quemvaita.util.PreferenceConnector;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import io.socket.client.Socket;

/**
 * Created by mercury-one on 12/4/16.
 */
public class UserConversActivity extends HideKeyActivity {

    @Bind(R.id.icon_menu)
    ImageView icon_menu;
    @Bind(R.id.toolbar_text)
    TextView toolbar_text;
    @Bind(R.id.profile_view)
    ImageView mFrinedProfileView;
    @Bind(R.id.emojicon_edit_text)
    EmojiconEditText emojicon_edit_text;
    /* @Bind(R.id.chat_layout)
     LinearLayout chat_layout;
     @Bind(R.id.mike)
     LinearLayout mike;
     @Bind(R.id.smile_layout)
     LinearLayout smile_layout;*/
    @Bind(R.id.relative_main)
    RelativeLayout relative_main;
    /*@Bind(R.id.music)
    LinearLayout music;*/
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.smile_button)
    ImageView mSmileBtn;
    @Bind(R.id.sendBtn)
    ImageView mSendBtn;
    CustomAdapter adapter;

    private CharBroadcastReceiver receiver;
    private ArrayList<MessageListTable> listAllMessage = new ArrayList<>();
    private ArrayList<MessageListTable> list = new ArrayList<>();
    private DbHelper dbHelper;
    private String friend_id = "", friend_name = "", friend_profile = "";
    private String user_id;
    private Socket mSocket;
    private int count = 1;
    private int messagesCount = 0;
    private boolean can_load_more = false;
    private int list_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_conversation);
        ButterKnife.bind(this);
        icon_menu.setImageResource(R.drawable.back_btn);

        icon_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(UserConversACtivity.this, MainActivity.class);
                startActivity(i);*/
                finish();
            }
        });


        Bundle b = getIntent().getExtras();
        if (b != null) {
            friend_id = b.getString("friend_id");
            friend_name = b.getString("friend_name");
            friend_profile = b.getString("friend_profile");
            toolbar_text.setText(friend_name);
            AQuery aQuery = new AQuery(UserConversActivity.this);
            aQuery.id(mFrinedProfileView).image(friend_profile, true, true, 100, R.drawable.com_facebook_profile_picture_blank_square);
            FragmentChat.friend_id = Integer.parseInt(friend_id);
            PreferenceConnector.writeInteger(UserConversActivity.this, PreferenceConnector.UNREAD_COUNT + "" + friend_id, 0);
        }

        user_id = PreferenceConnector.readString(UserConversActivity.this, PreferenceConnector.LOGIN_UserId, "");
        Quemvaita app = (Quemvaita) getApplication();
        mSocket = app.getSocket();
        dbHelper = DbHelper.getInstance();

// keybord send button listener...
        ((EmojiconEditText) findViewById(R.id.emojicon_edit_text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String message = ((EmojiconEditText) findViewById(R.id.emojicon_edit_text)).getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendSms(message);
                        ((EmojiconEditText) findViewById(R.id.emojicon_edit_text)).setText("");
                    }
                    handled = true;
                }
                return handled;
            }
        });
        ((EmojiconEditText) findViewById(R.id.emojicon_edit_text)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                listview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (list != null && list.size() != 0) {
                            listview.setSelection(adapter.getCount() - 1);
                        }
                    }
                }, 100);
                return false;
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = ((EmojiconEditText) findViewById(R.id.emojicon_edit_text)).getText().toString();
                if (!message.isEmpty()) {
                    sendSms(message);
                    ((EmojiconEditText) findViewById(R.id.emojicon_edit_text)).setText("");
                }
            }
        });
        new getMessageTask().execute();

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(findViewById(R.id.relative_main), this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(mSmileBtn, R.drawable.smile_icon);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojicon_edit_text == null || emojicon == null) {
                    return;
                }
                int start = emojicon_edit_text.getSelectionStart();
                int end = emojicon_edit_text.getSelectionEnd();
                if (start < 0) {
                    emojicon_edit_text.append(emojicon.getEmoji());
                } else {
                    emojicon_edit_text.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojicon_edit_text.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        mSmileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(UserConversActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                /*//If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        //keybord
                        changeEmojiKeyboardIcon(mSmileBtn, R.drawable.smile_icon);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojicon_edit_text.setFocusableInTouchMode(true);
                        emojicon_edit_text.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojicon_edit_text, InputMethodManager.SHOW_IMPLICIT);
                        //keybord
                        changeEmojiKeyboardIcon(mSmileBtn, R.drawable.smile_icon);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }*/
            }
        });
    }


    private void sendSms(String message) {
        // perform the sending message attempt.
/*{'SentBy':sender.value, 'SentTo':receiver.value, 'Message': self.value}*/
        try {

            Log.e("Message", "Message >  " + message);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sentBy", "" + user_id);
            jsonObject.put("sentTo", "" + friend_id);
            jsonObject.put("message", message);
            mSocket.emit("send", jsonObject);

            Log.e("", "Message Json " + jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get message from local database and set adapter...
    private class getMessageTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UserConversActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            list = (ArrayList<MessageListTable>) dbHelper.getMessages(Integer.parseInt(friend_id), (count * 20));
            // list = (ArrayList<MessageListTable>) dbHelper.getAllMessage(Integer.parseInt(friend_id));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
            Collections.reverse(list);
            list.add(0, null);
            list_pos = list.size() - list_pos;
            if (messagesCount != list.size()) {
                messagesCount = list.size();
                can_load_more = true;
            } else {
                can_load_more = false;
            }
            if (list.size() < 21) {
                can_load_more = false;
            }
            if (adapter == null) {
                adapter = new CustomAdapter(UserConversActivity.this);
                listview.setAdapter(adapter);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (list.size() != 0) {
                            listview.setSelection(list.size() - 1);
                        }
                    }
                }, 50);
            } else {
                adapter.notifyDataSetChanged();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (list.size() != 0) {
                            listview.setSelection(list_pos);
                        }
                    }
                }, 50);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new CharBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(Constant.ACTION_RECEIVER));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private class CustomAdapter extends BaseAdapter {
        final Context context;
        final LayoutInflater mInflater;
        ViewHolder holder;
        private AQuery aQuery;

        public CustomAdapter(Context context) {
            this.context = context;
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            aQuery = new AQuery(UserConversActivity.this);
        }

        private class ViewHolder {
            ImageView friend_image;
            TextView mSenderMsgTxt, mReceiverMsgTxt, load_more, time_receiver, time_sender;
            LinearLayout mSenderLayout, mReceiverLayout;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.user_conversation_list_1, null);
                holder.friend_image = (ImageView) convertView.findViewById(R.id.friend_image);
                holder.mSenderMsgTxt = (TextView) convertView.findViewById(R.id.user_text);
                holder.mReceiverMsgTxt = (TextView) convertView.findViewById(R.id.friend_text);
                holder.load_more = (TextView) convertView.findViewById(R.id.load_more_text);
                holder.time_receiver = (TextView) convertView.findViewById(R.id.time_receiver);
                holder.time_sender = (TextView) convertView.findViewById(R.id.time_sender);
                holder.mSenderLayout = (LinearLayout) convertView.findViewById(R.id.sender_layout);
                holder.mReceiverLayout = (LinearLayout) convertView.findViewById(R.id.receiver_layout);
                holder.friend_image.setVisibility(View.GONE);
                holder.load_more.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                if (can_load_more) {
                    holder.load_more.setVisibility(View.VISIBLE);
                    holder.load_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            list_pos = list.size();
                            count = count + 1;
                            new getMessageTask().execute();
                        }
                    });
                } else {
                    holder.load_more.setVisibility(View.GONE);
                }
                holder.mSenderLayout.setVisibility(View.GONE);
                holder.mReceiverLayout.setVisibility(View.GONE);
            } else {
                holder.load_more.setVisibility(View.GONE);

                MessageListTable mItem = list.get(position);

                if (mItem.message_type.equals(Constant.RECEIVER)) {
                    holder.mSenderLayout.setVisibility(View.GONE);
                    holder.mReceiverLayout.setVisibility(View.VISIBLE);
                    holder.mReceiverMsgTxt.setText(mItem.message_text);
                    //    holder.mReceiverDate.setText(mItem.message_date);
                    holder.mReceiverMsgTxt.setText(mItem.message_text);
                    holder.time_receiver.setText(mItem.message_time);
                } else if (mItem.message_type.equals(Constant.SENDER)) {
                    holder.mSenderLayout.setVisibility(View.VISIBLE);
                    holder.mReceiverLayout.setVisibility(View.GONE);
                    holder.mSenderMsgTxt.setText(mItem.message_text);
                    //   holder.mSenderDate.setText(mItem.message_date);
                    holder.mSenderMsgTxt.setText(mItem.message_text);
                    holder.time_sender.setText(mItem.message_time);
                }
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    private class CharBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if (b != null) {
                try {
                    JSONObject data = new JSONObject(b.getString("data"));
                    String time = b.getString("time");
                    String message = data.getString("message");
                    String sentBy = data.getString("sentBy");
                    String sentTo = data.getString("sentTo");
                    String timeing = data.getString("timing");

                    MessageListTable listTable = new MessageListTable(Integer.parseInt(sentBy), b.getString("type"), message, time, 1, "0", timeing);
                    list.add(listTable);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        if (list.size() != 0) {
                            listview.smoothScrollToPosition(list.size() - 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
}