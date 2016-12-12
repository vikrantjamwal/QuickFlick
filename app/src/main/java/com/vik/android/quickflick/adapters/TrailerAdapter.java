package com.vik.android.quickflick.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vik.android.quickflick.R;
import com.vik.android.quickflick.pojo.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> mTrailers = new ArrayList<>();
    private Context mContext;

    public TrailerAdapter(Context context) {
        this.mContext = context;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.mTrailers = trailers;

        notifyItemInserted(trailers.size() - 1);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_item, parent, false);
        TrailerViewHolder holder = new TrailerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);
        String url = "https://img.youtube.com/vi/" + trailer.getKey() + "/mqdefault.jpg";
        Glide.with(mContext).load(url).into(holder.trailerImage);

        holder.trailerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoUrl = "https://www.youtube.com/watch?v=" + mTrailers.get(holder.getAdapterPosition()).getKey();
                Uri webPage = Uri.parse(videoUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        ImageView trailerImage;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailerImage = (ImageView) itemView.findViewById(R.id.trailer_image);
        }
    }
}
