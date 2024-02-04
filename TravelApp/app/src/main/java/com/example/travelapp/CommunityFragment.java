package com.example.travelapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.travelapp.adapter.MypostingAdapter;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Posting;
import com.example.travelapp.model.PostingList;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommunityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityFragment newInstance(String param1, String param2) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Button btnView;
    Button btnShare;
    Button btnAdd;
    ProgressBar progressBar;

    // 리사이클러뷰 관련 변수
    RecyclerView recyclerView;
    MypostingAdapter adapter;
    ArrayList<Posting> postingArrayList = new ArrayList<>();

    ImageView imgError;

    int offset = 0;
    int limit = 15;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_community, container, false);

        btnView = rootView.findViewById(R.id.btnView);
        btnShare = rootView.findViewById(R.id.btnShare);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        progressBar = rootView.findViewById(R.id.progressBar);
        imgError = rootView.findViewById(R.id.imgError);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnView.setBackgroundColor(getResources().getColor(R.color.black));
        btnView.setTextColor(getResources().getColor(R.color.white));


        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetworkData();

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityTwoFragment communityTwoFragment = new CommunityTwoFragment();

                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout, communityTwoFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityThreeFragment communityThreeFragment = new CommunityThreeFragment();

                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout, communityThreeFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        getNetworkData();

        super.onResume();
    }

    private void getNetworkData() {

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        PostingApi api = retrofit.create(PostingApi.class);

        Call<PostingList> call = api.getMyPosting("Bearer " + token, offset, limit);

        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()){
                    PostingList postingList = response.body();

                    postingArrayList.clear();

                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;
                    if(postingArrayList.size() == 0)
                    {
                        imgError.setVisibility(View.VISIBLE);
                    }

                    adapter = new MypostingAdapter(getActivity(), postingArrayList);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(btnAdd, "네트워크 통신 실패", Snackbar.LENGTH_SHORT).show();
                return;
            }
        });

    }
}