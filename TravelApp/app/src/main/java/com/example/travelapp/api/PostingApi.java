package com.example.travelapp.api;

import com.example.travelapp.model.Comment;
import com.example.travelapp.model.DetailPosting;
import com.example.travelapp.model.Posting;
import com.example.travelapp.model.PostingList;
import com.example.travelapp.model.PostingUpdate;
import com.example.travelapp.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostingApi {

    // 포스팅 작성
    @Multipart
    @POST("/posting")
    Call<Res> addPosting(@Header("Authorization") String token,
                         @Part MultipartBody.Part image,
                         @Part("title") RequestBody title,
                         @Part("content") RequestBody content);

    // 친구 포스팅 조회 API
    @GET("/posting")
    Call<PostingList> getFriendPosting(@Header("Authorization") String token,
                                       @Query("offset") int offset,
                                       @Query("limit") int limit);

    // 내 포스팅 조회 API
    @GET("/posting/me")
    Call<PostingList> getMyPosting(@Header("Authorization") String token,
                                   @Query("offset") int offset,
                                   @Query("limit") int limit);

    // 포스팅 상세보기 API
    @GET("/posting/{postId}")
    Call<DetailPosting> detailPosting(@Path("postId") int postId, @Header("Authorization") String token);

    // 포스팅 수정 API
    @PUT("/posting/{posting_id}")
    Call<Res> updatePosting(@Path("posting_id") int posting_id, @Header("Authorization") String token, @Body PostingUpdate postingUpdate);

    // 포스팅 삭제 API
    @DELETE("/posting/{posting_id}")
    Call<Res> deletePosting(@Path("posting_id") int posting_id, @Header("Authorization") String token);

    // 북마크한 포스팅 보기
    @GET("/mypage/bookmark")
    Call<PostingList> getBookmarkPosting(@Header("Authorization") String token,
                                       @Query("offset") int offset,
                                       @Query("limit") int limit);

}
