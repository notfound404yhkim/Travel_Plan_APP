package com.example.travelapp;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
public class PlaceInfoActivity extends AppCompatActivity {
    int id;
    TextView txtTitle,txtRegion,txtContent,txtDate;
    ImageView imgPhoto;
    ArrayList<Place> placeArrayList = new ArrayList<>();
    LinearLayout linearLayout; // 레이아웃
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeinfo);
        txtTitle=findViewById(R.id.txtTitle);
        txtRegion=findViewById(R.id.txtRegion);
        txtContent=findViewById(R.id.txtContext);
        imgPhoto=findViewById(R.id.imgPhoto);
        txtDate=findViewById(R.id.txtDate);
        linearLayout=findViewById(R.id.LinearLayout);
        Log.i("AAA","전달받은값"+id);
        int id = getIntent().getIntExtra("id",0);
        int option = getIntent().getIntExtra("option",0);
        getInfo(id,option);
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
                        txtContent.setText(item.content);
                        if (item.option==1){
                            txtDate.setText(item.strDate +" ~ " + item.endDate);}
                        Picasso.get().load(item.imgUrl).into(imgPhoto);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
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
}