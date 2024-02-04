package com.example.travelapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelapp.adapter.CommentListAdapter;
import com.example.travelapp.api.BookmarkApi;
import com.example.travelapp.api.CommentApi;
import com.example.travelapp.api.LikeApi;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.api.UserApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Comment;
import com.example.travelapp.model.DetailPosting;
import com.example.travelapp.model.Posting;
import com.example.travelapp.model.Res;
import com.example.travelapp.model.UserRes;
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

public class BookMarkDetailActivity extends AppCompatActivity {

    TextView txtTitle;
    TextView txtName;
    ImageView imgPhoto;
    TextView txtContent;
    TextView txtTag;
    TextView txtDate;
    TextView txtUpdateDate;
    Button btnUpdate;
    Button btnDelete;
    ImageView imgLike;
    ImageView imgBookmark;
    TextView txtLikeCount;
    TextView txtBookmarkCount;
    ImageView txtProfilePhoto;
    EditText editCommentAdd;
    Button btnWrite;

    // 보였다 안보였다 할 레이아웃
    View linearDate;
    View linearBtn;

    RecyclerView recyclerView;
    CommentListAdapter adapter;
    ArrayList<DetailPosting.Comments> commentsArrayList = new ArrayList<>();

    // 현재 postId
    int postId;

    // 현재 로그인 유저의 이름
    String currentUserName = "";

    // 유저의 좋아요, 북마크 여부
    int isLike;
    int isBookmark;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_posting);

        txtTitle = findViewById(R.id.txtContext);
        txtName = findViewById(R.id.txtName);
        imgPhoto = findViewById(R.id.imgPhoto);
        txtContent = findViewById(R.id.txtContent);
        txtTag = findViewById(R.id.txtTag);
        txtDate = findViewById(R.id.txtDate);
        txtUpdateDate = findViewById(R.id.txtUpdateDate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        imgLike = findViewById(R.id.imgLike);
        imgBookmark = findViewById(R.id.imgBookmark);
        txtLikeCount = findViewById(R.id.txtLikeCount);
        txtBookmarkCount = findViewById(R.id.txtBookmarkCount);
        txtProfilePhoto = findViewById(R.id.txtProfilePhoto);
        editCommentAdd = findViewById(R.id.editCommentAdd);
        btnWrite = findViewById(R.id.btnWrite);

        linearDate = findViewById(R.id.linearDate);
        linearBtn = findViewById(R.id.linearBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BookMarkDetailActivity.this));

        Posting posting = (Posting) getIntent().getSerializableExtra("posting");
        postId = posting.postingId;

        // 글 수정
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookMarkDetailActivity.this);
                builder.setCancelable(false);
                builder.setTitle("게시글 수정");
                builder.setMessage("게시글을 수정하시겠습니까?");

                builder.setNegativeButton("No", null);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BookMarkDetailActivity.this, CommunityDetailUpdateActivity.class);
                        intent.putExtra("postId", postId);
                        startActivity(intent);
                    }
                });

                builder.show();
            }
        });

        // 글 삭제
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialog();
            }
        });

        // 좋아요
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(BookMarkDetailActivity.this);

                LikeApi api = retrofit.create(LikeApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");

                if (isLike == 0){
                    Call<Res> call = api.addLike(postId, "Bearer " + token);

                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()){
                                Snackbar.make(btnWrite, "좋아요 추가 성공", Snackbar.LENGTH_SHORT).show();
                                getNetworkData();
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    });

                } else {
                    Call<Res> call = api.deleteLike(postId, "Bearer " + token);

                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            Snackbar.make(btnWrite, "좋아요 삭제 성공", Snackbar.LENGTH_SHORT).show();
                            getNetworkData();
                            return;
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
            }
        });

        // 즐겨찾기
        imgBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(BookMarkDetailActivity.this);

                BookmarkApi api = retrofit.create(BookmarkApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");

                if (isBookmark == 0){
                    Call<Res> call = api.addBookmark(postId, "Bearer " + token);

                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            if (response.isSuccessful()){
                                Snackbar.make(btnWrite, "즐겨찾기에 추가되었습니다.", Snackbar.LENGTH_SHORT).show();
                                getNetworkData();
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    });

                } else {
                    Call<Res> call = api.deleteBookmark(postId, "Bearer " + token);

                    call.enqueue(new Callback<Res>() {
                        @Override
                        public void onResponse(Call<Res> call, Response<Res> response) {
                            Snackbar.make(btnWrite, "즐겨찾기에 삭제되었습니다.", Snackbar.LENGTH_SHORT).show();
                            getNetworkData();
                            return;
                        }

                        @Override
                        public void onFailure(Call<Res> call, Throwable t) {
                            Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
            }
        });

        // 댓글 
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editCommentAdd.getText().toString().trim();

                if (comment.isEmpty()){
                    Snackbar.make(btnWrite, "내용을 입력해주세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Retrofit retrofit = NetworkClient.getRetrofitClient(BookMarkDetailActivity.this);

                CommentApi api = retrofit.create(CommentApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");

                Comment comment1 = new Comment(comment);
                Call<Res> call = api.addComment(postId, "Bearer " + token, comment1);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()){
                            Snackbar.make(btnWrite, "댓글을 작성하였습니다.", Snackbar.LENGTH_SHORT).show();
                            editCommentAdd.setText("");
                            getNetworkData();

                            // 키보드 내리기
                            InputMethodManager imm = (InputMethodManager) getSystemService(BookMarkDetailActivity.this.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editCommentAdd.getWindowToken(), 0);
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        userInfo();

        super.onResume();
    }

    // 현재 유저 정보
    private void userInfo() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(BookMarkDetailActivity.this);

        UserApi api = retrofit.create(UserApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<UserRes> call = api.getProfile("Bearer " + token);

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                if (response.isSuccessful()){
                    UserRes res = response.body();

                    if (res.items.get(0).profileImg != null){
                        Glide.with(BookMarkDetailActivity.this).load(res.items.get(0).profileImg).into(txtProfilePhoto);
                    }

                    currentUserName = res.items.get(0).name;

                    adapter = new CommentListAdapter(BookMarkDetailActivity.this, commentsArrayList, res.items.get(0).name);
                    recyclerView.setAdapter(adapter);

                    getNetworkData();

                } else {
                    Snackbar.make(btnWrite, "유저 정보를 불러오지 못했습니다.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                Snackbar.make(btnWrite, "통신 실패", Snackbar.LENGTH_SHORT).show();
                return;
            }
        });

    }

    // 게시글 상세정보
    private void getNetworkData() {
        Retrofit retrofit = NetworkClient.getRetrofitClient(BookMarkDetailActivity.this);

        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        Call<DetailPosting> call = api.detailPosting(postId, "Bearer " + token);

        call.enqueue(new Callback<DetailPosting>() {
            @Override
            public void onResponse(Call<DetailPosting> call, Response<DetailPosting> response) {
                if (response.isSuccessful()){
                    DetailPosting detailPosting = response.body();

                    isLike = detailPosting.items.isLike;
                    isBookmark = detailPosting.items.isBookmark;

                    ImageChange();

                    // 현재 로그인 유저만 댓글 삭제, 글 수정 삭제 가능하게
                    if (currentUserName.equals(detailPosting.items.name)){
                        linearBtn.setVisibility(View.VISIBLE);
                    }

                    txtTitle.setText(detailPosting.items.title);

                    txtName.setText(detailPosting.items.name);
                    Glide.with(BookMarkDetailActivity.this).load(detailPosting.items.imgUrl).into(imgPhoto);
                    txtContent.setText(detailPosting.items.content);

                    StringBuilder tags = new StringBuilder();
                    for (int i = 0; i < detailPosting.tag.size(); i++) {
                        tags.append(detailPosting.tag.get(i));
                        if (i < detailPosting.tag.size() - 1) {
                            tags.append(", ");
                        }
                    }
                    String result = tags.toString();
                    txtTag.setText(result);

                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat df = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분ss초");
                    sf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    df.setTimeZone(TimeZone.getDefault());

                    try {
                        Date date = sf.parse(detailPosting.items.createdAt);
                        Date date2 = sf.parse(detailPosting.items.updatedAt);
                        String localtime = df.format(date);
                        String localtime2 = df.format(date2);
                        txtDate.setText(localtime);
                        txtUpdateDate.setText(localtime2);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    if (txtDate.getText().toString().trim().equals(txtUpdateDate.getText().toString().trim()) == false){
                        linearDate.setVisibility(View.VISIBLE);
                    }

                    txtLikeCount.setText(detailPosting.items.likeCnt+"");
                    txtBookmarkCount.setText(detailPosting.items.bookmarkCnt+"");

                    commentsArrayList.clear();

                    commentsArrayList.addAll(detailPosting.comments);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<DetailPosting> call, Throwable t) {
                Snackbar.make(btnWrite, "통신 실패입니다.", Snackbar.LENGTH_SHORT).show();
                return;
            }
        });
    }

    // 좋아요, 즐겨찾기 여부에 따라 이미지 수정
    private void ImageChange() {
        if (isLike == 0){
            imgLike.setImageResource(R.drawable.null_like);
        } else if (isLike == 1) {
            imgLike.setImageResource(R.drawable.like);
        }

        if (isBookmark == 0){
            imgBookmark.setImageResource(R.drawable.null_star);
        } else if (isBookmark == 1) {
            imgBookmark.setImageResource(R.drawable.star);
        }
    }

    private void ShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookMarkDetailActivity.this);
        builder.setCancelable(false);
        builder.setTitle("게시글 삭제");
        builder.setMessage("게시글을 삭제하시겠습니까?");

        builder.setNegativeButton("No", null);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Retrofit retrofit = NetworkClient.getRetrofitClient(BookMarkDetailActivity.this);

                PostingApi api = retrofit.create(PostingApi.class);

                SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                String token = sp.getString("token", "");

                Call<Res> call = api.deletePosting(postId, "Bearer " + token);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        if (response.isSuccessful()){
                            finish();
                            Toast.makeText(BookMarkDetailActivity.this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        Snackbar.make(btnWrite, "통신 실패", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                });

            }
        });

        builder.show();
    }

}