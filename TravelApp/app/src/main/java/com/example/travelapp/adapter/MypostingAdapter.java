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

import com.bumptech.glide.Glide;
import com.example.travelapp.CommunityDetailActivity;
import com.example.travelapp.R;
import com.example.travelapp.model.Posting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MypostingAdapter extends RecyclerView.Adapter<MypostingAdapter.ViewHolder> {
    Context context;
    ArrayList<Posting> postingArrayList;
    SimpleDateFormat sf;
    SimpleDateFormat df;

    public MypostingAdapter(Context context, ArrayList<Posting> postingArrayList){
        this.context = context;
        this.postingArrayList = postingArrayList;

        // 성능 개선을 위해 첫 1회만 실행하도록 생성자 안에다 넣는다.
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public MypostingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_posting_row, parent, false);

        return new MypostingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posting posting = postingArrayList.get(position);

        Glide.with(context).load(posting.imgUrl).into(holder.imgPhoto);
        holder.txtTitle.setText(posting.title);
        holder.txtContent.setText(posting.content);
        holder.txtName.setText(posting.name);

        try {
            Date date = sf.parse(posting.createdAt);
            String localtime = df.format(date);

            holder.txtCreatedAt.setText(localtime);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return postingArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgPhoto;
        TextView txtTitle;
        TextView txtContent;
        TextView txtName;
        TextView txtCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            txtTitle = itemView.findViewById(R.id.editTitle);
            txtContent = itemView.findViewById(R.id.editContent);
            txtName = itemView.findViewById(R.id.txtName);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Posting posting = postingArrayList.get(index);

                    Intent intent = new Intent(context, CommunityDetailActivity.class);
                    intent.putExtra("posting", posting);
                    context.startActivity(intent);
                }
            });

        }
    }
}
