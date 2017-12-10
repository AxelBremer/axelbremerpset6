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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        resultListView = findViewById(R.id.resultListView);
        searchBar = findViewById(R.id.searchBar);
        queue = Volley.newRequestQueue(this);
    }


    public void onSearchClick(View view) {
        query = searchBar.getText().toString();
        newUrl = url+query;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", "onResponse: " + response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("items");

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

                        updateListView();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("SHIT", "onErrorResponse: wrong");
            }
        });
        queue.add(stringRequest);
    }

    private void updateListView() {
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        resultListView.setAdapter(adapter);
    }
}
