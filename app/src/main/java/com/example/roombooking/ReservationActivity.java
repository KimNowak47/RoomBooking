package com.example.roombooking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReservationActivity extends AppCompatActivity
{


    private Button fromTimeButton;
    private Button toTimeButton;
    private Button dateButton;
    private Button reservationButton;

    EditText purposeText;
    LocalDate date;

    LocalTime fromTime;
    LocalTime toTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        fromTimeButton = findViewById(R.id.fromTimeButton);
        toTimeButton = findViewById(R.id.toTimeButton);
        dateButton = findViewById(R.id.fromDateButton);
        reservationButton = findViewById(R.id.reservationButton);

        purposeText = findViewById(R.id.reservationEditText);


        fromTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        fromTime = LocalTime.of(hourOfDay, minute);
                        fromTimeButton.setText(fromTime.getHour() + ":" + fromTime.getMinute());
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ReservationActivity.this, timeSetListener, currentHourOfDay, currentMinute, true);
                dialog.show();
            }
        });

        toTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        toTime = LocalTime.of(hourOfDay, minute);
                        toTimeButton.setText(fromTime.getHour() + ":" + fromTime.getMinute());
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(ReservationActivity.this, timeSetListener, currentHourOfDay, currentMinute, true);
                dialog.show();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date = LocalDate.of(year, month + 1, dayOfMonth);
                        dateButton.setText(date.format((DateTimeFormatter.ISO_DATE)));
                    }
                };
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDayOfMonth = calendar.get(Calendar.DATE);
                DatePickerDialog dialog = new DatePickerDialog(ReservationActivity.this, dateSetListener, currentYear, currentMonth, currentDayOfMonth);
                dialog.show();

            }
        });

        reservationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postReservation();
            }
        });
    }

    private void postReservation(){

        Log.d("TAG", "Started creating post reservation");


        long fromMilli = LocalDateTime.of(date, fromTime).toEpochSecond(OffsetDateTime.now().getOffset());

        long toMilli = LocalDateTime.of(date, toTime).toEpochSecond(OffsetDateTime.now().getOffset());

        int roomId = getIntent().getIntExtra("RoomId", -1);
        String purpose = purposeText.getText().toString();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Reservation r = new Reservation(fromMilli, toMilli, userId, purpose, roomId);

        String jsonReservation = new Gson().toJson(r);
        Log.d("TAG", jsonReservation);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(jsonReservation, JSON);

        final Request request = new Request.Builder().url("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations").post(requestBody).build();


        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException
            {
                if (!response.isSuccessful())
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), response.message(),Toast.LENGTH_SHORT).show();
                        }
                    });
                else {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }
        });


    }
}
