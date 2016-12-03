package com.vik.android.quickflick;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewResult {

    @SerializedName("results")
    @Expose
    private ArrayList<Review> reviews = new ArrayList<>();
    @SerializedName("total_results")
    @Expose
    private int totalResults;


    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}
