package com.vik.android.quickflick;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private RecyclerView trailerRecyclerView, reviewRecyclerView;

    private TrailerAdapter trailerAdapter;

    private ReviewAdapter reviewAdapter;

    ApiInterface apiService;

    Movie mMovie = null;

    ImageView mMoviePoster;

    TextView mMovieTitle, mRating, mReleaseDate, mDuration, mGenre, mOverview, mReviewHeading;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null){
            mMovie = arguments.getParcelable("movie");
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        if(mMovie != null) {

            final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(mMovie.getOriginalTitle());

            ImageView imageView = (ImageView) rootView.findViewById(R.id.backdrop);
            Glide.with(getActivity()).load("http://image.tmdb.org/t/p/w780" + mMovie.getBackdropPath()).into(imageView);

            trailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.video_recycler_view);
            trailerAdapter = new TrailerAdapter(getActivity());
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            trailerRecyclerView.setLayoutManager(layoutManager);
            trailerRecyclerView.setAdapter(trailerAdapter);
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
            trailerRecyclerView.addItemDecoration(itemDecoration);

            reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.review_recycler_view);
            reviewAdapter = new ReviewAdapter();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            reviewRecyclerView.setLayoutManager(linearLayoutManager);
            reviewRecyclerView.setAdapter(reviewAdapter);
            reviewRecyclerView.addItemDecoration(itemDecoration);

            mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
            mMovieTitle = (TextView) rootView.findViewById(R.id.movie_detail_title);
            mRating = (TextView) rootView.findViewById(R.id.movie_detail_rating);
            mReleaseDate = (TextView) rootView.findViewById(R.id.movie_detail_release);
            mDuration = (TextView) rootView.findViewById(R.id.movie_detail_duration);
            mGenre = (TextView) rootView.findViewById(R.id.movie_detail_genre);
            mOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            mReviewHeading = (TextView) rootView.findViewById(R.id.review_text);
            mMovieTitle.setSelected(true);
            mReleaseDate.setSelected(true);
            mGenre.setSelected(true);

            apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<MovieItem> call = apiService.getMovieDetails(mMovie.getId(), ApiClient.API_KEY);
            call.enqueue(new Callback<MovieItem>() {
                @Override
                public void onResponse(Call<MovieItem> call, Response<MovieItem> response) {
                    MovieItem movieItem = response.body();
                    if (movieItem != null) {
                        generateLayout(movieItem);
                    }
                }

                @Override
                public void onFailure(Call<MovieItem> call, Throwable t) {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), "Error in connection. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        return rootView;
    }

    private void generateLayout(MovieItem movieItem) {

        String url = "http://image.tmdb.org/t/p/w342" + movieItem.getPosterPath();
        if (isAdded()) {
            Glide.with(getActivity()).load(url).into(mMoviePoster);
        }
        mMovieTitle.setText(movieItem.getOriginalTitle());
        mRating.setText(String.valueOf(movieItem.getVoteAverage()));
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(movieItem.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = new SimpleDateFormat("MMM d, yyyy").format(date);
        String releaseDate = formattedDate + " (" + movieItem.getStatus() + ")";
        mReleaseDate.setText(releaseDate);
        int time = movieItem.getRuntime();
        int hours = time / 60;
        int minutes = time % 60;
        String duration = hours + " hr " + minutes + " min";
        mDuration.setText(duration);
        if (!movieItem.getGenres().isEmpty() && movieItem.getGenres() != null) {
            StringBuilder genre = new StringBuilder(movieItem.getGenres().get(0).getName());
            for (int i = 1; i < movieItem.getGenres().size(); i++) {
                genre.append(", ").append(movieItem.getGenres().get(i).getName());
            }
            mGenre.setText(genre);
        }
        mOverview.setText(movieItem.getOverview());

        Call<TrailerResult> call = apiService.getMovieTrailers(mMovie.getId(), ApiClient.API_KEY);
        call.enqueue(new Callback<TrailerResult>() {
            @Override
            public void onResponse(Call<TrailerResult> call, Response<TrailerResult> response) {
                ArrayList<Trailer> trailers = response.body().getTrailers();
                if (trailers != null && !trailers.isEmpty()) {
                    trailerAdapter.setTrailers(trailers);
                }
            }

            @Override
            public void onFailure(Call<TrailerResult> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });

        Call<ReviewResult> reviewResultCall = apiService.getMovieReviews(mMovie.getId(), ApiClient.API_KEY);
        reviewResultCall.enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                ArrayList<Review> reviews = response.body().getReviews();
                if(reviews!=null && !reviews.isEmpty()){
                    reviewAdapter.setReviews(reviews);
                    mReviewHeading.setText("Reviews");
                }
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });
    }
}