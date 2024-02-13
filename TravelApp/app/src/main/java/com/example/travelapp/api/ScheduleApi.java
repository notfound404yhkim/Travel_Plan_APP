package com.example.travelapp.api;

import com.example.travelapp.model.PlaceList;
import com.example.travelapp.model.PostingList;
import com.example.travelapp.model.Res;
import com.example.travelapp.model.Schedule;
import com.example.travelapp.model.ScheduleList;
import com.example.travelapp.model.ScheduleRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ScheduleApi {

    // 스케줄 생성 API
    //쿼리는 파라미터
    //Body값은 class

    @POST("mypage/mySchedule")
    Call<Res> addSchedule(@Header("Authorization") String token, @Body Schedule schedule);



    // 내 스케줄 조회 API

    @GET("mypage/mySchedule")
    Call<ScheduleList> getMySchedule(@Header("Authorization") String token);


    //내 스케줄 상세 조회
    @GET("mypage/mySchedule/{scheduleId}")
    Call<ScheduleRes> getMyScheduleInfo(@Header("Authorization") String token, @Path("scheduleId") int scheduleId);

    //내 스케줄 삭제  API
    @DELETE("mypage/mySchedule/{scheduleId}")
    Call<Res> deleteSchedule(@Path("scheduleId") int scheduleId, @Header("Authorization") String token);


}
