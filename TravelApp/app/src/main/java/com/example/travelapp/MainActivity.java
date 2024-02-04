package com.example.travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.UserApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Res;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MainFragment mainFragment;
    MapFragment mapFragment;
    CommunityFragment communityFragment;
    ProfileFragment profileFragment;

    ImageView imgLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        mapFragment = new MapFragment();
        communityFragment = new CommunityFragment();
        profileFragment = new ProfileFragment();
        imgLogout = findViewById(R.id.imgLogout);
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


//        getSupportActionBar().setTitle("여행 시작과 끝");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.menu_main){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,mainFragment).commit();
                }
                if(item.getItemId()==R.id.menu_map){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,mapFragment).commit();
                }
                if(item.getItemId()==R.id.menu_community){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,communityFragment).commit();
                }
                if(item.getItemId()==R.id.menu_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,profileFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_main);
    }

    private long time= 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    //로그 아웃 기능
    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// 이 다이얼 로그의 외곽부분을 눌렀을때, 사라지지 않도록 하는 코드.
        builder.setCancelable(false);
        builder.setTitle("로그 아웃");
        builder.setMessage("정말 로그 아웃 하시겠습니까? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {


                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token","");
                token = "Bearer " + token;

                Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                Call<Res> call = api.LogOut(token);
                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if(response.isSuccessful()){

                            // 쉐어드프리퍼런스의 token 을 없애야 한다.
                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", "");
                            editor.apply();


                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        Log.i("AAA","통신 오류 ");
                    }
                });
            }
        });

        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        builder.show(); //다이얼로그 출력
    }
}



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

