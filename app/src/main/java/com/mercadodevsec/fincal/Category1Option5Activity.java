package com.mercadodevsec.fincal;

import android.text.Html;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class Category1Option5Activity extends AppCompatActivity {

    private EditText annualIncomeEditText, monthlyDebtEditText;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option5);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        annualIncomeEditText = findViewById(R.id.annualIncome);
        monthlyDebtEditText = findViewById(R.id.monthlyDebt);
        resultTextView = findViewById(R.id.resultTextView);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(v -> calculateRent());
    }

    private void calculateRent() {
        String annualStr = annualIncomeEditText.getText().toString();
        String debtStr = monthlyDebtEditText.getText().toString();

        if (annualStr.isEmpty()) {
            Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double annualIncome = Double.parseDouble(annualStr);
            double monthlyDebt = debtStr.isEmpty() ? 0 : Double.parseDouble(debtStr);

            String result = getString(annualIncome, monthlyDebt);

            resultTextView.setText(Html.fromHtml(result, Html.FROM_HTML_MODE_LEGACY));
            resultTextView.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private static String getString(double annualIncome, double monthlyDebt) {
        double monthlyIncome = annualIncome / 12.0;

        // Using 28/36 rule for rental affordability
        // Recommended: 28% of gross monthly income minus debts
        // Max: 36% of gross monthly income minus debts
        double recommendedRent = (monthlyIncome * 0.28) - monthlyDebt;
        double maxRent = (monthlyIncome * 0.36) - monthlyDebt;

        if (maxRent < 0) maxRent = 0;
        if (recommendedRent < 0) recommendedRent = 0;

        DecimalFormat df = new DecimalFormat("#,##0");

        return "You can afford up to <b>$" + df.format(maxRent) + "</b> per month on a rental payment.<br/><br/>" +
                "It is recommended to keep your rental payment below $" + df.format(recommendedRent) + " per month.";
    }
}
