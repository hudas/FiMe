package com.example.ignas.fime;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.util.ArrayList;
import java.util.List;


public class SharingActivity extends Activity {

    private ArrayList<String> names;
    private ListView usersListView;


    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    private MessageAdapter messageAdapter;
    private MyUser userSelected;
    private ArrayList<ParseUser> usersListed ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messageAdapter = new MessageAdapter(this);
        setContentView(R.layout.activity_sharing);
        Log.d("ASD", "Setting ACTIVITY1");

        userSelected = new MyUser();
        userSelected.setContext(this);
        userSelected.bindMessagingServices(this);

        Log.d("ASD", "Setting ACTIVITY1");
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        names = new ArrayList<String>();
        usersListed = DataStorage.findFriends();
        for (int i=0; i < usersListed.size(); i++){
            names.add(usersListed.get(i).getUsername().toString());
        }

        usersListView = (ListView) findViewById(R.id.usersListView);
        ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.user_list_item, names);
        usersListView.setAdapter(namesArrayAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               openConversation(names, position);
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            messageBody = extras.getString("JSON_MESSAGE");
        }
    }

    public void openConversation(final ArrayList<String> names, final int pos){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null){
                    userSelected.setUser(usersListed.get(pos));
                    Log.d("ASD","STILL WORKING" + messageBody);
                    userSelected.sendSharedPlace(messageBody);

                    Toast.makeText(getApplicationContext(),
                            "SUCESS",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                       "NOT SUCESS",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        userSelected.unbindMessagingServices();
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("ASD", "Setting service");
            messageService = (MessageService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(new MyMessageClientListener());
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        //Notify the user if their message failed to send
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(getApplicationContext(), "Message failed to send.", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onIncomingMessage(MessageClient client, Message message) {
                //Display an incoming message
            Log.d("ASD","GOT YOUR MESSAGE*******************");
            Toast.makeText(getApplicationContext(), "You Have Received a Message!", Toast.LENGTH_LONG).show();

        }
        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {
            Log.d("ASD","SENT");

            final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

            //only add message to parse database if it doesn't already exist there
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Norification");
            query.whereEqualTo("sinchId", message.getMessageId());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> messageList, com.parse.ParseException e) {
                    if (e == null) {
                        if (messageList.size() == 0) {
                            ParseObject parseMessage = new ParseObject("Norification");
                            parseMessage.put("senderId", currentUserId);
                            parseMessage.put("recipientId", writableMessage.getRecipientIds().get(0));
                            parseMessage.put("messageText", writableMessage.getTextBody());
                            parseMessage.put("sinchId", writableMessage.getMessageId());
                            parseMessage.saveInBackground();

                            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);
                        }
                    }
                }
            });
            Log.d("ASD","SENT YPUR MESSAGE");
//Display the message that was just sent
            //Later, I'll show you how to store the
//message in Parse, so you can retrieve and
//display them every time the conversation is opened
        }
        //Do you want to notify your user when the message is delivered?
        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
            Log.d("ASD","DELIVERED");
        }

        @Override
        public void onShouldSendPushData(MessageClient messageClient, Message message, List<PushPair> pushPairs) {
            Log.d("ASD","OFFLINE");
        }


    }
}
