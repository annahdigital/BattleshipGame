package com.example.battleshipgame.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.example.battleshipgame.Grid.FieldView;
import com.example.battleshipgame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class CreateFieldActivity extends AppCompatActivity {

    private FieldView fieldView;
    private PopupWindow mPopupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_field);
        fieldView = findViewById(R.id.player_field);
        fieldView.createField();
        FloatingActionButton fab = findViewById(R.id.floatingActionButtonInfo2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHint();
            }
        });
        Button goToGameButton = findViewById(R.id.get_started_field_created);
        goToGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fieldView.endCreation())
                    showError();
            }
        });
    }

    private void showHint()
    {
        Context mContext = getApplicationContext();
        // popup window for entering rss
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.rules, null);
        mPopupWindow = new PopupWindow(
                customView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
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

    private void showError()
    {
        Toast toast = Toast.makeText(this,
                "Incorrect placement for ships.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(this);
        catImageView.setImageResource(R.drawable.kitty_wow);
        toastContainer.addView(catImageView, 0);
        toast.show();
    }
}
