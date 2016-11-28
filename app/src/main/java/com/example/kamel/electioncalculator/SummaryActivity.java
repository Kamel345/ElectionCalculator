package com.example.kamel.electioncalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    private static final String PESEL_ARRAY_TAG = "PESEL_ARRAY_ID";
    private static final String CANDIDATE_ARRAY_TAG = "CANDIDATE_ARRAY_ID";

    private ArrayList<String> usedPeselNumber = new ArrayList<>();
    private ArrayList<Candidate> candidateList;

    protected TextView tvCandidate;
    protected TextView tvVote;
    protected TextView tvPartyName;
    protected TextView tvPartyVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        candidateList = (ArrayList<Candidate>)getIntent().getSerializableExtra(CANDIDATE_ARRAY_TAG);

        createUI();

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usedPeselNumber = extras.getStringArrayList(PESEL_ARRAY_TAG);
        }

    }

    @Override
    public void onBackPressed()
    {

    }

    public void Logout(View v)
    {
        Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_LONG).show();

        Intent main = new Intent(getBaseContext(), MainActivity.class);
        main.putStringArrayListExtra(PESEL_ARRAY_TAG, usedPeselNumber);
        startActivity(main);
    }

    public void createUI()
    {
        int tempRow = 0;

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tlCandidate);

        //Table row 1
        TableRow tbRow0 = new TableRow(this);

        TextView tvTittle = new TextView(this);
        tvTittle.setText(R.string.candidate_title);
        tvTittle.setTextSize(20);

        tbRow0.addView(tvTittle);

        tableLayout.addView(tbRow0);

        //Table row 2
        TableRow tbRow1 = new TableRow(this);

        TextView tvName = new TextView(this);
        tvName.setText(R.string.candidate_name);
        tvName.setTextSize(16);

        TextView tvTittleVote = new TextView(this);
        tvTittleVote.setText(R.string.vote);
        tvTittleVote.setTextSize(16);

        tbRow1.addView(tvName);
        tbRow1.addView(tvTittleVote);

        tableLayout.addView(tbRow1, 1);

        for(int i = 0; i < candidateList.size() ; i++ )
        {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            tvCandidate = new TextView(this);
            tvVote = new TextView(this);

            tvCandidate.setText(candidateList.get(i).getName());
            tvVote.setText(Integer.toString(candidateList.get(i).getVote()));

            row.addView(tvCandidate);
            row.addView(tvVote);

            tableLayout.addView(row, i+2);

            tempRow += i+2;
        }

        //Table row 3
        TableRow tbRow2 = new TableRow(this);

        TextView tvParty = new TextView(this);
        tvParty.setText(R.string.party_title);
        tvParty.setTextSize(20);

        tbRow2.addView(tvParty);

        tableLayout.addView(tbRow2);

        //Table row 4
        TableRow tbRow3 = new TableRow(this);

        TextView tvNameParty = new TextView(this);
        tvNameParty.setText(R.string.party_name);
        tvNameParty.setTextSize(16);

        TextView tvTVote = new TextView(this);
        tvTVote.setText(R.string.vote);
        tvTVote.setTextSize(16);

        tbRow3.addView(tvNameParty);
        tbRow3.addView(tvTVote);

        tableLayout.addView(tbRow3);

        //Table row 5
        for(int i = 0; i < 4 ; i++ )
        {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            tvPartyName = new TextView(this);
            tvPartyVote = new TextView(this);

            tvPartyName.setText(candidateList.get((i*3)+3).getParty());
            tvPartyVote.setText("0");

            row.addView(tvPartyName);
            row.addView(tvPartyVote);

            tableLayout.addView(row);
        }


    }
}
