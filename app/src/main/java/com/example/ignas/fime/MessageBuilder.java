package com.example.ignas.fime;


import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ignas on 10/26/2014.
 */
public class MessageBuilder {

    private JSONObject jsonMessage = new JSONObject();

    public MessageBuilder(){

    }

    public JSONObject friendRequestMessage(String receiverId){
        try {
            jsonMessage.put("type","friend_request");
            jsonMessage.put("sender", ParseUser.getCurrentUser().getObjectId().toString());
            jsonMessage.put("receiver", receiverId);
        } catch (JSONException e) {

        }

        return jsonMessage;
    }

    public JSONObject sharedPlaceMessage(String receiverId, String message){
        try {
            jsonMessage.put("type","shared_place");
            jsonMessage.put("sender", ParseUser.getCurrentUser().getObjectId().toString());
            jsonMessage.put("receiver", receiverId);
            jsonMessage.put("body", message);
        } catch (JSONException e) {

        }

        return jsonMessage;
    }


}
