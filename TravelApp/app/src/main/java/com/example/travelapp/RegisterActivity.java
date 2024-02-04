package com.example.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travelapp.api.NetworkClient;
import com.example.travelapp.api.UserApi;
import com.example.travelapp.config.Config;
import com.example.travelapp.model.User;
import com.example.travelapp.model.UserRes;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button btn;
    ImageButton imgBtn;

    EditText register_username;
    EditText register_email;
    EditText register_phone;
    EditText register_password;
    EditText register_correct_password;;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressBar = findViewById(R.id.register_progressBar);
        btn = findViewById(R.id.register_button);
        imgBtn = findViewById(R.id.back_btn);

        progressBar.setVisibility(View.GONE);

        register_username = findViewById(R.id.register_username);
        register_email = findViewById(R.id.register_email);
        register_phone = findViewById(R.id.register_phone);
        register_password = findViewById(R.id.register_password);
        register_correct_password = findViewById(R.id.register_correct_password);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = register_username.getText().toString().trim();
                String email = register_email.getText().toString().trim();
                String password = register_password.getText().toString().trim();
                String password2 = register_correct_password.getText().toString().trim();
                String phone = register_phone.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty() || phone.isEmpty() || password2.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"항목을 모두 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식이 맞는지 체크
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false){
                    Toast.makeText(RegisterActivity.this,"이메일 형식을 확인하세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 길이체크 4~12자리까지 허용

                if(password.length() < 4 || password.length() >= 12){
                    Toast.makeText(RegisterActivity.this,"비밀번호 길이를 확인하세요.",Toast.LENGTH_SHORT).show();
                    return;
                }


                // 비밀번호 2개가 일치하는지 확인

                if(!password.equals(password2)){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }



                //네트워크로 회원가입 APi를 호출한다.

                // 0. 다이얼로그를 화면에 보여준다.
                showProgress();

                // 1. retrofit 변수 생성
                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);

                // 2. api 패키지에 있는, Interface 생성
                UserApi api = retrofit.create(UserApi.class);

                // 3. 보낼 데이터를 만든다.=> (묶음처리) :  클래스의 객체 생성
                User user = new User(name,email,phone,password);

                // 4. api 호출
                Call<UserRes> call = api.register(user);

                // 5.  서버로부터 받은 응답을 처리하는 코드 작성.
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();
                        //서버에서 보낸 응답이 200 ok 일때 처리하는 코드.
                        if (response.isSuccessful()){
                            UserRes userRes = response.body();
                            Log.i("AAA","result : " + userRes.result);
                            Log.i("AAA","Token : " + userRes.accessToken);
                            SharedPreferences sp =
                                    getSharedPreferences(Config.PREFERENCE_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token",userRes.accessToken);
                            editor.apply();

                            // 이상없으므로, 메인 액티비티를 실행한다.
                            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        else if(response.code() == 400){
                            Toast.makeText(RegisterActivity.this,"잘못된 이메일형식이거나 비밀번호길이가 맞지 않습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }else if(response.code() == 500){
                            Toast.makeText(RegisterActivity.this,"DB 처리중에 문제가 있습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"잠시 후 이용하세요.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        //유저한테 네트워크 통신 실패했다고 알려준다.
                        dismissProgress();
                        Toast.makeText(RegisterActivity.this,"통신실패",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private long time= 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    // 네트워크 데이터 처리할때 사용할 다이얼로그
    Dialog dialog;
    private void showProgress(){
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress(){
        dialog.dismiss();
    }

}