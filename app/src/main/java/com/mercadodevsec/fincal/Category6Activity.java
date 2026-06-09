package com.mercadodevsec.fincal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Category6Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category6);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SetupButton(R.id.backButton, CategoryOptionsActivity.class);
        SetupButton(R.id.option1, Category6Option1Activity.class);
        SetupButton(R.id.option2, Category6Option2Activity.class);
        SetupButton(R.id.option3, Category6Option3Activity.class);
        SetupButton(R.id.option4, Category6Option4Activity.class);
        SetupButton(R.id.option5, Category6Option5Activity.class);
        SetupButton(R.id.option6, Category6Option6Activity.class);
        SetupButton(R.id.option7, Category6Option7Activity.class);
        SetupButton(R.id.option8, Category6Option8Activity.class);
        SetupButton(R.id.option9, Category6Option9Activity.class);
        SetupButton(R.id.option10, Category6Option10Activity.class);
        SetupButton(R.id.option11, Category6Option11Activity.class);
        SetupButton(R.id.option12, Category6Option12Activity.class);
        SetupButton(R.id.option13, Category6Option13Activity.class);
        SetupButton(R.id.option14, Category6Option14Activity.class);
        SetupButton(R.id.option15, Category6Option15Activity.class);
        SetupButton(R.id.option16, Category6Option16Activity.class);
        SetupButton(R.id.option17, Category6Option17Activity.class);
        SetupButton(R.id.option18, Category6Option18Activity.class);
        SetupButton(R.id.option19, Category6Option19Activity.class);
        SetupButton(R.id.option20, Category6Option20Activity.class);
        SetupButton(R.id.option21, Category6Option21Activity.class);
        SetupButton(R.id.option22, Category6Option22Activity.class);
    }
    private void SetupButton(int buttonID, Class<?> activityClass) {
        Button button = findViewById(buttonID);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        });
    }
}
