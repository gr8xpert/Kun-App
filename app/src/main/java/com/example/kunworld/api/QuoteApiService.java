package com.example.kunworld.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {

    @GET("api/quotes.json")
    Call<QuoteResponse> getQuotes();
}
