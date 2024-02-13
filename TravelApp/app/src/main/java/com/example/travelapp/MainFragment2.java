package com.example.travelapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.PlaceAdapter;
import com.example.travelapp.adapter.PlaceAdapter2;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PlaceApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.PlaceList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainFragment2 extends Fragment implements View.OnClickListener {
    Button btnP1,btnP2;
    TextView txtMore;

    Calendar calendar;
    //선택한날짜 멤버변수

    ImageView[] imageViews;
    String[] region = {"서울","인천","대전","대구","광주","부산","제주"};
    ArrayList<Place> placeArrayList = new ArrayList<>();
    ArrayList<Place> placeArrayList2 = new ArrayList<>();
    ViewFlipper viewFlipper;

    RecyclerView recyclerView;

    // 페이징 처리를 위한 변수들
    int offset = 0;
    int limit = 25;
    int count = 0;

    String token;

    PlaceAdapter2 adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main2, container, false);  //프레그먼트 레이아웃지정.
        txtMore = view.findViewById(R.id.txtMore);
        btnP1 = view.findViewById(R.id.btnP1);
        btnP2 = view.findViewById(R.id.btnP2);
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        viewFlipper = view.findViewById(R.id.viewFlipper);

        // TextView 변수들을 초기화하고 클릭 리스너 등록
        int[] textViewIds = {R.id.txtRegion1, R.id.txtRegion2, R.id.txtRegion3, R.id.txtRegion4, R.id.txtRegion5, R.id.txtRegion6, R.id.txtRegion7};

        for (int textViewId : textViewIds) {
            TextView textView = view.findViewById(textViewId);
            textView.setOnClickListener(this);
        }

        //리사이클러뷰 설정
        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); //가로로 설정
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        // 축제 및 행사 리스트 더보기
        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlaceActivity.class);
                startActivity(intent);
            }
        });

        //첫번째 페이지로 이동
        btnP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment secondFragment = new MainFragment();
                //                               // Fragment 에서 다른 Fragment로 이동 .
                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout,secondFragment);
                    fragmentTransaction.commit();
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 앱 첫 실행시 랜덤으로 축제 리스트 출력
        Random random = new Random();
        int randomIndex = random.nextInt(region.length);

        String RandomRegion = region[randomIndex];
        System.out.println(RandomRegion);


        previewfestival(RandomRegion);

        getNetworkData(RandomRegion);

    }

    @Override
    public void onClick(View v) {
        TextViewClick((TextView) v);
    }

    // 클릭한 텍스트 처리
    public void TextViewClick(View view) {
        // 클릭한 텍스트에 대한 작업 수행
        Log.d("ClickedText", ((TextView) view).getText().toString());
        String select = ((TextView) view).getText().toString();
        getNetworkData(select);
    }


   //앱 실행시 핫플 정보 뿌려주기
    private void getNetworkData(String region) {

        //변수 초기화
        offset = 0;
        count = 0;

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        PlaceApi api = retrofit.create(PlaceApi.class);
        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;
        Log.i("AAA2",region);


        Call<PlaceList> call = api.getPlacelist(token,region, 0,offset, limit);

        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if(response.isSuccessful()){

                    Log.i("AAA",response.toString());

                    PlaceList placeList = response.body();
                    count = placeList.count;

                    placeArrayList.clear();
                    placeArrayList.addAll( placeList.items );

                    // 어댑터 만들어서, 리사이클러뷰에 적용 //새로고침
                    adapter = new PlaceAdapter2(getActivity(), placeArrayList);
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


    // 앱 처음 실행시 랜덤 축제 이미지 4개 가져오기
    public void previewfestival(String region){
        showProgress();
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PlaceApi api = retrofit.create(PlaceApi.class);
        Call<PlaceList> call = api.getImg(region, 1, 0,25);
        call.enqueue(new Callback<PlaceList>() {
            @Override
            public void onResponse(Call<PlaceList> call, Response<PlaceList> response) {


                if(response.isSuccessful()){

                    placeArrayList2.clear(); // 리스트 초기화

                    PlaceList placeList = response.body();
                    placeArrayList2.addAll(placeList.items);

                    imageViews = new ImageView[placeArrayList2.size()];
                    viewFlipper.removeAllViews();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    //사이즈만큼 반복분 이미지 뷰를 생성 .
                    for (int i = 0; i < placeArrayList2.size(); i++) {
                        imageViews[i] = new ImageView(getActivity());
                        viewFlipper.addView(imageViews[i]);

                        // 클릭 이벤트 추가
                        final int position = i; // 클로저(Closure)에서 final 변수를 사용해야 함
                        imageViews[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 클릭된 이미지뷰에 대한 동작 수행
                                Place item = placeArrayList2.get(position);

                                Intent intent = new Intent(getActivity(),PlaceInfoActivity.class);
                                intent.putExtra("id",item.id);
                                intent.putExtra("option",1);
                                Log.i("AAA",item.id+"테스트");
                                startActivity(intent);
                            }
                        });
                    }

                    int i = 0;
                    for (Place item : placeArrayList2) {
                        Picasso.get().load(item.imgUrl).into( imageViews[i]);
                        i = i+1;
                    }
                }
                dismissProgress();
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
        dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(getActivity()));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void dismissProgress(){
        dialog.dismiss();
    }


}
