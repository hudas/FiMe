package com.example.ignas.fime;



import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.WritableMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Ignas on 10/26/2014.
 */
public class DataStorage {

    public DataStorage(){
    }

    public static String parseNotificationTime(ParseObject notification){
        String body = notification.get("body").toString();
        String time = "";
        try {
            JSONObject jsonBody = new JSONObject(body);
            time = jsonBody.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static void storeMessage(final JSONObject messageJSON, Message message) {
        final WritableMessage writableMessage = new WritableMessage(message.getRecipientIds().get(0), message.getTextBody());

        try {
            if (messageJSON.get("type").toString().equals("friend_request")) {
                ParseObject parseMessage = new ParseObject("Notification");
                parseMessage.put("senderId", messageJSON.get("sender"));
                parseMessage.put("sender", ParseUser.getCurrentUser());
                parseMessage.put("recipientId", messageJSON.get("receiver"));
                parseMessage.put("type", messageJSON.get("type"));
                parseMessage.saveInBackground();
            } else {
                ParseObject notification = new ParseObject("Notification");
                notification.put("senderId", messageJSON.get("sender"));
                notification.put("sender", ParseUser.getCurrentUser());
                notification.put("recipientId", messageJSON.get("receiver"));
                notification.put("body", messageJSON.get("body"));
                notification.saveInBackground();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ParseObject> getUserNotifications(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Notification");
        List<ParseObject> notificationResults = new ArrayList<ParseObject>();
        query.whereEqualTo("recipientId", ParseUser.getCurrentUser().getObjectId().toString()).include("User");
        try {
            notificationResults = query.find();
        } catch (ParseException e) {

        }
        return (ArrayList<ParseObject>) notificationResults;
    }

    public static void setFriends(ParseUser user1, ParseUser user2){
        ParseObject notification = new ParseObject("FriendList");
        notification.put("firstUser", ParseUser.getCurrentUser());
        notification.put("secondUser", user2);
        notification.saveInBackground();
    }

    public static ArrayList<String> mapObjectsIds(ArrayList<ParseUser> list){
        ArrayList<String> idList = new ArrayList<String>();
        Iterator iterator = list.iterator();
        ParseObject object;

        while(iterator.hasNext()){
            object = (ParseUser) iterator.next();
            idList.add(object.getObjectId().toString());
        }
        return idList;
    }

    public static ArrayList<ParseUser> findFriends(){
        ArrayList<ParseUser> myFriends = new ArrayList<ParseUser>();
        ArrayList<ParseObject> temporaryFriends = new ArrayList<ParseObject>();
        ParseUser firstUser;
        ParseQuery<ParseObject> firstUserQuery = new ParseQuery<ParseObject>("FriendList");
        firstUserQuery.whereEqualTo("firstUser", ParseUser.getCurrentUser());
        ParseQuery<ParseObject> secondUserQuery = new ParseQuery<ParseObject>("FriendList");
        secondUserQuery.whereEqualTo("secondUser", ParseUser.getCurrentUser());
        ParseQuery<ParseObject>[] combindeQuery = new ParseQuery[]{firstUserQuery, secondUserQuery};
        ParseQuery<ParseObject> finalQuery = ParseQuery.or(Arrays.asList(combindeQuery));
        try {
            temporaryFriends = (ArrayList<ParseObject>) finalQuery.find();
            Iterator iterator = temporaryFriends.iterator();
            while (iterator.hasNext()){
                ParseObject listObject = (ParseObject) iterator.next();
                firstUser = listObject.getParseUser("firstUser").fetchIfNeeded();
                if (firstUser.getObjectId().toString().equals(ParseUser.getCurrentUser().getObjectId().toString())){
                    firstUser = listObject.getParseUser("secondUser").fetchIfNeeded();
                }
                Log.d("FRIEND", "USER" + firstUser.getObjectId());
                myFriends.add(firstUser);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return myFriends;
    }

    public static ArrayList<String> generateList(ArrayList list){
        ArrayList<String> notificationsList = new ArrayList<String>();
        ParseObject notification;
        Iterator iterator = list.iterator();
        try {
            while (iterator.hasNext()) {
                notification = (ParseObject) iterator.next();
                if (notification.get("type") != null && notification.get("type").equals("friend_request")) {
                    notificationsList.add("Friend Request From:" + notification.getParseUser("sender").fetchIfNeeded().getUsername().toUpperCase());
                } else {
                    notificationsList.add("Shared location From:" + notification.getParseUser("sender").fetchIfNeeded().getUsername().toUpperCase());
                }
            }
        } catch (ParseException e) {

        }

        return notificationsList;
    }
}
