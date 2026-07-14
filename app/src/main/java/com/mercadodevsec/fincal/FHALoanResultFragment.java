package com.mercadodevsec.fincal;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Locale;

public class FHALoanResultFragment extends Fragment {

    private static final String ARG_RESULTS = "results";
    private HashMap<String, Object> mResults;

    public FHALoanResultFragment() {
        // Required empty public constructor
    }

    public static FHALoanResultFragment newInstance(HashMap<String, Object> results) {
        FHALoanResultFragment fragment = new FHALoanResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESULTS, results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResults = (HashMap<String, Object>) getArguments().getSerializable(ARG_RESULTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fha_loan_result, container, false);

        TextView monthlyPayText = view.findViewById(R.id.monthlyPayText);
        TextView payoffTimeText = view.findViewById(R.id.payoffTimeText);
        TableLayout resultTable = view.findViewById(R.id.resultTable);
        LinearLayout summaryLayout = view.findViewById(R.id.summaryLayout);
        LinearLayout biweeklyLayout = view.findViewById(R.id.biweeklyLayout);
        TextView biweeklyDetails = view.findViewById(R.id.biweeklyDetails);
        Button btnBack = view.findViewById(R.id.btnBackToInput);

        double monthlyPI = getDouble("monthlyPay");
        monthlyPayText.setText(String.format(Locale.US, "Monthly Pay: $%,.2f", monthlyPI));

        int years = getInt("payoffYears");
        int months = getInt("payoffMonths");
        payoffTimeText.setText(String.format(Locale.US, "With the extra payment, the loan will be paid off in %d years and %d months.", years, months));

        populateTable(resultTable);
        populateSummary(summaryLayout);

        if (getBoolean()) {
            biweeklyLayout.setVisibility(View.VISIBLE);
            double biPay = getDouble("biweeklyPay");
            double biInterest = getDouble("biweeklyInterest");
            double biYears = getDouble("biweeklyYears");
            biweeklyDetails.setText(String.format(Locale.US, "Bi-weekly Payment: $%,.2f\nTotal Interest: $%,.2f\nPayoff: %.2f years", biPay, biInterest, biYears));
        }

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof Category1Option10Activity) {
                ((Category1Option10Activity) getActivity()).hideResultFragment();
            }
        });

        return view;
    }

    private void populateTable(TableLayout table) {
        addTableRow(table, "Mortgage Payment", getDouble("firstMortgage"), getDouble("totalMortgage"));
        addTableRow(table, "Extra Payment", getDouble("firstExtra"), getDouble("totalExtra"));
        addTableRow(table, "Property Tax", getDouble("firstPTax"), getDouble("totalPTax"));
        addTableRow(table, "Home Insurance", getDouble("firstHIns"), getDouble("totalHIns"));

        double totalMIP = getDouble("totalMIP");
        if (totalMIP > 0) {
            addTableRow(table, "Annual MIP", getDouble("firstMIP"), totalMIP);
        }

        addTableRow(table, "HOA Fee", getDouble("firstHFee"), getDouble("totalHFee"));
        addTableRow(table, "Other Costs", getDouble("firstOCosts"), getDouble("totalOCosts"));

        double totalFirst = getDouble("firstMortgage") + getDouble("firstExtra") +
                getDouble("firstPTax") + getDouble("firstHIns") +
                (totalMIP > 0 ? getDouble("firstMIP") : 0) +
                getDouble("firstHFee") + getDouble("firstOCosts");

        double totalOverall = getDouble("totalMortgage") + getDouble("totalExtra") +
                getDouble("totalPTax") + getDouble("totalHIns") +
                totalMIP + getDouble("totalHFee") +
                getDouble("totalOCosts");

        addTableRow(table, "Total Out-of-Pocket", totalFirst, totalOverall, true);
    }

    private void addTableRow(TableLayout table, String label, double first, double total) {
        addTableRow(table, label, first, total, false);
    }

    private void addTableRow(TableLayout table, String label, double first, double total, boolean isBold) {
        TableRow row = new TableRow(getContext());
        row.addView(createTextView(label, isBold));
        row.addView(createTextView(String.format(Locale.US, "$%,.2f", first), isBold));
        row.addView(createTextView(String.format(Locale.US, "$%,.2f", total), isBold));
        table.addView(row);
    }

    private void populateSummary(LinearLayout layout) {
        addSummaryItem(layout, "House Price", getDouble("housePrice"));
        addSummaryItem(layout, "Loan Amount with Upfront MIP", getDouble("loanWithMIP"));
        addSummaryItem(layout, "Down Payment", getDouble("dpAmount"));
        addSummaryItem(layout, "Upfront MIP", getDouble("ufMIP"));
        addSummaryTextItem(layout, String.format(Locale.US, "Total of %d Mortgage Payments", getInt("payoffCount")), String.format(Locale.US, "$%,.2f", getDouble("totalMortgage")));
        addSummaryItem(layout, "Total Interest", getDouble("totalInterest"));
        addSummaryItem(layout, "Total Extra Payments", getDouble("totalExtra"));
        addSummaryTextItem(layout, "Mortgage Payoff Date", getString("payoffDate"));

        String mipDate = getString("mipPayoffDate");
        if (!mipDate.isEmpty()) {
            int mipCount = getInt("mipPayoffCount");
            addSummaryTextItem(layout, String.format(Locale.US, "Annual MIP Payoff Date (%d Total Payments)", mipCount), mipDate);
        }
    }

    private void addSummaryItem(LinearLayout layout, String label, double value) {
        addSummaryTextItem(layout, label, String.format(Locale.US, "$%,.2f", value));
    }

    private void addSummaryTextItem(LinearLayout layout, String label, String value) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 4, 0, 4);

        TextView labelTv = new TextView(getContext());
        labelTv.setText(label);
        labelTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        labelTv.setTextColor(getResources().getColor(R.color.text_primary, null));

        TextView valueTv = new TextView(getContext());
        valueTv.setText(value);
        valueTv.setGravity(Gravity.END);
        valueTv.setTextColor(getResources().getColor(R.color.text_primary, null));
        valueTv.setTypeface(null, Typeface.BOLD);

        itemLayout.addView(labelTv);
        itemLayout.addView(valueTv);
        layout.addView(itemLayout);
    }

    private TextView createTextView(String text, boolean bold) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextColor(getResources().getColor(R.color.text_primary, null));
        if (bold) tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private double getDouble(String key) {
        if (mResults == null || !mResults.containsKey(key)) return 0;
        Object value = mResults.get(key);
        return value instanceof Double ? (Double) value : 0;
    }

    private int getInt(String key) {
        if (mResults == null || !mResults.containsKey(key)) return 0;
        Object value = mResults.get(key);
        return value instanceof Integer ? (Integer) value : 0;
    }

    private boolean getBoolean() {
        if (mResults == null || !mResults.containsKey("showBiweekly")) return false;
        Object value = mResults.get("showBiweekly");
        return value instanceof Boolean ? (Boolean) value : false;
    }

    private String getString(String key) {
        if (mResults == null || !mResults.containsKey(key)) return "";
        Object value = mResults.get(key);
        return value instanceof String ? (String) value : "";
    }
}
