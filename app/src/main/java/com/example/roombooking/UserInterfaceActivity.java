package com.example.roombooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

public class UserInterfaceActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ArrayList<Reservation> myReservations = new ArrayList<>();
    Room room;
    ListView myResListView;
    ArrayAdapter<Reservation> arrayAdapter;
    SwipeRefreshLayout sr;
    TextView userInfoView;
    int selectedItem = -1;
    View selectedView;
    Button delBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);

        myResListView = findViewById(R.id.reservationListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myReservations);
        myResListView.setAdapter(arrayAdapter);
        userInfoView = findViewById(R.id.userInfoView);

        delBtn = findViewById(R.id.deleteButton);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteReservation();
            }
        });

        showMyReservations(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }
        private void showMyReservations(String userId) {
            OkHttpClient client = new OkHttpClient();
            Log.d("TAG", userId);
            final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/user/" + userId).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code" + response);
                    else {

                        Gson gson = new Gson();
                        Reservation[] Reserve = gson.fromJson(response.body().string(), Reservation[].class);
                        Log.d("TAG", "Number of reservations received: " + Reserve.length);
                        myReservations.clear();
                        myReservations.addAll(Arrays.asList(Reserve));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("TAG", "run bitch");
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    sr.setRefreshing(false);
                }
            });
        }
}
