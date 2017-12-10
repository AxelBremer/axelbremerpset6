package axelbremer.axelbremerpset6;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);
        Intent intent = getIntent();
        queue = Volley.newRequestQueue(this);

        titleTextView = findViewById(R.id.titleTextView);
        descTextView = findViewById(R.id.descTextView);
        authorsTextView = findViewById(R.id.authorsTextView);
        thumbnailImageView = findViewById(R.id.thumbnailImageView);

        id = intent.getStringExtra("Id");

        newUrl = "https://www.googleapis.com/books/v1/volumes/"+id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, newUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", "onResponse: " + response);

                        try {
                            JSONObject volume = new JSONObject(response);
                            String title = volume.getJSONObject("volumeInfo").getString("title");
                            String desc = volume.getJSONObject("volumeInfo").getString("description");
                            String imageUrl = volume.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("medium");
                            String authors = "";
                            JSONArray arr = volume.getJSONObject("volumeInfo").getJSONArray("authors");

                            for(int i = 0; i < arr.length(); i++) {
                                authors += arr.getString(i);
                            }

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

    private void updateViews() {


        titleTextView.setText(currentVolume.getTitle());
        descTextView.setText(Html.fromHtml(currentVolume.getDesc(), Html.FROM_HTML_MODE_COMPACT));
        authorsTextView.setText(currentVolume.getAuthors());

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

    private void refreshImage() {
        thumbnailImageView.setImageBitmap(bmp);
    }

    private void setImage(Bitmap b) {
        bmp = b;
    }
}
