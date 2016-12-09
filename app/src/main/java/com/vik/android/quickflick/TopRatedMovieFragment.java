package com.vik.android.quickflick;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class TopRatedMovieFragment extends Fragment {

    private static final String LOG_TAG = TopRatedMovieFragment.class.getSimpleName();

    private static final String MOVIES_KEY = "movies_key";
    private static final String PAGE_KEY = "page_key";

    private RecyclerView recyclerView;

    private MovieAdapter movieAdapter;

    int mPageNo = 1;

    private ArrayList<Movie> mMovies = new ArrayList<>();

    private EndlessRecyclerViewScrollListener scrollListener;

    ApiInterface apiService;

    public TopRatedMovieFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_rated_movie, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.topRatedMovieList);
        movieAdapter = new MovieAdapter(getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), Utility.calculateNoOfColumns(getActivity()));
//        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
//            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
//        }
//        else{
//            gridLayoutManager = new GridLayoutManager(getActivity(), 5);
//        }
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
            Call<Result> call = apiService.getTopRatedMovies(1, ApiClient.API_KEY);
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
                    Log.e(LOG_TAG, t.toString());
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
        Log.e("GAG", "" + offset);
        Call<Result> call = apiService.getTopRatedMovies(offset, ApiClient.API_KEY);
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
                Log.e(LOG_TAG, t.toString());
            }
        });
    }
}
