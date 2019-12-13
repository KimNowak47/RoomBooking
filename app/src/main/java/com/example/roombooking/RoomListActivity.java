package com.example.roombooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoomListActivity extends AppCompatActivity
{

    ArrayList<Room> rooms = new ArrayList<>();
    ArrayAdapter<Room> roomArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        roomArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rooms);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent itemIntent = new Intent(getBaseContext(), RoomInfoActivity.class);
                Room room = (Room) parent.getItemAtPosition(position);
                itemIntent.putExtra("ROOM", room);
                startActivity(itemIntent);
            }
        };

        ListView listView = findViewById(R.id.roomListView);

        listView.setAdapter(roomArrayAdapter);

        listView.setOnItemClickListener(itemClickListener);

        getRooms();
    }

    private void getRooms()
    {
        Log.d("TAG", "Getting room from rest");
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Rooms").build();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code" + response);
                else
                    {
                        Gson gson = new Gson();
                        Room[] Rooms = gson.fromJson((response.body()).string(), Room[].class);
                        Log.d("TAG", Rooms.toString());
                        rooms.addAll(Arrays.asList(Rooms));
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                            roomArrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
            }
        });
    }


}
