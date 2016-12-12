package com.vik.android.quickflick.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vik.android.quickflick.MainActivity;
import com.vik.android.quickflick.MovieDetailActivity;
import com.vik.android.quickflick.MovieDetailFragment;
import com.vik.android.quickflick.R;
import com.vik.android.quickflick.data.MovieContract;

public class RecyclerCursorAdapter extends RecyclerView.Adapter<RecyclerCursorAdapter.ViewHolder> {

    CursorAdapter mCursorAdapter;

    Context mContext;

    public RecyclerCursorAdapter(Context context, Cursor c) {

        mContext = context;

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.movie_card_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView posterImage = (ImageView) view.findViewById(R.id.movie_poster);
                String posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));
                Glide.with(mContext).load("http://image.tmdb.org/t/p/w342" + posterPath).into(posterImage);
            }
        };
    }

    public void changeCursor(Cursor cursor) {
        mCursorAdapter.changeCursor(cursor);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.movie_poster);
        }
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Passing the binding operation to cursor-adapter
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursorAdapter.getCursor().moveToPosition(holder.getAdapterPosition());
                int movieColumnIndex = mCursorAdapter.getCursor().getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                int movieId = mCursorAdapter.getCursor().getInt(movieColumnIndex);
                if(MainActivity.TWO_PANE){
                    Bundle args = new Bundle();
                    args.putInt("movie", movieId);

                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(args);

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, MainActivity.DETAILFRAGMENT_TAG)
                            .commit();
                }else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra("movie_key", movieId);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }
}
