package com.widevision.quemvaita.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.widevision.quemvaita.R;
import com.widevision.quemvaita.View.StaggeredGridView;
import com.widevision.quemvaita.dao.GetEventSelectedMemberDao;
import com.widevision.quemvaita.dao.GetHomeDao;
import com.widevision.quemvaita.dao.GsonClass;
import com.widevision.quemvaita.dao.GsonEventMemberClass;
import com.widevision.quemvaita.dao.SetDislikeDao;
import com.widevision.quemvaita.dao.SetDoubleLikeDao;
import com.widevision.quemvaita.dao.SetLikeDao;
import com.widevision.quemvaita.loader.ImageLoader;
import com.widevision.quemvaita.model.HideKeyFragment;
import com.widevision.quemvaita.util.AsyncCallback;
import com.widevision.quemvaita.util.Constant;
import com.widevision.quemvaita.util.Extension;
import com.widevision.quemvaita.util.PreferenceConnector;
import com.widevision.quemvaita.util.ValidationTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mercury-one on 1/4/16.
 */
public class FragmentHome extends HideKeyFragment {

    @Bind(R.id.grid_view)
    GridView grid_view;
    @Bind(R.id.all_event_radio)
    TextView allEventTxt;
    @Bind(R.id.selected_event_radio)
    TextView eventISelectedTxt;
    // @Bind(R.id.image)  ImageView image;

    private String user_id;
    private Extension extension;
    private ArrayList<GsonClass.HomeData> allEventList;
    private ArrayList<GsonEventMemberClass.UserList> myEventList;
    private CustomAdapter allEventadapter;
    private CustomAdapterEventSelected selectedEventadapter;

    // private Homeadapter homeadapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Constant.width = displaymetrics.widthPixels;
        Constant.height = displaymetrics.heightPixels;

        user_id = PreferenceConnector.readString(getActivity(), PreferenceConnector.LOGIN_UserId, "");
        extension = new Extension();
        if (extension.executeStrategy(getActivity(), "", ValidationTemplate.INTERNET)) {
            getHomeView();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

        allEventTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((allEventList != null && allEventList.size() == 0) || allEventList == null) {
                    getHomeView();
                    allEventTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_selected, 0, 0, 0);
                    allEventTxt.setText("  All events");

                    eventISelectedTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_unselected, 0, 0, 0);
                    eventISelectedTxt.setText("  Event i selected");
                } else {
                    if (allEventadapter != null) {
                        grid_view.setAdapter(allEventadapter);
                        allEventTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_selected, 0, 0, 0);
                        allEventTxt.setText("  All events");
                        eventISelectedTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_unselected, 0, 0, 0);
                        eventISelectedTxt.setText("  Event i selected");
                    }
                }
            }
        });

        eventISelectedTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((myEventList != null && myEventList.size() == 0) || myEventList == null) {
                    getMyEventMemberList();
                    allEventTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_unselected, 0, 0, 0);
                    allEventTxt.setText("  All events");
                    eventISelectedTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_selected, 0, 0, 0);
                    eventISelectedTxt.setText("  Event i selected");
                } else {
                    if (selectedEventadapter != null) {
                        grid_view.setAdapter(selectedEventadapter);
                        allEventTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_unselected, 0, 0, 0);
                        allEventTxt.setText("  All events");
                        eventISelectedTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.radio_btn_selected, 0, 0, 0);
                        eventISelectedTxt.setText("  Event i selected");
                    }
                }
            }
        });

        return view;
    }

    private void getHomeView() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetHomeDao getHomeDao = new GetHomeDao(user_id);
        getHomeDao.query(new AsyncCallback<GsonClass>() {
            @Override
            public void onOperationCompleted(GsonClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            try {
                                int margin = getResources().getDimensionPixelSize(R.dimen.margin);
                                // set the GridView margin
                                grid_view.setPadding(margin, 0, margin, 0); // have the margin on the sides as well

                                // loop for removing blocked people...(people blocked by me or i blocked by other both are removed.)
                                for (int i = 0; i < result.data.size(); i++) {
                                    GsonClass.HomeData item = result.data.get(i);
                                    if (item.sender_status.equals("2") || item.receiver_status.equals("2") || !item.status.equals("active")) {
                                        result.data.remove(i);
                                        i--;
                                    }
                                }
                                allEventList = result.data;
                                allEventadapter = new CustomAdapter(getActivity(), allEventList);
                                grid_view.setAdapter(allEventadapter);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                                Toast.makeText(getActivity(), "No data to show.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "No data to show.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMyEventMemberList() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        GetEventSelectedMemberDao getHomeDao = new GetEventSelectedMemberDao(user_id);
        getHomeDao.query(new AsyncCallback<GsonEventMemberClass>() {
            @Override
            public void onOperationCompleted(GsonEventMemberClass result, Exception e) {
                progressDialog.dismiss();
                if (e == null && result != null) {
                    if (result.success.equals("1")) {
                        if (result.data != null && result.data.size() != 0) {
                            try {
                                int margin = getResources().getDimensionPixelSize(R.dimen.margin);
                                // set the GridView margin
                                grid_view.setPadding(margin, 0, margin, 0); // have the margin on the sides as well

                                myEventList = new ArrayList<>();

                                for (int i = 0; i < result.data.size(); i++) {
                                    if (result.data.get(i).user_list != null && result.data.get(i).user_list.size() != 0) {
                                        myEventList.addAll(result.data.get(i).user_list);
                                    }
                                }

                                if (myEventList != null && myEventList.size() != 0) {
                                    removeDuplicates(myEventList);
                                    // loop for removing blocked people...(people blocked by me or i blocked by other both are removed.)
                                    for (int i = 0; i < myEventList.size(); i++) {
                                        GsonEventMemberClass.UserList item = myEventList.get(i);
                                        if (item.sender_status.equals("2") || item.receiver_status.equals("2")) {
                                            myEventList.remove(i);
                                            i--;
                                        }
                                    }

                                    selectedEventadapter = new CustomAdapterEventSelected(getActivity(), myEventList);
                                    grid_view.setAdapter(selectedEventadapter);
                                } else {
                                    Toast.makeText(getActivity(), "You are not going in any of the evet.", Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e1) {
                                e1.printStackTrace();
                                Toast.makeText(getActivity(), "You are not going in any of the event.", Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private List<GsonEventMemberClass.UserList> removeDuplicates(List<GsonEventMemberClass.UserList> l) {
        int size = l.size();
        String user_id = PreferenceConnector.readString(getActivity(), PreferenceConnector.LOGIN_UserId, "");
        int duplicates = 0;
        // not using a method in the check also speeds up the execution
        // also i must be less that size-1 so that j doesn't
        // throw IndexOutOfBoundsException
        for (int i = 0; i < size - 1; i++) {
            // start from the next item after strings[i]
            // since the ones before are checked
            for (int j = i + 1; j < size; j++) {
                // no need for if ( i == j ) here
                if (!l.get(j).id.trim().equalsIgnoreCase(l.get(i).id.trim()) && !l.get(j).id.equals(user_id))
                    continue;
                duplicates++;
                l.remove(j);
                // decrease j because the array got re-indexed
                j--;
                // decrease the size of the array
                size--;
            } // for j
        } // for i
        return l;
    }

    private class CustomAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<GsonClass.HomeData> rowItems;
        private final SparseArray<Double> sPositionHeightRatios = new SparseArray<>();
        private final Random mRandom;
        private final LayoutInflater mInflater;
        private ViewHolder holder;
        private final AQuery aQuery;
        private final GridView.LayoutParams layoutParams;
        private ImageLoader imageLoader;

        public CustomAdapter(Context context, ArrayList<GsonClass.HomeData> items) {
            this.context = context;
            this.rowItems = items;
            this.mRandom = new Random();
            mInflater = getActivity().getLayoutInflater();
            aQuery = new AQuery(context);
            layoutParams = new GridView.LayoutParams((Constant.width / 2) - 10, (Constant.height / 3));
            imageLoader = new ImageLoader(context);
        }

        private class ViewHolder {
            TextView label, image_text;
            ImageView camera, image, like, like1, dislike, dislike1;
            LinearLayout bottomLayout, bottomLayout1;
            View lineView;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonClass.HomeData item = rowItems.get(position);
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.home_user_list_test, null);
            holder.label = (TextView) convertView.findViewById(R.id.user_name);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.like = (ImageView) convertView.findViewById(R.id.like);
            holder.like1 = (ImageView) convertView.findViewById(R.id.like1);
            holder.dislike = (ImageView) convertView.findViewById(R.id.dislike);
            holder.dislike1 = (ImageView) convertView.findViewById(R.id.dislike1);
            holder.bottomLayout1 = (LinearLayout) convertView.findViewById(R.id.bottom1);
            holder.bottomLayout = (LinearLayout) convertView.findViewById(R.id.bottom);
            holder.lineView = convertView.findViewById(R.id.line);
            convertView.setTag(holder);
            convertView.setLayoutParams(layoutParams);
            holder.bottomLayout1.setVisibility(View.GONE);

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.issent != null && item.issent.equals("0")) {
                        Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (item.sender_status.equals("1") && item.receiver_status.equals("1")) {
                            Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (item.sender_status.equals("1")) {
                                setDoubleLike(rowItems.get(position).id);
                                rowItems.get(position).receiver_status = "1";
                            } else {
                                setLike(rowItems.get(position).id);
                                rowItems.get(position).sender_status = "1";
                                rowItems.get(position).issent = "0";
                            }
                        }
                    }
                }
            });

            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!item.receiver_status.equals("1")) {
                        rowItems.get(position).receiver_status = "1";
                        setDisLike(rowItems.get(position).id);
                    } else {
                        Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityFullProfile.class);
                    intent.putExtra("profile", item.profile_pic);
                    intent.putExtra("about", item.about);
                    intent.putExtra("interest", item.interest);
                    intent.putExtra("name", item.first_name + " " + item.last_name);
                    intent.putExtra("id", item.id);
                    Bundle bundle = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
                    startActivity(intent, bundle);
                }
            });

            holder.label.setText(item.first_name);

            aQuery.id(holder.image).image(item.profile_pic, true, true, (Constant.height / 3), R.drawable.cdefault);
            //  imageLoader.DisplayImage(item.profile_pic, holder.image);

            if (item.issent != null && item.issent.equals("0")) {
                if (item.receiver_status.equals("1")) {
                    holder.like.setImageResource(R.drawable.facebook_like_thumb_copy);
                    holder.dislike.setImageResource(R.drawable.double_like);
                    holder.dislike.setVisibility(View.VISIBLE);
                    holder.lineView.setVisibility(View.GONE);
                } else if (item.sender_status.equals("1")) {
                    holder.like.setImageResource(R.drawable.facebook_like_thumb_copy);
                    holder.dislike.setVisibility(View.GONE);
                    holder.dislike.setVisibility(View.GONE);
                    holder.lineView.setVisibility(View.GONE);
                }
            } else {
                if (item.receiver_status.equals("1") && item.sender_status.equals("1")) {
                    holder.like.setImageResource(R.drawable.facebook_like_thumb_copy);
                    holder.dislike.setImageResource(R.drawable.double_like);
                    holder.dislike.setVisibility(View.VISIBLE);
                    holder.lineView.setVisibility(View.GONE);
                } else {
                    holder.bottomLayout1.setVisibility(View.VISIBLE);
                    holder.bottomLayout.setVisibility(View.GONE);
                    holder.like1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (item.issent != null && item.issent.equals("0")) {
                                Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (item.sender_status.equals("1") && item.receiver_status.equals("1")) {
                                    Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (item.sender_status.equals("1")) {
                                        setDoubleLike(rowItems.get(position).id);
                                        rowItems.get(position).receiver_status = "1";
                                    } else {
                                        setLike(rowItems.get(position).id);
                                        rowItems.get(position).sender_status = "1";
                                        rowItems.get(position).issent = "0";
                                    }
                                }
                            }
                        }
                    });
                    holder.dislike1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!item.receiver_status.equals("1")) {
                                rowItems.get(position).receiver_status = "1";
                                setDisLike(rowItems.get(position).id);
                            } else {
                                Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            return convertView;
        }

        private void setLike(String id) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            SetLikeDao likeDao = new SetLikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setDoubleLike(String id) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            SetDoubleLikeDao likeDao = new SetDoubleLikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setDisLike(String id) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            SetDislikeDao likeDao = new SetDislikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {

                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        private double getPositionRatio(final int position) {
            double ratio = sPositionHeightRatios.get(position, 0.0);
            // if not yet done generate and stash the columns height
            // in our real world scenario this will be determined by
            // some match based on the known height and width of the image
            // and maybe a helpful way to get the column height!
            if (ratio == 0) {
                ratio = getRandomHeightRatio();
                sPositionHeightRatios.append(position, ratio);
            }
            return ratio;
        }

        private double getRandomHeightRatio() {
            return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
            // the width
        }
    }


    private class CustomAdapterEventSelected extends BaseAdapter {
        private final Context context;
        private final ArrayList<GsonEventMemberClass.UserList> rowItems;
        private final SparseArray<Double> sPositionHeightRatios = new SparseArray<>();
        private final Random mRandom;
        private final LayoutInflater mInflater;
        private ViewHolder holder;
        private final AQuery aQuery;
        private final GridView.LayoutParams layoutParams;
        private ImageLoader imageLoader;

        public CustomAdapterEventSelected(Context context, ArrayList<GsonEventMemberClass.UserList> items) {
            this.context = context;
            this.rowItems = items;
            this.mRandom = new Random();
            mInflater = getActivity().getLayoutInflater();
            aQuery = new AQuery(context);
            layoutParams = new GridView.LayoutParams((Constant.width / 2) - 10, (Constant.height / 3));
            imageLoader = new ImageLoader(context);
        }

        private class ViewHolder {
            TextView label, image_text;
            ImageView camera, image, like, like1, dislike, dislike1;
            LinearLayout bottomLayout, bottomLayout1;
            View lineView;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final GsonEventMemberClass.UserList item = rowItems.get(position);
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.home_user_list_test, null);
            holder.label = (TextView) convertView.findViewById(R.id.user_name);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.like = (ImageView) convertView.findViewById(R.id.like);
            holder.like1 = (ImageView) convertView.findViewById(R.id.like1);
            holder.dislike = (ImageView) convertView.findViewById(R.id.dislike);
            holder.dislike1 = (ImageView) convertView.findViewById(R.id.dislike1);
            holder.bottomLayout1 = (LinearLayout) convertView.findViewById(R.id.bottom1);
            holder.bottomLayout = (LinearLayout) convertView.findViewById(R.id.bottom);
            holder.lineView = convertView.findViewById(R.id.line);
            convertView.setTag(holder);
            convertView.setLayoutParams(layoutParams);
            holder.bottomLayout1.setVisibility(View.GONE);

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.issent != null && item.issent.equals("0")) {
                        Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (item.sender_status.equals("1") && item.receiver_status.equals("1")) {
                            Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (item.sender_status.equals("1")) {
                                setDoubleLike(rowItems.get(position).id);
                                rowItems.get(position).receiver_status = "1";
                            } else {
                                setLike(rowItems.get(position).id);
                                rowItems.get(position).sender_status = "1";
                                rowItems.get(position).issent = "0";
                            }
                        }
                    }
                }
            });

            holder.dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!item.receiver_status.equals("1")) {
                        rowItems.get(position).receiver_status = "1";
                        setDisLike(rowItems.get(position).id);
                    } else {
                        Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityFullProfile.class);
                    intent.putExtra("profile", item.profile_pic);
                    intent.putExtra("about", item.about);
                    intent.putExtra("interest", item.interest);
                    intent.putExtra("name", item.first_name + " " + item.last_name);
                    intent.putExtra("id", item.id);
                    Bundle bundle = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
                    startActivity(intent, bundle);
                }
            });

            holder.label.setText(item.first_name);

            aQuery.id(holder.image).image(item.profile_pic, true, true, (Constant.height / 3), R.drawable.cdefault);
            //  imageLoader.DisplayImage(item.profile_pic, holder.image);

            if (item.issent != null && item.issent.equals("0")) {
                if (item.receiver_status.equals("1")) {
                    holder.like.setImageResource(R.drawable.facebook_like_thumb_copy);
                    holder.dislike.setImageResource(R.drawable.double_like);
                    holder.dislike.setVisibility(View.VISIBLE);
                    holder.lineView.setVisibility(View.GONE);
                } else if (item.sender_status.equals("1")) {
                    holder.like.setImageResource(R.drawable.facebook_like_thumb_copy);
                    holder.dislike.setVisibility(View.GONE);
                    holder.dislike.setVisibility(View.GONE);
                    holder.lineView.setVisibility(View.GONE);
                }
            } else {
                if (item.receiver_status.equals("1") && item.sender_status.equals("1")) {
                    holder.like.setImageResource(R.drawable.facebook_like_thumb_copy);
                    holder.dislike.setImageResource(R.drawable.double_like);
                    holder.dislike.setVisibility(View.VISIBLE);
                    holder.lineView.setVisibility(View.GONE);
                } else {
                    holder.bottomLayout1.setVisibility(View.VISIBLE);
                    holder.bottomLayout.setVisibility(View.GONE);
                    holder.like1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (item.issent != null && item.issent.equals("0")) {
                                Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (item.sender_status.equals("1") && item.receiver_status.equals("1")) {
                                    Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (item.sender_status.equals("1")) {
                                        setDoubleLike(rowItems.get(position).id);
                                        rowItems.get(position).receiver_status = "1";
                                    } else {
                                        setLike(rowItems.get(position).id);
                                        rowItems.get(position).sender_status = "1";
                                        rowItems.get(position).issent = "0";
                                    }
                                }
                            }
                        }
                    });
                    holder.dislike1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!item.receiver_status.equals("1")) {
                                rowItems.get(position).receiver_status = "1";
                                setDisLike(rowItems.get(position).id);
                            } else {
                                Toast.makeText(getActivity(), "You already like this person.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            return convertView;
        }

        private void setLike(String id) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            SetLikeDao likeDao = new SetLikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setDoubleLike(String id) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            SetDoubleLikeDao likeDao = new SetDoubleLikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {
                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void setDisLike(String id) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            SetDislikeDao likeDao = new SetDislikeDao(user_id, id);
            likeDao.query(new AsyncCallback<GsonClass>() {
                @Override
                public void onOperationCompleted(GsonClass result, Exception e) {

                    notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}