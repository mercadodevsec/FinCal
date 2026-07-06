package com.mercadodevsec.fincal;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class RentalResultFragment extends Fragment {

    private static final String ARG_RESULTS = "results";

    private HashMap<String, Object> mResults;

    public RentalResultFragment() {
    }

    public static RentalResultFragment newInstance(HashMap<String, Object> results) {
        RentalResultFragment fragment = new RentalResultFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rental_result, container, false);

        TableLayout multiYearTable = view.findViewById(R.id.multiYearTable);
        TableLayout firstYearTable = view.findViewById(R.id.firstYearTable);
        TextView multiYearTitle = view.findViewById(R.id.multiYearTitle);
        Button btnBack = view.findViewById(R.id.btnBackToInput);

        multiYearTitle.setText(String.format("For the %s Years Invested", mResults.get("hLength")));

        populateMultiYearTable(multiYearTable);
        populateFirstYearTable(firstYearTable);

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof Category1Option8Activity) {
                ((Category1Option8Activity) getActivity()).hideResultFragment();
            }
        });

        return view;
    }

    private void populateMultiYearTable(TableLayout table) {
        addResultRow(table, "Annual Return (IRR):", (String) mResults.get("irr"), true);
        addResultRow(table, "Total Profit when Sold:", (String) mResults.get("totalProfit"), true);
        addResultRow(table, "Cash on Cash Return:", (String) mResults.get("cashOnCash"), true);
        addResultRow(table, "Capitalization Rate:", (String) mResults.get("capRate"), true);
        addResultRow(table, "Total Rental Income:", (String) mResults.get("totalRentalIncome"), false);
        addResultRow(table, "Total Mortgage Payments:", (String) mResults.get("totalMortgagePayments"), false);
        addResultRow(table, "Total Expenses:", (String) mResults.get("totalExpenses"), false);
        addResultRow(table, "Total Net Operating Income:", (String) mResults.get("totalNOI"), false);
    }

    private void populateFirstYearTable(TableLayout table) {
        // Header
        TableRow header = new TableRow(getContext());
        header.addView(createTextView("", false, false));
        header.addView(createTextView("Monthly", true, false));
        header.addView(createTextView("Annual", true, false));
        table.addView(header);

        addFirstYearRow(table, "Income:", (String) mResults.get("grossMonthlyIncome"), (String) mResults.get("grossAnnualIncome"), false);
        addFirstYearRow(table, "Mortgage Pay:", (String) mResults.get("monthlyMortgage"), (String) mResults.get("annualMortgage"), false);
        addFirstYearRow(table, "Vacancy (" + mResults.get("vRate") + "%):", (String) mResults.get("vacancyMonthly"), (String) mResults.get("vacancyAnnual"), false);
        addFirstYearRow(table, "Management Fee (" + mResults.get("mFee") + "%):", (String) mResults.get("managementMonthly"), (String) mResults.get("managementAnnual"), false);
        addFirstYearRow(table, "Property Tax:", (String) mResults.get("propertyTaxMonthly"), (String) mResults.get("propertyTaxAnnual"), false);
        addFirstYearRow(table, "Total Insurance:", (String) mResults.get("insuranceMonthly"), (String) mResults.get("insuranceAnnual"), false);
        addFirstYearRow(table, "HOA Fee:", (String) mResults.get("hoaMonthly"), (String) mResults.get("hoaAnnual"), false);
        addFirstYearRow(table, "Maintenance Cost:", (String) mResults.get("maintenanceMonthly"), (String) mResults.get("maintenanceAnnual"), false);
        addFirstYearRow(table, "Other Cost:", (String) mResults.get("otherCostsMonthly"), (String) mResults.get("otherCostsAnnual"), false);
        addFirstYearRow(table, "Cash Flow:", (String) mResults.get("cashFlowMonthly"), (String) mResults.get("cashFlowAnnual"), true);
        addFirstYearRow(table, "Net Operating Income (NOI):", (String) mResults.get("netOperatingIncomeMonthly"), (String) mResults.get("netOperatingIncomeAnnual"), false);
    }

    private void addResultRow(TableLayout table, String label, String value, boolean colorize) {
        TableRow row = new TableRow(getContext());
        row.addView(createTextView(label, false, false));
        row.addView(createTextView(value, colorize, colorize));
        table.addView(row);
    }

    private void addFirstYearRow(TableLayout table, String label, String monthly, String annual, boolean isCashFlow) {
        TableRow row = new TableRow(getContext());
        row.addView(createTextView(label, false, false));
        row.addView(createTextView(monthly, isCashFlow, isCashFlow));
        row.addView(createTextView(annual, isCashFlow, isCashFlow));
        table.addView(row);
    }

    private TextView createTextView(String text, boolean bold, boolean colorize) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setPadding(16, 8, 16, 8);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setTextColor(getResources().getColor(R.color.text_primary, null));
        if (bold) {
            tv.setTypeface(null, Typeface.BOLD);
        }
        if (colorize) {
            if (text.contains("-")) {
                tv.setTextColor(Color.RED);
            } else {
                tv.setTextColor(Color.parseColor("#008000")); // Green
            }
        }
        return tv;
    }
}
