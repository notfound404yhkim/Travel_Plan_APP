package com.example.travelapp.api;

import com.example.travelapp.model.Res;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookmarkApi {

    // 즐겨찾기 추가
    @POST("bookmark/{posting_id}")
    Call<Res> addBookmark(@Path("posting_id") int posting_id, @Header("Authorization") String token);

    // 즐겨찾기 삭제
    @DELETE("bookmark/{posting_id}")
    Call<Res> deleteBookmark(@Path("posting_id") int posting_id, @Header("Authorization") String token);

}
