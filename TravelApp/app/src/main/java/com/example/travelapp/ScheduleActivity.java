package com.example.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.ScheduleAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.ScheduleApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Schedule;
import com.example.travelapp.model.ScheduleList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScheduleActivity extends AppCompatActivity {



    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;
    int count = 0;

    String token;


    // 리싸이클뷰는, 함께 선언(3종)

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    ArrayList<Schedule> ScheduleArrayList = new ArrayList<>(); //스케줄 목록

    ImageView imgProfile,imgError;
    TextView txtName;

    FloatingActionButton btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        imgProfile = findViewById(R.id.imgProfile);
        imgError = findViewById(R.id.imgError);
        txtName = findViewById(R.id.txtName);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgError.setVisibility(View.GONE);
                Intent intent = new Intent(ScheduleActivity.this, ScheduleAddActivity.class);
                startActivity(intent);
            }
        });


        String name = getIntent().getStringExtra("name");
        String imgurl = getIntent().getStringExtra("imgUrl");

        txtName.setText(name);
        Log.i("AAA","전달받은 프사 주소:" + imgurl);
        if(imgurl == null)  //이미지가 있을때만 표시
        {
            imgProfile.setImageResource(R.drawable.person_icon);
        }
        else{
            Picasso.get().load(imgurl).into( imgProfile);
        }




        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount){
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if( limit == count){
                        // DB에 데이터가 더 존재할수 있으니, 데이터를 불러온다.
                        addNetworkData();
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        getNetworkData();
    }



    private void getNetworkData() {

        //변수 초기화
        offset = 0;
        count = 0;

        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleActivity.this);

        ScheduleApi api = retrofit.create(ScheduleApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<ScheduleList> call = api.getMySchedule(token);

        call.enqueue(new Callback<ScheduleList>() {
            @Override
            public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {


                if(response.isSuccessful()){

                    Log.i("AAA",response.toString());
                    ScheduleList scheduleList = response.body();
                    count = scheduleList.count;

                    ScheduleArrayList.clear();
                    ScheduleArrayList.addAll(scheduleList.items);
                    if(ScheduleArrayList.size() == 0)
                    {
                        imgError.setVisibility(View.VISIBLE);
                    }

                    // 어댑터 만들어서, 리사이클러뷰에 적용 //새로고침
                    adapter = new ScheduleAdapter(ScheduleActivity.this, ScheduleArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{

                }

            }

            @Override
            public void onFailure(Call<ScheduleList> call, Throwable t) {
            }
        });
    }


    private void addNetworkData() {


        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleActivity.this);

        ScheduleApi api = retrofit.create(ScheduleApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;


        Call<ScheduleList> call = api.getMySchedule(token);

        call.enqueue(new Callback<ScheduleList>() {
            @Override
            public void onResponse(Call<ScheduleList> call, Response<ScheduleList> response) {


                if(response.isSuccessful()){

                    ScheduleList scheduleList = response.body();

                    ScheduleArrayList.clear();
                    ScheduleArrayList.addAll( scheduleList.items );

                    count = scheduleList.count;

                    adapter.notifyDataSetChanged();

                }else{

                }
            }

            @Override
            public void onFailure(Call<ScheduleList> call, Throwable t) {

            }
        });

    }


}

