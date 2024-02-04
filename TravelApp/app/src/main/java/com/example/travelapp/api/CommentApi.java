package com.example.travelapp.api;

import com.example.travelapp.model.Comment;
import com.example.travelapp.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentApi {

    // 댓글 작성 API
    @POST("/comment/{postId}")
    Call<Res> addComment(@Path("postId") int postId, @Header("Authorization") String token, @Body Comment comment);

    // 댓글 삭제 API
    @DELETE("/comment/{postId}")
    Call<Res> deleteComment(@Path("postId") int postId, @Query("commentId") int commentId, @Header("Authorization") String token);
}
