package com.vik.android.quickflick;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.vik.android.quickflick.adapters.ReviewAdapter;
import com.vik.android.quickflick.adapters.TrailerAdapter;
import com.vik.android.quickflick.data.MovieContract.MovieEntry;
import com.vik.android.quickflick.network.ApiClient;
import com.vik.android.quickflick.network.ApiInterface;
import com.vik.android.quickflick.pojo.MovieItem;
import com.vik.android.quickflick.pojo.Review;
import com.vik.android.quickflick.pojo.ReviewResult;
import com.vik.android.quickflick.pojo.Trailer;
import com.vik.android.quickflick.pojo.TrailerResult;
import com.vik.android.quickflick.utility.ItemOffsetDecoration;

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

    MovieItem mMovieItem = null;

    int mMovie_Id = -1;

    ImageView mMoviePoster, mBackdropImage, ratingStar;

    TextView mMovieTitle, mRating, mReleaseDate, mDuration, mGenre, mOverview, mReviewHeading, mOverviewHeading, mTrailerHeading;

    CollapsingToolbarLayout collapsingToolbar;

    FloatingActionButton fab;

    boolean isPresent = false;

    long primaryKey = -1;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int rowDeleted = 0;

            if (isPresent && primaryKey != -1) {
                Uri uri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, primaryKey);
                rowDeleted = getActivity().getContentResolver().delete(uri, null, null);
                if (rowDeleted != 0) {
                    Toast.makeText(getActivity(), "Movie removed from Favourites", Toast.LENGTH_SHORT).show();
                    isPresent = false;
                    fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            } else {
                String movieName = mMovieItem.getOriginalTitle();
                int movieId = mMovieItem.getId();
                String moviePosterPath = mMovieItem.getPosterPath();

                ContentValues values = new ContentValues();
                values.put(MovieEntry.COLUMN_MOVIE_TITLE, movieName);
                values.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
                values.put(MovieEntry.COLUMN_MOVIE_POSTER, moviePosterPath);

                Uri rowId = getActivity().getContentResolver().insert(MovieEntry.CONTENT_URI, values);
                if (ContentUris.parseId(rowId) != -1) {
                    Toast.makeText(getActivity(), "Movie added to Favourites", Toast.LENGTH_SHORT).show();
                    isPresent = true;
                    primaryKey = ContentUris.parseId(rowId);
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie_Id = arguments.getInt("movie");
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);

        if (mMovie_Id != -1) {

            final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(" ");

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

            mBackdropImage = (ImageView) rootView.findViewById(R.id.backdrop);
            mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
            mMovieTitle = (TextView) rootView.findViewById(R.id.movie_detail_title);
            ratingStar = (ImageView) rootView.findViewById(R.id.rating_image);
            mRating = (TextView) rootView.findViewById(R.id.movie_detail_rating);
            mReleaseDate = (TextView) rootView.findViewById(R.id.movie_detail_release);
            mDuration = (TextView) rootView.findViewById(R.id.movie_detail_duration);
            mGenre = (TextView) rootView.findViewById(R.id.movie_detail_genre);
            mOverview = (TextView) rootView.findViewById(R.id.movie_detail_overview);
            mReviewHeading = (TextView) rootView.findViewById(R.id.review_text);
            mOverviewHeading = (TextView) rootView.findViewById(R.id.overview_title);
            mTrailerHeading = (TextView) rootView.findViewById(R.id.trailer_title);
            mMovieTitle.setSelected(true);
            mReleaseDate.setSelected(true);
            mGenre.setSelected(true);

            apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<MovieItem> call = apiService.getMovieDetails(mMovie_Id, ApiClient.API_KEY);
            call.enqueue(new Callback<MovieItem>() {
                @Override
                public void onResponse(Call<MovieItem> call, Response<MovieItem> response) {
                    mMovieItem = response.body();
                    if (mMovieItem != null) {
                        isPresentInDB(mMovieItem);
                        generateLayout(mMovieItem);
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

    private void isPresentInDB(MovieItem movieItem) {
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_ID,
        };
        Cursor cursor = null;
        if(isAdded()) {
            cursor = getActivity().getContentResolver().query(MovieEntry.CONTENT_URI, projection, null, null, null);
        }
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
                    if (id == movieItem.getId()) {
                        primaryKey = cursor.getInt(cursor.getColumnIndex(MovieEntry._ID));
                        isPresent = true;
                        return;
                    }
                }
            }
        } finally {
            if(cursor!=null)
                cursor.close();
        }
    }

    private void generateLayout(MovieItem movieItem) {
        mOverviewHeading.setVisibility(View.VISIBLE);
        collapsingToolbar.setTitle(movieItem.getOriginalTitle());
        if (isAdded()) {
            Glide.with(getActivity()).load("http://image.tmdb.org/t/p/w780" + movieItem.getBackdropPath()).into(mBackdropImage);
            Glide.with(getActivity()).load("http://image.tmdb.org/t/p/w342" + movieItem.getPosterPath()).into(mMoviePoster);
        }
        if (isPresent) {
            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        fab.setVisibility(View.VISIBLE);
        mMovieTitle.setText(movieItem.getOriginalTitle());
        ratingStar.setVisibility(View.VISIBLE);
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

        Call<TrailerResult> call = apiService.getMovieTrailers(mMovie_Id, ApiClient.API_KEY);
        call.enqueue(new Callback<TrailerResult>() {
            @Override
            public void onResponse(Call<TrailerResult> call, Response<TrailerResult> response) {
                ArrayList<Trailer> trailers = response.body().getTrailers();
                if (trailers != null && !trailers.isEmpty()) {
                    mTrailerHeading.setVisibility(View.VISIBLE);
                    trailerAdapter.setTrailers(trailers);
                }
            }

            @Override
            public void onFailure(Call<TrailerResult> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });

        Call<ReviewResult> reviewResultCall = apiService.getMovieReviews(mMovie_Id, ApiClient.API_KEY);
        reviewResultCall.enqueue(new Callback<ReviewResult>() {
            @Override
            public void onResponse(Call<ReviewResult> call, Response<ReviewResult> response) {
                ArrayList<Review> reviews = response.body().getReviews();
                if (reviews != null && !reviews.isEmpty()) {
                    reviewAdapter.setReviews(reviews);
                    mReviewHeading.setText(R.string.review);
                }
            }

            @Override
            public void onFailure(Call<ReviewResult> call, Throwable t) {
                Log.e(LOG_TAG, t.toString());
            }
        });
    }
}