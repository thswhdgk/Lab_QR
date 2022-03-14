package com.example.lab_qr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    public FirebaseFirestore db;
    public String name;
    private TextView tv_profile_name;
    private TextView tv_profile_id;
    private ImageView imv_profile;
    private Button btn_modify;
    private AlertDialog dialog;
    private EditText et_stid, et_name;
    private StorageReference storageRef;
    public String user_name, user_id;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        tv_profile_id = (TextView)findViewById(R.id.tv_profile_stid);
        tv_profile_name = (TextView) findViewById(R.id.tv_profilename);
        imv_profile = (ImageView)findViewById(R.id.imv_profile);
        btn_modify = (Button)findViewById(R.id.btn_profile_modify);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        initView();
        getUser();

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.info_dialog, null);
                showDialog(view);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(getApplicationContext(), KakaoLogin.class);
                        startActivity(intent);
                        // 로그아웃 성공시 수행
                        finish();
                    }
                });
            }
        });

    }

    // 해당 유저 정보 가져오기
    public void getUser() {
        DocumentReference productRef = db.collection("user").document(name);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        user_name = document.getString("name");
                        user_id = document.getString("id");
                        tv_profile_id.setText(user_id);
                        tv_profile_name.setText(user_name);
                    } else {
                        // 학번, 이름 정보 생성하기
                        Log.e("###","해당 문서에 데이터가 없음");
                        Toast.makeText(getApplicationContext(),"회원정보가 없습니다. 회원정보를 수정해주세요", Toast.LENGTH_SHORT).show();
//                        LayoutInflater inflater = getLayoutInflater();
//                        final View v = inflater.inflate(R.layout.info_dialog, null);
//                        showDialog(v);
                    }
                } else {
                    Log.e("###","데이터 가져오기 실패");
                }
            }
        });
    }

    // 신규 가입자 학번 및 이름 적기
    public void showDialog(View view) {
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_stid = (EditText) view.findViewById(R.id.et_id);
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setView(view);
        dialog = ad.create();
        dialog.show();
    }

    // 학번, 이름 기입 후 데이터베이스에 저장
    public void onStoreButtonClicked(View view) {
        DocumentReference productRef = db.collection("user").document(name);
        Map<String, Object> user = new HashMap<>();
        user.put("id",et_stid.getText().toString());
        user.put("name",et_name.getText().toString());
        user.put("use",false);
        user.put("documentId",null);
        productRef.set(user);
        getUser();
        dialog.dismiss();
    }

    //toolbar 보이게 하는거
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //뒤로가기버튼 생성

    }

    //점점점 눌렀을 때 하위 메뉴 보이게 하는거
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    //하위 메뉴들 눌렸을 때 id로 기능 만들어주는거
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            case R.id.logout:
//                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
//                    @Override
//                    public void onCompleteLogout() {
//                        //intent에 정보 더 넣어서 여기서 나간거면 mainactivity도 finish하게 나중에 수정예정
//                        Intent intent = new Intent(getApplicationContext(), KakaoLogin.class);
//                        startActivity(intent);
//                        // 로그아웃 성공시 수행
//                        finish();
//                    }
//                });
//                return true;
            case  R.id.item_account:
                return true;
            case android.R.id.home:
                finish(); //현재 액티비티 없애서 뒤로가기
                return true;
        }
        return false;
    }
}