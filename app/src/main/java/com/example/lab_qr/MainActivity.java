package com.example.lab_qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private long clickTime = 0, recyclerClickTime = 0;
    private ListData getList;
    private String imageFilePath, imageFileName;
    private Uri photoUri;
    private Button btn_scan;
    private IntentIntegrator qr_scan;
    private AlertDialog dialog;
    private EditText et_stid, et_name;
    private StorageReference storageRef;
    public String name;
    public FirebaseFirestore db;
    public String user_name, user_id, document_id;
    public boolean now_use;

    // 이용자 목록 리사이클러뷰, 어뎁터, 데이터 불러오기
    private ArrayList<ListData> arrayList;
    private ListAdapter listAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        initView();

        btn_scan = findViewById(R.id.btn_scan);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // 새로고침
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        qr_scan = new IntentIntegrator(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        if(intent.getExtras() != null && intent.getExtras().getString("state").equals("off")){
            Log.e("###","현재 state - off 정보 있음");
            final Intent new_intent = new Intent(MainActivity.this, KakaoLogin.class);
            startActivity(new_intent);
            finish();
        }

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // 로그인 정보 가져오기
        getUser();

        // "User"의 "use" 필드가 false일 경우 스캔 기능, true일 경우 카메라 기능
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!now_use) {
                    // 스캔 세로 모드
                    qr_scan.setOrientationLocked(false);
                    qr_scan.setPrompt("스캔중입니다..");
                    // 스캔 실행
                    qr_scan.initiateScan();
                } else {
                    // 카메라 권한 확인
                    checkPermission();
                }
            }
        });

        // 리사이클러뷰 연결
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        listAdapter = new ListAdapter(arrayList);
        recyclerView.setAdapter(listAdapter);

        // 리사이클러뷰에 데이터 가져오기
        getInfo();

        // 해당 위치 데이터 리스트 접근
        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if(SystemClock.elapsedRealtime() - recyclerClickTime < 1000) return;
                recyclerClickTime = SystemClock.elapsedRealtime();
                getList = arrayList.get(position);
                Log.e("###",getList.getImage_url());
                storageRef.child(getList.getImage_url()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("###",String.valueOf(uri));
                        CustomDialog.getInstance(MainActivity.this).showDialog(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
    }

    // 뒤로가기 버튼 2번 누를 시에 앱 종료
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                finish();
                overridePendingTransition(0,0);
                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplication(),"한번 더 클릭하시면 앱을 종료합니다",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    // 카메라 권한 확인
    public void checkPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(cameraPermission == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},99);
        }
    }

    // 카메라 권한 수락하기
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 99) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this,"수락하지 않으면 앱이 종료됩니다",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // 카메라 실행하기
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {

            }
            if(photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 이미지 파일 생성
    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        imageFileName = "lab_"+timestamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        Log.e("###",imageFileName);
        imageFilePath = image.getAbsolutePath();
        return image;
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
                        Log.e("###","데이터 가져오기 성공");
                        user_name = document.getString("name");
                        user_id = document.getString("id");
                        now_use = document.getBoolean("use");
                        document_id = document.getString("documentId");
                        if(!now_use) btn_scan.setText("이용시작");
                        else btn_scan.setText("이용종료");
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

    // 리사이클러뷰에 정보 가져오기
    public void getInfo() {
        db.collection("info")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 데이터 가져오기 성공
                        if(task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Log.e("###",doc.getData().toString());
                                ListData listData = new ListData(doc.get("name").toString(), doc.get("id").toString(), doc.get("population").toString(), doc.get("start_time").toString(), doc.get("finish_time").toString(), doc.get("image_url").toString());
                                arrayList.add(listData);
                            }
                            listAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("###","error");
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

    // 카메라 촬영 후 확인 버튼 눌렀을 때 & 스캔 정보 가져오기
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 카메라 촬영 후 확인 버튼 눌렀을 때 데이터베이스 갱신 및 스토리지에 추가
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.e("###","카메라 확인 버튼 눌렀음");
            // 스토리지에 추가
            StorageReference riversRef = storageRef.child("lab_image/"+imageFileName);
            UploadTask uploadTask = riversRef.putFile(photoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("###","스토리지 업로드 실패");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("###","스토리지 업로드 완료");
                }
            });
            // 데이터베이스 갱신
            LocalDateTime dateTime = LocalDateTime.now();
            String finish_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime);
            DocumentReference DocRef = db.collection("info").document(document_id);
            Map<String, Object> info = new HashMap<>();
            info.put("finish_time",finish_time);
            info.put("image_url","lab_image/"+imageFileName);
            DocRef.update(info);

            DocumentReference productRef = db.collection("user").document(name);
            Map<String, Object> user = new HashMap<>();
            user.put("use",false);
            user.put("documentId",null);
            productRef.update(user);

            getInfo();
            getUser();
        }
        // 스캔 정보 가져오기
        else {
            IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result!=null) {
                String str = "Lab_Permission_QR";
                // 스캔 중 뒤로 가기 눌렀을 경우
                if(result.getContents() == null) { }
                // QR 코드 정보 있을 경우
                if(result.getContents().equals(str)) {
                    Toast.makeText(MainActivity.this,"QR 코드를 정보를 가져왔습니다",Toast.LENGTH_SHORT).show();
                    // QR 정보 데이터를 json으로 변환 및 사용자 정보 가져옴
                    try {
                        JSONObject obj=new JSONObject(result.getContents());
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    // 과랩 출입 인원수 체크
                    PopulationPickerDialog populationPickerDialog = PopulationPickerDialog.getInstance(MainActivity.this);
                    populationPickerDialog.setContentView(R.layout.activity_population_picker_dialog);

                    int minvalue = 1, maxvalue = 30, step = 1;
                    String[] arrayValue = getArrayWithSteps(minvalue,maxvalue,step);

                    NumberPicker population_picker = populationPickerDialog.findViewById(R.id.population_picker);
                    Button btn_save = populationPickerDialog.findViewById(R.id.btn_save);

                    population_picker.setMinValue(minvalue);
                    population_picker.setMaxValue(maxvalue);
                    population_picker.setDisplayedValues(arrayValue);
                    population_picker.setValue(3);
                    population_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String population = String.valueOf(population_picker.getValue());
                            // 과랩 출입 정보 기록
                            LocalDateTime dateTime = LocalDateTime.now();
                            String start_time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime);
                            DocumentReference DocRef = db.collection("info").document(start_time+' '+user_name);
                            Map<String, Object> info = new HashMap<>();
                            info.put("name",user_name);
                            info.put("id",user_id);
                            info.put("population",population+"명");
                            info.put("start_time",start_time);
                            info.put("finish_time","사용 중");
                            info.put("image_url","");
                            DocRef.set(info);

                            DocumentReference productRef = db.collection("user").document(name);
                            Map<String, Object> user = new HashMap<>();
                            user.put("id",user_id);
                            user.put("name",user_name);
                            user.put("use",true);
                            user.put("documentId",start_time+' '+user_name);
                            productRef.set(user);

                            getInfo();
                            getUser();
                            populationPickerDialog.dismiss();
                        }
                    });
                    populationPickerDialog.show();
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

    // NumberPicker 설정
    public String[] getArrayWithSteps(int minvalue, int maxvalue, int step) {
        int number_of_array = (maxvalue - minvalue) / step + 1;
        String[] result = new String[number_of_array];
        for(int i=0; i<number_of_array; i++) {
            result[i] = String.valueOf(minvalue+step*i);
        }
        return result;
    }

    // toolbar 선언
    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
    }

    // 메뉴 눌렀을 때 하위 메뉴 보이게 하는거
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // 하위 메뉴들 눌렸을 때 id로 기능 만들어주는거
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case  R.id.item_account:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
                return true;
        }
        return false;
    }
}