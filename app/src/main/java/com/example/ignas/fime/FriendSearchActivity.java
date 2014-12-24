package com.example.ignas.fime;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.List;


public class FriendSearchActivity extends ActionBarActivity {
    private String currentUserId;
    private ArrayList<String> names = new ArrayList<String>();
    private EditText usernameField;
    private ListView usersListView;
    private ArrayList<ParseUser> usersListed = new ArrayList<ParseUser>();
    private MyUser userSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
        usernameField = (EditText) findViewById(R.id.usernameInput);
        usersListView = (ListView) findViewById(R.id.usersListView);
        userSelected = new MyUser();
        userSelected.setContext(this);
        userSelected.bindMessagingServices(this);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               userSelected.setUser(usersListed.get(position));
               userSelected.sendFriendRequest();
            }
        });

        usernameField.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUsersListView();
            }
        });

    }

    @Override
    protected void onDestroy(){
        userSelected.unbindMessagingServices(this);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUsersListView(){
        currentUserId = ParseUser.getCurrentUser().getObjectId();
        String userInputFieldValue = usernameField.getText().toString();
        if (userInputFieldValue != null && !userInputFieldValue.trim().equals("")){
            findUsers(userInputFieldValue);
        }

    }

    public void findUsers(String searchString){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        Log.d("LIVE", "WTHF" + searchString);
        final ArrayList<ParseUser> friends = DataStorage.findFriends();
        final ArrayList<String> friendsIds = DataStorage.mapObjectsIds(friends);
        query.whereContains("username", searchString).setLimit(21);
        query.findInBackground(new FindCallback<ParseUser>() {
            ParseUser userFound;

            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                names.clear();
                usersListed.clear();

                if(e == null && parseUsers != null){
                    Log.d("LIVE", "KIEK " + parseUsers.size());
                    for (int i=0; i < parseUsers.size(); i++){
                        userFound = parseUsers.get(i);

                        if (!userFound.getObjectId().toString().equals(currentUserId) &&
                                !friendsIds.contains(userFound.getObjectId().toString())){
                            names.add(parseUsers.get(i).getUsername().toString());
                            usersListed.add(parseUsers.get(i));
                        }
                    }

                    if (names.isEmpty()) {
                        names.add("No users Found");
                    }

                    ArrayAdapter<String> namesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.user_list_item, names);
                    usersListView.setAdapter(namesArrayAdapter);

                }

            }
        });
    }
}
