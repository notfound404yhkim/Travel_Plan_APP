package com.example.travelapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.R;
import com.example.travelapp.api.CommentApi;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.DetailPosting;
import com.example.travelapp.model.Res;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    Context context;
    ArrayList<DetailPosting.Comments> commentsArrayList;
    String currentUserName = "";
    SimpleDateFormat sf;
    SimpleDateFormat df;

    public CommentListAdapter(Context context, ArrayList<DetailPosting.Comments> commentsArrayList, String currentUserName) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;
        this.currentUserName = currentUserName;

        // 성능 개선을 위해 첫 1회만 실행하도록 생성자 안에다 넣는다.
        sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_list_row, parent, false);

        return new CommentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailPosting.Comments comments = commentsArrayList.get(position);

        Log.i("currentUserName : ", currentUserName);
//        Log.i("댓글 이름 : ", comments.name);

        // 본인 댓글만 삭제 가능하게
        if (comments.name == null || (comments.name.equals(currentUserName) == false)){
            holder.btnDelete.setVisibility(View.GONE);
        }

        if (comments.profileImg != null){
            Glide.with(context).load(comments.profileImg).into(holder.txtProfilePhoto);
        }

        holder.txtName.setText(comments.name);

        try {
            Date date = sf.parse(comments.createdAt);
            String localTime = df.format(date);

            holder.txtDate.setText(localTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.txtContent.setText(comments.content);

    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView txtProfilePhoto;
        TextView txtName;
        TextView txtDate;
        TextView txtContent;
        Button btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProfilePhoto = itemView.findViewById(R.id.txtProfilePhoto);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtContent = itemView.findViewById(R.id.editContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowAlertDialog();
                }
            });

        }

        private void ShowAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle("댓글 삭제");
            builder.setMessage("댓글을 삭제하시겠습니까?");

            builder.setNegativeButton("No", null);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int index = getAdapterPosition();
                    DetailPosting.Comments comments = commentsArrayList.get(index);

                    Retrofit retrofit = NetworkClient.getRetrofitClient(context);

                    CommentApi api = retrofit.create(CommentApi.class);

                    SharedPreferences sp = context.getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    String token = sp.getString("token", "");

                    Call<Res> call = api.deleteComment(comments.postid, comments.commentId, "Bearer " + token);

                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()){
                                Snackbar.make(btnDelete, "댓글이 삭제되었습니다.", Snackbar.LENGTH_SHORT).show();
                                commentsArrayList.remove(index);
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            Snackbar.make(btnDelete, "통신 실패", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    });

                }
            });

            builder.show();
        }
    }
}
