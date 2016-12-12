package com.vik.android.quickflick;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vik.android.quickflick.adapters.MovieAdapter;
import com.vik.android.quickflick.network.ApiClient;
import com.vik.android.quickflick.network.ApiInterface;
import com.vik.android.quickflick.pojo.Movie;
import com.vik.android.quickflick.pojo.Result;
import com.vik.android.quickflick.utility.EndlessRecyclerViewScrollListener;
import com.vik.android.quickflick.utility.ItemOffsetDecoration;
import com.vik.android.quickflick.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopularMovieFragment extends Fragment {

    private static final String LOG_TAG = PopularMovieFragment.class.getSimpleName();

    private static final String MOVIES_KEY = "movies_key";
    private static final String PAGE_KEY = "page_key";

    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;

    int mPageNo = 1;

    private ArrayList<Movie> mMovies = new ArrayList<>();

    private EndlessRecyclerViewScrollListener scrollListener;

    ApiInterface apiService;

    public PopularMovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_popular_movie, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.popularMovieList);
        movieAdapter = new MovieAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), Utility.calculateNoOfColumns(getActivity()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(movieAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        if (savedInstanceState != null) {
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            movieAdapter.setMovies(mMovies);
            mPageNo = savedInstanceState.getInt(PAGE_KEY);
        } else {
            Call<Result> call = apiService.getPopularMovies(1, ApiClient.API_KEY);
            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    mMovies = response.body().getResults();
                    if (mMovies != null && !mMovies.isEmpty()) {
                        movieAdapter.setMovies(mMovies);
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), "Connection Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mPageNo++;
                loadNextDataFromApi(mPageNo);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        outState.putInt(PAGE_KEY, mPageNo);
    }

    public void loadNextDataFromApi(int offset) {
        Call<Result> call = apiService.getPopularMovies(offset, ApiClient.API_KEY);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                List<Movie> movies = response.body().getResults();
                mMovies.addAll(movies);
                if (mMovies != null && !mMovies.isEmpty()) {
                    movieAdapter.setMovies(mMovies);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "Connection Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
