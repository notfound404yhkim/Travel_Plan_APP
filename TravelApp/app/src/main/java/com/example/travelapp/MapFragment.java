package com.example.travelapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelapp.adapter.MapAdapter;
import com.example.travelapp.api.GoogleMapNetworkClient;
import com.example.travelapp.api.GooglePlaceApi;
import com.example.travelapp.config.MapConfig;
import com.example.travelapp.model.GooglePlaceList;
import com.example.travelapp.model.Map;
import com.example.travelapp.model.MapItemClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MapFragment extends Fragment implements MapItemClickListener {

    Double lat;
    Double lng;

    GoogleMap googleMap;
    Marker currentLocationMarker;
    LocationManager locationManager;
    LocationListener locationListener;

    EditText autoCompleteTextView;
    LinearLayout linearLayout;
    Button imgBtn;
    ImageButton locationBtn;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<Map> mapArrayList = new ArrayList<>();

    String keyword = "";
    String pagetoken = "";
    String language = "ko";
    int radius = 4000;
    MapAdapter adapter;
    boolean isFirstLocationUpdate; //첫 위치 업데이트 여부를 나타내는 변수

    private int mapInitRetryCount = 0;
    private static final int MAX_MAP_INIT_RETRY = 3; // 최대 재시도 횟수

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstLocationUpdate = true; //  첫 위치 업데이트 여부를 나타내는 변수
        currentLocationMarker = null;

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 권한 허용하는 코드
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            // 위치 권한이 이미 허용된 경우
            getLocationUpdates();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        linearLayout = view.findViewById(R.id.Map_root);
        autoCompleteTextView = view.findViewById(R.id.autoComplete);
        imgBtn = view.findViewById(R.id.mapBtn);
        locationBtn = view.findViewById(R.id.my_locationBtn);
        progressBar = view.findViewById(R.id.progressBar);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
                // 키보드 숨기기
                hideKeyboard();
            }
        });
        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();

                    return true;
                }
                return false;
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToMyLocation();
            }
        });


        recyclerView = view.findViewById(R.id.MapRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount){
                    if(pagetoken.isEmpty() == false){
                        addNetworkData();
                    }
                }

            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RecyclerView가 보이면 숨기고, 숨겨져 있으면 보이게 함
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                if (map == null && mapInitRetryCount < MAX_MAP_INIT_RETRY) {
                    // 지도가 null이면서 최대 재시도 횟수보다 작은 경우 재시도
                    mapInitRetryCount++;
                    mapFragment.getMapAsync(this); // 자기 자신을 다시 호출
                }
                googleMap = map;
                googleMap.getUiSettings().setMyLocationButtonEnabled(true); // 내 위치 버튼 활성화

                // Google Map의 마커 클릭 리스너 설정
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // 마커를 클릭했을 때 구글 지도 웹사이트로 이동하는 인텐트 생성
                        String latitude = String.valueOf(marker.getPosition().latitude);
                        String longitude = String.valueOf(marker.getPosition().longitude);
                        String label = marker.getTitle();
                        String uriBegin = "geo:" + latitude + "," + longitude;
                        String query = latitude + "," + longitude + "(" + label + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);

                        return true; // true를 반환하여 이벤트 소비를 표시합니다.
                    }
                });
            }
        });


        return view;
    }



    // 위치 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용된 경우
                getLocationUpdates();
            } else {
                // 위치 권한이 거부된 경우
                Toast.makeText(getActivity(), "위치 권한 요청이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
            getNetworkData();
        }
    }

    private void getNetworkData() {

        Retrofit retrofit = GoogleMapNetworkClient.getRetrofitClient(getActivity());

        GooglePlaceApi api = retrofit.create(GooglePlaceApi.class);

        Call<GooglePlaceList> call = api.getPlaceList(lat+","+lng,
                radius,
                language,
                keyword,
                MapConfig.MAP_API_KEY);

        call.enqueue(new Callback<GooglePlaceList>() {
            @Override
            public void onResponse(Call<GooglePlaceList> call, Response<GooglePlaceList> response) {
                if(response.isSuccessful()){
                    GooglePlaceList googlePlaceList = response.body();

                    if(googlePlaceList.next_page_token != null){
                        pagetoken = googlePlaceList.next_page_token;
                    }
                    mapArrayList.clear();
                    mapArrayList.addAll( googlePlaceList.results);


                    adapter = new MapAdapter(getActivity(),mapArrayList, MapFragment.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }else {

                }
            }

            @Override
            public void onFailure(Call<GooglePlaceList> call, Throwable t) {

            }
        });






    }

    private void addNetworkData() {
        Retrofit retrofit = GoogleMapNetworkClient.getRetrofitClient(getActivity());

        GooglePlaceApi api = retrofit.create(GooglePlaceApi.class);

        Call<GooglePlaceList> call = api.getPlaceList(lat+","+lng,
                radius,
                language,
                keyword,
                MapConfig.MAP_API_KEY);

        call.enqueue(new Callback<GooglePlaceList>() {
            @Override
            public void onResponse(Call<GooglePlaceList> call, Response<GooglePlaceList> response) {
                if(response.isSuccessful()){
                    GooglePlaceList googlePlaceList = response.body();

                    if(googlePlaceList.next_page_token != null){
                        pagetoken = googlePlaceList.next_page_token;
                    }
                    mapArrayList.clear();
                    mapArrayList.addAll( googlePlaceList.results);

                }else {

                }
            }
            @Override
            public void onFailure(Call<GooglePlaceList> call, Throwable t) {

            }
        });
    }

    private void getLocationUpdates() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                Log.i("AAA",lat+"lat");
                Log.i("AAA",lng+"lng");
                if (googleMap != null) {
                    LatLng myLocation = new LatLng(lat, lng);
                    Log.i("AAA", myLocation.toString());
                    if (isFirstLocationUpdate) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
                        isFirstLocationUpdate = false;
                        progressBar.setVisibility(View.GONE);
                        Log.i("AAA","카메라 이동 성공");
                    }
                    updateCurrentLocationMarker(myLocation); // 마커 업데이트
                }
            }
        };
        // 위치 권한 체크
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // 권한이 없으면 메서드 종료
        }

        // 권한이 허용된 경우 위치 업데이트 요청
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                -1,
                locationListener
        );
        // NETWORK_PROVIDER를 사용하여 위치 업데이트 요청
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                3000, // 위치 업데이트 간격 (3초)
                -1,   // 위치 업데이트 최소 거리 변경 (미사용)
                locationListener
        );
    }



    public void moveToLocation(double lat, double lng) {
        LatLng location = new LatLng(lat, lng);
        if (googleMap != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15)); // 지도 이동
            googleMap.clear(); // 이전에 추가된 마커 제거
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title("선택한 장소");
            googleMap.addMarker(markerOptions); // 선택한 장소에 마커 추가
            // 현재 위치에 마커 추가
            if (currentLocationMarker != null) {
                currentLocationMarker.remove(); // 이전에 추가된 현재 위치 마커 제거
            }
            MarkerOptions currentLocationMarkerOptions = new MarkerOptions()
                    .position(location)
                    .title("현재 위치");
            currentLocationMarker = googleMap.addMarker(currentLocationMarkerOptions);
        }

    }



    private void updateCurrentLocationMarker(LatLng latLng) {
        if (currentLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("현재 위치");
            currentLocationMarker = googleMap.addMarker(markerOptions);
        } else {
            currentLocationMarker.setPosition(latLng); // 기존 마커의 위치 업데이트

        }
    }

    @Override
    public void onMapItemClick(double lat, double lng) {
        moveToLocation(lat, lng);
        clearEditText(); // EditText 초기화
        recyclerView.setVisibility(View.GONE);
    }

    private void moveToMyLocation() {
        if (googleMap != null && lat != null && lng != null) {
            LatLng myLocation = new LatLng(lat, lng);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
        }
    }

    private void performSearch() {
        keyword = autoCompleteTextView.getText().toString().trim();
        if(keyword.isEmpty()){
            Toast.makeText(getActivity(),"검색어를 입력하세요",Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        getNetworkData();
        // 키보드 숨기기
        hideKeyboard();


    }
    public void clearEditText() {
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setText("");
        }
    }
    // 키보드 숨기는 메서드
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
    }

}