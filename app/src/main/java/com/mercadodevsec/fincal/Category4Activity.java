package com.mercadodevsec.fincal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Category4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SetupButton(R.id.backButton, CategoryOptionsActivity.class);
        SetupButton(R.id.option1, Category4Option1Activity.class);
        SetupButton(R.id.option2, Category4Option2Activity.class);
        SetupButton(R.id.option3, Category4Option3Activity.class);
        SetupButton(R.id.option4, Category4Option4Activity.class);
        SetupButton(R.id.option5, Category4Option5Activity.class);
        SetupButton(R.id.option6, Category4Option6Activity.class);
        SetupButton(R.id.option7, Category4Option7Activity.class);
        SetupButton(R.id.option8, Category4Option8Activity.class);
        SetupButton(R.id.option9, Category4Option9Activity.class);
    }
    private void SetupButton(int buttonID, Class<?> activityClass) {
        Button button = findViewById(buttonID);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        });
    }
}
