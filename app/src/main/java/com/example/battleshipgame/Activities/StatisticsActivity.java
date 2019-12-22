package com.example.battleshipgame.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.example.battleshipgame.Adapters.StatisticsAdapter;
import com.example.battleshipgame.Models.GameStatistics;
import com.example.battleshipgame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class StatisticsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference statsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ArrayList<GameStatistics> statistics;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mProgressBar = findViewById(R.id.progressBarStats);
        mRecyclerView = findViewById(R.id.stats_list);
        database = FirebaseDatabase.getInstance();
        statsRef = database.getReference("stats");
        statistics = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Map.Entry<String, Object> item : ((Map<String,Object>) dataSnapshot.getValue()).entrySet())
                {
                    Map itemStat = (Map) item.getValue();
                    if (currentUser.getDisplayName().equals(String.valueOf(itemStat.get("player_1"))) ||
                                currentUser.getDisplayName().equals(String.valueOf(itemStat.get("player_2")))) {
                        statistics.add(new GameStatistics(String.valueOf(itemStat.get("player_1")),
                                String.valueOf(itemStat.get("player_2")),
                                (int) (long) itemStat.get("score_1"),
                                (int) (long) itemStat.get("score_2")));
                    }
                }
                if (statistics.size() != 0) {
                    mRecyclerView.setAdapter(new StatisticsAdapter(statistics));
                    mProgressBar.setVisibility(View.GONE);
                }
                else
                    showNoStats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showNoStats()
    {
        Context mContext = getApplicationContext();
        // popup window for entering rss
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.no_stats, null);
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        mPopupWindow.setElevation(5.0f);
        findViewById(R.id.stats_holder).post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.showAtLocation(findViewById(R.id.stats_holder), Gravity.CENTER,0,0);
            }
        });
        Button okButton = customView.findViewById(R.id.ok_no_stats);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                finish();
            }
        });
    }
}
