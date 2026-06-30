package com.mercadodevsec.fincal;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class Category1Option6Activity extends AppCompatActivity {

    private EditText salaryIncome, pension, investmentSavings, otherIncome;
    private EditText rentalCost, mortgage, propertyTax, hoaFee, homeInsurance, creditCards, studentLoan, autoLoan, otherLoans;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option6);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Income views
        salaryIncome = findViewById(R.id.salaryIncome);
        pension = findViewById(R.id.pension);
        investmentSavings = findViewById(R.id.investmentSavings);
        otherIncome = findViewById(R.id.otherIncome);

        // Initialize Debt views
        rentalCost = findViewById(R.id.rentalCost);
        mortgage = findViewById(R.id.mortgage);
        propertyTax = findViewById(R.id.propertyTax);
        hoaFee = findViewById(R.id.hoaFee);
        homeInsurance = findViewById(R.id.homeInsurance);
        creditCards = findViewById(R.id.creditCards);
        studentLoan = findViewById(R.id.studentLoan);
        autoLoan = findViewById(R.id.autoLoan);
        otherLoans = findViewById(R.id.otherLoans);

        resultTextView = findViewById(R.id.resultTextView);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(v -> calculateDTI());
    }

    private void calculateDTI() {
        try {
            double totalAnnualIncome = getDouble(salaryIncome) + getDouble(pension) + getDouble(investmentSavings) + getDouble(otherIncome);
            
            double monthlyHousingDebt = getDouble(rentalCost) + getDouble(mortgage) + getDouble(propertyTax) + getDouble(hoaFee) + getDouble(homeInsurance);
            double otherMonthlyDebt = getDouble(creditCards) + getDouble(studentLoan) + getDouble(autoLoan) + getDouble(otherLoans);
            double totalMonthlyDebt = monthlyHousingDebt + otherMonthlyDebt;

            if (totalAnnualIncome <= 0) {
                Toast.makeText(this, "Please enter your income", Toast.LENGTH_SHORT).show();
                return;
            }

            double monthlyIncome = totalAnnualIncome / 12.0;
            double frontEndDti = (monthlyHousingDebt / monthlyIncome) * 100.0;
            double backEndDti = (totalMonthlyDebt / monthlyIncome) * 100.0;

            String statusMessage;
            if (backEndDti <= 35) {
                statusMessage = "Your DTI ratio is <b>good</b>.";
            } else if (backEndDti < 50) {
                statusMessage = "Your DTI ratio is in the <b>mid-range</b>. While it is not considered low, certain actions can be taken to lower it.";
            } else {
                statusMessage = "Your DTI ratio is <b>high</b>, and may put you in more financially-risky situations. We recommend taking actions to lower it.";
            }

            DecimalFormat df = new DecimalFormat("#,##0");
            DecimalFormat pf = new DecimalFormat("0");

            String result = "Front-End DTI: <b>" + pf.format(frontEndDti) + "%</b><br/>" +
                    "Back-End DTI: <b>" + pf.format(backEndDti) + "%</b>.<br/>" +
                    statusMessage + "<br/><br/>" +
                    "Total Income:\t$" + df.format(totalAnnualIncome) + " / year or<br/>" +
                    "$" + df.format(monthlyIncome) + " / month<br/><br/>" +
                    "Total Debt:\t$" + df.format(totalMonthlyDebt * 12) + " / year or<br/>" +
                    "$" + df.format(totalMonthlyDebt) + " / month";

            resultTextView.setText(Html.fromHtml(result, Html.FROM_HTML_MODE_LEGACY));

        } catch (Exception e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private double getDouble(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty() ? 0 : Double.parseDouble(text);
    }
}
