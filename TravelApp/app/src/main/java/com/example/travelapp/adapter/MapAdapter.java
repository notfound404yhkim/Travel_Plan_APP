package com.example.travelapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.R;
import com.example.travelapp.model.Map;
import com.example.travelapp.model.MapItemClickListener;

import java.util.ArrayList;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    Context context;
    ArrayList<Map> mapArrayList;

    private MapItemClickListener itemClickListener;


    public MapAdapter(Context context, ArrayList<Map> mapArrayList,MapItemClickListener itemClickListener) {
        this.context = context;
        this.mapArrayList = mapArrayList;
        this.itemClickListener = itemClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.maplist_row, parent, false);
        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map map = mapArrayList.get(position);

        if (map.name == null) {
            holder.txtName.setText("없음");
        } else {
            holder.txtName.setText(map.name);
        }

        if (map.vicinity == null) {
            holder.txtAddress.setText("주소 없음");
        } else {
            holder.txtAddress.setText(map.vicinity);
        }
    }

    @Override
    public int getItemCount() {
        return mapArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtName, txtAddress;
        CardView cardView;


        public ViewHolder(@NonNull View itemView, MapItemClickListener itemClickListener) {
            super(itemView);

            cardView = itemView.findViewById(R.id.MapCardView);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtName = itemView.findViewById(R.id.txtName);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Map selectedMap = mapArrayList.get(position);
                        itemClickListener.onMapItemClick(selectedMap.geometry.location.lat, selectedMap.geometry.location.lng);
                    }

                }
            });
        }

    }
}