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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity contains the search funtion. A user can look for books here. And after searching
 * the user can click on a title to open the details page about a certain volume.
 */

public class SearchActivity extends AppCompatActivity {
    ListView resultListView;
    ArrayAdapter adapter;
    RequestQueue queue;
    List<String> list = new ArrayList<>();
    List<String> idList = new ArrayList<>();
    EditText searchBar;
    String url = "https://www.googleapis.com/books/v1/volumes?maxResults=40&q=";
    String newUrl;
    String query;
    private FirebaseAuth mAuth;


    /**
     * Initializes the firebase objects and finds the views.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();
        resultListView = findViewById(R.id.resultListView);
        searchBar = findViewById(R.id.searchBar);
        queue = Volley.newRequestQueue(this);
    }


    /**
     * When a user hits the search button the app makes a GET request to the api server using
     * the search terms the user put in the searchbar editText. Each returned volume is added
     * to a list and updatelistview is called to show it in the UI.
     */
    public void onSearchClick(View view) {
        query = searchBar.getText().toString();
        newUrl = url+query;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", "onResponse: " + response);

                        parseJSONResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SEARCH", "SearchActivity: wrong");
            }
        });
        queue.add(stringRequest);
    }

    private void parseJSONResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONArray arr = obj.getJSONArray("items");

            list = new ArrayList<>();
            idList = new ArrayList<>();

            for(int i = 0; i < arr.length(); i++) {
                JSONObject volume = arr.getJSONObject(i);
                String title = volume.getJSONObject("volumeInfo").getString("title");
                Log.d("RESPONSE", "title: " + title);
                String id = volume.getString("id");
                Log.d("RESPONSE", "id: " + id);
                list.add(title);
                idList.add(id);
            }

            updateListView();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setListener();
        updateListView();
    }

    private void setListener() {
        // when the list is
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(SearchActivity.this, VolumeActivity.class);
                String chosenId = idList.get(position);
                intent.putExtra("Id", chosenId);
                startActivity(intent);
            }
        });
    }


    /**
     * If user is not logged he gets sent back to the firstActivity.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(SearchActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Updates the listview according to the new lists.
     */
    private void updateListView() {
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        resultListView.setAdapter(adapter);
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
                Intent intent = new Intent(SearchActivity.this, FavoritesActivity.class);
                startActivity(intent);
                break;
            case R.id.signOutMenuItem:
                mAuth.signOut();
                Intent firstIntent = new Intent(SearchActivity.this, FirstActivity.class);
                Toast.makeText(SearchActivity.this, "Signed out.",
                        Toast.LENGTH_SHORT).show();
                startActivity(firstIntent);
                break;
        }
        return true;
    }
}
