package com.example.kamel.electioncalculator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    protected EditText name;
    protected EditText lastName;
    protected EditText idNumber;

    private static final String TAG_DISALLOWED = "disallowed";
    private static final String TAG_PERSON = "person";
    private static final String TAG_PESEL = "pesel";
    private static final String TAG_PUB_DATE = "publicationDate";

    private static final String PESEL = "PESEL_ID";
    private static final String PESEL_ARRAY_TAG = "PESEL_ARRAY_ID";

    private ArrayList<String> peselList;
    private ArrayList<String> usedPeselNumber;

    private String pubDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        peselList = new ArrayList<>();

        new GetDisallowed().execute();

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usedPeselNumber = extras.getStringArrayList(PESEL_ARRAY_TAG);
        }

    }

    public void Login(View v)
    {
        name = (EditText) findViewById(R.id.etName);
        lastName = (EditText) findViewById(R.id.etLastName);
        idNumber = (EditText) findViewById(R.id.etIdNumeber);

        if(name.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"You must set First Name to vote",Toast.LENGTH_LONG).show();
        }

        else if(lastName.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"You must set Last Name to vote",Toast.LENGTH_LONG).show();
        }

        else if(idNumber.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"You must set PESEL to vote",Toast.LENGTH_LONG).show();
        }

        else if(!checkFormatPesel(idNumber.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Incorrect PESEL number",Toast.LENGTH_LONG).show();
        }

        else if(checkBlackList(idNumber.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"PESEL number on BlackList",Toast.LENGTH_LONG).show();
        }

        else if(!(checkAge(idNumber.getText().toString())))
        {
            Toast.makeText(getApplicationContext(),"You are too yong to participate in the vote",Toast.LENGTH_LONG).show();
        }

        else if(checkUsedPesel(idNumber.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"This number PESEL has been used",Toast.LENGTH_LONG).show();
        }

        else
        {

            Intent intent = new Intent(getBaseContext(), ElectionListActivity.class);
            intent.putExtra(PESEL, idNumber.getText().toString());
            startActivity(intent);
        }
    }

    public boolean checkBlackList(String peselNumber)
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

    public static boolean checkFormatPesel(String peselNumber)
    {
        //Importance number in PESEL
        int [] importance = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

        //Chceck length PESEL
        if(peselNumber.length() != 11)
            return false;

        //Checksum
        int total = 0;

        //Count correct PESEL by multiple number with importance
        for(short i = 0; i < 10; i++)
        {
            total += Integer.parseInt(peselNumber.substring(i, i+1)) * importance[i];
        }

        //Last number in PESEL
        int lastNumber = Integer.parseInt(peselNumber.substring(10,11));

        total %= 10;
        total = 10 - total;
        total %= 10;

        return (total == lastNumber);
    }

    public boolean checkAge(String peselNumber)
    {
        int currentYear = Integer.parseInt(pubDate.substring(0,4));
        int currentMonth = Integer.parseInt(pubDate.substring(5,7));
        int currentDay = Integer.parseInt(pubDate.substring(8,10));

        int peselYear = Integer.parseInt(peselNumber.substring(0,2));
        int peselMonth = Integer.parseInt(peselNumber.substring(2,4));
        int peselDay = Integer.parseInt(peselNumber.substring(4,6));

        if(peselMonth <= 12)
        {
            peselYear += 1900;
        }
        else
        {
            peselYear += 2000;
        }

        if((currentYear - peselYear) > 18)
        {
            return true;
        }
        else if((currentYear - peselYear) == 18)
        {
            int temp = currentMonth - peselMonth;

            if(temp > 0)
            {
                return true;
            }
            else if(temp == 0)
            {
                int temp1 = currentDay - peselDay;

                if(temp1 >= 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if(temp < 0)
            {
                return false;
            }
        }
        else if((currentYear - peselYear) < 18)
        {
            return false;
        }

        Log.i("rok", Integer.toString(currentYear));
        Log.i("miesiac", Integer.toString(currentMonth));
        Log.i("dzien", Integer.toString(currentDay));

        Log.i("prok", Integer.toString(peselYear));
        Log.i("pmiesiac", Integer.toString(peselMonth));
        Log.i("pdzien", Integer.toString(peselDay));

        return true;
    }

    public boolean checkUsedPesel(String peselNumber)
    {
        if(usedPeselNumber != null) {
            for (int i = 0; i < usedPeselNumber.size(); i++)
            {
                if (usedPeselNumber.get(i).equals(peselNumber))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }

        return false;
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

                    pubDate = disallowed.getString(TAG_PUB_DATE);

                    Log.i(TAG, pubDate);

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
