package com.vik.android.quickflick;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("popular")
    Call<Result> getPopularMovies(@Query("page") int page,@Query("api_key") String apiKey);

    @GET("top_rated")
    Call<Result> getTopRatedMovies(@Query("page") int page,@Query("api_key") String apiKey);

    @GET("{id}")
    Call<MovieItem> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Call<TrailerResult> getMovieTrailers(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Call<ReviewResult> getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey);
}
