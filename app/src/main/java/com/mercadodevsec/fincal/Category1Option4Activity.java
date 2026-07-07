package com.mercadodevsec.fincal;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TabStopSpan;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
    private Spinner refSignSpinner1, refSignSpinner2, refSignSpinner3, refSignSpinner4, dtiRatioSpinner;
    private TextView summaryTextView, resultTextView;
    private ScrollView scrollView;

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
        propertyTaxes = findViewById(R.id.propertyTax);
        hoaFee = findViewById(R.id.hoaFee);
        homeInsurance = findViewById(R.id.homeInsurance);
        refSignSpinner1 = findViewById(R.id.refSignSpinner1);
        refSignSpinner2 = findViewById(R.id.refSignSpinner2);
        refSignSpinner3 = findViewById(R.id.refSignSpinner3);
        refSignSpinner4 = findViewById(R.id.refSignSpinner4);
        dtiRatioSpinner = findViewById(R.id.dtiRatioSpinner);
        summaryTextView = findViewById(R.id.summaryTextView);
        resultTextView = findViewById(R.id.resultTextView);
        scrollView = findViewById(R.id.scrollView);
        Button calculateButton = findViewById(R.id.calculateButton);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

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

            if (termYears <= 0) {
                Toast.makeText(this, R.string.loan_term_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            // Get %, $ options
            boolean isDownPercent = refSignSpinner1.getSelectedItemPosition() == 0;
            double downVal = downStr.isEmpty() ? 0 : Double.parseDouble(downStr);
            double downPercent = isDownPercent ? downVal / 100.0 : 0;
            double downAmount = isDownPercent ? 0 : downVal;

            boolean isTaxPercent = refSignSpinner2.getSelectedItemPosition() == 0;
            double taxVal = taxStr.isEmpty() ? 0 : Double.parseDouble(taxStr);
            double taxK = isTaxPercent ? taxVal / 1200.0 : 0; // monthly multiplier
            double taxC = isTaxPercent ? 0 : taxVal / 12.0; // monthly dollar

            boolean isHoaPercent = refSignSpinner3.getSelectedItemPosition() == 0;
            double hoaVal = hoaStr.isEmpty() ? 0 : Double.parseDouble(hoaStr);
            double hoaK = isHoaPercent ? hoaVal / 1200.0 : 0;
            double hoaC = isHoaPercent ? 0 : hoaVal / 12.0;

            boolean isInsPercent = refSignSpinner4.getSelectedItemPosition() == 0;
            double insVal = insuranceStr.isEmpty() ? 0 : Double.parseDouble(insuranceStr);
            double insK = isInsPercent ? insVal / 1200.0 : 0;
            double insC = isInsPercent ? 0 : insVal / 12.0;

            double monthlyIncome = annualIncome / 12.0;

            double backEndRatio;
            double frontEndRatio;
            String loanTypeDescription;
            double monthlyFeeRate = 0;
            double upfrontFeeRate = 0;
            double qualifyingFeeRate = 0;

            int position = dtiRatioSpinner.getSelectedItemPosition();
            switch (position) {
                case 0: // Conventional 28/36
                    frontEndRatio = 0.28;
                    backEndRatio = 0.36;
                    loanTypeDescription = " according to the 28/36 rule, within which ";
                    // PMI for Conventional if down payment < 20%
                    if (isDownPercent && downPercent < 0.20) {
                        monthlyFeeRate = 0.005 / 12.0; // 0.5% annual PMI
                        qualifyingFeeRate = monthlyFeeRate;
                    }
                    break;
                case 1: // FHA 31/43
                    frontEndRatio = 0.31;
                    backEndRatio = 0.43;
                    loanTypeDescription = " with an FHA loan, within which ";
                    upfrontFeeRate = 0.0175; // Upfront MIP 1.75%
                    if (isDownPercent) {
                        monthlyFeeRate = (downPercent < 0.05) ? 0.0055 / 12.0 : 0.005 / 12.0;
                    } else {
                        monthlyFeeRate = 0.0055 / 12.0; // Assume low down for initial calc
                    }
                    qualifyingFeeRate = monthlyFeeRate;
                    break;
                case 2: // VA 41
                    frontEndRatio = 0.36;
                    backEndRatio = 0.41;
                    loanTypeDescription = " with a VA loan, within which ";
                    if (isDownPercent) {
                        if (downPercent < 0.05) monthlyFeeRate = 0.0215 / 12.0;
                        else if (downPercent < 0.10) monthlyFeeRate = 0.015 / 12.0;
                        else monthlyFeeRate = 0.0125 / 12.0;
                    } else {
                        monthlyFeeRate = 0.0215 / 12.0; // Assume low down for initial calc
                    }
                    qualifyingFeeRate = monthlyFeeRate;
                    break;
                default: // Percentages 10-50
                    backEndRatio = (position - 3) * 0.05 + 0.10;
                    frontEndRatio = 1.0; // No separate front-end limit for raw percentage cases
                    loanTypeDescription = " within which ";
                    // Raw percentage cases assume Conventional behavior (PMI if < 20%)
                    if (isDownPercent && downPercent < 0.20) {
                        monthlyFeeRate = 0.005 / 12.0;
                        qualifyingFeeRate = monthlyFeeRate;
                    }
                    break;
            }

            // Monthly P&I factor
            double monthlyRate = annualRate / 12.0;
            double n = termYears * 12;
            double factor = (monthlyRate > 0) ?
                    (monthlyRate * Math.pow(1 + monthlyRate, n)) / (Math.pow(1 + monthlyRate, n) - 1) :
                    1.0 / n;

            // Limiting monthly qualifying payment M
            double maxQualifyingMonthly_BackEnd = (monthlyIncome * backEndRatio) - monthlyDebt;
            double maxQualifyingMonthly_FrontEnd = monthlyIncome * frontEndRatio;
            double M = Math.min(maxQualifyingMonthly_BackEnd, maxQualifyingMonthly_FrontEnd);

            if (M < 0) M = 0;

            // Denominator components: P&I + qualifying fee + tax + ins + hoa
            double f = factor + qualifyingFeeRate;
            double sumK = taxK + insK + hoaK;
            double sumC = taxC + insC + hoaC;

            double maxHomePrice;
            if (position == 0 || position > 2) {
                // Conventional and Custom Percentages: Taxes/Ins/HOA are on home price
                if (isDownPercent) {
                    maxHomePrice = (M - sumC) / ((1 - downPercent) * f + sumK);
                } else {
                    maxHomePrice = (M + downAmount * f - sumC) / (f + sumK);
                    // Check if PMI applies/changes for dollar down in non-FHA/VA cases
                    if (qualifyingFeeRate == 0 && maxHomePrice > 0) {
                        if (downAmount / maxHomePrice < 0.20) {
                            qualifyingFeeRate = 0.005 / 12.0;
                            monthlyFeeRate = qualifyingFeeRate;
                            f = factor + qualifyingFeeRate;
                            maxHomePrice = (M + downAmount * f - sumC) / (f + sumK);
                        }
                    }
                }
            } else {
                // FHA and VA:
                if (isDownPercent) {
                    // Percentage Down: Taxes/Ins/HOA are calculated on the LOAN amount for qualifying
                    double maxLoan = (M - sumC) / (f + sumK);
                    maxHomePrice = maxLoan / (1 - downPercent);
                } else {
                    // Dollar Down: Taxes/Ins/HOA are calculated on the HOUSE PRICE for qualifying
                    double maxLoan = (M - downAmount * sumK - sumC) / (f + sumK);
                    maxHomePrice = maxLoan + downAmount;
                    // Recalculate if tier changes for FHA/VA dollar down
                    if (maxHomePrice > 0) {
                        double actualDownPercent = downAmount / maxHomePrice;
                        boolean needRecalc = false;
                        if (position == 1) {
                            double newFee = (actualDownPercent < 0.05) ? 0.0055 / 12.0 : 0.005 / 12.0;
                            if (Math.abs(newFee - monthlyFeeRate) > 1e-9) {
                                monthlyFeeRate = newFee;
                                needRecalc = true;
                            }
                        } else if (position == 2) {
                            double newFee;
                            if (actualDownPercent < 0.05) newFee = 0.0215 / 12.0;
                            else if (actualDownPercent < 0.10) newFee = 0.015 / 12.0;
                            else newFee = 0.0125 / 12.0;
                            if (Math.abs(newFee - monthlyFeeRate) > 1e-9) {
                                monthlyFeeRate = newFee;
                                needRecalc = true;
                            }
                        }
                        if (needRecalc) {
                            qualifyingFeeRate = monthlyFeeRate;
                            f = factor + qualifyingFeeRate;
                            maxLoan = (M - downAmount * sumK - sumC) / (f + sumK);
                            maxHomePrice = maxLoan + downAmount;
                        }
                    }
                }
            }

            if (maxHomePrice < 0) maxHomePrice = 0;

            double downPaymentAmount = isDownPercent ? maxHomePrice * downPercent : downAmount;
            double loanAmount = maxHomePrice - downPaymentAmount;
            if (loanAmount < 0) loanAmount = 0;

            double calculatedDownPercent = (maxHomePrice > 0) ? (downPaymentAmount / maxHomePrice) : 0;

            double upfrontFee = loanAmount * upfrontFeeRate;
            double closingCost = maxHomePrice * 0.03;
            double totalOneTimePayment = downPaymentAmount + upfrontFee + closingCost;

            double monthlyMortgagePayment = loanAmount * factor;
            double monthlyInsuranceFee = loanAmount * monthlyFeeRate;
            double monthlyQualifyingFee = loanAmount * qualifyingFeeRate;
            
            double annualPropertyTax = isTaxPercent ? maxHomePrice * (taxVal / 100.0) : taxVal;
            double annualHOA = isHoaPercent ? maxHomePrice * (hoaVal / 100.0) : hoaVal;
            double annualInsurance = isInsPercent ? maxHomePrice * (insVal / 100.0) : insVal;
            
            double annualMaintenance = maxHomePrice * 0.015;
            double totalMonthlyCost = monthlyMortgagePayment + monthlyInsuranceFee + (annualPropertyTax / 12.0) + (annualHOA / 12.0) + (annualInsurance / 12.0) + (annualMaintenance / 12.0);

            // DTI reporting: reflects the logic used for qualifying
            double actualFrontEnd;
            double actualBackEnd;
            double qualifyingHousingCosts;
            if (position == 1 || position == 2) {
                if (isDownPercent) {
                    // FHA/VA % down: Loan-based anchor for qualifying taxes
                    qualifyingHousingCosts = loanAmount * (factor + monthlyQualifyingFee + sumK) + sumC;
                } else {
                    // FHA/VA $ down: Price-based anchor for qualifying taxes
                    double qTax = isTaxPercent ? maxHomePrice * taxK : taxC;
                    double qIns = isInsPercent ? maxHomePrice * insK : insC;
                    double qHoa = isHoaPercent ? maxHomePrice * hoaK : hoaC;
                    qualifyingHousingCosts = monthlyMortgagePayment + qTax + qIns + qHoa + monthlyQualifyingFee;
                }
            } else {
                // Conventional/Custom: Always price-based anchor for qualifying taxes
                double qTax = isTaxPercent ? maxHomePrice * taxK : taxC;
                double qIns = isInsPercent ? maxHomePrice * insK : insC;
                double qHoa = isHoaPercent ? maxHomePrice * hoaK : hoaC;
                qualifyingHousingCosts = monthlyMortgagePayment + qTax + qIns + qHoa + monthlyQualifyingFee;
            }
            actualFrontEnd = qualifyingHousingCosts / monthlyIncome;
            actualBackEnd = (qualifyingHousingCosts + monthlyDebt) / monthlyIncome;

            DecimalFormat df = new DecimalFormat("#,##0");
            DecimalFormat pf = new DecimalFormat("0");
            String tab = "\t";

            StringBuilder summary = new StringBuilder();
            summary.append("You can afford a house up to <b>$").append(df.format(maxHomePrice)).append("</b>");
            summary.append(loanTypeDescription).append("$").append(df.format(loanAmount)).append(" is the loan and $").append(df.format(downPaymentAmount)).append(" is the down payment");
            
            if (!isDownPercent && maxHomePrice > 0) {
                summary.append(", which is ").append(new DecimalFormat("0.#").format(calculatedDownPercent * 100)).append("% of the house price");
            }
            summary.append(".");

            if (position == 0) {
                summary.append(" Most conventional loan lenders use the 28/36 rule.");
            }

            if (position == 10 || position == 11) { // 45% or 50% DTI
                String dtiPercent = (position == 10) ? "45%" : "50%";
                summary.append(" A Debt-to-Income Ratio of ").append(dtiPercent).append(" is very aggressive and is not recommended.");
            }

            if (position == 1 && calculatedDownPercent < 0.035) {
                summary.append(" FHA loan needs 3.5% down payment or more.");
            } else if (position != 2 && calculatedDownPercent < 0.05) {
                summary.append(" Very few lenders are willing to work with less than 5% down payment.");
            }

            summaryTextView.setText(Html.fromHtml(summary.toString(), Html.FROM_HTML_MODE_LEGACY));

            StringBuilder result = new StringBuilder();
            result.append("You can borrow").append(tab).append("$").append(df.format(loanAmount)).append("<br/>");
            result.append("Total price of the house").append(tab).append("$").append(df.format(maxHomePrice)).append("<br/>");
            result.append("Down payment").append(tab).append("$").append(df.format(downPaymentAmount)).append("<br/>");

            if (position == 1) {
                result.append("FHA upfront insurance premium (1.75%)").append(tab).append("$").append(df.format(upfrontFee)).append("<br/>");
            }

            result.append("Estimated closing cost (one time, assume 3%)").append(tab).append("$").append(df.format(closingCost)).append("<br/>");
            result.append("Front-end debt-to-income (DTI) ratio").append(tab).append(pf.format(actualFrontEnd * 100)).append("%<br/>");
            result.append("Back-end debt-to-income (DTI) ratio").append(tab).append(pf.format(actualBackEnd * 100)).append("%<br/>");
            result.append("Total one-time payment at closing").append(tab).append("<b>$").append(df.format(totalOneTimePayment)).append("</b><br/><br/>");

            result.append("Monthly mortgage payment").append(tab).append("$").append(df.format(monthlyMortgagePayment)).append("<br/>");

            if ((position == 0 || position > 2) && monthlyInsuranceFee > 0) {
                result.append("Monthly PMI insurance payment").append(tab).append("$").append(df.format(monthlyInsuranceFee)).append("<br/>");
            } else if (position == 1) {
                String mipRate = new DecimalFormat("0.##").format(monthlyFeeRate * 12 * 100);
                result.append("Monthly MIP payment (").append(mipRate).append("%)").append(tab).append("$").append(df.format(monthlyInsuranceFee)).append("<br/>");
            } else if (position == 2) {
                String vaRate = new DecimalFormat("0.##").format(monthlyFeeRate * 12 * 100);
                result.append("Monthly VA loan funding fee (").append(vaRate).append("%)<br/>Assuming veteran first time use").append(tab).append("$").append(df.format(monthlyInsuranceFee)).append("<br/>");
            }

            result.append("Annual property tax").append(tab).append("$").append(df.format(annualPropertyTax)).append("<br/>");
            result.append("Annual HOA or co-op fee").append(tab).append("$").append(df.format(annualHOA)).append("<br/>");
            result.append("Annual insurance cost").append(tab).append("$").append(df.format(annualInsurance)).append("<br/>");
            result.append("Estimated annual maintenance cost<br/>(repair, utility etc., assume 1.5%)").append(tab).append("$").append(df.format(annualMaintenance)).append("<br/>");
            result.append("Total monthly cost on the house").append(tab).append("<b>$").append(df.format(totalMonthlyCost)).append("</b>");

            Spanned spanned = Html.fromHtml(result.toString(), Html.FROM_HTML_MODE_LEGACY);
            SpannableString spannable = new SpannableString(spanned);
            // Large tab stop for clear column separation, fits well within HorizontalScrollView
            spannable.setSpan(new TabStopSpan.Standard(1000), 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            resultTextView.setText(spannable);

            // Auto-scroll to summary
            scrollView.post(() -> scrollView.smoothScrollTo(0, summaryTextView.getTop()));

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_input_format_warning, Toast.LENGTH_SHORT).show();
        }
    }
}
