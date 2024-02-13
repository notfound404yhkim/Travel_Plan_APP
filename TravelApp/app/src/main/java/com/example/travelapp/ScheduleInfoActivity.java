package com.example.travelapp;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.ScheduleApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Items;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.ScheduleRes;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class ScheduleInfoActivity extends AppCompatActivity {
    TextView txtTile,txtContent,txtDate;
    ArrayList<Place> ScheduleArrayList = new ArrayList<>();
    LinearLayout linearLayout; // 레이아웃
    ViewFlipper viewFlipper;
    String token;
    Items item;
    ImageView[] imageViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_info);
        txtTile=findViewById(R.id.txtRegion);
        txtContent=findViewById(R.id.txtContent);
        viewFlipper=findViewById(R.id.viewFlipper);
        txtDate=findViewById(R.id.txtDate);
        linearLayout=findViewById(R.id.LinearLayout);
        int id = getIntent().getIntExtra("id",0);
        Log.i("AAA","전달받은값"+id);
        getInfo(id);
    }
    //행사,축제 정보 출력.
    public void getInfo(int id){
        Retrofit retrofit = NetworkClient.getRetrofitClient(ScheduleInfoActivity.this);
        ScheduleApi api = retrofit.create(ScheduleApi.class);
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        token = sp.getString("token", "");
        token = "Bearer " + token;
        Call<ScheduleRes> call = api.getMyScheduleInfo(token,id);
        call.enqueue(new Callback<ScheduleRes>() {
            @Override
            public void onResponse(Call<ScheduleRes> call, Response<ScheduleRes> response) {
                Log.i("AAA", response.toString());
                if(response.isSuccessful()){
                    ScheduleRes scheduleres = response.body();
                    item = scheduleres.items;
                    txtTile.setText(item.region);
                    txtContent.setText(item.content);
                    txtDate.setText(item.getFormattedStartDate() + " ~ " + item.getFormattedEndDate());
                    viewFlipper.removeAllViews();
                    ScheduleArrayList.addAll(scheduleres.place_list);
                    imageViews = new ImageView[ScheduleArrayList.size()];
                    int i = 0;
                    for (Place item : ScheduleArrayList) {
                        Log.i("AAA",item.imgUrl);
                        imageViews[i] = new ImageView(ScheduleInfoActivity.this);
                        viewFlipper.addView(imageViews[i]);
                        Picasso.get().load(item.imgUrl).into( imageViews[i]);
                        i = i+1;
                    }
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<ScheduleRes> call, Throwable t) {
                Log.i("AAA", t.toString());
            }
        });
    }
}