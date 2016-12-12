package com.vik.android.quickflick;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vik.android.quickflick.data.MovieContract.MovieEntry;
import com.vik.android.quickflick.utility.ItemOffsetDecoration;
import com.vik.android.quickflick.adapters.RecyclerCursorAdapter;
import com.vik.android.quickflick.utility.Utility;

public class FavouriteMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView recyclerView;

    RecyclerCursorAdapter myRecyclerAdapter;

    public FavouriteMovieFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourite_movie, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.favouriteMovieList);
        myRecyclerAdapter = new RecyclerCursorAdapter(getActivity(), null);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), Utility.calculateNoOfColumns(getActivity()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(myRecyclerAdapter);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        getLoaderManager().initLoader(0, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_TITLE,
                MovieEntry.COLUMN_MOVIE_ID,
                MovieEntry.COLUMN_MOVIE_POSTER
        };
        return new CursorLoader(getActivity().getApplicationContext(), MovieEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myRecyclerAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myRecyclerAdapter.changeCursor(null);
    }
}
