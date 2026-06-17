package com.mercadodevsec.fincal;

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

import java.util.Locale;

public class TableFragment extends Fragment {

    private static final String ARG_PRINCIPAL = "principal";
    private static final String ARG_YEARS = "years";
    private static final String ARG_RATE = "rate";

    private double mPrincipal;
    private double mYears;
    private double mRate;

    public TableFragment() {
        // Required empty public constructor
    }

    public static TableFragment newInstance(double principal, double years, double rate) {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PRINCIPAL, principal);
        args.putDouble(ARG_YEARS, years);
        args.putDouble(ARG_RATE, rate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPrincipal = getArguments().getDouble(ARG_PRINCIPAL);
            mYears = getArguments().getDouble(ARG_YEARS);
            mRate = getArguments().getDouble(ARG_RATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        TableLayout table = view.findViewById(R.id.amortizationTable);
        Button btnBack = view.findViewById(R.id.btnBackToInput);

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof Category1Option2Activity) {
                ((Category1Option2Activity) getActivity()).hideTableFragment();
            }
        });

        calculateAndPopulateTable(table);

        return view;
    }

    private void calculateAndPopulateTable(TableLayout table) {
        double monthlyRate = mRate / 100 / 12;
        int totalMonths = (int) (mYears * 12);
        
        if (monthlyRate == 0) {
            double monthlyPayment = mPrincipal / totalMonths;
            double balance = mPrincipal;
            for (int year = 1; year <= Math.ceil(mYears); year++) {
                double yearlyPrincipal = 0;
                for (int month = 1; month <= 12 && (year - 1) * 12 + month <= totalMonths; month++) {
                    yearlyPrincipal += monthlyPayment;
                    balance -= monthlyPayment;
                }
                if (balance < 0) balance = 0;
                addRow(table, year, 0, yearlyPrincipal, balance);
            }
            return;
        }

        double monthlyPayment = (mPrincipal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -totalMonths));
        double balance = mPrincipal;

        for (int year = 1; year <= Math.ceil(mYears); year++) {
            double yearlyInterest = 0;
            double yearlyPrincipal = 0;
            for (int month = 1; month <= 12 && (year - 1) * 12 + month <= totalMonths; month++) {
                double interest = balance * monthlyRate;
                double principalPaid = monthlyPayment - interest;
                yearlyInterest += interest;
                yearlyPrincipal += principalPaid;
                balance -= principalPaid;
            }
            if (balance < 0) balance = 0;
            addRow(table, year, yearlyInterest, yearlyPrincipal, balance);
        }
    }

    private void addRow(TableLayout table, int year, double interest, double principal, double balance) {
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        row.addView(createTextView(String.valueOf(year)));
        row.addView(createTextView(String.format(Locale.US, "%.2f", interest)));
        row.addView(createTextView(String.format(Locale.US, "%.2f", principal)));
        row.addView(createTextView(String.format(Locale.US, "%.2f", balance)));

        table.addView(row);
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.text_primary, null));
        return tv;
    }
}
