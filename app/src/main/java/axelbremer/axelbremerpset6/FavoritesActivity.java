package axelbremer.axelbremerpset6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * This activity shows a users favorites. Starting with the logged in users favorites. Another
 * users favorites can be shown by looking for the users e-mail address.
 */

public class FavoritesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference mDatabase;
    ArrayAdapter adapter;
    ListView favoritesListView;
    TextView nameTextView;
    EditText searchUserBar;
    List<String> idList;
    List<String> nameList;
    FirebaseUser loggedInUser;
    String viewedUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        loggedInUser = mAuth.getCurrentUser();

        if(loggedInUser == null){
            Intent intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
        }

        viewedUid = loggedInUser.getUid();

        favoritesListView = findViewById(R.id.favoritesListView);
        nameTextView = findViewById(R.id.nameTextView);
        searchUserBar = findViewById(R.id.searchUserBar);

        setListeners();
    }

    private void setListeners() {
        /**
         * When clicking on an item, the app takes the user to the details page of that volume.
         */
        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(FavoritesActivity.this, VolumeActivity.class);
                String chosenId = idList.get(position);
                intent.putExtra("Id", chosenId);
                startActivity(intent);
            }
        });


        /**
         * When long clicking an item the app the selected item will be deleted from the favorites.
         * This only happens when the currently viewed favorites are from the currently logged in
         * user.
         */
        favoritesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                if(viewedUid.equals(loggedInUser.getUid())){
                    deleteFavorite(pos);
                    return true;
                } else {
                    Toast.makeText(FavoritesActivity.this, "Can't delete from someone else's favorites.",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
    }

    /**
     * When long clicking an item the app the selected item will be deleted from the favorites.
     * If the last book is deleted a new book is added because firebase won't allow an
     * empty list to be saved.
     */
    private void deleteFavorite(int pos) {
        mDatabase = db.getReference();
        Favorites fav;

        nameList.remove(pos);
        idList.remove(pos);

        // if the list is empty make a new one with the starting book.
        if(idList.size() == 0){
            fav = new Favorites();
            Toast.makeText(FavoritesActivity.this, "Gotta have at least 1 favorite.",
                    Toast.LENGTH_SHORT).show();
        } else{
            fav = new Favorites(nameList, idList);
        }

        // Set the updated favorites in the database.
        mDatabase.child("favorites").child(viewedUid).setValue(fav);
        updateListView(loggedInUser.getUid(), loggedInUser.getEmail());
    }

    //
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateListView(currentUser.getUid(), currentUser.getEmail());
    }


    /**
     * Update the list view with the selected users favorites. Also checks if the selected user
     * exists.
     */
    private void updateListView(String uid, String email) {
        try {
            mDatabase = db.getReference("favorites").child(uid);

            // Change the title according to whose favorites are being shown.
            if (uid.equals(loggedInUser.getUid())) {
                nameTextView.setText("Your favorites");
            } else {
                String mail = email.replace(',','.');
                nameTextView.setText(mail + "'s favorites");
            }

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Favorites object and set the listview.
                    Favorites fav = dataSnapshot.getValue(Favorites.class);

                    setLists(fav);

                    adapter = new ArrayAdapter(FavoritesActivity.this, android.R.layout.simple_list_item_1, nameList);

                    favoritesListView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("FavoritesActivity", "loadPost:onCancelled", databaseError.toException());
                }

            };
            mDatabase.addListenerForSingleValueEvent(postListener);
        } catch (Exception e){
            Toast.makeText(FavoritesActivity.this, "User not found.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Set the lists using the given Favorites object.
     */
    private void setLists(Favorites fav) {
        idList = fav.getIdList();
        nameList = fav.getNameList();
    }

    /**
     * When the user searches for another user's favorites using mail the uid is retrieved from
     * the database and updatelistview() is called using the mail and uid.
     */
    public void onFavoritesSearchClick(View view) {
        String email = searchUserBar.getText().toString().replace('.',',');
        mDatabase = db.getReference("emailToUid").child(email);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get uid from database and pass it to updatelistview()
                String uid = dataSnapshot.getValue(String.class);
                viewedUid = uid;
                String email = searchUserBar.getText().toString().replace('.',',');
                updateListView(uid, email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FavoritesActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        };
        mDatabase.addListenerForSingleValueEvent(postListener);
    }

    /**
     * Creates the top right menu.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sets the functionality of the Favorites button.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favoritesMenuItem:
                Intent intent = new Intent(FavoritesActivity.this, FavoritesActivity.class);
                startActivity(intent);
                break;
            case R.id.signOutMenuItem:
                signOut();
                break;
        }
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent firstIntent = new Intent(FavoritesActivity.this, FirstActivity.class);
        Toast.makeText(FavoritesActivity.this, "Signed out.",
                Toast.LENGTH_SHORT).show();
        startActivity(firstIntent);
    }
}
