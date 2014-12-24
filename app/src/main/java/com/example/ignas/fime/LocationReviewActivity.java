package com.example.ignas.fime;

import com.example.ignas.fime.util.SystemUiHider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LocationReviewActivity extends Activity {

    private GoogleMap map;
    private TextView textMessage, timeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_review);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setZoomControlsEnabled(false);

        Bundle extras = getIntent().getExtras();
        String jsonMessage = extras.getString("SHARED_LOCATION");
        Log.d("ASD", "WORKWORK" + jsonMessage);
        textMessage = (TextView) findViewById(R.id.locationMessageTextView);
        timeInput = (TextView) findViewById(R.id.timeReviewField);

        try {
            JSONObject jobject = new JSONObject(jsonMessage);
            String latitude = jobject.getString("coordinate_y");
            String longtitude = jobject.getString("coordinate_x");
            Log.d("ASD", "WORKWORK" + latitude.toString() + longtitude.toString());
            LatLng currentLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            map.addMarker(new MarkerOptions().position(currentLocation));
            textMessage.setText(jobject.getString("message").toString());
            timeInput.setText(jobject.getString("time").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void deleteNotification(View view){
        Bundle extras = getIntent().getExtras();
        String notificationId = extras.getString("NOTIFICATION_ID");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
        try {
            ParseObject oldNotification = query.get(notificationId);
            oldNotification.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, BasicMeniuActivity.class);
        startActivity(intent);
        finish();
    }
}
