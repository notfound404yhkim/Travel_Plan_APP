package com.example.travelapp;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class PlaceInfoActivity extends AppCompatActivity  {

    private ScrollView scrollView;
    int id,option;
    int[] nextIndex;
    TextView txtTitle,txtRegion,txtContent,txtDate;
    ImageView imgPhoto;
    ArrayList<Place> placeArrayList = new ArrayList<>();
    LinearLayout linearLayout; // 레이아웃

    String Region;

    int[] arr;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeinfo);
        scrollView = findViewById(R.id.scrollView);
        txtTitle=findViewById(R.id.txtTitle);
        txtRegion=findViewById(R.id.txtRegion);
        txtContent=findViewById(R.id.txtContext);
        imgPhoto=findViewById(R.id.imgPhoto);
        txtDate=findViewById(R.id.txtDate);
        linearLayout=findViewById(R.id.LinearLayout);
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureListener());

        id = getIntent().getIntExtra("id",0);
        option = getIntent().getIntExtra("option",0);
        getInfo(id,option);


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // 스와이프 우측
                        if (id == nextIndex[0]) {
                            Toast.makeText(PlaceInfoActivity.this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Intent intent = new Intent(PlaceInfoActivity.this, PlaceInfoActivity.class);
                        intent.putExtra("id", nextIndex[0]);
                        intent.putExtra("option", option);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    } else {
                        // 스와이프 좌측
                        if (id == nextIndex[1]) {
                            Toast.makeText(PlaceInfoActivity.this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        Intent intent = new Intent(PlaceInfoActivity.this, PlaceInfoActivity.class);
                        intent.putExtra("id", nextIndex[1]);
                        intent.putExtra("option", option);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    //행사,축제 정보 출력.
    public void getInfo(int id,int option){
        Retrofit retrofit = NetworkClient.getRetrofitClient(PlaceInfoActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);
        Call<PlaceList> call = api.getPlaceInfo(id,option);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                Log.i("AAA", response.toString());
                if(response.isSuccessful()){
                    PlaceList placeList = response.body();
                    placeArrayList.addAll(placeList.items);
                    for (Place item : placeArrayList) {
                        txtTitle.setText(item.placeName);
                        txtRegion.setText(item.region);
                        Region = item.region;
                        txtContent.setText(item.content);
                        if (item.option==1){
                            txtDate.setText(item.strDate +" ~ " + item.endDate);}
                        Picasso.get().load(item.imgUrl).into(imgPhoto);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    getList();
                }
            }
            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                Log.i("AAA", "에러");
                dismissProgress();
            }
        });
    }
    // 네트워크 데이터 처리할때 사용할 다이얼로그
    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(PlaceInfoActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(PlaceInfoActivity.this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void dismissProgress(){
        dialog.dismiss();
    }

    public void getList()
    {
        Retrofit retrofit = NetworkClient.getRetrofitClient(PlaceInfoActivity.this);
        PlaceApi api = retrofit.create(PlaceApi.class);
        Call<PlaceList> call = api.getImg(Region,option,0,15);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {
                Log.i("AAA", response.toString());
                if(response.isSuccessful()){
                    PlaceList placeList = response.body();
                    placeArrayList.addAll(placeList.items);
                    arr = new int[placeArrayList.size()];
                    int i = 0;
                    for (Place item : placeArrayList) {
                        //0이 들어가는것을 방지
                        if (item.id !=0 ){
                            arr[i] = item.id;
                            Log.i("AAA",item.id +"출력id");
                        }
                        i=i+1;
                    }
                    nextIndex = findNearestValues(arr,id);
                    Log.i("AAA", "현재 나의 값 " + id);
                    Log.i("AAA","제일 인접한 값" + nextIndex[0]);
                    Log.i("AAA","제일 인접한 값" + nextIndex[1]);
                }
            }
            @Override
            public void onFailure(Call<PlaceList> call, Throwable t) {
                Log.i("AAA", "에러");
                dismissProgress();
            }
        });

    }


    public static int[] findNearestValues(int[] arr, int baseValue) {
        if (arr == null || arr.length == 0) {
            throw new IllegalArgumentException("배열이 비어 있습니다.");
        }

        int nearestGreaterValue = -1; // 초기값 설정
        int nearestSmallerValue = -1; // 초기값 설정
        int minGreaterDifference = Integer.MAX_VALUE; // 초기값 설정
        int minSmallerDifference = Integer.MAX_VALUE; // 초기값 설정

        for (int i = 0; i < arr.length; i++) {
            int difference = arr[i] - baseValue; // 현재 값과 기준 값의 차이를 구함
            if (difference > 0 && difference < minGreaterDifference) {
                minGreaterDifference = difference; // 최소 차이값 업데이트
                nearestGreaterValue = arr[i]; // 최소 차이값에 해당하는 값을 업데이트
            } else if (difference < 0 && -difference < minSmallerDifference) {
                minSmallerDifference = -difference; // 최소 차이값 업데이트
                nearestSmallerValue = arr[i]; // 최소 차이값에 해당하는 값을 업데이트
            }
        }

        // nearestSmallerValue와 nearestGreaterValue가 여전히 -1이라면 배열 안에서 baseValue보다 작은 또는 큰 값이 없다는 것이므로 baseValue를 반환
        if (nearestSmallerValue == -1) {
            nearestSmallerValue = baseValue;
        }
        if (nearestGreaterValue == -1) {
            nearestGreaterValue = baseValue;
        }

        // 만약 nearestSmallerValue가 0인 경우 baseValue를 반환하도록 함
        if (nearestSmallerValue == 0) {
            nearestSmallerValue = baseValue;
        }

        return new int[]{nearestGreaterValue, nearestSmallerValue};
    }
}