package com.example.ignas.fime;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Ignas on 11/23/2014.
 */
public class NotificationAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ParseObject> data;
    private static LayoutInflater inflater = null;
    private NotificationAdapter adapterContext = this;
    private MyUser userSelected = new MyUser();

    public NotificationAdapter(Context context, ArrayList<ParseObject> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.notification_row, null);
        TextView text = (TextView) vi.findViewById(R.id.rowHeader);
        ImageView image = (ImageView) vi.findViewById(R.id.notificationImageView);
        TextView username = (TextView) vi.findViewById(R.id.userName);
        Button deleteNotification = (Button) vi.findViewById(R.id.deleteNotificationButton);

        ParseObject notification = data.get(position);

        listenForConfiramtions(image, notification, position);
        listenForDeleting(deleteNotification, notification, position);

        if (notification.get("type") != null && notification.get("type").toString().equals("friend_request")){
            image.setImageDrawable(vi.getResources().getDrawable(R.drawable.ic_action_new_friend));
            username.setVisibility(View.GONE);
        } else {
            deleteNotification.setVisibility(View.GONE);
            image.setImageDrawable(vi.getResources().getDrawable(R.drawable.ic_action_earth));
            username.setText(DataStorage.parseNotificationTime(notification));
        }

        try {
            text.setText(notification.getParseUser("sender").fetchIfNeeded().getString("username"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return vi;
    }

    private void listenForDeleting(Button button, final ParseObject notification, final int position){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ASD", "DELETING WORKS RLY WELL");
                try {
                    notification.delete();
                    data.remove(position);
                    adapterContext.notifyDataSetChanged();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void listenForConfiramtions(ImageView image, final ParseObject notification, final int position){
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("ASD", "NOT EXPECTED BEHAVIOUR");
                    if (notification.get("type") != null && notification.get("type").toString().equals("friend_request")) {
                        userSelected.setUser(notification.getParseUser("sender").fetchIfNeeded());
                        userSelected.acceptFriendRequest();
                        data.remove(position);
                        notification.delete();
                        adapterContext.notifyDataSetChanged();
                    } else {
                        Intent intent = new Intent(context, LocationReviewActivity.class);
                        intent.putExtra("SHARED_LOCATION", notification.get("body").toString());
                        intent.putExtra("NOTIFICATION_ID", notification.getObjectId().toString());
                        context.startActivity(intent);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
