package com.example.kamel.electioncalculator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kamel on 27.11.2016.
 */

public class Candidate implements Parcelable {

    private String name;
    private String party;
    private int vote;
    private boolean box;


    public Candidate(String name, String party) {
        this.name = name;
        this.party = party;
    }

    public Candidate(Parcel input) {
        this.name = input.readString();
        this.party = input.readString();
        this.vote = input.readInt();
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(party);
        dest.writeInt(vote);

    }

    public static final Parcelable.Creator<Candidate> CREATOR = new Parcelable.Creator<Candidate>() {
        public Candidate createFromParcel(Parcel input) {
            return new Candidate(input);
        }

        public Candidate[] newArray(int size) {
            return new Candidate[size];

        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public boolean isBox() {
        return box;
    }

    public void setBox(boolean box) {
        this.box = box;
    }

    public void vote()
    {
        this.vote++;
    }

}
