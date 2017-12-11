package axelbremer.axelbremerpset6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        viewedUid = loggedInUser.getUid();

        favoritesListView = findViewById(R.id.favoritesListView);
        nameTextView = findViewById(R.id.nameTextView);
        searchUserBar = findViewById(R.id.searchUserBar);

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

        favoritesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                if(viewedUid.equals(loggedInUser.getUid())){
                    mDatabase = db.getReference();
                    nameList.remove(pos);
                    idList.remove(pos);
                    Favorites fav;
                    if(idList.size() == 0){
                        fav = new Favorites();
                        Toast.makeText(FavoritesActivity.this, "Gotta have at least 1 favorite.",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        fav = new Favorites(nameList, idList);
                    }
                    mDatabase.child("favorites").child(viewedUid).setValue(fav);
                    updateListView(loggedInUser.getUid(), loggedInUser.getEmail());
                    return true;
                } else {
                    Toast.makeText(FavoritesActivity.this, "Can't delete from someone else's favorites.",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateListView(currentUser.getUid(), currentUser.getEmail());
    }

    private void updateListView(String uid, String email) {
        try {
            mDatabase = db.getReference("favorites").child(uid);

            Log.d("Favorites", "uid" + uid + "loggeduid" + loggedInUser.getUid());

            if (uid.equals(loggedInUser.getUid())) {
                nameTextView.setText("Your favorites");
            } else {
                nameTextView.setText(email + "'s favorites");
            }

            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Favorites fav = dataSnapshot.getValue(Favorites.class);

                    setLists(fav);

                    adapter = new ArrayAdapter(FavoritesActivity.this, android.R.layout.simple_list_item_1, nameList);

                    favoritesListView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("FavoritesActivity", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }

            };
            mDatabase.addListenerForSingleValueEvent(postListener);
        } catch (Exception e){
            Toast.makeText(FavoritesActivity.this, "User not found.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setLists(Favorites fav) {
        idList = fav.getIdList();
        nameList = fav.getNameList();
    }

    public void onFavoritesSearchClick(View view) {
        String email = searchUserBar.getText().toString().replace('.',',');
        mDatabase = db.getReference("emailToUid").child(email);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
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
}
