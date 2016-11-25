package com.vik.android.quickflick;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovies = new ArrayList<Movie>();
    private Context mContext;
    private View mView;

    public MovieAdapter(Context context){
        this.mContext = context;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.mMovies = movies;
        //update the adapter to reflect the new set of movies
        notifyItemInserted(movies.size()-1);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card_item, parent, false);
        MovieViewHolder holder = new MovieViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        String url = "http://image.tmdb.org/t/p/w342"+movie.getPosterPath();
        Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.posterImage);

        holder.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ABC", mMovies.get(holder.getAdapterPosition()).getOriginalTitle());
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("movie_object", mMovies.get(holder.getAdapterPosition()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        ImageView posterImage;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            posterImage = (ImageView) itemView.findViewById(R.id.movie_poster);
        }
    }
}
