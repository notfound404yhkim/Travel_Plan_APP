package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.travelapp.adapter.HistoryAdapter;
import com.example.travelapp.api.HistoryApi;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.History;
import com.example.travelapp.model.HistoryList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HistoryActivity extends AppCompatActivity {

    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;

    String token;

    HistoryAdapter adapter;

    RecyclerView recyclerView;


    ArrayList<History> historyArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

        // 네트워크 요청 초기화
        addNetworkData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();


                if (lastPosition + 1 == totalCount) {
                    // 페이징 처리
                    addNetworkData();
                }
            }
        });

    }

    private void addNetworkData() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(HistoryActivity.this);
        HistoryApi api = retrofit.create(HistoryApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;

        // 네트워크 요청
        Call<HistoryList> call = api.getHistoryList(token, offset, limit);

        call.enqueue(new Callback<HistoryList>() {
            @Override
            public void onResponse(Call<HistoryList> call, Response<HistoryList> response) {
                if (response.isSuccessful()) {
                    HistoryList historyList = response.body();
                    if (historyList != null) {
                        // 새로 받은 데이터를 리스트에 추가
                        historyArrayList.addAll(historyList.items);

                        // 어댑터가 없으면 생성, 있으면 데이터 갱신
                        if (adapter == null) {
                            adapter = new HistoryAdapter(HistoryActivity.this, historyArrayList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        // 오프셋 증가
                        offset += limit;
                    }
                } else {
                    // 실패 처리
                }
            }

            @Override
            public void onFailure(Call<HistoryList> call, Throwable t) {
                // 네트워크 오류 처리
            }
        });
    }
}