package com.example.travelapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.PostingApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.Res;
import com.google.android.material.snackbar.Snackbar;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommunityThreeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommunityThreeFragment() {
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
    public static CommunityThreeFragment newInstance(String param1, String param2) {
        CommunityThreeFragment fragment = new CommunityThreeFragment();
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

    ImageView imgPhoto;
    EditText editTitle;
    EditText editContent;
    Button btnPosting;

    // 사진 파일
    File photoFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_community_three, container, false);

        btnView = rootView.findViewById(R.id.btnView);
        btnShare = rootView.findViewById(R.id.btnShare);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        progressBar = rootView.findViewById(R.id.progressBar);

        imgPhoto = rootView.findViewById(R.id.imgPhoto);
        editTitle = rootView.findViewById(R.id.editTitle);
        editContent = rootView.findViewById(R.id.editContent);
        btnPosting = rootView.findViewById(R.id.btnPosting);

        btnAdd.setBackgroundColor(getResources().getColor(R.color.black));
        btnAdd.setTextColor(getResources().getColor(R.color.white));

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityFragment communityFragment = new CommunityFragment();

                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame_layout, communityFragment);
                    fragmentTransaction.commit();
                }
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

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String content = editContent.getText().toString().trim();

                if (photoFile == null || title.isEmpty() || content.isEmpty()){
                    Snackbar.make(btnView, "사진과 내용 모두 입력하세요.", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());

                PostingApi api = retrofit.create(PostingApi.class);

                SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                String token = sp.getString("token", "");

                // 보낼 파일
                RequestBody fileBody = RequestBody.create(photoFile, MediaType.parse("image/jpg"));
                MultipartBody.Part photo = MultipartBody.Part.createFormData("image", photoFile.getName(), fileBody);

                // 보낼 텍스트
                RequestBody textBody = RequestBody.create(title, MediaType.parse("text/plain"));
                RequestBody textBody2 = RequestBody.create(content, MediaType.parse("text/plain"));

                Call<Res> call = api.addPosting("Bearer " + token, photo, textBody, textBody2);

                call.enqueue(new Callback<Res>() {
                    @Override
                    public void onResponse(Call<Res> call, Response<Res> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()){
                            Snackbar.make(btnView, "포스팅이 생성되었습니다.", Snackbar.LENGTH_SHORT).show();

                            imgPhoto.setImageResource(R.drawable.add_photo);
                            editTitle.setText("");
                            editContent.setText("");

                            CommunityFragment communityFragment = new CommunityFragment();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_frame_layout, communityFragment);
                            fragmentTransaction.commit();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<Res> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(btnView, "통신 실패", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                });

            }
        });

        return rootView;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title);
        builder.setItems(R.array.alert_photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    // 첫번째 항목 클릭 시
                    camera();
                } else if (which == 1) {
                    // 두번째 항목 클릭 시
                    album();
                }
            }
        });

        builder.show();
    }

    private void camera(){
        // 권한 체크
        int permissionCheck = ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.CAMERA);

        // 권한 승인 유무
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(getActivity(), "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(i.resolveActivity(getActivity().getPackageManager())  != null  ){

                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                // todo : 패키지명을 현재의 프로젝트에 맞게 수정해야 함.
                Uri fileProvider = FileProvider.getUriForFile(getActivity(),
                        "com.example.travelapp.fileprovider", photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(i, 100);

            } else{
                Toast.makeText(getActivity(), "이 핸드폰에는 카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    private File getPhotoFile(String fileName) {
        File storageDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 20 && resultCode == getActivity().RESULT_OK) {

            Uri albumUri = data.getData();
            String fileName = getFileName(albumUri);
            try {

                ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(albumUri, "r");
                if (parcelFileDescriptor == null) return;
                FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                photoFile = new File(getActivity().getCacheDir(), fileName);
                FileOutputStream outputStream = new FileOutputStream(photoFile);
                IOUtils.copy(inputStream, outputStream);

                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                imgPhoto.setImageBitmap(photo);
                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch (Exception e) {
                e.printStackTrace();
            }

            // 네트워크로 보낸다.
        }
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 100 && resultCode == getActivity().RESULT_OK){

            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            // 압축시킨다. 해상도 낮춰서
            OutputStream os;
            try {
                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imgPhoto.setImageBitmap(photo);
            imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // 네트워크로 데이터 보낸다.



        }else if(requestCode == 300 && resultCode == getActivity().RESULT_OK && data != null &&
                data.getData() != null){

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );
            try {

                ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                photoFile = new File( getActivity().getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copy( inputStream, outputStream );

//                //임시파일 생성
//                File file = createImgCacheFile( );
//                String cacheFilePath = file.getAbsolutePath( );


                // 압축시킨다. 해상도 낮춰서
                Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                imgPhoto.setImageBitmap(photo);
                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch ( Exception e ) {
                e.printStackTrace( );
            }

            // 네트워크로 보낸다.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void album(){
        if(checkPermission()){
            displayFileChoose();
        }else{
            requestPermission();
            Intent intent= new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,20);
            requestPermission();
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }

    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Log.i("DEBUGGING5", "true");
            Toast.makeText(getActivity(), "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            Log.i("DEBUGGING6", "false");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }

    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    //앨범에서 선택한 사진이름 가져오기
    public String getFileName( Uri uri ) {
        Cursor cursor = getActivity().getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }
}
