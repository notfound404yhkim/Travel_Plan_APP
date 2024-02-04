package com.example.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.PlaceAdapter;
import com.example.travelapp.adapter.SchedulePlaceSelectAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScheduleMapSelectActivity extends AppCompatActivity {



    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;
    int count = 0;

    String token;
    String region;
    TextView txtSelect;

    String selectRegion;




    String[] regionlist = {"서울","인천","대전","대구","광주","부산","제주"};

    // 리싸이클뷰는, 함께 선언(3종)

    RecyclerView recyclerView;
    SchedulePlaceSelectAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add_place);

        region = getIntent().getStringExtra("region");
        txtSelect = findViewById(R.id.txtSelect);



        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleMapSelectActivity.this));
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
                        addNetworkData(region);
                    }
                }
            }
        });


        //백 버튼 눌렀을때 일정 추가 페이지로 선택한 장소들을 보냄
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                selectRegion = txtSelect.getText().toString().trim();

                Intent intent = new Intent();
                intent.putExtra("selectRegion",selectRegion);
                setResult(100,intent);
                finish();}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getNetworkData(region);
    }


    private void getNetworkData(String region) {



        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleMapSelectActivity.this);

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", region);
        token = "Bearer " + token;


        Call<PlaceList> call = api.getPlacelist(token,region, 0,offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if(response.isSuccessful()){
                    //        //변수 초기화
                    offset = 0;
                    count = 0;

                    Log.i("AAA",response.toString());
                    PlaceList placeList = response.body();
                    count = placeList.count;

                    placeArrayList.clear();
                    placeArrayList.addAll( placeList.items );

                    // 어댑터 만들어서, 리사이클러뷰에 적용 //새로고침
                    adapter = new SchedulePlaceSelectAdapter(ScheduleMapSelectActivity.this, placeArrayList);
                    adapter.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Place data = (Place)v.getTag();
                            Log.i("AAA",data.placeName);
                            // 현재 텍스트 가져오기
                            String currentText = txtSelect.getText().toString();

                            // 쉼표로 시작하는지 확인하고 쉼표가 있다면 첫 번째 문자 제거
                            if (currentText.startsWith(",")) {
                                currentText = currentText.substring(1);
                            }

                            // 콤마로 분리된 값들을 배열로 저장
                            String[] values = currentText.split(",");

                            // 분리된 값들이 4개를 초과하면 더 이상 값을 추가하지 않음
                            if (values.length > 3) {
                                Toast.makeText(ScheduleMapSelectActivity.this,"장소는 최대 4군대 선택이 가능합니다.",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                txtSelect.setText(currentText + ","+data.placeName);}
                            }
                    });

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }else{

                }

            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
            }
        });
    }





    private void addNetworkData(String region) {


        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleMapSelectActivity.this);

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;

        offset = offset + count;
        Log.i("AAA",offset+"개");

        Call<PlaceList> call = api.getPlacelist(token,region, 0,offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if(response.isSuccessful()){

                    PlaceList placeList = response.body();

                    placeArrayList.clear();
                    placeArrayList.addAll( placeList.items );

                    count = placeList.count;

                    adapter.notifyDataSetChanged();

                }else{

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {

            }
        });

    }


}

