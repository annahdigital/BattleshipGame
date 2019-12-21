package com.example.battleshipgame.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.battleshipgame.Grid.FieldView;
import com.example.battleshipgame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.UUID;

public class CreateFieldActivity extends AppCompatActivity {

    private FieldView fieldView;
    private PopupWindow mPopupWindow;
    private PopupWindow idPopupWindow;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_field);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        fieldView = findViewById(R.id.player_field);
        fieldView.createField();;
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
                else {
                    getGameId();
                }
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

    private void getGameId()
    {
        Context mContext = getApplicationContext();
        // popup window for entering rss
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = Objects.requireNonNull(inflater).inflate(R.layout.game_id_layout, null);
        idPopupWindow = new PopupWindow(
                customView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                true
        );
        idPopupWindow.setElevation(5.0f);
        findViewById(R.id.create_field_layout).post(new Runnable() {
            @Override
            public void run() {
                idPopupWindow.showAtLocation(findViewById(R.id.create_field_layout), Gravity.CENTER,0,0);
            }
        });

        final TextView idView = customView.findViewById(R.id.game_id_view);
        UUID U_id = UUID.randomUUID();
        String id = U_id.toString().replace("-", "");
        idView.setText(id);

        Button okButton = customView.findViewById(R.id.game_id_getting_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idPopupWindow.dismiss();
            }
        });

        Button copyButton = customView.findViewById(R.id.copy_to_clipboard);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", idView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Id copied to Clipboard",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToGame()
    {
        Intent intent = new Intent(this, GameActivity.class);
        this.startActivity(intent);
        this.finish();
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
