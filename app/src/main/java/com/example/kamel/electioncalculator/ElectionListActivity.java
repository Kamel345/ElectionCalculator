package com.example.kamel.electioncalculator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ElectionListActivity extends AppCompatActivity {

    private String TAG = ElectionListActivity.class.getSimpleName();

    //JSON Node
    private static final String TAG_CANDIDATE_INFO = "candidate";
    private static final String TAG_CANDIDATE_INFOS = "candidates";
    private static final String TAG_CANDIDATE_NAME = "name";
    private static final String TAG_CANDIDATE_PARTY = "party";
    private static final String TAG_PUB_DATE = "publicationDate";

    //private ArrayList<HashMap<String,String>> candidateList;
    ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
    ListsAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_list);


        new GetCandidates().execute();
        boxAdapter = new ListsAdapter(this,candidateList);

        ListView lvCandidate = (ListView) findViewById(R.id.lvCandidates);
        lvCandidate.setAdapter(boxAdapter);

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
                    //JSONObject pubDate = candidates.getJSONObject(TAG_PUB_DATE);
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



            /* ListAdapter adapter = new SimpleAdapter(ElectionListActivity.this, candidateList, R.layout.list_item,
                    new String[] { "candidateName", "candidateParty" },
                    new int[] { R.id.tvCandidateName, R.id.tvCandidateParty });

            lvCandidate.setAdapter(adapter);*/

            Log.i(TAG, "wykonuje");
        }
    }
}
