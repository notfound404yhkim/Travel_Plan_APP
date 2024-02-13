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

import com.example.travelapp.PlaceInfoActivity;
import com.example.travelapp.R;
import com.example.travelapp.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter2 extends RecyclerView.Adapter<PlaceAdapter2.ViewHolder> {

    Context context;
    ArrayList<Place> placeArrayList = new ArrayList<>();
    Place place;
    int index;



    public PlaceAdapter2(Context context, ArrayList<Place> placeArrayList) {
        this.context = context;
        this.placeArrayList = placeArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.place_column,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placeArrayList.get(position);
        holder.txtRegion.setText("<" + place.region + ">");
        holder.txtName.setText(place.placeName);
//        holder.txtDate.setText(place.strDate +" ~ " + place.endDate);
        Picasso.get().load(place.imgUrl).into( holder.imgPhoto);

    }

    @Override
    public int getItemCount() {

        return placeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtRegion;
        TextView txtName;
        TextView txtDate;

        ImageView imgPhoto;

        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRegion = itemView.findViewById(R.id.txtRegion);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = getAdapterPosition();
                    place = placeArrayList.get(index);
                    Intent intent = new Intent(context, PlaceInfoActivity.class);
                    intent.putExtra("id",place.id);
                    intent.putExtra("option",0);
                    context.startActivity(intent);
                }
            });

        }
    }
}
