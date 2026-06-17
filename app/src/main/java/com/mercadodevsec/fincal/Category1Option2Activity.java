package com.mercadodevsec.fincal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Category1Option2Activity extends AppCompatActivity {

    private EditText loanAmount, loanTerm, interestRate;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loanAmount = findViewById(R.id.loanAmount);
        loanTerm = findViewById(R.id.loanTerm);
        interestRate = findViewById(R.id.interestRate);
        Button calculateButton = findViewById(R.id.calculateButton);
        fragmentContainer = findViewById(R.id.fragment_container);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentContainer.getVisibility() == View.VISIBLE) {
                    hideTableFragment();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        calculateButton.setOnClickListener(v -> {
            String amountStr = loanAmount.getText().toString();
            String termStr = loanTerm.getText().toString();
            String rateStr = interestRate.getText().toString();

            if (amountStr.isEmpty() || termStr.isEmpty() || rateStr.isEmpty()) {
                Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double principal = Double.parseDouble(amountStr);
                double years = Double.parseDouble(termStr);
                double rate = Double.parseDouble(rateStr);

                if (years <= 0) {
                    Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                    return;
                }

                showTableFragment(principal, years, rate);
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTableFragment(double principal, double years, double rate) {
        TableFragment fragment = TableFragment.newInstance(principal, years, rate);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        fragmentContainer.setVisibility(View.VISIBLE);
    }

    public void hideTableFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        fragmentContainer.setVisibility(View.GONE);
    }
}
