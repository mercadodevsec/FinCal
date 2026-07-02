package com.mercadodevsec.fincal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Category1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SetupButton(R.id.backButton, CategoryOptionsActivity.class);
        SetupButton(R.id.option1, Category1Option1Activity.class);
        SetupButton(R.id.option2, Category1Option2Activity.class);
        SetupButton(R.id.option3, Category1Option3Activity.class);
        SetupButton(R.id.option4, Category1Option4Activity.class);
        SetupButton(R.id.option5, Category1Option5Activity.class);
        SetupButton(R.id.option6, Category1Option6Activity.class);
        SetupButton(R.id.option7, Category1Option7Activity.class);
        SetupButton(R.id.option8, Category1Option8Activity.class);
        SetupButton(R.id.option9, Category1Option9Activity.class);
        SetupButton(R.id.option10, Category1Option10Activity.class);
        SetupButton(R.id.option11, Category1Option11Activity.class);
        SetupButton(R.id.option12, Category1Option12Activity.class);
        SetupButton(R.id.option13, Category1Option13Activity.class);
        SetupButton(R.id.option14, Category1Option14Activity.class);
        SetupButton(R.id.option15, Category1Option15Activity.class);
    }

    private void SetupButton(int buttonID, Class<?> activityClass) {
        Button button = findViewById(buttonID);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        });
    }
}