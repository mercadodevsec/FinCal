package com.mercadodevsec.fincal;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Category1Option1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CheckBox includeTaxesCheckBox = findViewById(R.id.includeTaxesCheckBox);
        LinearLayout additionalCostsLayout = findViewById(R.id.additionalCostsLayout);

        includeTaxesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                additionalCostsLayout.setVisibility(View.VISIBLE);
            } else {
                additionalCostsLayout.setVisibility(View.GONE);
            }
        });

        Button calculateButton = findViewById(R.id.calculateButton);
        TextView resultTextView = findViewById(R.id.resultTextView);
        EditText homePrice = findViewById(R.id.homePrice);
        EditText downPayment = findViewById(R.id.downPayment);
        EditText loanTerm = findViewById(R.id.loanTerm);
        EditText interestRate = findViewById(R.id.interestRate);
        EditText propertyTax = findViewById(R.id.propertyTax);
        EditText homeInsurance = findViewById(R.id.homeInsurance);
        EditText pmiInsurance = findViewById(R.id.pmiInsurance);
        EditText hoaFee = findViewById(R.id.hoaFee);
        EditText otherCost = findViewById(R.id.otherCosts);

        calculateButton.setOnClickListener(v -> {
            String priceStr = homePrice.getText().toString();
            String downPaymentStr = downPayment.getText().toString();
            String termStr = loanTerm.getText().toString();
            String rateStr = interestRate.getText().toString();

            if (!priceStr.isEmpty() && !downPaymentStr.isEmpty() && !termStr.isEmpty() && !rateStr.isEmpty()) {
                try {
                    double p = Double.parseDouble(priceStr);
                    double dp = Double.parseDouble(downPaymentStr);
                    p = p - dp;
                    double r = Double.parseDouble(rateStr) / 100 / 12;
                    int n = Integer.parseInt(termStr) * 12;

                    if (n <= 0) {
                        Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double monthlyPayment;
                    if (r == 0) {
                        monthlyPayment = p / n;
                    } else {
                        monthlyPayment = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
                    }

                    if (includeTaxesCheckBox.isChecked()) {
                        String taxStr = propertyTax.getText().toString();
                        String insuranceStr = homeInsurance.getText().toString();
                        String pmiStr = pmiInsurance.getText().toString();
                        String hoaStr = hoaFee.getText().toString();
                        String otherStr = otherCost.getText().toString();

                        double tax = taxStr.isEmpty() ? 0 : (Double.parseDouble(taxStr) / 100 / 12) * (p + dp);
                        double ins = insuranceStr.isEmpty() ? 0 : Double.parseDouble(insuranceStr) / 12;
                        double pmi = pmiStr.isEmpty() ? 0 : (Double.parseDouble(pmiStr)) / 12;
                        double hoa = hoaStr.isEmpty() ? 0 : Double.parseDouble(hoaStr) / 12;
                        double other = otherStr.isEmpty() ? 0 : Double.parseDouble(otherStr) / 12;

                        monthlyPayment += tax + ins + pmi + hoa + other;
                    }

                    resultTextView.setText(String.format(Locale.getDefault(), "Result: $%.2f", monthlyPayment));
                    resultTextView.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
            }
        });
    }
}