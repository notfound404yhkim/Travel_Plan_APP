package com.example.travelapp.adapter;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseBooleanArray;
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
    private SparseBooleanArray clickedItems; // 클릭한 상태를 저장하는 배열


    //액티비티로 전달하기 위해서 클릭리스너 정의
    private View.OnClickListener onClickListener;
    private CardClickListener cardClickListener;

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }
    public interface CardClickListener {
        void onCardClick(int position);
    }

    public SchedulePlaceSelectAdapter(Context context, ArrayList<Place> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;
    }

    public SchedulePlaceSelectAdapter(ArrayList<Place> placeArrayList,CardClickListener cardClickListener,Context context) {
        this.placeArrayList = placeArrayList;
        this.cardClickListener = cardClickListener;
        this.context = context;
        this.clickedItems = new SparseBooleanArray();
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedItems.get(position, false)) {
                    // 이미 클릭한 상태일 때
                    holder.imageView.setImageResource(R.drawable.image_uncheck);
                } else {
                    // 처음 또는 클릭을 해제한 상태일 때
                    holder.imageView.setImageResource(R.drawable.image_check);
                }
                clickedItems.put(position, !clickedItems.get(position, false));
                cardClickListener.onCardClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {

        return placeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView txtPlaceName;

        ImageView imgPhoto;

        CardView cardView;

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlaceName = itemView.findViewById(R.id.txtPlaceName);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            cardView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
