package axelbremer.axelbremerpset6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    List<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        favoritesListView = findViewById(R.id.favoritesListView);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateListView(currentUser);
    }

    private void updateListView(FirebaseUser currentUser) {
        String uid = currentUser.getUid();
        mDatabase = db.getReference("favorites").child(uid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Favorites fav = dataSnapshot.getValue(Favorites.class);

                setIdList(fav);

                adapter = new ArrayAdapter(FavoritesActivity.this, android.R.layout.simple_list_item_1, fav.getNameList());

                favoritesListView.setAdapter(adapter);

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("FavoritesActivity", "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        };
        mDatabase.addValueEventListener(postListener);
    }

    private void setIdList(Favorites fav) {
        idList = fav.getIdList();
    }
}
