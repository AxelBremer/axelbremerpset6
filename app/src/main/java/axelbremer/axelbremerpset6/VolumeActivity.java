package axelbremer.axelbremerpset6;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The volume activity shows the detail for the Volume with the given id.
 */

public class VolumeActivity extends AppCompatActivity {
    String newUrl;
    String id;
    RequestQueue queue;
    TextView titleTextView;
    TextView descTextView;
    TextView authorsTextView;
    ImageView thumbnailImageView;
    Bitmap bmp;
    Volume currentVolume;
    Favorites favorites;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference mDatabase;


    /**
     * Initializes the firebase objects and finds all the views.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);
        queue = Volley.newRequestQueue(this);

        titleTextView = findViewById(R.id.titleTextView);
        descTextView = findViewById(R.id.descTextView);
        authorsTextView = findViewById(R.id.authorsTextView);
        thumbnailImageView = findViewById(R.id.thumbnailImageView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        newVolume();
    }

    /**
     * Updates the ui according to the id given with the Intent. Gets the details using a GET
     * request to the google books API.
     */
    private void newVolume(){
        Intent intent = getIntent();

        id = intent.getStringExtra("Id");

        newUrl = "https://www.googleapis.com/books/v1/volumes/"+id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", "onResponse: " + response);

                        // Extracts the details from the JSON obtained from the api.
                        try {
                            JSONObject volume = new JSONObject(response);
                            String title = volume.getJSONObject("volumeInfo").optString("title");
                            String desc = volume.getJSONObject("volumeInfo").optString("description");
                            if(desc == ""){
                                desc = "no description";
                            }
                            JSONObject imageLinks = volume.getJSONObject("volumeInfo").optJSONObject("imageLinks");
                            String imageUrl = "";
                            if(imageLinks != null) {
                                imageUrl = imageLinks.optString("medium");
                                if(imageUrl == ""){
                                    imageUrl = imageLinks.optString("thumbnail");
                                }
                            }
                            String authors = "";
                            JSONArray arr = volume.getJSONObject("volumeInfo").optJSONArray("authors");

                            if(arr != null) {
                                for (int i = 0; i < arr.length(); i++) {
                                    authors += arr.getString(i);
                                }
                            }

                            // Save these details in a Volume object.
                            currentVolume = new Volume(title, authors, id, desc, imageUrl);

                            updateViews();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SHIT", "onErrorResponse: wrong");
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onResume(){
        super.onResume();
        newVolume();
    }

    // Update all the views according to the current Volume object.
    private void updateViews() {
        titleTextView.setText(currentVolume.getTitle());
        descTextView.setText(Html.fromHtml(currentVolume.getDesc(), Html.FROM_HTML_MODE_COMPACT));
        authorsTextView.setText(currentVolume.getAuthors());

        // Retrieve the image
        if(currentVolume.getImageUrl() != "") {
            new Thread() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL(currentVolume.getImageUrl());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap bmp = null;
                    try {
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        setImage(bmp);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshImage();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    private void refreshImage() {
        thumbnailImageView.setImageBitmap(bmp);
    }

    private void setImage(Bitmap b) {
        bmp = b;
    }


    /**
     * When the add to favorites button is clicked the volume is added to the favorites and the
     * new favorites are uploaded to the database.
     */
    public void addToFavorites(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = db.getReference("favorites").child(uid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favorites = dataSnapshot.getValue(Favorites.class);
                favorites.addVolume(currentVolume.getTitle(), currentVolume.getId());
                addToDatabase(favorites);
                Intent intent = new Intent(VolumeActivity.this, FavoritesActivity.class);
                startActivity(intent);
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
     * Uploads the given Favorites object to the database.
     */
    private void addToDatabase(Favorites favorites) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        mDatabase = db.getReference();

        mDatabase.child("favorites").child(uid).setValue(favorites);
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
                Intent intent = new Intent(VolumeActivity.this, FavoritesActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}

