package com.example.travelapp.api;

import com.example.travelapp.model.Res;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikeApi {

    // 좋아요 추가
    @POST("like/{posting_id}")
    Call<Res> addLike(@Path("posting_id") int posting_id, @Header("Authorization") String token);

    // 좋아요 삭제
    @DELETE("like/{posting_id}")
    Call<Res> deleteLike(@Path("posting_id") int posting_id, @Header("Authorization") String token);

}
