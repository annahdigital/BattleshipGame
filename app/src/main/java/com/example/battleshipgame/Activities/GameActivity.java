package com.example.battleshipgame.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.battleshipgame.Grid.CurrentFieldMode;
import com.example.battleshipgame.Models.MoveType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import com.example.battleshipgame.Grid.FieldView;
import com.example.battleshipgame.Models.Field;
import com.example.battleshipgame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference game;
    private DatabaseReference player_1_field_db;
    private DatabaseReference player_2_field_db;
    private DatabaseReference player_1;
    private DatabaseReference player_2;
    private DatabaseReference player_1_score;
    private DatabaseReference player_2_score;
    private DatabaseReference currentMove;

    private String gameId;
    private boolean started_game;
    private boolean secondPlayerJoined = false;

    private PopupWindow mPopupWindow;
    private FieldView player_1_field;
    private FieldView player_2_field;
    private TextView player_1_name;
    private TextView player_2_name;
    private TextView player_1_name_field;
    private TextView player_2_name_field;
    private  int score1 = 0;
    private  int score2 = 0;
    private final int winPoints = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        gameId = getIntent().getStringExtra("id");
        started_game = getIntent().getBooleanExtra("start", false);

        player_1_name = findViewById(R.id.player1_name);
        player_2_name = findViewById(R.id.player2_name);
        player_1_name_field = findViewById(R.id.player_1_field_name);
        player_2_name_field = findViewById(R.id.player_2_field_name);

        player_1_field = findViewById(R.id.player1_field);
        player_2_field = findViewById(R.id.player2_field);
        player_1_field.initializeField(CurrentFieldMode.PLAYER);
        player_2_field.initializeField(CurrentFieldMode.OPPONENT);
        player_2_field.setFieldMode(CurrentFieldMode.READONLY);

        database = FirebaseDatabase.getInstance();
        game = database.getReference("games").child(gameId);
        player_1_field_db = game.child("player_1_field");
        player_2_field_db = game.child("player_2_field");
        player_1_score = game.child("score_1");
        player_2_score = game.child("score_2");
        currentMove = game.child("currentMoveByPlayer");
        player_1 = game.child("player_1");
        player_2 = game.child("player_2");

        initFirstPlayerField();
        initSecondPlayerField();
        trackCurrentMove();
        trackScore1Update();
        trackScore2Update();

        initStatsView();

       final FloatingActionButton showHintButton = findViewById(R.id.floatingActionButtonInfo);
       showHintButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               showHint();
           }
       });

    }

    public void updatingMove(MoveType moveType)
    {
        if (moveType == MoveType.MISS)
        {
            if (started_game)
                currentMove.setValue("p_2_move");
            else
                currentMove.setValue("p_1_move");
        }
        else if (moveType == MoveType.HIT)
        {
            currentMoveStatusMessage();
            if (started_game) {
                score1++;
                player_1_score.setValue(score1);
            }
            else {
                score2++;
                player_2_score.setValue(score2);
            }
        }
        else return;
        Gson gson = new Gson();
        String jsonField1 = gson.toJson(player_1_field.getField());
        String jsonField2 = gson.toJson(player_2_field.getField());
        if (started_game)
        {
            player_1_field_db.setValue(jsonField1);
            player_2_field_db.setValue(jsonField2);
        }
        else {
            player_1_field_db.setValue(jsonField2);
            player_2_field_db.setValue(jsonField1);
        }

    }

    private void trackCurrentMove()
    {
        currentMove.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!secondPlayerJoined)
                    return;
                if (dataSnapshot.getValue(String.class) == null)
                {
                    onBackPressed();
                    return;
                }
                String value = dataSnapshot.getValue(String.class);
                if (started_game)
                {
                    if (value == "p_1_move")
                    {
                        currentMoveMessage(true);
                        player_2_field.setFieldMode(CurrentFieldMode.OPPONENT);
                    }
                    else {
                        currentMoveMessage(false);
                        player_2_field.setFieldMode(CurrentFieldMode.READONLY);
                    }
                }
                else {
                    if (value == "p_2_move")
                    {
                        currentMoveMessage(true);
                        player_2_field.setFieldMode(CurrentFieldMode.OPPONENT);
                    }
                    else
                    {
                        currentMoveMessage(false);
                        player_2_field.setFieldMode(CurrentFieldMode.READONLY);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirstPlayerField()
    {
        player_1_field_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null)
                {
                    onBackPressed();
                    return;
                }
                if (dataSnapshot.getValue(String.class).isEmpty()) {
                    finish();
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<Field>() {}.getType();
                String value = dataSnapshot.getValue(String.class);
                if (started_game)
                    player_1_field.updateField((Field) gson.fromJson(value, type));
                else
                    player_2_field.updateField((Field) gson.fromJson(value, type));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initSecondPlayerField()
    {
        player_2_field_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null)
                {
                    onBackPressed();
                    return;
                }
                if (dataSnapshot.getValue(String.class).isEmpty() && secondPlayerJoined) {
                    finish();
                    return;
                }
                String value = dataSnapshot.getValue(String.class);
                if (value.isEmpty()) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Field>() {
                    }.getType();
                    secondPlayerJoined = true;
                    if (started_game)
                        player_2_field.updateField((Field) gson.fromJson(value, type));
                    else
                        player_1_field.updateField((Field) gson.fromJson(value, type));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        player_2_field_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class) == null)
                {
                    onBackPressed();
                    return;
                }
                String value = dataSnapshot.getValue(String.class);
                if (!value.isEmpty() && started_game)
                {
                    secondPlayerJoined = true;
                    Toast.makeText(getApplicationContext(),
                            "Opponent connects! Your move!",
                            Toast.LENGTH_SHORT).show();
                    player_2_field.setFieldMode(CurrentFieldMode.OPPONENT);
                    player_2_field_db.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initStatsView()
    {
        player_1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    onBackPressed();
                    return;
                }
                player_1_name.setText(value);
                player_1_name_field.setText(value);
                player_1.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        player_2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    onBackPressed();
                    return;
                }
                player_2_name.setText(value);
                player_2_name_field.setText(value);
                player_2.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        database.getReference("games").child(gameId).removeValue();

        super.onBackPressed();
    }

    private void currentMoveMessage(boolean your)
    {
        String mes;
        if (your)
            mes = "Your move!";
        else
            mes = "Second player's move!";
        Toast toast = Toast.makeText(this,
                mes, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(this);
        catImageView.setImageResource(R.drawable.kitty);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }

    private void currentMoveStatusMessage()
    {
        String mes = "You can hit one more time. :p";
        Toast toast = Toast.makeText(this,
                mes, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(this);
        catImageView.setImageResource(R.drawable.kitty);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }

    private void gameEnded(boolean didWin)
    {
        String mes;
        if (didWin)
            mes = "CONGRATULATIONS! YOU WIN!";
        else
            mes = "Unfortunately, you lost...";
        Toast toast = Toast.makeText(this,
                mes, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(this);
        if (didWin)
            catImageView.setImageResource(R.drawable.kitty_win);
        else
            catImageView.setImageResource(R.drawable.kitty_lost);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }

    private void showHint()
    {
        Context mContext = getApplicationContext();
        // popup window for entering rss
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.rules, null);
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        mPopupWindow.setElevation(5.0f);
        findViewById(R.id.create_field_layout).post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.showAtLocation(findViewById(R.id.create_field_layout), Gravity.CENTER,0,0);
            }
        });
        Button okButton = customView.findViewById(R.id.rules_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
    }

    private void trackScore1Update()
    {

    }

    private void trackScore2Update()
    {

    }
}
