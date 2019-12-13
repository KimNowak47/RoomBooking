package com.example.roombooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class RoomInfoActivity extends AppCompatActivity
{

    ArrayList<Reservation> reservations = new ArrayList<>();
    Room room;
    Button reservationButton;
    ListView roomInfoList;

    ArrayAdapter<Reservation> arrayAdapter;

    Button bookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);


        room = (Room) getIntent().getSerializableExtra("ROOM");
        reservationButton = findViewById(R.id.bookButton);
        roomInfoList = findViewById(R.id.roomInfoListView);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reservations);

        roomInfoList.setAdapter(arrayAdapter);

        getReservations(room.getId());

        bookButton = findViewById(R.id.bookButton);

        bookButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReservationActivity.class);
                intent.putExtra("RoomId", room.getId());
                startActivity(intent);
            }
        });

    }

    private void getReservations(int roomId)
    {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/room/" + roomId).build();
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
                        Reservation[] Reserve = gson.fromJson((response.body()).string(), Reservation[].class);
                        reservations.clear();
                        reservations.addAll(Arrays.asList(Reserve));
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                            arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }

            }
        });
    }
}
