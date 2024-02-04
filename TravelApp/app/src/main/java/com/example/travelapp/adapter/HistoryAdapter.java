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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.HistoryInfoActivity;
import com.example.travelapp.R;
import com.example.travelapp.api.HistoryApi;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.ScheduleApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.History;
import com.example.travelapp.model.Res;
import com.example.travelapp.model.Schedule;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<History> historyArrayList;
    int index;

    History history;


    public HistoryAdapter(Context context, ArrayList<History> historyArrayList) {
        this.context = context;
        this.historyArrayList = historyArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.history_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyArrayList.get(position);
        holder.txtRegion.setText(history.region);
        holder.txtDate.setText(history.strDate +" ~ "+ history.endDate);


    }

    @Override
    public int getItemCount() {
        return historyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtRegion,txtDate;
        RelativeLayout relativeLayout;
        ImageButton imgBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegion = itemView.findViewById(R.id.txtRegion);
            txtDate = itemView.findViewById(R.id.txtDate);
            relativeLayout = itemView.findViewById(R.id.box);
            imgBtn = itemView.findViewById(R.id.btnDelete);

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = getAdapterPosition();
                    history = historyArrayList.get(index);
                    Intent intent = new Intent(context, HistoryInfoActivity.class);
                    intent.putExtra("id",history.id);
                    context.startActivity(intent);
                }
            });
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialog();
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
                    History history = historyArrayList.get(index);
                    int historyId = history.id;

                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                    String token = sp.getString("token","");
                    token = "Bearer " + token;

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);
                    HistoryApi api = retrofit.create(HistoryApi.class);
                    Call<Res> call = api.deleteHistory(historyId,token);
                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if(response.isSuccessful()){

                                historyArrayList.remove(index);
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
