package com.example.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.DetailPosting;
import com.example.travelapp.model.PostingUpdate;
import com.example.travelapp.model.Res;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommunityDetailUpdateActivity extends AppCompatActivity {

    EditText editTitle;
    TextView txtName;
    EditText editContent;
    ImageView imgPhoto;
    TextView txtTag;
    TextView txtDate;
    Button btnUpdate;

    // 받아온 데이터
    int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_detail_update);

        editTitle = findViewById(R.id.editTitle);
        txtName = findViewById(R.id.txtName);
        editContent = findViewById(R.id.editContent);
        imgPhoto = findViewById(R.id.imgPhoto);
        txtTag = findViewById(R.id.txtTag);
        txtDate = findViewById(R.id.txtDate);
        btnUpdate = findViewById(R.id.btnUpdate);

        postId = getIntent().getIntExtra("postId", -1);

        getNetworkData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updateTitle = editTitle.getText().toString().trim();
                String updateContent = editContent.getText().toString().trim();

                if (updateTitle.isEmpty() || updateContent.isEmpty()){
                    Snackbar.make(btnUpdate, "빈 칸을 채워주세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Retrofit retrofit = NetworkClient.getRetrofitClient(CommunityDetailUpdateActivity.this);

                PostingApi api = retrofit.create(PostingApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");

                PostingUpdate postingUpdate = new PostingUpdate(updateTitle, updateContent);
                Call<Res> call = api.updatePosting(postId, "Bearer " + token, postingUpdate);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(CommunityDetailUpdateActivity.this, "글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        Snackbar.make(btnUpdate, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });

    }

    private void getNetworkData() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(CommunityDetailUpdateActivity.this);

        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<DetailPosting> call = api.detailPosting(postId, "Bearer " + token);

        call.enqueue(new Callback<DetailPosting>() {
            @Override
            public void onResponse(Call<DetailPosting> call, Response<DetailPosting> response) {
                if (response.isSuccessful()){
                    DetailPosting detailPosting = response.body();

                    editTitle.setText(detailPosting.items.title);

                    txtName.setText(detailPosting.items.name);
                    Glide.with(CommunityDetailUpdateActivity.this).load(detailPosting.items.imgUrl).into(imgPhoto);
                    editContent.setText(detailPosting.items.content);

                    StringBuilder tags = new StringBuilder();
                    for (int i = 0; i < detailPosting.tag.size(); i++) {
                        tags.append(detailPosting.tag.get(i));
                        if (i < detailPosting.tag.size() - 1) {
                            tags.append(", ");
                        }
                    }
                    String result = tags.toString();
                    txtTag.setText(result);

                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
                    sf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    df.setTimeZone(TimeZone.getDefault());

                    try {
                        Date date = sf.parse(detailPosting.items.createdAt);
                        String localtime = df.format(date);
                        txtDate.setText(localtime);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<DetailPosting> call, Throwable t) {
                Snackbar.make(btnUpdate, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                return;
            }
        });
    }

}