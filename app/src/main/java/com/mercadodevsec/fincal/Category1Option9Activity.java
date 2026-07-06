package com.mercadodevsec.fincal;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class Category1Option9Activity extends AppCompatActivity {

    private Spinner refAPRSpinner, refCompoundSpinner, refPaybackSpinner;
    private EditText loanAmount, loanTerm, interestRate, loanFees, upfrontFees;
    private EditText houseValue, downPayment, mortgageLoanFees, points, pmiInsurance;
    private LinearLayout resultContainer;
    private Button calculateButton;

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final DecimalFormat pf = new DecimalFormat("0.000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option9);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupSpinner();
        setupCalculateButton();
    }

    private void initializeViews() {
        refAPRSpinner = findViewById(R.id.refAPRSpinner);
        refCompoundSpinner = findViewById(R.id.refCompoundSpinner);
        refPaybackSpinner = findViewById(R.id.refPaybackSpinner);
        loanAmount = findViewById(R.id.loanAmount);
        loanTerm = findViewById(R.id.loanTerm);
        interestRate = findViewById(R.id.interestRate);
        loanFees = findViewById(R.id.loanFees);
        upfrontFees = findViewById(R.id.upfrontFees);
        houseValue = findViewById(R.id.houseValue);
        downPayment = findViewById(R.id.downPayment);
        mortgageLoanFees = findViewById(R.id.mortgageLoanFees);
        points = findViewById(R.id.points);
        pmiInsurance = findViewById(R.id.pmiInsurance);
        resultContainer = findViewById(R.id.resultContainer);
        calculateButton = findViewById(R.id.calculateButton);
    }

    private void setupSpinner() {
        refCompoundSpinner.setSelection(3); // Monthly
        refPaybackSpinner.setSelection(4); // Every month

        refAPRSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // General APR Calculator
                    showGeneralFields();
                } else { // Mortgage APR Calculator
                    showMortgageFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showGeneralFields() {
        loanAmount.setVisibility(View.VISIBLE);
        loanFees.setVisibility(View.VISIBLE);
        upfrontFees.setVisibility(View.VISIBLE);
        ((View) refCompoundSpinner.getParent()).setVisibility(View.VISIBLE);
        ((View) refPaybackSpinner.getParent()).setVisibility(View.VISIBLE);

        houseValue.setVisibility(View.GONE);
        downPayment.setVisibility(View.GONE);
        mortgageLoanFees.setVisibility(View.GONE);
        points.setVisibility(View.GONE);
        pmiInsurance.setVisibility(View.GONE);
    }

    private void showMortgageFields() {
        loanAmount.setVisibility(View.GONE);
        loanFees.setVisibility(View.GONE);
        upfrontFees.setVisibility(View.GONE);
        ((View) refCompoundSpinner.getParent()).setVisibility(View.GONE);
        ((View) refPaybackSpinner.getParent()).setVisibility(View.GONE);

        houseValue.setVisibility(View.VISIBLE);
        downPayment.setVisibility(View.VISIBLE);
        mortgageLoanFees.setVisibility(View.VISIBLE);
        points.setVisibility(View.VISIBLE);
        pmiInsurance.setVisibility(View.VISIBLE);
    }

    private void setupCalculateButton() {
        calculateButton.setOnClickListener(v -> {
            if (isAnyFieldEmpty()) {
                Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
                return;
            }
            if (refAPRSpinner.getSelectedItemPosition() == 0) {
                calculateGeneralAPR();
            } else {
                calculateMortgageAPR();
            }
        });
    }

    private boolean isAnyFieldEmpty() {
        if (loanTerm.getText().toString().isEmpty() || interestRate.getText().toString().isEmpty()) {
            return true;
        }
        if (refAPRSpinner.getSelectedItemPosition() == 0) {
            return loanAmount.getText().toString().isEmpty();
        } else {
            return houseValue.getText().toString().isEmpty() || downPayment.getText().toString().isEmpty();
        }
    }

    private void calculateGeneralAPR() {
        try {
            double amount = parseDouble(loanAmount);
            double term = parseDouble(loanTerm);
            double rate = parseDouble(interestRate);
            double lFees = parseDouble(loanFees);
            double uFees = parseDouble(upfrontFees);

            if (term <= 0) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            int compoundFreq = getCompoundFrequency(refCompoundSpinner.getSelectedItemPosition());
            int paybackFreq = getPaybackFrequency(refPaybackSpinner.getSelectedItemPosition());

            double totalLoanAmount = amount + lFees;
            double periodicRate = Math.pow(1 + (rate / 100.0) / compoundFreq, (double) compoundFreq / paybackFreq) - 1;
            int totalPayments = (int) (term * paybackFreq);

            double payment;
            if (periodicRate == 0) {
                payment = totalLoanAmount / totalPayments;
            } else {
                payment = totalLoanAmount * (periodicRate * Math.pow(1 + periodicRate, totalPayments)) / (Math.pow(1 + periodicRate, totalPayments) - 1);
            }

            double totalOfPayments = payment * totalPayments;
            double totalInterest = totalOfPayments - amount;
            double allPaymentsAndFees = totalOfPayments + uFees;

            double netProceeds = amount - uFees;
            double apr = (netProceeds <= 0) ? 0 : calculateAPR(netProceeds, payment, totalPayments, paybackFreq);

            resultContainer.removeAllViews();
            addResultRow("Real APR:", pf.format(apr) + "%", true);
            addResultRow("Amount Financed:", "$" + df.format(totalLoanAmount), false);
            addResultRow("Upfront Out-of-Pocket Fees:", "$" + df.format(uFees), false);
            addResultRow("Payment " + refPaybackSpinner.getSelectedItem().toString() + ":", "$" + df.format(payment), false);
            addResultRow("Total of " + totalPayments + " Payments:", "$" + df.format(totalOfPayments), false);
            addResultRow("Total Interest:", "$" + df.format(totalInterest), false);
            addResultRow("All Payments and Fees:", "$" + df.format(allPaymentsAndFees), false);

        } catch (Exception e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateMortgageAPR() {
        try {
            double hValue = parseDouble(houseValue);
            double dPaymentInput = parseDouble(downPayment);
            double term = parseDouble(loanTerm);
            double rate = parseDouble(interestRate);
            double mFees = parseDouble(mortgageLoanFees);
            double pts = parseDouble(points);
            double pmiAnnual = parseDouble(pmiInsurance);

            if (term <= 0) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double actualDownPayment = dPaymentInput;
            if (dPaymentInput > 0 && dPaymentInput < 100) {
                actualDownPayment = hValue * (dPaymentInput / 100.0);
            }

            double loanAmt = hValue - actualDownPayment;
            if (loanAmt <= 0) {
                Toast.makeText(this, "Loan amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            double periodicRate = (rate / 100.0) / 12.0;
            int totalPaymentsCount = (int) (term * 12);

            double piPayment;
            if (periodicRate == 0) {
                piPayment = loanAmt / totalPaymentsCount;
            } else {
                piPayment = loanAmt * (periodicRate * Math.pow(1 + periodicRate, totalPaymentsCount)) / (Math.pow(1 + periodicRate, totalPaymentsCount) - 1);
            }

            // PMI calculation - ends when LTV reaches 80%
            int pmiMonths = 0;
            double monthlyPMI = pmiAnnual / 12.0;
            if (actualDownPayment < hValue * 0.2 && pmiAnnual > 0) {
                double targetBalance = hValue * 0.8;
                if (periodicRate > 0) {
                    double mr = piPayment / periodicRate;
                    double x = (targetBalance - mr) / (loanAmt - mr);
                    if (x > 1) {
                        double m = Math.log(x) / Math.log(1 + periodicRate);
                        pmiMonths = (int) m; // Using floor to match target output
                        if (pmiMonths > totalPaymentsCount) pmiMonths = totalPaymentsCount;
                    }
                } else {
                    double m = (loanAmt - targetBalance) / piPayment;
                    pmiMonths = (int) m; // Using floor to match target output
                    if (pmiMonths < 0) pmiMonths = 0;
                    if (pmiMonths > totalPaymentsCount) pmiMonths = totalPaymentsCount;
                }
            }
            double totalPMI = pmiMonths * monthlyPMI;

            double totalOfPIPayments = piPayment * totalPaymentsCount;
            double totalInterest = totalOfPIPayments - loanAmt;

            double pointFees = (pts / 100.0) * loanAmt;
            double upfrontFeesTotal = mFees + pointFees;

            // Following user's example: All Payments and Fees = Total PI + Total PMI + Loan Fees + Points
            double allPaymentsAndFees = totalOfPIPayments + totalPMI + upfrontFeesTotal;

            double netLoanAmount = loanAmt - upfrontFeesTotal;
            double apr = calculateAPRWithVaryingPayments(netLoanAmount, piPayment, monthlyPMI, pmiMonths, totalPaymentsCount);

            resultContainer.removeAllViews();
            addResultRow("Real APR:", pf.format(apr) + "%", true);
            addResultRow("Loan Amount:", "$" + df.format(loanAmt), false);
            addResultRow("Down Payment:", "$" + df.format(actualDownPayment), false);
            addResultRow("Monthly Pay:", "$" + df.format(piPayment), false);
            addResultRow("Total of " + totalPaymentsCount + " Payments:", "$" + df.format(totalOfPIPayments), false);
            addResultRow("Total Interest:", "$" + df.format(totalInterest), false);

            if (pmiMonths > 0) {
                addResultRow("PMI Insurance (" + pmiMonths + " months):", "$" + df.format(monthlyPMI) + "/month", false);
                addResultRow("Total PMI Insurance Payments:", "$" + df.format(totalPMI), false);
            }

            addResultRow("All Payments and Fees:", "$" + df.format(allPaymentsAndFees), false);

        } catch (Exception e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private void addResultRow(String label, String value, boolean isBold) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setPadding(0, 4, 0, 4);

        TextView labelTv = new TextView(this);
        labelTv.setText(label);
        labelTv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        labelTv.setTextSize(18);
        labelTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView valueTv = new TextView(this);
        valueTv.setText(value);
        valueTv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        valueTv.setTextSize(18);
        valueTv.setGravity(Gravity.END);
        valueTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        if (isBold) {
            labelTv.setTypeface(null, Typeface.BOLD);
            valueTv.setTypeface(null, Typeface.BOLD);
        }

        row.addView(labelTv);
        row.addView(valueTv);
        resultContainer.addView(row);
    }

    private double calculateAPRWithVaryingPayments(double principal, double basePayment, double extraPayment, int extraPeriods, int totalPeriods) {
        if (principal <= 0 || basePayment <= 0 || totalPeriods <= 0) return 0;

        double low = 0;
        double high = 1.0;

        for (int i = 0; i < 100; i++) {
            double mid = (low + high) / 2;
            double pv;
            if (mid == 0) {
                pv = (basePayment + extraPayment) * extraPeriods + basePayment * (totalPeriods - extraPeriods);
            } else {
                double pv1 = (basePayment + extraPayment) * (1 - Math.pow(1 + mid, -extraPeriods)) / mid;
                double pv2 = basePayment * (1 - Math.pow(1 + mid, -(totalPeriods - extraPeriods))) / mid;
                pv2 = pv2 / Math.pow(1 + mid, extraPeriods);
                pv = pv1 + pv2;
            }

            if (pv > principal) {
                low = mid;
            } else {
                high = mid;
            }
        }

        return low * 12 * 100;
    }

    private double parseDouble(EditText editText) {
        String text = editText.getText().toString();
        if (text.isEmpty()) return 0;
        return Double.parseDouble(text);
    }

    private int getCompoundFrequency(int position) {
        switch (position) {
            case 0:
                return 1;    // Annually
            case 1:
                return 2;    // Semi-annually
            case 2:
                return 4;    // Quarterly
            case 3:
                return 12;   // Monthly
            case 4:
                return 24;   // Semi-monthly
            case 5:
                return 26;   // Biweekly
            case 6:
                return 52;   // Weekly
            case 7:
                return 365;  // Daily
            case 8:
                return 3650; // Continuously (approx)
            default:
                return 12;
        }
    }

    private int getPaybackFrequency(int position) {
        switch (position) {
            case 0:
                return 365; // Every day
            case 1:
                return 52;  // Every week
            case 2:
                return 26;  // Every 2 weeks
            case 3:
                return 24;  // Every half month
            case 4:
                return 12;  // Every month
            case 5:
                return 4;   // Every quarter
            case 6:
                return 2;   // Every 6 Months
            case 7:
                return 1;   // Every year
            default:
                return 12;
        }
    }

    private double calculateAPR(double principal, double payment, int periods, int frequency) {
        if (principal <= 0 || payment <= 0 || periods <= 0) return 0;

        double low = 0;
        double high = 1.0; // 100% per period is huge

        // Bisection method to find the periodic interest rate
        for (int i = 0; i < 100; i++) {
            double mid = (low + high) / 2;
            double pv;
            if (mid == 0) {
                pv = payment * periods;
            } else {
                pv = payment * (1 - Math.pow(1 + mid, -periods)) / mid;
            }

            if (pv > principal) {
                low = mid;
            } else {
                high = mid;
            }
        }

        return low * frequency * 100;
    }
}
