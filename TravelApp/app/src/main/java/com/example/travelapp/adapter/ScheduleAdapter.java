package com.example.travelapp.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.R;
import com.example.travelapp.ScheduleInfoActivity;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.ScheduleApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Res;
import com.example.travelapp.model.Schedule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    Context context;
    ArrayList<Schedule> ScheduleArrayList = new ArrayList<>(); //스케줄 목록
    Schedule schedule;
    int index;



    public ScheduleAdapter(Context context, ArrayList<Schedule> scheduleArrayList) {
        this.context = context;
        this.ScheduleArrayList = scheduleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.place_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = ScheduleArrayList.get(position);
        holder.txtRegion.setText(schedule.region);
        holder.txtName.setText(schedule.content);
        holder.txtDate.setText(schedule.getFormattedStartDate() + " ~ " + schedule.getFormattedEndDate());
        Picasso.get().load(schedule.imgUrl).into( holder.imgPhoto);

    }

    @Override
    public int getItemCount() {

        return ScheduleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtRegion;
        TextView txtName;
        TextView txtDate;

        ImageView imgPhoto;

        CardView cardView;

        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegion = itemView.findViewById(R.id.txtRegion);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDelete.setVisibility(View.VISIBLE); //스케줄일때는 삭제가 보이도록
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog();
                }
            });
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = getAdapterPosition();
                    schedule = ScheduleArrayList.get(index);
                    Intent intent = new Intent(context, ScheduleInfoActivity.class);
                    intent.putExtra("id",schedule.id);
                    context.startActivity(intent);
                }
            });

        }


        private void showAlertDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
// 이 다이얼 로그의 외곽부분을 눌렀을때, 사라지지 않도록 하는 코드.
            builder.setCancelable(false);
            builder.setTitle("일정 삭제");
            builder.setMessage("정말 일정 하시겠습니까? ");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    index = getAdapterPosition();
                    Schedule schedule = ScheduleArrayList.get(index);
                    int scheduleId = schedule.id;

                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                    String token = sp.getString("token","");
                    token = "Bearer " + token;

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);
                    ScheduleApi api = retrofit.create(ScheduleApi.class);
                    Call<Res> call = api.deleteSchedule(scheduleId,token);
                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if(response.isSuccessful()){

                                ScheduleArrayList.remove(index);
                                notifyDataSetChanged();


                            }else{

                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {

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
}
