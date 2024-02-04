package com.example.travelapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.R;
import com.example.travelapp.ScheduleInfoActivity;
import com.example.travelapp.model.Place;
import com.example.travelapp.model.Schedule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SchedulePlaceSelectAdapter extends RecyclerView.Adapter<SchedulePlaceSelectAdapter.ViewHolder> {

    Context context;
    ArrayList<Place> placeArrayList = new ArrayList<>();

    //액티비티로 전달하기 위해서 클릭리스너 정의
    private View.OnClickListener onClickListener;

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    public SchedulePlaceSelectAdapter(Context context, ArrayList<Place> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.place_select_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placeArrayList.get(position);
        holder.txtPlaceName.setText(place.placeName);
        Picasso.get().load(place.imgUrl).into( holder.imgPhoto);
        final Place item = placeArrayList.get(position);
        //이벤트 리스너 생성
        holder.cardView.setOnClickListener(onClickListener);
        holder.cardView.setTag(item);

    }

    @Override
    public int getItemCount() {

        return placeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView txtPlaceName;

        ImageView imgPhoto;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlaceName = itemView.findViewById(R.id.txtPlaceName);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
