package com.mercadodevsec.fincal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Category1Option8Activity extends AppCompatActivity {

    private EditText purchasePrice, downPayment, interestRate, loanTerm, closingCosts;
    private EditText repairCosts, afterRepairs;
    private EditText propertyTaxes, propertyTaxesIncrease;
    private EditText totalInsurance, totalInsuranceIncrease;
    private EditText hoaFee, hoaFeeIncrease;
    private EditText maintenance, maintenanceIncrease;
    private EditText otherCosts, otherCostsIncrease;
    private EditText monthlyRent, monthlyRentIncrease;
    private EditText otherIncome, otherIncomeIncrease;
    private EditText vacancyRate, managementFee;
    private EditText valueAppreciation, sellPrice, holdingLength, costToSell;
    private RadioGroup radioGroupUseLoan, radioGroupNeedRepairs, radioGroupKnowSellPrice;
    private FrameLayout fragmentContainer;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category1_option8);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupVisibilityToggles();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (fragmentContainer.getVisibility() == View.VISIBLE) {
                    hideResultFragment();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        calculateButton.setOnClickListener(v -> calculateRental());
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        purchasePrice = findViewById(R.id.purchasePrice);
        downPayment = findViewById(R.id.downPayment);
        interestRate = findViewById(R.id.interestRate);
        loanTerm = findViewById(R.id.loanTerm);
        closingCosts = findViewById(R.id.closingCosts);
        repairCosts = findViewById(R.id.repairCosts);
        afterRepairs = findViewById(R.id.afterRepairs);
        propertyTaxes = findViewById(R.id.propertyTaxes);
        propertyTaxesIncrease = findViewById(R.id.propertyTaxesIncrease);
        totalInsurance = findViewById(R.id.totalInsurance);
        totalInsuranceIncrease = findViewById(R.id.totalInsuranceIncrease);
        hoaFee = findViewById(R.id.hoaFee);
        hoaFeeIncrease = findViewById(R.id.hoaFeeIncrease);
        maintenance = findViewById(R.id.maintenance);
        maintenanceIncrease = findViewById(R.id.maintenanceIncrease);
        otherCosts = findViewById(R.id.otherCosts);
        otherCostsIncrease = findViewById(R.id.otherCostsIncrease);
        monthlyRent = findViewById(R.id.monthlyRent);
        monthlyRentIncrease = findViewById(R.id.monthlyRentIncrease);
        otherIncome = findViewById(R.id.otherIncome);
        otherIncomeIncrease = findViewById(R.id.otherIncomeIncrease);
        vacancyRate = findViewById(R.id.vacancyRate);
        managementFee = findViewById(R.id.managementFee);
        valueAppreciation = findViewById(R.id.valueAppreciation);
        sellPrice = findViewById(R.id.sellPrice);
        holdingLength = findViewById(R.id.holdingLength);
        costToSell = findViewById(R.id.costToSell);

        radioGroupUseLoan = findViewById(R.id.radioGroupUseLoan);
        radioGroupNeedRepairs = findViewById(R.id.radioGroupNeedRepairs);
        radioGroupKnowSellPrice = findViewById(R.id.radioGroupKnowSellPrice);

        fragmentContainer = new FrameLayout(this);
        fragmentContainer.setId(View.generateViewId());
        ((android.view.ViewGroup) findViewById(R.id.main)).addView(fragmentContainer, new android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        fragmentContainer.setVisibility(View.GONE);

        calculateButton = findViewById(R.id.calculateButton);
    }

    private void setupVisibilityToggles() {
        radioGroupUseLoan.setOnCheckedChangeListener((group, checkedId) -> {
            int visibility = (checkedId == R.id.radioUseLoanYes) ? View.VISIBLE : View.GONE;
            downPayment.setVisibility(visibility);
            interestRate.setVisibility(visibility);
            loanTerm.setVisibility(visibility);
        });

        radioGroupNeedRepairs.setOnCheckedChangeListener((group, checkedId) -> {
            int visibility = (checkedId == R.id.radioNeedRepairYes) ? View.VISIBLE : View.GONE;
            repairCosts.setVisibility(visibility);
            afterRepairs.setVisibility(visibility);
        });

        radioGroupKnowSellPrice.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioKnowSellPriceYes) {
                sellPrice.setVisibility(View.VISIBLE);
                valueAppreciation.setVisibility(View.GONE);
            } else {
                sellPrice.setVisibility(View.GONE);
                valueAppreciation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void calculateRental() {
        if (purchasePrice.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in purchase price field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (monthlyRent.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in monthly rent field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (holdingLength.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill in holding length field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getDouble(holdingLength) <= 0) {
            Toast.makeText(this, "Holding length must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double pPrice = getDouble(purchasePrice);
            boolean useLoan = radioGroupUseLoan.getCheckedRadioButtonId() == R.id.radioUseLoanYes;

            if (useLoan && (loanTerm.getText().toString().isEmpty() || getDouble(loanTerm) <= 0)) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            double dPaymentPct = useLoan ? getDouble(downPayment) : 0;
            double iRate = useLoan ? getDouble(interestRate) : 0;
            double lTerm = useLoan ? getDouble(loanTerm) : 0;
            double cCosts = getDouble(closingCosts);

            boolean needRepairs = radioGroupNeedRepairs.getCheckedRadioButtonId() == R.id.radioNeedRepairYes;
            double rCosts = needRepairs ? getDouble(repairCosts) : 0;
            double aRepairs = needRepairs ? getDouble(afterRepairs) : pPrice;

            double pTaxes = getDouble(propertyTaxes);
            double pTaxesInc = getDouble(propertyTaxesIncrease);
            double tInsurance = getDouble(totalInsurance);
            double tInsuranceInc = getDouble(totalInsuranceIncrease);
            double hFee = getDouble(hoaFee);
            double hFeeInc = getDouble(hoaFeeIncrease);
            double maint = getDouble(maintenance);
            double maintInc = getDouble(maintenanceIncrease);
            double oCosts = getDouble(otherCosts);
            double oCostsInc = getDouble(otherCostsIncrease);

            double mRent = getDouble(monthlyRent);
            double mRentInc = getDouble(monthlyRentIncrease);
            double oIncome = getDouble(otherIncome);
            double oIncomeInc = getDouble(otherIncomeIncrease);
            double vRate = getDouble(vacancyRate);
            double mFee = getDouble(managementFee);

            boolean knowSellPrice = radioGroupKnowSellPrice.getCheckedRadioButtonId() == R.id.radioKnowSellPriceYes;
            double vAppreciation = knowSellPrice ? 0 : getDouble(valueAppreciation);
            double sPrice = knowSellPrice ? getDouble(sellPrice) : 0;
            double hLength = getDouble(holdingLength);
            double cToSell = getDouble(costToSell);

            if (hLength <= 0) hLength = 1;

            // Calculations
            double loanAmount = useLoan ? pPrice * (1 - dPaymentPct / 100) : 0;
            double monthlyMortgage = 0;
            if (useLoan && iRate > 0) {
                double monthlyRate = iRate / 100 / 12;
                double months = lTerm * 12;
                monthlyMortgage = loanAmount * monthlyRate * Math.pow(1 + monthlyRate, months) / (Math.pow(1 + monthlyRate, months) - 1);
            } else if (useLoan && lTerm > 0) {
                monthlyMortgage = loanAmount / (lTerm * 12);
            }

            double annualMortgage = monthlyMortgage * 12;

            // Year 1
            double grossMonthlyIncome = mRent + oIncome;
            double grossAnnualIncome = grossMonthlyIncome * 12;
            double vacancyMonthly = (grossMonthlyIncome * vRate / 100);
            double vacancyAnnual = vacancyMonthly * 12;

            double propertyTaxMonthly = pTaxes / 12;
            double insuranceMonthly = tInsurance / 12;
            double hoaMonthly = hFee / 12;
            double maintenanceMonthly = maint / 12;
            double otherCostsMonthly = oCosts / 12;
            double managementMonthly = (grossMonthlyIncome - vacancyMonthly) * mFee / 100;
            double managementAnnual = managementMonthly * 12;

            double totalOperatingExpensesMonthly = propertyTaxMonthly + insuranceMonthly + hoaMonthly + maintenanceMonthly + otherCostsMonthly;
            double netOperatingIncomeMonthly = (grossMonthlyIncome - vacancyMonthly - managementMonthly) - totalOperatingExpensesMonthly;
            double netOperatingIncomeAnnual = netOperatingIncomeMonthly * 12;

            double cashFlowMonthly = netOperatingIncomeMonthly - monthlyMortgage;
            double cashFlowAnnual = cashFlowMonthly * 12;

            // Multi-year analysis
            double totalRentalIncome = 0;
            double totalMortgagePayments = 0;
            double totalExpenses = 0;
            double totalNOI = 0;
            double totalCashFlow = 0;

            double currentMRent = mRent;
            double currentOIncome = oIncome;
            double currentPTaxes = pTaxes;
            double currentTInsurance = tInsurance;
            double currentHFee = hFee;
            double currentMaint = maint;
            double currentOCosts = oCosts;

            List<Double> cashFlows = new ArrayList<>();
            double initialInvestment = (useLoan ? (pPrice * dPaymentPct / 100) : pPrice) + cCosts + rCosts;
            cashFlows.add(-initialInvestment);

            for (int i = 1; i <= (int) hLength; i++) {
                double yearGrossIncome = (currentMRent + currentOIncome) * 12;
                double yearVacancy = (yearGrossIncome * vRate / 100);
                double yearManagement = (yearGrossIncome - yearVacancy) * mFee / 100;
                double yearOperatingExpenses = currentPTaxes + currentTInsurance + currentHFee + currentMaint + currentOCosts;
                double yearNOI = (yearGrossIncome - yearVacancy - yearManagement) - yearOperatingExpenses;
                double yearCashFlow = yearNOI - annualMortgage;

                totalRentalIncome += (yearGrossIncome - yearVacancy - yearManagement);
                totalMortgagePayments += annualMortgage;
                totalExpenses += yearOperatingExpenses;
                totalNOI += yearNOI;
                totalCashFlow += yearCashFlow;

                cashFlows.add(yearCashFlow);

                currentMRent *= (1 + mRentInc / 100);
                currentOIncome *= (1 + oIncomeInc / 100);
                currentPTaxes *= (1 + pTaxesInc / 100);
                currentTInsurance *= (1 + tInsuranceInc / 100);
                currentHFee *= (1 + hFeeInc / 100);
                currentMaint *= (1 + maintInc / 100);
                currentOCosts *= (1 + oCostsInc / 100);
            }

            double finalSellPrice = knowSellPrice ? sPrice : aRepairs * Math.pow(1 + vAppreciation / 100, hLength);
            double sellingCosts = finalSellPrice * cToSell / 100;

            double remainingBalance = 0;
            if (useLoan && iRate > 0) {
                double monthlyRate = iRate / 100 / 12;
                double months = lTerm * 12;
                double monthsPaid = hLength * 12;
                if (monthsPaid < months) {
                    remainingBalance = loanAmount * (Math.pow(1 + monthlyRate, months) - Math.pow(1 + monthlyRate, monthsPaid)) / (Math.pow(1 + monthlyRate, months) - 1);
                }
            }

            double netProceeds = finalSellPrice - sellingCosts - remainingBalance;
            cashFlows.set(cashFlows.size() - 1, cashFlows.get(cashFlows.size() - 1) + netProceeds);

            double totalProfit = totalCashFlow + netProceeds - initialInvestment;
            double irr = calculateIRR(cashFlows);
            double cashOnCash = (totalProfit / initialInvestment) * 100;
            double capRate = (netOperatingIncomeAnnual / pPrice) * 100;

            HashMap<String, Object> results = new HashMap<>();
            results.put("hLength", (int) hLength);
            results.put("irr", String.format(Locale.US, "%.2f%%", irr * 100));
            results.put("totalProfit", formatCurrency(totalProfit));
            results.put("cashOnCash", String.format(Locale.US, "%.2f%%", cashOnCash));
            results.put("capRate", String.format(Locale.US, "%.2f%%", capRate));
            results.put("totalRentalIncome", formatCurrency(totalRentalIncome));
            results.put("totalMortgagePayments", formatCurrency(totalMortgagePayments));
            results.put("totalExpenses", formatCurrency(totalExpenses));
            results.put("totalNOI", formatCurrency(totalNOI));
            results.put("grossMonthlyIncome", formatCurrency(grossMonthlyIncome));
            results.put("grossAnnualIncome", formatCurrency(grossAnnualIncome));
            results.put("monthlyMortgage", formatCurrency(monthlyMortgage));
            results.put("annualMortgage", formatCurrency(annualMortgage));
            results.put("vRate", (int) vRate);
            results.put("vacancyMonthly", formatCurrency(vacancyMonthly));
            results.put("vacancyAnnual", formatCurrency(vacancyAnnual));
            results.put("mFee", (int) mFee);
            results.put("managementMonthly", formatCurrency(managementMonthly));
            results.put("managementAnnual", formatCurrency(managementAnnual));
            results.put("propertyTaxMonthly", formatCurrency(propertyTaxMonthly));
            results.put("propertyTaxAnnual", formatCurrency(pTaxes));
            results.put("insuranceMonthly", formatCurrency(insuranceMonthly));
            results.put("insuranceAnnual", formatCurrency(tInsurance));
            results.put("hoaMonthly", formatCurrency(hoaMonthly));
            results.put("hoaAnnual", formatCurrency(hFee));
            results.put("maintenanceMonthly", formatCurrency(maintenanceMonthly));
            results.put("maintenanceAnnual", formatCurrency(maint));
            results.put("otherCostsMonthly", formatCurrency(otherCostsMonthly));
            results.put("otherCostsAnnual", formatCurrency(oCosts));
            results.put("cashFlowMonthly", formatCurrency(cashFlowMonthly));
            results.put("cashFlowAnnual", formatCurrency(cashFlowAnnual));
            results.put("netOperatingIncomeMonthly", formatCurrency(netOperatingIncomeMonthly));
            results.put("netOperatingIncomeAnnual", formatCurrency(netOperatingIncomeAnnual));

            showResultFragment(results);

        } catch (Exception e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResultFragment(HashMap<String, Object> results) {
        RentalResultFragment fragment = RentalResultFragment.newInstance(results);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentContainer.getId(), fragment);
        transaction.commit();
        fragmentContainer.setVisibility(View.VISIBLE);
    }

    public void hideResultFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentContainer.getId());
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        fragmentContainer.setVisibility(View.GONE);
    }

    private double getDouble(EditText et) {
        String s = et.getText().toString();
        if (s.isEmpty()) return 0;
        return Double.parseDouble(s);
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.US, "$%,.2f", amount);
    }

    private double calculateIRR(List<Double> cashFlows) {
        double low = -1.0;
        double high = 10.0;
        double guess = 0.1;

        for (int i = 0; i < 100; i++) {
            double npv = 0;
            for (int j = 0; j < cashFlows.size(); j++) {
                npv += cashFlows.get(j) / Math.pow(1 + guess, j);
            }
            if (Math.abs(npv) < 0.0001) return guess;
            if (npv > 0) {
                low = guess;
            } else {
                high = guess;
            }
            guess = (low + high) / 2;
        }
        return guess;
    }
}
