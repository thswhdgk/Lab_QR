package com.example.lab_qr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private Button btn_scan;
    private TextView tv_name,tv_address, tv_result;
    private IntentIntegrator qr_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scan=findViewById(R.id.btn_scan);
        tv_name=findViewById(R.id.tv_name);
        tv_address=findViewById(R.id.tv_address);
        tv_result=findViewById(R.id.tv_result);

        qr_scan=new IntentIntegrator(this);

        // 스캔 버튼 -> 추후에 수정 예정
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 스캔 세로 모드
                qr_scan.setOrientationLocked(false);
                qr_scan.setPrompt("스캔중입니다..");
                // 스캔 실행
                qr_scan.initiateScan();
            }
        });

        // 로그아웃 버튼 -> 추후에 수정 예정
        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // 로그아웃 성공시 수행
                        finish();
                    }
                });
            }
        });
    }

    // 스캔 정보 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null) {
            // QR 코드 정보 없을 경우
            if(result.getContents()==null) {
                Toast.makeText(MainActivity.this,"QR 코드를 읽을 수 없습니다",Toast.LENGTH_SHORT).show();
            }
            // QR 코드 정보 있을 경우
            else {
                Toast.makeText(MainActivity.this,"QR 코드를 정보를 가져왔습니다",Toast.LENGTH_SHORT).show();
                // QR 정보 데이터를 json으로 변환
                try {
                    JSONObject obj=new JSONObject(result.getContents());
                    tv_name.setText(obj.getString("name"));
                    tv_address.setText(obj.getString("address"));
                } catch(JSONException e) {
                    e.printStackTrace();
                    tv_result.setText(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}