package com.example.kunworld.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GroqApiService {

    @POST("openai/v1/chat/completions")
    Call<GroqResponse> chat(
        @Header("Authorization") String authToken,
        @Body GroqRequest request
    );
}
