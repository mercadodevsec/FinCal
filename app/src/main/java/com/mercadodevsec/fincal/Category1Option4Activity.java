package com.mercadodevsec.fincal;

import android.text.Html;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class Category1Option4Activity extends AppCompatActivity {

    private EditText annualHouseholdIncome, loanTerm, interestRate, monthlyDebtPayback;
    private EditText downPayment, propertyTaxes, hoaFee, homeInsurance;
    private Spinner dtiRatioSpinner;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        annualHouseholdIncome = findViewById(R.id.annualHouseholdIncome);
        loanTerm = findViewById(R.id.loanTerm);
        interestRate = findViewById(R.id.interestRate);
        monthlyDebtPayback = findViewById(R.id.monthlyDebtPayback);
        downPayment = findViewById(R.id.downPayment);
        propertyTaxes = findViewById(R.id.propertyTaxes);
        hoaFee = findViewById(R.id.hoaFee);
        homeInsurance = findViewById(R.id.homeInsurance);
        dtiRatioSpinner = findViewById(R.id.dtiRatioSpinner);
        resultTextView = findViewById(R.id.resultTextView);
        Button calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(v -> calculate());
    }

    private void calculate() {
        try {
            String incomeStr = annualHouseholdIncome.getText().toString();
            String termStr = loanTerm.getText().toString();
            String rateStr = interestRate.getText().toString();
            String debtStr = monthlyDebtPayback.getText().toString();
            String downStr = downPayment.getText().toString();
            String taxStr = propertyTaxes.getText().toString();
            String hoaStr = hoaFee.getText().toString();
            String insuranceStr = homeInsurance.getText().toString();

            if (incomeStr.isEmpty() || termStr.isEmpty() || rateStr.isEmpty()) {
                Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double annualIncome = Double.parseDouble(incomeStr);
            double termYears = Double.parseDouble(termStr);
            double annualRate = Double.parseDouble(rateStr) / 100.0;
            double monthlyDebt = debtStr.isEmpty() ? 0 : Double.parseDouble(debtStr);
            double downPercent = downStr.isEmpty() ? 0 : Double.parseDouble(downStr) / 100.0;
            double taxPercent = taxStr.isEmpty() ? 0 : Double.parseDouble(taxStr) / 100.0;
            double hoaPercent = hoaStr.isEmpty() ? 0 : Double.parseDouble(hoaStr) / 100.0;
            double insurancePercent = insuranceStr.isEmpty() ? 0 : Double.parseDouble(insuranceStr) / 100.0;

            if (termYears <= 0) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double monthlyIncome = annualIncome / 12.0;

            double backEndRatio;
            double frontEndRatio = 1.0; // Default no front-end limit
            String loanTypeDescription;
            double monthlyFeeRate = 0;
            double upfrontFeeRate = 0;

            int position = dtiRatioSpinner.getSelectedItemPosition();
            switch (position) {
                case 0: // Conventional 28/36
                    frontEndRatio = 0.28;
                    backEndRatio = 0.36;
                    loanTypeDescription = " according to the 28/36 rule, within which ";
                    break;
                case 1: // FHA 31/43
                    frontEndRatio = 0.31;
                    backEndRatio = 0.43;
                    loanTypeDescription = " with an FHA loan, within which ";
                    monthlyFeeRate = 0.005 / 12.0; // Monthly MIP 0.5% annual
                    upfrontFeeRate = 0.0175; // Upfront MIP 1.75%
                    break;
                case 2: // VA 41
                    backEndRatio = 0.41;
                    loanTypeDescription = " with a VA loan, within which ";
                    monthlyFeeRate = 0.0125 / 12.0; // Monthly Fee 1.25% annual
                    break;
                default: // Percentages 10-50
                    backEndRatio = (position - 3) * 0.05 + 0.10;
                    loanTypeDescription = " within which ";
                    break;
            }

            // Monthly PITI + Fees factor
            double monthlyRate = annualRate / 12.0;
            double n = termYears * 12;
            double factor = (monthlyRate > 0) ?
                    (monthlyRate * Math.pow(1 + monthlyRate, n)) / (Math.pow(1 + monthlyRate, n) - 1) :
                    1.0 / n;

            // qualifying housing costs include mortgage, tax, ins, hoa, and recurring fees
            double housingCostsMultiplier = (1 - downPercent) * (factor + monthlyFeeRate) + (taxPercent / 12.0) + (insurancePercent / 12.0) + (hoaPercent / 12.0);

            // The limiting factor is the minimum of Front-end and Back-end
            double maxQualifyingMonthly_BackEnd = (monthlyIncome * backEndRatio) - monthlyDebt;
            double maxQualifyingMonthly_FrontEnd = monthlyIncome * frontEndRatio;
            double maxMonthlyQualifyingPayment = Math.min(maxQualifyingMonthly_BackEnd, maxQualifyingMonthly_FrontEnd);

            if (maxMonthlyQualifyingPayment < 0) maxMonthlyQualifyingPayment = 0;

            double maxHomePrice = (housingCostsMultiplier > 0) ? maxMonthlyQualifyingPayment / housingCostsMultiplier : 0;
            double loanAmount = maxHomePrice * (1 - downPercent);
            double downPaymentAmount = maxHomePrice * downPercent;
            double upfrontFee = loanAmount * upfrontFeeRate;
            double closingCost = maxHomePrice * 0.03;
            double totalOneTimePayment = downPaymentAmount + upfrontFee + closingCost;

            double monthlyMortgagePayment = loanAmount * factor;
            double monthlyInsuranceFee = loanAmount * monthlyFeeRate;
            double annualPropertyTax = maxHomePrice * taxPercent;
            double annualHOA = maxHomePrice * hoaPercent;
            double annualInsurance = maxHomePrice * insurancePercent;
            double annualMaintenance = maxHomePrice * 0.015;
            double totalMonthlyCost = monthlyMortgagePayment + monthlyInsuranceFee + (annualPropertyTax / 12.0) + (annualHOA / 12.0) + (annualInsurance / 12.0) + (annualMaintenance / 12.0);

            double actualFrontEnd = (monthlyMortgagePayment + monthlyInsuranceFee + (annualPropertyTax / 12.0) + (annualHOA / 12.0) + (annualInsurance / 12.0)) / monthlyIncome;
            double actualBackEnd = (monthlyMortgagePayment + monthlyInsuranceFee + (annualPropertyTax / 12.0) + (annualHOA / 12.0) + (annualInsurance / 12.0) + monthlyDebt) / monthlyIncome;

            DecimalFormat df = new DecimalFormat("#,##0");
            DecimalFormat pf = new DecimalFormat("0");

            StringBuilder result = new StringBuilder();
            result.append("You can afford a house up to <b>$").append(df.format(maxHomePrice)).append("</b>");
            result.append(loanTypeDescription);
            result.append("$").append(df.format(loanAmount)).append(" is the loan and $").append(df.format(downPaymentAmount)).append(" is the down payment.");
            if (position == 0) {
                result.append(" Most conventional loan lenders use the 28/36 rule.");
            }
            result.append("<br/><br/>");

            result.append("You can borrow $").append(df.format(loanAmount)).append("<br/>");
            result.append("Total price of the house $").append(df.format(maxHomePrice)).append("<br/>");
            result.append("Down payment $").append(df.format(downPaymentAmount)).append("<br/>");

            if (position == 1) {
                result.append("FHA upfront insurance premium (1.75%) $").append(df.format(upfrontFee)).append("<br/>");
            }

            result.append("Estimated closing cost (one time, assume 3%) $").append(df.format(closingCost)).append("<br/>");
            result.append("Front-end debt-to-income (DTI) ratio ").append(pf.format(actualFrontEnd * 100)).append("%<br/>");
            result.append("Back-end debt-to-income (DTI) ratio ").append(pf.format(actualBackEnd * 100)).append("%<br/>");
            result.append("Total one-time payment at closing<b> $").append(df.format(totalOneTimePayment)).append("</b><br/><br/>");

            result.append("Monthly mortgage payment $").append(df.format(monthlyMortgagePayment)).append("<br/>");

            if (position == 1) {
                result.append("Monthly MIP payment (0.5%) $").append(df.format(monthlyInsuranceFee)).append("<br/>");
            } else if (position == 2) {
                result.append("Monthly VA loan funding fee (1.25%)<br/>Assuming veteran first time use\t$").append(df.format(monthlyInsuranceFee)).append("<br/>");
            }

            result.append("Annual property tax $").append(df.format(annualPropertyTax)).append("<br/>");
            result.append("Annual HOA or co-op fee $").append(df.format(annualHOA)).append("<br/>");
            result.append("Annual insurance cost $").append(df.format(annualInsurance)).append("<br/>");
            result.append("Estimated annual maintenance cost <br/>(repair, utility etc., assume 1.5%)\t$").append(df.format(annualMaintenance)).append("<br/>");
            result.append("Total monthly cost on the house <b>$").append(df.format(totalMonthlyCost)).append("</b>");

            resultTextView.setText(Html.fromHtml(result.toString(), Html.FROM_HTML_MODE_LEGACY));
            resultTextView.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }
}
