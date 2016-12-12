package com.vik.android.quickflick.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vik.android.quickflick.R;
import com.vik.android.quickflick.pojo.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> mReviews = new ArrayList<>();

    public void setReviews(ArrayList<Review> reviews) {
        this.mReviews = reviews;

        notifyItemInserted(reviews.size() - 1);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_item, parent, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        Review review = mReviews.get(position);
        holder.reviewHeader.setText(review.getAuthor());

        String body[] = review.getContent().split("\r?\n|\r");
        holder.reviewBody.setText(body[0]);

    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView reviewHeader, reviewBody;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewHeader = (TextView) itemView.findViewById(R.id.review_header);
            reviewBody = (TextView) itemView.findViewById(R.id.review_body);
        }
    }
}
