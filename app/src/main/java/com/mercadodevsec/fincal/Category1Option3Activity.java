package com.mercadodevsec.fincal;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class Category1Option3Activity extends AppCompatActivity {

    private EditText origLoanAmount, origLoanTerm, interestRate, remainingTerm;
    private EditText perMonth, perYear, oneTimePay;
    private RadioGroup repaymentRadioGroup;
    private LinearLayout extraPaymentsContainer, resultContainer;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        origLoanAmount = findViewById(R.id.origLoanAmount);
        origLoanTerm = findViewById(R.id.origLoanTerm);
        interestRate = findViewById(R.id.interestRate);
        remainingTerm = findViewById(R.id.remainingTerm);
        perMonth = findViewById(R.id.perMonth);
        perYear = findViewById(R.id.perYear);
        oneTimePay = findViewById(R.id.oneTimePay);
        repaymentRadioGroup = findViewById(R.id.repaymentRadioGroup);
        extraPaymentsContainer = findViewById(R.id.extraPaymentsContainer);
        resultContainer = findViewById(R.id.resultContainer);
        scrollView = findViewById(R.id.scrollView);
        Button calculateButton = findViewById(R.id.calculateButton);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        repaymentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioExtraPayment || checkedId == R.id.radioBiweekly) {
                extraPaymentsContainer.setVisibility(View.VISIBLE);
            } else {
                extraPaymentsContainer.setVisibility(View.GONE);
            }
        });

        calculateButton.setOnClickListener(v -> calculate());
    }

    private void calculate() {
        try {
            String amountStr = origLoanAmount.getText().toString();
            String termStr = origLoanTerm.getText().toString();
            String rateStr = interestRate.getText().toString();
            String remainingStr = remainingTerm.getText().toString();

            if (amountStr.isEmpty() || termStr.isEmpty() || rateStr.isEmpty() || remainingStr.isEmpty()) {
                Toast.makeText(this, R.string.empty_fields_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double principal = Double.parseDouble(amountStr);
            double termYears = Double.parseDouble(termStr);
            double rateYear = Double.parseDouble(rateStr) / 100.0;
            double remainingYears = Double.parseDouble(remainingStr);

            if (termYears <= 0) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double rateMonth = rateYear / 12.0;
            int totalMonths = (int) (termYears * 12);
            int monthsPaid = (int) Math.max(0, (termYears - remainingYears) * 12);

            // Standard Monthly payment formula
            double monthlyPayment;
            if (rateMonth > 0) {
                monthlyPayment = principal * (rateMonth * Math.pow(1 + rateMonth, totalMonths)) / (Math.pow(1 + rateMonth, totalMonths) - 1);
            } else {
                monthlyPayment = principal / totalMonths;
            }

            // Current balance (remaining principal)
            double currentBalance;
            if (rateMonth > 0) {
                currentBalance = principal * (Math.pow(1 + rateMonth, totalMonths) - Math.pow(1 + rateMonth, monthsPaid)) / (Math.pow(1 + rateMonth, totalMonths) - 1);
            } else {
                currentBalance = principal - (monthlyPayment * monthsPaid);
            }
            if (currentBalance < 0) currentBalance = 0;

            int checkedId = repaymentRadioGroup.getCheckedRadioButtonId();
            DecimalFormat df = new DecimalFormat("#,##0.00");

            resultContainer.removeAllViews();

            double alreadyPaid = monthlyPayment * monthsPaid;

            if (checkedId == R.id.radioNormal) {
                double totalPay = monthlyPayment * totalMonths;
                double totalInt = totalPay - principal;
                addResultRow("Monthly Pay:", "$" + df.format(monthlyPayment), false);
                addResultRow("Total Payments:", "$" + df.format(totalPay), false);
                addResultRow("Total Interest:", "$" + df.format(totalInt), false);
            } else if (checkedId == R.id.radioPaybackAltogether) {
                addResultRow("Payoff Amount:", "$" + df.format(currentBalance), false);
            } else if (checkedId == R.id.radioExtraPayment || checkedId == R.id.radioBiweekly) {
                double extraM = 0, extraY = 0, extraO = 0;
                boolean isBiweekly = (checkedId == R.id.radioBiweekly);

                if (checkedId == R.id.radioExtraPayment) {
                    extraM = parseDouble(perMonth.getText().toString());
                    extraY = parseDouble(perYear.getText().toString());
                    extraO = parseDouble(oneTimePay.getText().toString());
                }

                // Simulate remaining term
                SimulationResult original = simulate(currentBalance, monthlyPayment, rateMonth, 0, 0, 0, false);
                SimulationResult modified = simulate(currentBalance, monthlyPayment, rateMonth, extraM, extraY, extraO, isBiweekly);

                addResultRow3("", "Original", "With payoff", true);
                addResultRow3("Total payments", "$" + df.format(alreadyPaid + original.totalPaid), "$" + df.format(alreadyPaid + modified.totalPaid), false);
                addResultRow3("Total interest", "$" + df.format(alreadyPaid + original.totalPaid - principal), "$" + df.format(alreadyPaid + modified.totalPaid - principal), false);
                addResultRow3("Remaining payments", "$" + df.format(original.totalPaid), "$" + df.format(modified.totalPaid), false);
                addResultRow3("Remaining interest", "$" + df.format(original.totalInterest), "$" + df.format(modified.totalInterest), false);
                addResultRow3("Payoff in", formatMonths(original.months), formatMonths(modified.months), false);

                View spacer = new View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(1, 25));
                resultContainer.addView(spacer);

                addResultRow("With Payoff:", "", true);
                addResultRow("Total Interest Saved", "$" + df.format(original.totalInterest - modified.totalInterest), false);
                addResultRow("Time Saved", formatMonths(original.months - modified.months), false);
            }

            // Auto-scroll to results
            scrollView.post(() -> {
                if (resultContainer.getChildCount() > 0) {
                    scrollView.smoothScrollTo(0, resultContainer.getTop());
                }
            });

        } catch (NumberFormatException e) {
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
        } else {
            valueTv.setTypeface(null, Typeface.BOLD); // Keep value bold by default as before
        }

        row.addView(labelTv);
        if (value != null && !value.isEmpty()) {
            row.addView(valueTv);
        }

        resultContainer.addView(row);
    }

    private void addResultRow3(String label, String val1, String val2, boolean isHeader) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setPadding(0, 8, 0, 8);

        TextView labelTv = new TextView(this);
        labelTv.setText(label);
        labelTv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        labelTv.setTextSize(14);
        labelTv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f));

        TextView val1Tv = new TextView(this);
        val1Tv.setText(val1);
        val1Tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        val1Tv.setTextSize(14);
        val1Tv.setGravity(Gravity.END);
        val1Tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView val2Tv = new TextView(this);
        val2Tv.setText(val2);
        val2Tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        val2Tv.setTextSize(14);
        val2Tv.setGravity(Gravity.END);
        val2Tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if (isHeader) {
            labelTv.setTypeface(null, Typeface.BOLD);
            val1Tv.setTypeface(null, Typeface.BOLD);
            val2Tv.setTypeface(null, Typeface.BOLD);
            labelTv.setTextSize(15);
            val1Tv.setTextSize(15);
            val2Tv.setTextSize(15);
        }

        row.addView(labelTv);
        row.addView(val1Tv);
        row.addView(val2Tv);

        resultContainer.addView(row);
    }

    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatMonths(int totalMonths) {
        if (totalMonths <= 0) return "0 mos";
        int years = totalMonths / 12;
        int months = totalMonths % 12;
        if (years > 0) {
            if (months > 0) {
                return years + " yrs, " + months + " mos";
            } else {
                return years + " yrs";
            }
        } else {
            return months + " mos";
        }
    }

    private SimulationResult simulate(double balance, double basePayment, double rateMonth, double extraM, double extraY, double extraO, boolean isBiweekly) {
        double totalPaid = 0;
        double totalInterest = 0;
        int months = 0;
        double currentBalance = balance;

        // Apply one-time payment immediately if provided
        if (extraO > 0) {
            double payment = Math.min(currentBalance, extraO);
            currentBalance -= payment;
            totalPaid += payment;
        }

        while (currentBalance > 0.001 && months < 1200) { // Limit to 100 years safety
            months++;
            double interest = currentBalance * rateMonth;
            double payment = basePayment + extraM;

            if (isBiweekly) {
                // Biweekly effectively adds one extra monthly payment per year (approximate as 13/12 factor)
                payment = basePayment * (13.0 / 12.0);
            }

            if (months % 12 == 0) {
                payment += extraY;
            }

            if (payment > currentBalance + interest) {
                payment = currentBalance + interest;
            }

            double principalPaid = payment - interest;
            currentBalance -= principalPaid;
            totalPaid += payment;
            totalInterest += interest;
        }

        return new SimulationResult(totalPaid, totalInterest, months);
    }

    private static class SimulationResult {
        double totalPaid;
        double totalInterest;
        int months;

        SimulationResult(double totalPaid, double totalInterest, int months) {
            this.totalPaid = totalPaid;
            this.totalInterest = totalInterest;
            this.months = months;
        }
    }
}
