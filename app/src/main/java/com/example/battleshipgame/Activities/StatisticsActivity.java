package com.example.battleshipgame.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.battleshipgame.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatisticsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }
}
