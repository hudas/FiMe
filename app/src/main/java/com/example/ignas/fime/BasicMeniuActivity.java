package com.example.ignas.fime;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;


import java.util.ArrayList;
import java.util.List;


public class BasicMeniuActivity extends ActionBarActivity {

 //   private ServiceConnection serviceConnection = new MyServiceConnection();
    private MessageService.MessageServiceInterface messageService;
    //private MyMessageClientListener messageClientListener;
    private Intent serviceIntent;
    private ArrayList<ParseObject> notifications = new ArrayList<ParseObject>();
    private ArrayList<String> notificationsList = new ArrayList<String>();

    private ListView usersListView;
    private TextView noNotificationsMessageView;
    private TextView noNotificationsHeadlineView;
    private MyUser userSelected;
    private NotificationAdapter namesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_meniu);

        serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);

        userSelected = new MyUser();
        userSelected.setContext(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        LongOperation task = new LongOperation();
        task.execute();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        //unbindService(serviceConnection);
        stopService(serviceIntent);
        //messageService.removeMessageClientListener(messageClientListener);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.basic_meniu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add_person:
                openFriendSearch();
                return true;
            case R.id.logOut:
                ParseUser.logOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                return true;
            case R.id.friendsList:
                startActivity(new Intent(getApplicationContext(), FriendListActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openFriendSearch(){
        startActivity(new Intent(this, FriendSearchActivity.class));
    }

    public void sendShareMessage(View view){
        Intent intent = new Intent(this, MapSharingActivity.class);
        startActivityForResult(intent, 100);
    }

    private void manageNotificationClicked(NotificationAdapter namesArrayAdapter, int position){
        try {
            Log.d("ASD", "NOT EXPECTED BEHAVIOUR");
            ParseObject notification = notifications.get(position);
            if (notification.get("type") != null && notification.get("type").toString().equals("friend_request")){
                userSelected.setUser(notifications.get(position).getParseUser("sender").fetchIfNeeded());
                userSelected.acceptFriendRequest();
                notificationsList.remove(position);
                notification.delete();
                namesArrayAdapter.notifyDataSetChanged();
            } else {
                Intent intent = new Intent(this, LocationReviewActivity.class);
                intent.putExtra("SHARED_LOCATION",notification.get("body").toString());
                intent.putExtra("NOTIFICATION_ID",notification.getObjectId().toString());
                startActivity(intent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initializeNotifiactionListView(){
        usersListView = (ListView) findViewById(R.id.notificationsListView);


        notificationsList = DataStorage.generateList(notifications);

        if (!notificationsList.isEmpty()){
            noNotificationsMessageView = (TextView) findViewById(R.id.textView);
            noNotificationsHeadlineView = (TextView) findViewById(R.id.textView2);

            noNotificationsMessageView.setVisibility(View.GONE);
            noNotificationsHeadlineView.setVisibility(View.GONE);
        }
        namesArrayAdapter = new NotificationAdapter(this, notifications);
        usersListView.setAdapter(namesArrayAdapter);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                manageNotificationClicked(namesArrayAdapter, position);

            }
        });

    }
    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            notifications = DataStorage.getUserNotifications();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            initializeNotifiactionListView();


            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
