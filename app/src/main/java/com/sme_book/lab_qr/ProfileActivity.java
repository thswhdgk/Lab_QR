package com.sme_book.lab_qr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sme_book.lab_qr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    public FirebaseFirestore db;
    public String name;
    private TextView tv_name;
    private TextView tv_stid;
    private TextView tv_modify, tv_logout;
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
        tv_stid = (TextView)findViewById(R.id.tv_stid);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_modify = (TextView)findViewById(R.id.tv_modify);
        tv_logout = (TextView) findViewById(R.id.tv_logout);

        initView();
        getUser();

        tv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.info_dialog, null);
                showDialog(view);
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("state","off");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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
                        tv_stid.setText(user_id);
                        tv_name.setText(user_name);
                    }
                } else {
                    Log.e("###","데이터 가져오기 실패");
                }
            }
        });
    }

    // 회원정보 수정
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
        if(et_name.length() < 2) {
            Toast toast = Toast.makeText(getApplicationContext(),"이름은 2글자 이상이어야 합니다.",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(et_stid.length() != 10) {
            Toast toast = Toast.makeText(getApplicationContext(),"학번은 10글자이어야 합니다.",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else {
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
    }

    //toolbar 보이게 하는거
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
    }

    //하위 메뉴들 눌렸을 때 id로 기능 만들어주는거
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("state","on");
                startActivity(intent);
                finish(); //현재 액티비티 없애서 뒤로가기
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("name",name);
            intent.putExtra("state","on");
            startActivity(intent);
            finish();
        }
        return true;
    }
}