package com.example.lab_qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btn_scan;
    private TextView tv_name, tv_id, tv_result, tv_timestamp;
    private IntentIntegrator qr_scan;
    private AlertDialog dialog;
    private EditText et_id, et_name;
    public String name;
    public FirebaseFirestore db;
    public String user_name, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scan=findViewById(R.id.btn_scan);
        tv_name=findViewById(R.id.tv_name);
        tv_id=findViewById(R.id.tv_id);
        tv_result=findViewById(R.id.tv_result);
        tv_timestamp=findViewById(R.id.tv_timestamp);

        qr_scan=new IntentIntegrator(this);

        // 로그인 정보 가져오기
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        db = FirebaseFirestore.getInstance();
        getInfo();
        Log.e("###",name.toString());

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

    // 해당 유저 정보 가져오기
    public void getInfo() {
        DocumentReference productRef = db.collection("user").document(name);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Log.e("###","데이터 가져오기 성공");
                        user_name = document.getString("name");
                        user_id = document.getString("id");
                    } else {
                        // 학번, 이름 정보 생성하기
                        Log.e("###","해당 문서에 데이터가 없음");
                        LayoutInflater inflater = getLayoutInflater();
                        final View v = inflater.inflate(R.layout.info_dialog, null);
                        showDialog(v);
                    }
                } else {
                    Log.e("###","데이터 가져오기 실패");
                }
            }
        });
    }

    public void showDialog(View view) {
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_id = (EditText) view.findViewById(R.id.et_id);
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setView(view);
        dialog = ad.create();
        dialog.show();
    }

    public void onStoreButtonClicked(View view) {
        DocumentReference productRef = db.collection("user").document(name);
        Map<String, Object> info = new HashMap<>();
        info.put("id",et_id.getText().toString());
        info.put("name",et_name.getText().toString());
        productRef.set(info);
        getInfo();
        dialog.dismiss();
    }

    // 스캔 정보 가져오기
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result!=null) {
            // QR 코드 정보 있을 경우
            String str = "Lab_Permission_QR";
            if(result.getContents().equals(str)) {
                Toast.makeText(MainActivity.this,"QR 코드를 정보를 가져왔습니다",Toast.LENGTH_SHORT).show();
                // QR 정보 데이터를 json으로 변환 및 사용자 정보 가져옴
                try {
                    tv_name.setText(user_name);
                    tv_id.setText(user_id);
                    LocalDateTime dateTime = LocalDateTime.now();
                    String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime);
                    tv_timestamp.setText(timestamp);
                    JSONObject obj=new JSONObject(result.getContents());
                } catch(JSONException e) {
                    e.printStackTrace();
                    tv_result.setText(result.getContents());
                }
            }
            // QR 코드 정보 없을 경우
            else {
                Toast.makeText(MainActivity.this,"QR 코드를 읽을 수 없습니다",Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}