package com.sme_book.lab_qr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sme_book.lab_qr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private long clickTime = 0, recyclerClickTime = 0;
    private ListData getList;
    private String imageFilePath, imageFileName;
    private Uri photoUri;
    private Button btn_scan, btn_now;
    private IntentIntegrator qr_scan;
    private AlertDialog dialog;
    private EditText et_stid, et_name;
    private StorageReference storageRef;
    public String name;
    public FirebaseFirestore db;
    public String year_month, user_name, user_id, document_id, now_year, now_month;
    public List<String> yearList;
    public boolean now_use, click_cancel;
    public TabLayout tab;
    public Spinner year_spinner;
    public TextView tv_name, tv_stid;

    // ????????? ?????? ??????????????????, ?????????, ????????? ????????????
    private ArrayList<ListData> arrayList;
    private ListAdapter listAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scan = findViewById(R.id.btn_scan);
        btn_now = findViewById(R.id.btn_now);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        year_spinner = findViewById(R.id.year_spinner);
        tab = findViewById(R.id.tab);
        tv_name = findViewById(R.id.tv_name);
        tv_stid = findViewById(R.id.tv_stid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getCurrentTime();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        if(intent.getExtras() != null && intent.getExtras().getString("state").equals("off")){
            final Intent new_intent = new Intent(MainActivity.this, KakaoLogin.class);
            startActivity(new_intent);
            finish();
        }

        // ?????? ?????? ?????????
        yearList = new ArrayList<String>();
        for(int i=2021; i<=Integer.parseInt(now_year); i++) {
            yearList.add(Integer.toString(i));
        }
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,yearList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(adapter);
        year_spinner.setSelection(yearList.size()-1);
        // ?????? ?????? ?????????
        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                now_year = yearList.get(position);
                getInfo(now_year+"-"+now_month);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ??? ?????? ????????????
        tab.setScrollPosition(Integer.parseInt(now_month)-1,0,true);
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==1) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==2) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==3) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==4) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==5) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==6) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==7) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==8) {
                    now_month = "0"+String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==9) {
                    now_month = String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==10) {
                    now_month = String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                } else if (tab.getPosition()==11) {
                    now_month = String.valueOf(tab.getPosition()+1);
                    getInfo(now_year+"-"+now_month);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // ????????????
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInfo(now_year+"-"+now_month);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        qr_scan = new IntentIntegrator(this);

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // ?????? ?????? ????????????
        getUser();
        getInfo(now_year+"-"+now_month);

        btn_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentTime();
                tab.setScrollPosition(Integer.parseInt(now_month)-1,0,true);
                year_spinner.setSelection(yearList.size()-1);
                getInfo(now_year+"-"+now_month);
            }
        });

        // "User"??? "use" ????????? false??? ?????? ?????? ??????, true??? ?????? ????????? ??????
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!now_use) {
                    // ?????? ?????? ??????
                    qr_scan.setOrientationLocked(false);
                    qr_scan.setPrompt("??????????????????..");
                    // ?????? ??????
                    qr_scan.initiateScan();
                } else {
                    // ????????? ?????? ??????
                    checkPermission();
                }
            }
        });

        // ?????????????????? ??????
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        listAdapter = new ListAdapter(arrayList);
        recyclerView.setAdapter(listAdapter);

        // ?????? ?????? ????????? ????????? ??????
        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                click_cancel = false;
                getList = arrayList.get(position);
                Log.e("###",getList.getImage_url());
                if(getList.getImage_url() == "") {
                    Toast.makeText(getApplication(),"?????? ????????????",Toast.LENGTH_SHORT).show();
                }
                else {
                    // Progress Dialog ??????
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, 1);
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setCancelable(true);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            click_cancel = true;
                        }
                    });
                    if (SystemClock.elapsedRealtime() - recyclerClickTime < 1000) return;
                    recyclerClickTime = SystemClock.elapsedRealtime();
                    storageRef.child(getList.getImage_url()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (click_cancel == true) return;
                            CustomDialog.getInstance(MainActivity.this).showDialog(uri);
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
        });
    }

    // ?????? ?????? ?????? ????????????
    private void getCurrentTime() {
        Long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat mFormat1 = new SimpleDateFormat("yyyy");
        SimpleDateFormat mFormat2 = new SimpleDateFormat("MM");
        now_year = mFormat1.format(date);
        now_month = mFormat2.format(date);
    }

    // ???????????? ?????? 2??? ?????? ?????? ??? ??????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                finish();
                overridePendingTransition(0,0);
                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplication(),"?????? ??? ??????????????? ?????? ???????????????",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    // ????????? ?????? ??????
    public void checkPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(cameraPermission == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},99);
        }
    }

    // ????????? ?????? ????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 99) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this,"???????????? ????????? ?????? ???????????????",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // ????????? ????????????
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

    // ????????? ?????? ??????
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

    // ?????? ?????? ?????? ????????????
    public void getUser() {
        DocumentReference productRef = db.collection("user").document(name);
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.e("###","????????? ???????????? ??????");
                        user_name = document.getString("name");
                        user_id = document.getString("id");
                        now_use = document.getBoolean("use");
                        year_month = document.getString("year_month");
                        if(year_month == null) year_month = " ";
                        document_id = document.getString("documentId");
                        if(!now_use) btn_scan.setText("????????????");
                        else btn_scan.setText("????????????");

                        tv_name.setText(user_name);
                        tv_stid.setText(user_id);
                    } else {
                        // ??????, ?????? ?????? ????????????
                        Log.e("###","?????? ????????? ???????????? ??????");
                        LayoutInflater inflater = getLayoutInflater();
                        final View v = inflater.inflate(R.layout.info_dialog, null);
                        showDialog(v);
                    }
                } else {
                    Log.e("###","????????? ???????????? ??????");
                }
            }
        });
    }

    // ????????????????????? ?????? ????????????
    public void getInfo(String now_time) {
        Log.e("###",now_time);
        db.collection(now_time)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // ????????? ???????????? ??????
                        if(task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot doc  : task.getResult()) {
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

    // ?????? ????????? ?????? ??? ?????? ??????
    public void showDialog(View view) {
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_stid = (EditText) view.findViewById(R.id.et_id);
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setView(view);
        dialog = ad.create();
        dialog.show();
    }

    // ??????, ?????? ?????? ??? ????????????????????? ??????
    public void onStoreButtonClicked(View view) {
        DocumentReference productRef = db.collection("user").document(name);
        Map<String, Object> user = new HashMap<>();
        user.put("id",et_stid.getText().toString());
        user.put("name",et_name.getText().toString());
        user.put("use",false);
        user.put("year_month",null);
        user.put("documentId",null);
        productRef.set(user);
        getUser();
        dialog.dismiss();
    }

    // ????????? ?????? ??? ?????? ?????? ????????? ??? & ?????? ?????? ????????????
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ????????? ?????? ??? ?????? ?????? ????????? ??? ?????????????????? ?????? ??? ??????????????? ??????
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Progress Dialog ??????
            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, 2);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setCancelable(false);
            progressDialog.show();
            // ??????????????? ??????
            StorageReference riversRef = storageRef.child("lab_image/"+imageFileName);
            UploadTask uploadTask = riversRef.putFile(photoUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("###","???????????? ????????? ??????");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Log.e("###","???????????? ????????? ??????");
                }
            });
            // ?????????????????? ??????
            LocalDateTime dateTime = LocalDateTime.now();
            String finish_time = DateTimeFormatter.ofPattern("dd??? HH:mm").format(dateTime);
            DocumentReference DocRef = db.collection(year_month).document(document_id);
            Map<String, Object> info = new HashMap<>();
            info.put("finish_time",finish_time);
            info.put("image_url","lab_image/"+imageFileName);
            DocRef.update(info);
            String tmp_year_month = year_month;

            DocumentReference productRef = db.collection("user").document(name);
            Map<String, Object> user = new HashMap<>();
            user.put("use",false);
            user.put("year_month",null);
            user.put("documentId",null);
            productRef.update(user);

            getInfo(tmp_year_month);
            getUser();
        }
        // ?????? ?????? ????????????
        else {
            IntentResult result=IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result!=null) {
                String str = "Lab_Permission_QR";
                // ?????? ??? ?????? ?????? ????????? ??????
                if(result.getContents() == null) {
                    Log.e("###","?????? ??? ????????????");
                    return;
                }
                // QR ?????? ?????? ?????? ??????
                if(result.getContents().equals(str)) {
                    Toast.makeText(MainActivity.this,"QR ?????? ????????? ??????????????????",Toast.LENGTH_SHORT).show();
                    // QR ?????? ???????????? json?????? ?????? ??? ????????? ?????? ?????????
                    try {
                        JSONObject obj=new JSONObject(result.getContents());
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    // ?????? ?????? ????????? ??????
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
                            // ?????? ?????? ?????? ??????
                            LocalDateTime dateTime = LocalDateTime.now();
                            String start_time = DateTimeFormatter.ofPattern("dd??? HH:mm").format(dateTime);
                            year_month =  DateTimeFormatter.ofPattern("yyyy-MM").format(dateTime);
                            DocumentReference DocRef = db.collection(year_month).document(start_time+' '+user_name);
                            Map<String, Object> info = new HashMap<>();
                            info.put("name",user_name);
                            info.put("id",user_id);
                            info.put("population",population+"???");
                            info.put("start_time",start_time);
                            info.put("finish_time","?????? ???");
                            info.put("image_url","");
                            DocRef.set(info);

                            DocumentReference productRef = db.collection("user").document(name);
                            Map<String, Object> user = new HashMap<>();
                            user.put("year_month", year_month);
                            user.put("id",user_id);
                            user.put("name",user_name);
                            user.put("use",true);
                            user.put("documentId",start_time+' '+user_name);
                            productRef.set(user);

                            getInfo(year_month);
                            getUser();
                            populationPickerDialog.dismiss();
                        }
                    });
                    populationPickerDialog.show();
                }
                // QR ?????? ?????? ?????? ??????
                else {
                    Toast.makeText(MainActivity.this,"QR ????????? ?????? ??? ????????????",Toast.LENGTH_SHORT).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    //????????? ????????? ??? ?????? ?????? ????????? ?????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    //?????? ????????? ????????? ??? id??? ?????? ??????????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_profile:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }

    // NumberPicker ??????
    public String[] getArrayWithSteps(int minvalue, int maxvalue, int step) {
        int number_of_array = (maxvalue - minvalue) / step + 1;
        String[] result = new String[number_of_array];
        for(int i=0; i<number_of_array; i++) {
            result[i] = String.valueOf(minvalue+step*i);
        }
        return result;
    }

}