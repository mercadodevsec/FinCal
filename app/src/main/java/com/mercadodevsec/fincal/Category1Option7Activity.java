package com.mercadodevsec.fincal;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.NumberFormat;
import java.util.Locale;

public class Category1Option7Activity extends AppCompatActivity {

    private Spinner calculationTypeSpinner, cashRefSpinner;
    private EditText remainingBal, monthlyPayment, loanAmount, loanTerm, timeRemaining, currentInterestRate;
    private EditText newLoanTerm, newInterestRate, points, costsAndFees, cashAmount;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option7);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        calculationTypeSpinner = findViewById(R.id.refSpinner); // Reusing the ID from layout
        remainingBal = findViewById(R.id.remainingBal);
        monthlyPayment = findViewById(R.id.monthlyPayment);
        loanAmount = findViewById(R.id.loanAmount);
        loanTerm = findViewById(R.id.loanTerm);
        timeRemaining = findViewById(R.id.timeRemaining);
        currentInterestRate = findViewById(R.id.currentInterestRate);

        newLoanTerm = findViewById(R.id.newLoanTerm);
        newInterestRate = findViewById(R.id.newInterestRate);
        points = findViewById(R.id.points);
        costsAndFees = findViewById(R.id.costsAndFees);
        cashRefSpinner = findViewById(R.id.cashRefSpinner);
        cashAmount = findViewById(R.id.cashAmount);

        resultTextView = findViewById(R.id.resultTextView);
        Button calculateButton = findViewById(R.id.calculateButton);

        // Spinner logic
        calculationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // I know my remaining balance
                    remainingBal.setVisibility(View.VISIBLE);
                    monthlyPayment.setVisibility(View.VISIBLE);
                    loanAmount.setVisibility(View.GONE);
                    loanTerm.setVisibility(View.GONE);
                    timeRemaining.setVisibility(View.GONE);
                } else { // I know the original loan amount
                    remainingBal.setVisibility(View.GONE);
                    monthlyPayment.setVisibility(View.GONE);
                    loanAmount.setVisibility(View.VISIBLE);
                    loanTerm.setVisibility(View.VISIBLE);
                    timeRemaining.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        calculateButton.setOnClickListener(v -> calculateRefinance());
    }

    private void calculateRefinance() {
        boolean isRemainingBalanceMode = calculationTypeSpinner.getSelectedItemPosition() == 0;

        double currRate = parseDouble(currentInterestRate) / 100.0;
        double newRate = parseDouble(newInterestRate) / 100.0;
        double newTermYears = parseDouble(newLoanTerm);
        double pts = parseDouble(points) / 100.0;
        double fees = parseDouble(costsAndFees);
        double cashVal = parseDouble(cashAmount);

        if (newTermYears == 0) {
            Toast.makeText(this, "Please enter valid new loan term", Toast.LENGTH_SHORT).show();
            return;
        }

        double pCurr;
        double mCurr;
        double nRemaining;
        double mCurrUnrounded;

        if (isRemainingBalanceMode) {
            pCurr = parseDouble(remainingBal);
            mCurr = parseDouble(monthlyPayment);
            mCurrUnrounded = mCurr;
            if (pCurr <= 0 || mCurr <= 0 || currRate < 0) {
                Toast.makeText(this, "Please fill in all current loan fields", Toast.LENGTH_SHORT).show();
                return;
            }
            double r = currRate / 12.0;
            if (mCurr <= pCurr * r) {
                Toast.makeText(this, "Monthly payment must be greater than interest", Toast.LENGTH_SHORT).show();
                return;
            }
            nRemaining = Math.log(mCurr / (mCurr - pCurr * r)) / Math.log(1 + r);
            nRemaining = Math.floor(nRemaining);
        } else {
            double origAmount = parseDouble(loanAmount);
            double origTerm = parseDouble(loanTerm);
            double timeRem = parseDouble(timeRemaining);
            if (origAmount <= 0 || origTerm <= 0 || timeRem < 0) {
                Toast.makeText(this, "Please fill in all current loan fields", Toast.LENGTH_SHORT).show();
                return;
            }
            double r = currRate / 12.0;
            double nTotal = origTerm * 12.0;
            nRemaining = timeRem * 12.0;
            mCurrUnrounded = origAmount * (r * Math.pow(1 + r, nTotal)) / (Math.pow(1 + r, nTotal) - 1);
            mCurr = Math.round(mCurrUnrounded * 100.0) / 100.0;
            double pCurrUnrounded = mCurrUnrounded * (1 - Math.pow(1 + r, -nRemaining)) / r;
            pCurr = Math.round(pCurrUnrounded * 100.0) / 100.0;
        }

        // New Loan Calculation
        double cashAdj = (cashRefSpinner.getSelectedItemPosition() == 0) ? cashVal : -cashVal;
        double pNew = pCurr + cashAdj;

        double rNewMonthly = newRate / 12.0;
        double nNew = newTermYears * 12.0;
        double mNewUnrounded = (rNewMonthly != 0) ? pNew * (rNewMonthly * Math.pow(1 + rNewMonthly, nNew)) / (Math.pow(1 + rNewMonthly, nNew) - 1) : pNew / nNew;
        double mNew = Math.round(mNewUnrounded * 100.0) / 100.0;

        double upfrontCost = pNew * pts + fees;
        double apr = (rNewMonthly != 0) ? (calculateAPR(pNew - upfrontCost, mNewUnrounded, (int) nNew)) : 0;

        // Savings
        double monthlySavings = mCurr - mNew;
        double totalCurrentPayments = mCurrUnrounded * nRemaining;
        double totalNewPayments = mNewUnrounded * nNew + upfrontCost;
        double lifetimeSavings = totalCurrentPayments - totalNewPayments + cashAdj;

        // Break even calculation based on cumulative interest savings
        int breakEven = -1;
        if (apr < currRate) {
            double cumulativeInterestSavings = 0;
            double balCurr = pCurr;
            double balNew = pNew;
            double rCurr = currRate / 12.0;
            double rNew = newRate / 12.0;

            // Iterate month by month to find when interest savings cover upfront costs
            for (int m = 1; m <= nRemaining && m <= 600; m++) {
                double intCurr = balCurr * rCurr;
                double intNew = balNew * rNew;
                cumulativeInterestSavings += (intCurr - intNew);

                if (cumulativeInterestSavings >= upfrontCost) {
                    breakEven = m;
                    break;
                }

                // Update balances for next month
                balCurr -= (mCurrUnrounded - intCurr);
                balNew -= (mNewUnrounded - intNew);

                // If current loan is paid off, but new one isn't, we stop saving interest
                if (balCurr <= 0) break;
            }
        }

        displayResult(apr, currRate, mNew, monthlySavings, nRemaining, nNew, lifetimeSavings, upfrontCost, breakEven);
    }


    private void displayResult(double apr, double currRate, double mNew, double monthlySavings,
                               double nRemaining, double nNew, double lifetimeSavings, double upfrontCost, int breakEven) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        String aprStr = String.format(Locale.US, "%.3f%%", apr * 100);
        double displayDiff = Math.abs(apr * 100 - (currRate * 100));
        String diffRateStr = String.format(Locale.US, "%.3f%%", displayDiff);

        String mNewStr = currencyFormat.format(mNew);
        String monthlySavingsStr = currencyFormat.format(Math.abs(monthlySavings));
        String lifetimeSavingsStr = currencyFormat.format(Math.abs(lifetimeSavings));
        String upfrontCostStr = currencyFormat.format(upfrontCost);

        boolean isBetter = apr < currRate;
        String compareText = isBetter ? "lower" : "higher";
        String expenseText = isBetter ? "less expensive" : "more expensive";

        String greenBold = "<font color='#008000'><b>";
        String redBold = "<font color='#FF0000'><b>";
        String bold = "<b>";
        String close = "</b></font>";
        String closeBold = "</b>";

        StringBuilder sb = new StringBuilder();
        if (apr != 0) {
            sb.append("The APR for the new loan is ").append(bold).append(aprStr).append(closeBold)
                    .append(", which is ").append(diffRateStr).append(" ").append(compareText).append(" than the ")
                    .append(String.format(Locale.US, "%.0f%%", currRate * 100)).append(" interest rate of the current loan. ")
                    .append("Refinancing would be financially ").append(isBetter ? greenBold : redBold).append(expenseText).append(close).append(".<br/><br/>");
        }

        sb.append("New monthly payment: ").append(bold).append(mNewStr).append(closeBold).append("<br/><br/>");

        if (monthlySavings >= 0) {
            sb.append(greenBold).append(monthlySavingsStr).append("/month savings").append(close).append(" in monthly pay<br/>");
        } else {
            sb.append(redBold).append(monthlySavingsStr).append("/month extra cost").append(close).append(" in monthly pay<br/>");
        }

        int monthsDiff = (int) Math.round(Math.abs(nRemaining - nNew));
        if (monthsDiff > 0) {
            if (nNew < nRemaining) {
                sb.append(greenBold).append(monthsDiff).append(" months faster").append(close).append(" the loan will be paid off<br/>");
            } else {
                sb.append(redBold).append(monthsDiff).append(" months slower").append(close).append(" the loan will be paid off<br/>");
            }
        }

        if (lifetimeSavings >= 0) {
            sb.append(greenBold).append(lifetimeSavingsStr).append(" lifetime savings").append(close).append(" for the new loan<br/>");
        } else {
            sb.append(redBold).append(lifetimeSavingsStr).append(" total extra cost").append(close).append(" for the new loan<br/>");
        }

        sb.append(redBold).append(upfrontCostStr).append(close).append(" upfront cost<br/>");

        if (breakEven > 0 && isBetter) {
            sb.append("Break even point: ").append(greenBold).append(breakEven).append(" months").append(close);
        }

        resultTextView.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY));
    }

    private double calculateAPR(double loanAmount, double monthlyPayment, int months) {
        if (loanAmount <= 0) return 0;
        double low = 0;
        double high = 1.0;
        for (int i = 0; i < 100; i++) {
            double mid = (low + high) / 2;
            double r = mid / 12.0;
            if (r == 0) continue;
            double p = monthlyPayment * (1 - Math.pow(1 + r, -months)) / r;
            if (p > loanAmount) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return (low + high) / 2;
    }

    private double parseDouble(EditText editText) {
        String s = editText.getText().toString();
        if (s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
