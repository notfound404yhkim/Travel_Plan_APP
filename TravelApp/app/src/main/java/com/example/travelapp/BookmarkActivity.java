package com.example.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.MybookmarkAdapter;
import com.example.travelapp.adapter.MypostingAdapter;
import com.example.travelapp.adapter.PlaceAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;
import com.example.travelapp.model.Posting;
import com.example.travelapp.model.PostingList;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookmarkActivity extends AppCompatActivity {

    int offset = 0;
    int limit = 15;
    int count = 0;
    MybookmarkAdapter adapter;

    RecyclerView recyclerView;
    ArrayList<Posting> postingArrayList = new ArrayList<>();

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookmarkActivity.this));

    }

    @Override
    protected void onResume() {
        getNetworkData();
        super.onResume();
    }


    private void getNetworkData() {

        Retrofit retrofit = NetworkClient.getRetrofitClient(BookmarkActivity.this);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        PostingApi api = retrofit.create(PostingApi.class);

        Call<PostingList> call = api.getBookmarkPosting("Bearer " + token, offset, limit);

        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);


                if (response.isSuccessful()){
                    PostingList postingList = response.body();
                    Log.i("AAA2",response.toString());

                    postingArrayList.clear();

                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    for (Posting item : postingArrayList) {
                       Log.i("AAA2",item.postingId+"아디");
                        Log.i("AAA2",item.title);
                    }

                    adapter = new MybookmarkAdapter(BookmarkActivity.this, postingArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {


            }
        });

    }
}

