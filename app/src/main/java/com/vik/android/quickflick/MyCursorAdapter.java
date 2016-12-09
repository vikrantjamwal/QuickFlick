package com.vik.android.quickflick;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vik.android.quickflick.data.MovieContract;

public class MyCursorAdapter extends CursorRecyclerViewAdapter<MyCursorAdapter.ViewHolder>{

    Context mContext;

    public MyCursorAdapter(Context context,Cursor cursor){
        super(context,cursor);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mPoster;
        public ViewHolder(View view) {
            super(view);
            mPoster = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
        String posterUrl = "http://image.tmdb.org/t/p/w342" + posterPath;
        Glide.with(mContext).load(posterUrl).into(viewHolder.mPoster);
    }
}
