package com.example.ignas.fime;

import android.content.Intent;
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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FriendListActivity extends ActionBarActivity {

    private ArrayList<ParseUser> myFriends;
    private ListView usersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        usersListView = (ListView) findViewById(R.id.friendsListView);

        myFriends = DataStorage.findFriends();
        Log.d("FRIEND", "SIZE" + myFriends.size());
        ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.user_list_item, adaptFriends());
        usersListView.setAdapter(namesArrayAdapter);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // DO nothing

            }
        });
    }


    private ArrayList<String> adaptFriends(){
        ArrayList<String> namesList = new ArrayList<String>();
        ParseUser friend;
        Iterator iterator = myFriends.iterator();
        while (iterator.hasNext()){
            friend = (ParseUser) iterator.next();
            Log.d("FRIEND", "STRING" + friend.getUsername().toString());

            namesList.add(friend.getUsername().toString());
        }

        return namesList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //
        getMenuInflater().inflate(R.menu.friend_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.add_person:
                startActivity(new Intent(this, FriendSearchActivity.class));
                finish();
                return true;
            case R.id.logOut:
                ParseUser.logOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
