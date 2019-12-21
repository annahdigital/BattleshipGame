package com.example.battleshipgame.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.battleshipgame.Grid.FieldView;
import com.example.battleshipgame.Models.Field;
import com.example.battleshipgame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class GameActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String gameId;
    private boolean currentPlayer;

    private FieldView player_1_field;
    private FieldView player_2_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        TextView player_1_name = findViewById(R.id.player1_name);
        TextView player_2_name = findViewById(R.id.player2_name);

        player_1_field = findViewById(R.id.player1_field);
        player_2_field = findViewById(R.id.player2_field);



    }
}
