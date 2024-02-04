package com.example.travelapp.api;

import com.example.travelapp.model.History;
import com.example.travelapp.model.HistoryList;
import com.example.travelapp.model.Res;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HistoryApi {


    // GPT 대화 내용 생성
    @POST("/history")
    Call<Res> addHistory(@Header("Authorization") String token,@Body History history);

    // 내 대화기록 조회 API
    @GET("/historylist")
    Call<HistoryList> getHistoryList(@Header("Authorization") String token,
                                     @Query("offset") int offset,
                                     @Query("limit") int limit);

    //대화기록 자세히 보기
    @GET("/history/{historyId}")
    Call<HistoryList> getHistoryInfo(@Header("Authorization") String token, @Path("historyId") int historyId);


    //대화기록 삭제

    //내 스케줄 삭제  API
    @DELETE("/history/{historyId}")
    Call<Res> deleteHistory(@Path("historyId") int historyId, @Header("Authorization") String token);

}
