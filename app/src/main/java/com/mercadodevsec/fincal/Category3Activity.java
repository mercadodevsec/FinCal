package com.mercadodevsec.fincal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Category3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SetupButton(R.id.backButton, CategoryOptionsActivity.class);
        SetupButton(R.id.option1, Category3Option1Activity.class);
        SetupButton(R.id.option2, Category3Option2Activity.class);
        SetupButton(R.id.option3, Category3Option3Activity.class);
        SetupButton(R.id.option4, Category3Option4Activity.class);
        SetupButton(R.id.option5, Category3Option5Activity.class);
        SetupButton(R.id.option6, Category3Option6Activity.class);
        SetupButton(R.id.option7, Category3Option7Activity.class);
        SetupButton(R.id.option8, Category3Option8Activity.class);
        SetupButton(R.id.option9, Category3Option9Activity.class);
        SetupButton(R.id.option10, Category3Option10Activity.class);
        SetupButton(R.id.option11, Category3Option11Activity.class);
        SetupButton(R.id.option12, Category3Option12Activity.class);
        SetupButton(R.id.option13, Category3Option13Activity.class);
        SetupButton(R.id.option14, Category3Option14Activity.class);
        SetupButton(R.id.option15, Category3Option15Activity.class);
        SetupButton(R.id.option16, Category3Option16Activity.class);
    }
    private void SetupButton(int buttonID, Class<?> activityClass) {
        Button button = findViewById(buttonID);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        });
    }
}
