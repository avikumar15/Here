package com.here;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.here.models.LocalBusiness;
import com.here.models.MyLocationListener;
import com.here.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    public static double latestLatitude = -1.0;
    public static double latestLongitude = -1.0;
    private static int messageIndex = 100;
    private LocationManager locationManager;
    private TextInputLayout messageInputLayout;
    private TextInputEditText messageInputEditText;
    private FloatingActionButton sendMessageFab;
    private RecyclerView messageRecycler;
    private MessageListAdapter messageListAdapter;
    private List<Message> messageList;
    private List<LocalBusiness> localBusinessList;
    private User self = new User("you");
    private User bot = new User("HelpMeBot");
    List<String> urls;
    String base = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageInputLayout = findViewById(R.id.messageInputLayout);
        messageInputEditText = findViewById(R.id.messageInputEditText);
        sendMessageFab = findViewById(R.id.sendMessageFab);

        messageRecycler = findViewById(R.id.messageListRecyclerView);

        setListenerOnFab();

        messageList = new ArrayList<>();
        localBusinessList = new ArrayList<>();
        urls = new ArrayList<>();

        messageListAdapter = new MessageListAdapter(this, messageList);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageRecycler.setAdapter(messageListAdapter);

    }

    private void setListenerOnFab() {
        sendMessageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence typedMessage = messageInputEditText.getText();
                if (typedMessage != null) {
                    String userMessage = typedMessage.toString().trim();
                    if (!userMessage.trim().isEmpty()) {
                        if(userMessage.split(":")[0].equals("https")) {
                            base = userMessage;
                            addMessage(userMessage, self, 1);
                            addMessage("Bot connected!", bot, 2);
                        } else if(base.equals("")) {
                            Toast.makeText(MainActivity.this, "Abey Sale", Toast.LENGTH_SHORT).show();
                        } else
                            sendMessage(userMessage);
                    }
                }
                messageInputEditText.setText("");
                urls.add("");
            }
        });
    }

    private void sendMessage(final String message) {
        addMessage(message, self, 1);

        final RequestQueue queue = Volley.newRequestQueue(this);
        String url = base + "chatbot/"+message;

        Log.i("URL", url);

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("onResponse", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("reply");
                    Log.i("result", result);
                    addMessage(result, bot, 2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String errorString = error.toString();
                if (response != null && response.data != null) {
                    errorString = new String(response.data);
                    Log.i("log error", errorString);
                }
                Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("body", message);
                params.put("latitude", latestLatitude + "");
                params.put("longitude", latestLongitude + "");
                Log.i("sending", params.toString());
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void addMessage(String message, User sender, int type) {
        messageList.add(new Message(message, sender, Calendar.getInstance().getTimeInMillis(), type));
        messageListAdapter.setList(messageList);
        messageListAdapter.notifyDataSetChanged();
        messageRecycler.smoothScrollToPosition(messageList.size() - 1);
    }
}