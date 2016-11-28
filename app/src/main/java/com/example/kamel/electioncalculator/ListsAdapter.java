package com.example.kamel.electioncalculator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kamel on 27.11.2016.
 */

public class ListsAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater inflater;
    ArrayList<Candidate> objects;

    public ListsAdapter(Context ctx, ArrayList<Candidate> candidate) {
        this.ctx = ctx;
        this.objects = candidate;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null)
        {
            view = inflater.inflate(R.layout.list_item,parent,false);
        }

        Candidate person = getCandidate(position);

        ((TextView) view.findViewById(R.id.tvCandidateName)).setText(person.getName());
        ((TextView) view.findViewById(R.id.tvCandidateParty)).setText(person.getParty());

        CheckBox cbVote = (CheckBox)view.findViewById(R.id.cbVote);
        cbVote.setOnCheckedChangeListener(myCheckChangList);
        cbVote.setTag(position);
        cbVote.setChecked(person.isBox());

        return view;
    }

    Candidate getCandidate(int position)
    {
        return ((Candidate) getItem(position));
    }

    ArrayList<Candidate> getBox()
    {
        ArrayList<Candidate> box = new ArrayList<Candidate>();
        for(Candidate person: objects)
        {
            if(person.isBox()) {
                box.add(person);
            }
        }
        return box;
    }


    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            getCandidate((Integer) buttonView.getTag()).setBox(isChecked);
        }
    };
}
