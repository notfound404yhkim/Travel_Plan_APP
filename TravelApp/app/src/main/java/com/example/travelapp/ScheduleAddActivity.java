package com.example.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.PlaceAdapter;
import com.example.travelapp.api.HistoryApi;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.api.ScheduleApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.History;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;
import com.example.travelapp.model.Res;
import com.example.travelapp.model.Schedule;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScheduleAddActivity extends AppCompatActivity {

    String[] region = {"서울", "인천", "대전", "대구", "광주", "부산", "제주"};
    ListView listRegion; //지역 리스트 뷰
    TextView txtRegion, txtDate, txtPlace;
    EditText editContent;
    RelativeLayout RegionLayout; //지역 선택부분 레이아웃
    RelativeLayout DateLayout, PlaceLayout;
    LinearLayout bottomLayout;
    String dateString1 = null;
    String dateString2 = null;

    Button btnSave;
    String token;

    String selectRegionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);

        listRegion = findViewById(R.id.listRegion);
        txtRegion = findViewById(R.id.txtRegion);
        txtDate = findViewById(R.id.txtDate);
        RegionLayout = findViewById(R.id.RegionLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        DateLayout = findViewById(R.id.DateLayout);
        PlaceLayout = findViewById(R.id.PlaceLayout);
        txtPlace = findViewById(R.id.txtPlace);
        btnSave = findViewById(R.id.btnSave);
        editContent = findViewById(R.id.editContent);

        //지역 선택 부분    프레그먼트이므로 getActivity()

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScheduleAddActivity.this,
                android.R.layout.simple_list_item_1, region);
        listRegion.setAdapter(adapter);


        RegionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRegion.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.GONE);
            }
        });
        listRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                txtRegion.setText(region[arg2]); //선택한지역값은 arg2에있음.
                listRegion.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.VISIBLE);
            }
        });


        //날짜 선택부분 (캘린더)
        DateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("Date Picker");
                builder.setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()));
                MaterialDatePicker materialDatePicker = builder.build();
                materialDatePicker.show(ScheduleAddActivity.this.getSupportFragmentManager(), "DATE_PICKER");
                //확인버튼
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date1 = new Date();
                        Date date2 = new Date();
                        date1.setTime(selection.first);
                        date2.setTime(selection.second);
                        dateString1 = simpleDateFormat.format(date1);
                        dateString2 = simpleDateFormat.format(date2);
                        txtDate.setText(dateString1 + " ~ " + dateString2);
                    }
                });
            }
        });

        //장소 선택

        PlaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleAddActivity.this, ScheduleMapSelectActivity.class);

                String region = txtRegion.getText().toString().trim();
                //지역이 선택되어야지 장소 선택가능
                if (region.isEmpty()) {
                    Toast.makeText(ScheduleAddActivity.this, "여행을 떠날 지역을 선택하세요 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("region", region);
                launcher.launch(intent);
            }
        });

        //일정 저장 이벤트

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String region = txtRegion.getText().toString().trim();
                String date = txtDate.getText().toString().trim();
                String place = txtPlace.getText().toString().trim();
                String content = editContent.getText().toString().trim();
                if (region.isEmpty() || date.isEmpty() || place.isEmpty() || content.isEmpty()) {
                    Toast.makeText(ScheduleAddActivity.this, "항목을 모두 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 쉼표로 문자열을 나누어 배열로 저장
                String[] places = selectRegionId.split(",");
                String[] placesArray = new String[places.length];  //places의 크기 만큼 배열

                // 나머지 빈 공간에는 빈 문자열(" ") 할당
                for (int i = 0; i < placesArray.length; i++) {
                    if (i < places.length) {
                        placesArray[i] = places[i];
                    } else {
                        placesArray[i] = "";  // 빈 문자열 할당
                    }
                }

                int[] intArray = convertToIntArray(placesArray);

                // placesArray의 내용 출력
                for (String value : placesArray) {
                    Log.i("AAA", value.trim()); // trim()을 사용하여 문자열 앞뒤의 공백 제거
                }

                Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleAddActivity.this);
                ScheduleApi api = retrofit.create(ScheduleApi.class);

                // 토큰 가져온다.
                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                token = sp.getString("token", "");
                token = "Bearer " + token;

                Schedule schedule = new Schedule(region, dateString1, dateString2, content, intArray);
                Call<Res> call = api.addSchedule(token, schedule);  //토큰,스케줄정보,장소값들
                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ScheduleAddActivity.this, "일정 저장 완료.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        Log.i("AAA", "에러");
                    }
                });
            }
        });


    }


    //장소 선택 후 여기서 처리
    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if (o.getResultCode() == 100) {
                                String selectRegion = o.getData().getStringExtra("selectRegion");
                                selectRegionId = o.getData().getStringExtra("selectRegionId");
                                txtPlace.setText(selectRegion);
                                Log.i("AAA","전달 받은 장소 ID " +selectRegionId );
                            }
                        }
                    });

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 문자열 배열을 정수 배열로 변환하는 함수
    public static int[] convertToIntArray(String[] stringArray) {
        int length = stringArray.length;
        int[] intArray = new int[length];

        for (int i = 0; i < length; i++) {
            // Integer.parseInt를 사용하여 문자열을 정수로 변환
            intArray[i] = Integer.parseInt(stringArray[i]);
        }

        return intArray;
    }
}

