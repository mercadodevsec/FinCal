package com.mercadodevsec.fincal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Category1Option10Activity extends AppCompatActivity {

    private EditText homePrice, downPayment, interestRate, loanTerm, upfrontFHA, annualFHA;
    private Spinner fhaSpinner, startMonthSpinner1, startMonthSpinner2, startMonthSpinner3, startMonthSpinner4;
    private EditText year1, year2, year3, year5;
    private EditText propertyTax, homeInsurance, hoaFee, otherCosts;
    private EditText propertyTaxesIncrease, homeInsuranceIncrease, hoaFeeIncrease, otherCostsIncrease;
    private EditText extraMonthlyAmount, extraYearlyAmount, extraOneTimePay;
    private CheckBox includeOtherOptions1, includeOtherOptions2, showBiweeklyPayback;
    private LinearLayout additionalOptionsBelow, moreOptions;
    private Button calculateButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option10);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        homePrice = findViewById(R.id.homePrice);
        downPayment = findViewById(R.id.downPayment);
        interestRate = findViewById(R.id.interestRate);
        loanTerm = findViewById(R.id.loanTerm);
        upfrontFHA = findViewById(R.id.upfrontFHA);
        annualFHA = findViewById(R.id.annualFHA);
        fhaSpinner = findViewById(R.id.fhaSpinner);
        startMonthSpinner1 = findViewById(R.id.startMonthSpinner1);
        startMonthSpinner2 = findViewById(R.id.startMonthSpinner2);
        startMonthSpinner3 = findViewById(R.id.startMonthSpinner3);
        startMonthSpinner4 = findViewById(R.id.startMonthSpinner4);
        year1 = findViewById(R.id.year1);
        year2 = findViewById(R.id.year2);
        year3 = findViewById(R.id.year3);
        year5 = findViewById(R.id.year5);
        propertyTax = findViewById(R.id.propertyTax);
        homeInsurance = findViewById(R.id.homeInsurance);
        hoaFee = findViewById(R.id.hoaFee);
        otherCosts = findViewById(R.id.otherCosts);
        propertyTaxesIncrease = findViewById(R.id.propertyTaxesIncrease);
        homeInsuranceIncrease = findViewById(R.id.homeInsuranceIncrease);
        hoaFeeIncrease = findViewById(R.id.hoaFeeIncrease);
        otherCostsIncrease = findViewById(R.id.otherCostsIncrease);
        extraMonthlyAmount = findViewById(R.id.extraMonthlyAmount);
        extraYearlyAmount = findViewById(R.id.extraYearlyAmount);
        extraOneTimePay = findViewById(R.id.extraOneTimePay);
        includeOtherOptions1 = findViewById(R.id.includeOtherOptions1);
        includeOtherOptions2 = findViewById(R.id.includeOtherOptions2);
        showBiweeklyPayback = findViewById(R.id.showBiweeklyPayback);
        additionalOptionsBelow = findViewById(R.id.additionalOptionsBelow);
        moreOptions = findViewById(R.id.moreOptions);
        calculateButton = findViewById(R.id.calculateButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupListeners() {
        includeOtherOptions1.setOnCheckedChangeListener((buttonView, isChecked) -> additionalOptionsBelow.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        includeOtherOptions2.setOnCheckedChangeListener((buttonView, isChecked) -> moreOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        calculateButton.setOnClickListener(v -> calculate());
        backButton.setOnClickListener(v -> finish());
    }

    private void calculate() {
        try {
            double hPrice = parseDouble(homePrice.getText().toString());
            double dpValue = parseDouble(downPayment.getText().toString());
            double iRate = parseDouble(interestRate.getText().toString());
            int term = (int) parseDouble(loanTerm.getText().toString());
            double ufFHA = parseDouble(upfrontFHA.getText().toString());
            double annFHA = parseDouble(annualFHA.getText().toString());

            if (term <= 0) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double dpAmount;
            if (dpValue > 100) {
                dpAmount = dpValue;
            } else {
                dpAmount = hPrice * dpValue / 100.0;
            }

            double baseLoan = hPrice - dpAmount;
            double ufMIPAmount = hPrice * ufFHA / 100.0; // Based on user example observation
            double totalLoan = baseLoan + ufMIPAmount;

            double monthlyRate = iRate / 100.0 / 12.0;
            int totalMonths = term * 12;
            double monthlyPI = (totalLoan * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -totalMonths));

            // Optional fields
            double pTaxRate = includeOtherOptions1.isChecked() ? parseDouble(propertyTax.getText().toString()) : 0;
            double hIns = includeOtherOptions1.isChecked() ? parseDouble(homeInsurance.getText().toString()) : 0;
            double hFee = includeOtherOptions1.isChecked() ? parseDouble(hoaFee.getText().toString()) : 0;
            double oCosts = includeOtherOptions1.isChecked() ? parseDouble(otherCosts.getText().toString()) : 0;

            int startMonth = startMonthSpinner1.getSelectedItemPosition();
            int startYear = (int) parseDouble(year1.getText().toString());
            if (startYear == 0) startYear = Calendar.getInstance().get(Calendar.YEAR);

            double pTaxInc = includeOtherOptions2.isChecked() ? parseDouble(propertyTaxesIncrease.getText().toString()) : 0;
            double hInsInc = includeOtherOptions2.isChecked() ? parseDouble(homeInsuranceIncrease.getText().toString()) : 0;
            double hFeeInc = includeOtherOptions2.isChecked() ? parseDouble(hoaFeeIncrease.getText().toString()) : 0;
            double oCostsInc = includeOtherOptions2.isChecked() ? parseDouble(otherCostsIncrease.getText().toString()) : 0;

            double extraMonthly = includeOtherOptions2.isChecked() ? parseDouble(extraMonthlyAmount.getText().toString()) : 0;
            int exMonthStart = startMonthSpinner2.getSelectedItemPosition();
            int exYearStart = (int) parseDouble(year2.getText().toString());

            double extraYearly = includeOtherOptions2.isChecked() ? parseDouble(extraYearlyAmount.getText().toString()) : 0;
            int exMonthYearly = startMonthSpinner3.getSelectedItemPosition();
            int exYearYearly = (int) parseDouble(year3.getText().toString());

            double extraOneTime = includeOtherOptions2.isChecked() ? parseDouble(extraOneTimePay.getText().toString()) : 0;
            int exMonthOneTime = startMonthSpinner4.getSelectedItemPosition();
            int exYearOneTime = (int) parseDouble(year5.getText().toString());

            // Simulation
            double balance = totalLoan;
            double totalInterest = 0;
            double totalExtra = 0;
            double totalPTax = 0;
            double totalHIns = 0;
            double totalHFee = 0;
            double totalOCosts = 0;
            double totalMIP = 0;
            double totalMortgagePayments = 0;

            double firstMonthPTax = (hPrice * pTaxRate / 100.0) / 12.0;
            double firstMonthHIns = hIns / 12.0;
            double firstMonthHFee = hFee / 12.0;
            double firstMonthOCosts = oCosts / 12.0;
            double firstMonthMIP = (baseLoan * annFHA / 100.0) / 12.0;
            double firstMonthExtra = 0; // Will check in loop

            int currentMonth = 0;
            int payoffMonth = 0;
            int mipStopMonth = 0;
            Calendar cal = Calendar.getInstance();
            cal.set(startYear, startMonth, 1);

            int mipDurationIdx = fhaSpinner.getSelectedItemPosition();
            // 0: Loan term, 1: 11 years, 2: 5 years, 3: 78% LTV, 4: No annual MIP

            while (balance > 0.01 && currentMonth < totalMonths * 2) {
                currentMonth++;
                int yearsSinceStart = (currentMonth - 1) / 12;

                double interest = balance * monthlyRate;
                double principal = monthlyPI - interest;
                if (principal > balance) principal = balance;

                totalInterest += interest;
                totalMortgagePayments += (principal + interest);
                balance -= principal;

                // Extra payments
                double monthlyExtra = 0;
                if (isDateReached(startYear, startMonth, currentMonth, exYearStart, exMonthStart)) {
                    monthlyExtra += extraMonthly;
                }
                if (isYearlyDateReached(startYear, startMonth, currentMonth, exYearYearly, exMonthYearly)) {
                    monthlyExtra += extraYearly;
                }
                if (isOneTimeDateReached(startYear, startMonth, currentMonth, exYearOneTime, exMonthOneTime)) {
                    monthlyExtra += extraOneTime;
                }

                if (monthlyExtra > balance) monthlyExtra = balance;
                balance -= monthlyExtra;
                totalExtra += monthlyExtra;
                if (currentMonth == 1) firstMonthExtra = monthlyExtra;

                // MIP
                boolean hasMIP = false;
                switch (mipDurationIdx) {
                    case 0: hasMIP = true; break;
                    case 1: if (currentMonth <= 132) hasMIP = true; break;
                    case 2: if (currentMonth <= 60) hasMIP = true; break;
                    case 3: if (balance > hPrice * 0.78) hasMIP = true; break;
                    case 4:
                        break;
                }
                if (hasMIP) {
                    double currentMIP = (baseLoan * annFHA / 100.0) / 12.0; // Simplification as per example
                    totalMIP += currentMIP;
                    mipStopMonth = currentMonth;
                }

                // Taxes & Costs with increases
                double currentPTax = ((hPrice * pTaxRate / 100.0) / 12.0) * Math.pow(1 + pTaxInc / 100.0, yearsSinceStart);
                double currentHIns = (hIns / 12.0) * Math.pow(1 + hInsInc / 100.0, yearsSinceStart);
                double currentHFee = (hFee / 12.0) * Math.pow(1 + hFeeInc / 100.0, yearsSinceStart);
                double currentOCosts = (oCosts / 12.0) * Math.pow(1 + oCostsInc / 100.0, yearsSinceStart);

                totalPTax += currentPTax;
                totalHIns += currentHIns;
                totalHFee += currentHFee;
                totalOCosts += currentOCosts;

                if (balance <= 0.01) {
                    payoffMonth = currentMonth;
                    break;
                }
            }

            // Payoff Date
            Calendar payoffCal = Calendar.getInstance();
            payoffCal.set(startYear, startMonth, 1);
            payoffCal.add(Calendar.MONTH, payoffMonth);
            String payoffDateStr = String.format(Locale.US, "%s. %d", getMonthName(payoffCal.get(Calendar.MONTH)), payoffCal.get(Calendar.YEAR));

            // MIP Payoff Date
            String mipPayoffDateStr = "";
            if (mipStopMonth > 0 && mipDurationIdx != 0) {
                Calendar mipCal = Calendar.getInstance();
                mipCal.set(startYear, startMonth, 1);
                mipCal.add(Calendar.MONTH, mipStopMonth);
                mipPayoffDateStr = String.format(Locale.US, "%s. %d", getMonthName(mipCal.get(Calendar.MONTH)), mipCal.get(Calendar.YEAR));
            }

            // Bi-weekly calculation (Simplified: without extra payments)
            double monthlyPIRounded = Math.round(monthlyPI * 100.0) / 100.0;
            double biweeklyPay = monthlyPIRounded / 2.0;
            double biweeklyYearFreq = 26;
            double biweeklyRate = (iRate / 100.0) / biweeklyYearFreq;
            int biweeklyTotalPayments = 0;
            double biweeklyBalance = totalLoan;
            double biweeklyTotalInterest = 0;
            double lastPayment = 0;
            while (biweeklyBalance > 0.01 && biweeklyTotalPayments < totalMonths * 4) {
                biweeklyTotalPayments++;
                double interest = biweeklyBalance * biweeklyRate;
                double principal = biweeklyPay - interest;
                if (principal > biweeklyBalance) {
                    principal = biweeklyBalance;
                    lastPayment = principal + interest;
                } else {
                    lastPayment = biweeklyPay;
                }
                biweeklyTotalInterest += interest;
                biweeklyBalance -= principal;
            }
            double effectivePayments = (biweeklyTotalPayments > 0) ? (biweeklyTotalPayments - 1) + (lastPayment / biweeklyPay) : 0;
            double biweeklyPayoffYears = effectivePayments / biweeklyYearFreq;
            biweeklyPayoffYears = Math.round(biweeklyPayoffYears * 100.0) / 100.0;

            // Prepare results
            HashMap<String, Object> results = new HashMap<>();
            results.put("monthlyPay", monthlyPI);
            results.put("payoffYears", payoffMonth / 12);
            results.put("payoffMonths", payoffMonth % 12);
            results.put("payoffCount", payoffMonth);
            
            results.put("firstMortgage", monthlyPI);
            results.put("totalMortgage", totalMortgagePayments);
            results.put("firstExtra", firstMonthExtra);
            results.put("totalExtra", totalExtra);
            results.put("firstPTax", firstMonthPTax);
            results.put("totalPTax", totalPTax);
            results.put("firstHIns", firstMonthHIns);
            results.put("totalHIns", totalHIns);
            results.put("firstMIP", firstMonthMIP);
            results.put("totalMIP", totalMIP);
            results.put("firstHFee", firstMonthHFee);
            results.put("totalHFee", totalHFee);
            results.put("firstOCosts", firstMonthOCosts);
            results.put("totalOCosts", totalOCosts);
            
            results.put("housePrice", hPrice);
            results.put("loanWithMIP", totalLoan);
            results.put("dpAmount", dpAmount);
            results.put("ufMIP", ufMIPAmount);
            results.put("totalInterest", totalInterest);
            results.put("payoffDate", payoffDateStr);
            results.put("mipPayoffDate", mipPayoffDateStr);
            results.put("mipPayoffCount", mipStopMonth);

            results.put("showBiweekly", showBiweeklyPayback.isChecked());
            results.put("biweeklyPay", biweeklyPay);
            results.put("biweeklyInterest", biweeklyTotalInterest);
            results.put("biweeklyYears", biweeklyPayoffYears);

            showResultFragment(results);

        } catch (Exception e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDateReached(int startY, int startM, int elapsedMonths, int targetY, int targetM) {
        if (targetY == 0) return false;
        int startTotal = startY * 12 + startM;
        int targetTotal = targetY * 12 + targetM;
        int currentTotal = startTotal + elapsedMonths - 1;
        return currentTotal >= targetTotal;
    }

    private boolean isYearlyDateReached(int startY, int startM, int elapsedMonths, int targetY, int targetM) {
        if (targetY == 0) return false;
        int startTotal = startY * 12 + startM;
        int targetTotal = targetY * 12 + targetM;
        int currentTotal = startTotal + elapsedMonths - 1;
        return currentTotal >= targetTotal && (currentTotal - targetTotal) % 12 == 0;
    }

    private boolean isOneTimeDateReached(int startY, int startM, int elapsedMonths, int targetY, int targetM) {
        if (targetY == 0) return false;
        int startTotal = startY * 12 + startM;
        int targetTotal = targetY * 12 + targetM;
        int currentTotal = startTotal + elapsedMonths - 1;
        if (targetTotal <= startTotal) {
            return currentTotal == startTotal;
        }
        return currentTotal == targetTotal;
    }

    private String getMonthName(int month) {
        String[] months = getResources().getStringArray(R.array.months_options);
        if (month >= 0 && month < months.length) return months[month];
        return "";
    }

    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void showResultFragment(HashMap<String, Object> results) {
        FHALoanResultFragment fragment = FHALoanResultFragment.newInstance(results);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void hideResultFragment() {
        getSupportFragmentManager().popBackStack();
    }
}
