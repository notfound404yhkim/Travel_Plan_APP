package com.example.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.SchedulePlaceSelectAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScheduleMapSelectActivity extends AppCompatActivity implements SchedulePlaceSelectAdapter.CardClickListener {


    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;
    int count = 0;

    String token;
    String region;
    TextView txtSelect;

    String selectRegion;
    String selectRegionId;
    Button btnSelect;
    Place data;

    // 리싸이클뷰는, 함께 선언(3종)

    RecyclerView recyclerView;
    SchedulePlaceSelectAdapter adapter;
    ArrayList<Place> placeArrayList = new ArrayList<>();
    private List<Integer> clickedPositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add_place);

        region = getIntent().getStringExtra("region");
        txtSelect = findViewById(R.id.txtSelect);
        btnSelect = findViewById(R.id.btnSelect);


        //선택완료시
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceInfoSend();
            }
        });
        //리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ScheduleMapSelectActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if (lastPosition + 1 == totalCount) {
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if (limit == count) {
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
                PlaceInfoSend();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getNetworkData(region);
    }

    public void PlaceInfoSend() {

        Intent intent = new Intent();
        intent.putExtra("selectRegion", selectRegion);
        intent.putExtra("selectRegionId", selectRegionId);
        setResult(100, intent);
        finish();
    }


    private void getNetworkData(String region) {


        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleMapSelectActivity.this);

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", region);
        token = "Bearer " + token;


        Call<PlaceList> call = api.getPlacelist(token, region, 0, offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if (response.isSuccessful()) {
                    //        //변수 초기화
                    offset = 0;
                    count = 0;

                    Log.i("AAA", response.toString());
                    PlaceList placeList = response.body();
                    count = placeList.count;

                    placeArrayList.clear();
                    placeArrayList.addAll(placeList.items);
                    // 어댑터생성시 클릭 리스너 정보도 생성
                    adapter = new SchedulePlaceSelectAdapter(placeArrayList, (SchedulePlaceSelectAdapter.CardClickListener) ScheduleMapSelectActivity.this, ScheduleMapSelectActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {

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
        Log.i("AAA", offset + "개");

        Call<PlaceList> call = api.getPlacelist(token, region, 0, offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if (response.isSuccessful()) {

                    PlaceList placeList = response.body();

                    placeArrayList.clear();
                    placeArrayList.addAll(placeList.items);

                    count = placeList.count;

                    adapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {

            }
        });

    }


    @Override
    public void onCardClick(int position) {
        data = placeArrayList.get(position);
        Log.i("AAA", data.placeName);
        if (clickedPositions.contains(position)) {
            Log.i("AAA",position+"아디");
            String check = data.placeName;
            selectRegion = selectRegion.replace(check + ",", "").replace("," + check, "").replace(check, "").replaceAll(",{2,}", ",").trim();
            txtSelect.setText(selectRegion);
            clickedPositions.remove(Integer.valueOf(position));
            return;
        }
        // 현재 position 값을 배열에 추가
        clickedPositions.add(position);

        // 현재 텍스트 가져오기
        String currentText = txtSelect.getText().toString();

        // 쉼표로 시작하는지 확인하고 쉼표가 있다면 첫 번째 문자 제거
        if (currentText.startsWith(",")) {
            currentText = currentText.substring(1);
        }
        if (selectRegionId != null) {
            if (selectRegionId.startsWith(",")) {
                selectRegionId = selectRegionId.substring(1);
            }
        }

        txtSelect.setText(currentText + "," + data.placeName);

        selectRegionId = (selectRegionId + "," + data.id);
        // 문자열을 배열로 분리
        String[] array = selectRegionId.split(",");

        // 배열에서 null 제거
        List<String> resultList = new ArrayList<>();
        for (String name : array) {
            if (name != null && !name.trim().equalsIgnoreCase("null")) {
                resultList.add(name);
            }
        }
        // 결과를 다시 문자열로 변환
        selectRegionId = String.join(",", resultList);
        Log.i("AAA", selectRegionId);
        selectRegion = txtSelect.getText().toString().trim();

        if (selectRegionId.contains(",") || selectRegion.contains(",") ) {
            // 중복된 값을 제거하기 위해 Set을 사용
            Set<String> regionIdSet = new HashSet<>(Arrays.asList(selectRegionId.split(",")));
            Set<String> regionSet = new HashSet<>(Arrays.asList(selectRegion.split(",")));
            selectRegionId = TextUtils.join(",", regionIdSet);
            selectRegion = TextUtils.join(",", regionSet);
        }

        txtSelect.setText(selectRegion);

    }
}


