package com.example.kamel.electioncalculator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ElectionListActivity extends AppCompatActivity {

    private String TAG = ElectionListActivity.class.getSimpleName();

    //JSON Node
    private static final String TAG_CANDIDATE_INFO = "candidate";
    private static final String TAG_CANDIDATE_INFOS = "candidates";
    private static final String TAG_CANDIDATE_NAME = "name";
    private static final String TAG_CANDIDATE_PARTY = "party";

    private static final String PESEL_TAG = "PESEL_ID";
    private static final String PESEL_ARRAY_TAG = "PESEL_ARRAY_ID";
    private static final String CANDIDATE_ARRAY_TAG = "CANDIDATE_ARRAY_ID";

    private ArrayList<Candidate> candidateList = new ArrayList<>();
    private ListsAdapter boxAdapter;

    private String peselNumber;
    private ArrayList<String> usedPeselNumber = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_list);

        new GetCandidates().execute();
        boxAdapter = new ListsAdapter(this,candidateList);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            peselNumber = extras.getString(PESEL_TAG);
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void Logout(View v)
    {
        Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void Vote(View v)
    {
        areYouSure().show();

        Log.i("Pesel number", peselNumber);
    }

    private class GetCandidates extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh  = new HttpHandler();

            //URL adress
            String url = "http://webtask.future-processing.com:8069/candidates/";

            String jsonStr = sh.makeServiceCall(url);

            if(jsonStr != null){
                try{
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONObject candidates = jsonObject.getJSONObject(TAG_CANDIDATE_INFOS);

                    //JSON Array node
                    JSONArray candidate = candidates.getJSONArray(TAG_CANDIDATE_INFO);

                    //looping all candidates
                    for (short i=0; i < candidate.length(); i++)
                    {
                        JSONObject tmp = candidate.getJSONObject(i);
                        String candidateName = tmp.getString(TAG_CANDIDATE_NAME);
                        String candidateParty = tmp.getString(TAG_CANDIDATE_PARTY);

                        //for single candidate
                        Candidate person = new Candidate(candidateName,candidateParty);

                       Log.i("Imie:", candidateName);

                        candidateList.add(person);
                    }


                }catch(final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ListView lvCandidate = (ListView) findViewById(R.id.lvCandidates);
            lvCandidate.setAdapter(boxAdapter);

            Log.i(TAG, "wykonuje");
        }
    }

    private Dialog areYouSure()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vote");
        builder.setMessage("Are you sure ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                for(Candidate person : boxAdapter.getBox())
                {
                    if(person.isBox())
                    {
                        person.vote();

                        Log.i("Who:", person.getName());
                        Log.i("Vote:", "" + person.getVote());
                    }
                }

                usedPeselNumber.add(peselNumber);


                Intent intent = new Intent(getBaseContext(), SummaryActivity.class);
                intent.putExtra(CANDIDATE_ARRAY_TAG, candidateList);
                intent.putStringArrayListExtra(PESEL_ARRAY_TAG, usedPeselNumber);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }
}
