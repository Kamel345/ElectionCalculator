package com.example.kamel.electioncalculator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private EditText name;
    private EditText lastName;
    private EditText idNumber;

    private static final String TAG_DISALLOWED = "disallowed";
    private static final String TAG_PERSON = "person";
    private static final String TAG_PESEL = "pesel";

    private ArrayList<String> peselList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peselList = new ArrayList<>();

        new GetDisallowed().execute();


    }

    public void Login(View v)
    {
        name = (EditText) findViewById(R.id.etName);
        lastName = (EditText) findViewById(R.id.etLastName);
        idNumber = (EditText) findViewById(R.id.etIdNumeber);

        if(name.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"You must set Name to vote",Toast.LENGTH_LONG).show();
        }

        else if(lastName.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"You must set Last Name to vote",Toast.LENGTH_LONG).show();
        }

        else if(idNumber.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"You must set PESEL to vote",Toast.LENGTH_LONG).show();
        }

        else if(!checkPESEL(idNumber.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Incorrect PESEL number",Toast.LENGTH_LONG).show();
        }

        else if(checkBlacklist(idNumber.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"PESEL number on BlackList",Toast.LENGTH_LONG).show();
        }

        else
        {
            Intent intent = new Intent(getBaseContext(), ElectionListActivity.class);
            startActivity(intent);
        }
    }

    public boolean checkBlacklist(String peselNumber)
    {
        for (int i = 0; i < peselList.size(); i++ )
        {
            Log.i("peselList", peselList.get(i));
            if(peselList.get(i).equals(peselNumber)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPESEL(String idNumber)
    {
        //Importance number in PESEL
        int [] importance = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

        //Chceck length PESEL
        if(idNumber.length() != 11)
            return false;

        //Checksum
        int total = 0;

        //Count correct PESEL by multiple number with importance
        for(short i = 0; i < 10; i++)
        {
            total += Integer.parseInt(idNumber.substring(i, i+1)) * importance[i];
        }

        //Last number in PESEL
        int lastNumber = Integer.parseInt(idNumber.substring(10,11));

        total %= 10;
        total = 10 - total;
        total %= 10;

        return (total == lastNumber);
    }

    private class GetDisallowed extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            String url = "http://webtask.future-processing.com:8069/blocked";

            String jsonStr = sh.makeServiceCall(url);

            Log.i("jsonStr", jsonStr);

            if(jsonStr != null)
            {
                try{
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONObject disallowed = jsonObject.getJSONObject(TAG_DISALLOWED);

                    JSONArray person = disallowed.getJSONArray(TAG_PERSON);

                    for(int i=0; i < person.length(); i++)
                    {
                        JSONObject tmp = person.getJSONObject(i);
                        String peselNumber = tmp.getString(TAG_PESEL);

                        Log.i("peselNumber:", peselNumber);

                        peselList.add(peselNumber);

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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }


}
