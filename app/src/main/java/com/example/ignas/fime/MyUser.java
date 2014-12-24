package com.example.ignas.fime;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ignas on 10/26/2014.
 */
public class MyUser {

    private ParseUser user;
    private MessageBuilder messageBuilder = new MessageBuilder();
    private MessageService.MessageServiceInterface messageService;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private JSONObject message = new JSONObject();
    private Context applicationContext;

    public MyUser(){

    }

    public MyUser(ParseUser parseUser){
        user = parseUser;
    }

    public void sendMessage(){

    }

    public void setContext(Context context){
        applicationContext = context;
    }

    public void bindMessagingServices(Context context){
        context.bindService(new Intent(context, MessageService.class), serviceConnection, context.BIND_AUTO_CREATE);
    }

    public void unbindMessagingServices(Context context){
        context.unbindService(serviceConnection);
    }

    public void bindMessagingServices(){
        applicationContext.bindService(new Intent(applicationContext, MessageService.class), serviceConnection, applicationContext.BIND_AUTO_CREATE);
    }

    public void unbindMessagingServices(){
        applicationContext.unbindService(serviceConnection);
    }


    public void setUser(ParseUser parseUser){
        user = parseUser;
    }

    public void sendFriendRequest(){
        message = messageBuilder.friendRequestMessage(user.getObjectId().toString());
        messageService.sendMessage(user.getObjectId(), message.toString());
    }

    public void sendSharedPlace(String jsonMessage){
        message = messageBuilder.sharedPlaceMessage(user.getObjectId().toString(), jsonMessage);
        Log.d("ASD","SUPER"+ message.toString());
        messageService.sendMessage(user.getObjectId(), message.toString());
    }

    public void acceptFriendRequest(){
        DataStorage.setFriends(ParseUser.getCurrentUser(), user);

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
            Log.d("ASD","FAILED");

        }
        @Override
        public void onIncomingMessage(MessageClient client, Message message) {
            //Display an incoming message
        }

        @Override
        public void onMessageSent(MessageClient client, Message messageSent, String recipientId) {
            DataStorage.storeMessage(message, messageSent);
            Log.d("HAHA", "STORIGN******************");

            try {
                UserNotification.notifyUserOfMessage(applicationContext , message.get("type").toString());
            } catch (JSONException e) {
                UserNotification.notifyUserOfMessage(applicationContext , "Message");
            }
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
