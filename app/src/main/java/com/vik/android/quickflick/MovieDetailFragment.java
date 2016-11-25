package com.vik.android.quickflick;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Movie movie = getActivity().getIntent().getExtras().getParcelable("movie_object");

        //Toast.makeText(getActivity(), movie.getOriginalTitle(), Toast.LENGTH_SHORT).show();

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(movie.getOriginalTitle());

        ImageView imageView = (ImageView) rootView.findViewById(R.id.backdrop);
        Glide.with(getActivity()).load("http://image.tmdb.org/t/p/w780"+movie.getBackdropPath()).centerCrop().into(imageView);

        return rootView;
    }

}
