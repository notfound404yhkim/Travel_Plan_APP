package com.example.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class HistoryInfoActivity extends AppCompatActivity {

    TextView txtTitle, txtInfo;
    int id;
    ArrayList<History> historyArrayList = new ArrayList<>();
    String token;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_info);

        txtTitle = findViewById(R.id.editTitle);
        txtInfo = findViewById(R.id.txtInfo);
        id = getIntent().getIntExtra("id", 0);
        getInfo(id);
    }


    private void getInfo(int id) {
        showProgress();
        Retrofit retrofit = NetworkClient.getRetrofitClient(HistoryInfoActivity.this);
        HistoryApi api = retrofit.create(HistoryApi.class);
        // 토큰 가져온다.
        SharedPreferences sp = this.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;
        Call<HistoryList> call = api.getHistoryInfo(token,id);
        call.enqueue(new Callback<HistoryList>() {
            @Override
            public void onResponse(Call<HistoryList> call, Response<HistoryList> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    HistoryList historyList = response.body();
                    historyArrayList.addAll(historyList.items);
                    Log.i("AAA", "Size: " + historyArrayList.size());
                    Log.i("AAA",response.toString());
                    StringBuilder contentBuilder = new StringBuilder();

                    for (History item : historyArrayList) {
                        if (item.firstDay != null && !item.firstDay.isEmpty()) {
                            contentBuilder.append(item.firstDay).append("\n\n");

                        }
                        if (item.secondDay != null && !item.secondDay.isEmpty()) {
                            contentBuilder.append(item.secondDay).append("\n\n");
                        }
                        if (item.thirdDay != null && !item.thirdDay.isEmpty()) {
                            contentBuilder.append(item.thirdDay).append("\n\n");
                        }
                        if (item.fourthDay != null && !item.fourthDay.isEmpty()) {
                            contentBuilder.append(item.fourthDay).append("\n\n");
                        }
                        if (item.fifthDay != null && !item.fifthDay.isEmpty()) {
                            contentBuilder.append(item.fifthDay).append("\n\n");
                        }
                    }
                    txtInfo.setText(contentBuilder.toString());
                    }
                }


            @Override
            public void onFailure(Call<HistoryList> call, Throwable t) {
                dismissProgress();
                Log.i("AAA", "에러");
            }
        });
    }

    private void showProgress() {
        dialog = new Dialog(HistoryInfoActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(HistoryInfoActivity.this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}