package com.example.hyejin.hh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main11Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText idEdit, passwdEdit;
    TextView findID, findPASSWD;
    Button okButton, signUpButton;

    String id;
    String passwd;
    String id_temp;

    SharedPreferences test;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idEdit = (EditText) findViewById(R.id.idEdit);
        passwdEdit = (EditText) findViewById(R.id.passwdEdit);
        findID = (TextView) findViewById(R.id.findID); // 아이디 찾기
        findPASSWD = (TextView) findViewById(R.id.findPASSWD); // 비밀번호 찾기
        okButton = (Button) findViewById(R.id.okButton); // 버튼: 확인
        signUpButton = (Button) findViewById(R.id.signUpButton); // 버튼: 회원가입

        test = getSharedPreferences("test", MODE_PRIVATE);
        editor = test.edit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String strColor = "#b9ffbf3f";
        idEdit = (EditText) findViewById(R.id.idEdit);
        passwdEdit = (EditText) findViewById(R.id.passwdEdit);
        findID = (TextView) findViewById(R.id.findID); // 아이디 찾기
        SpannableString content = new SpannableString("아이디 찾기");    //아이디 찾기 밑줄
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        findID.setText(content);
        findID.setBackgroundColor(Color.parseColor(strColor));
        findPASSWD = (TextView) findViewById(R.id.findPASSWD); // 비밀번호 찾기
        SpannableString content2 = new SpannableString("비밀번호 찾기");   //비밀번호 찾기 밑줄
        content2.setSpan(new UnderlineSpan(),0,content2.length(),0);
        findPASSWD.setText(content2);
        findPASSWD.setBackgroundColor(Color.parseColor(strColor));
        okButton = (Button) findViewById(R.id.okButton); // 버튼: 확인
        signUpButton = (Button) findViewById(R.id.signUpButton); // 버튼: 회원가입

        // ID 찾기
        findID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pj9039.ipdisk.co.kr:8080/namju/findID.html"));
                startActivity(intent);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loginCheckServer();
            }
        });

        findPASSWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pj9039.ipdisk.co.kr:8080/namju/findPASSWD.html"));
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() { // 버튼: 회원가입
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main11Activity.this, Main12Activity.class);
                startActivity(intent);
            }
        });
    }

    private void loginCheckServer(){
        Thread thread = new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub

                id = idEdit.getText().toString();
                passwd = passwdEdit.getText().toString();

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/successsignin.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("passwd", passwd));
                    post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                    //Get 방식의 요청
                    HttpResponse responseGet = new DefaultHttpClient().execute(post);
                    HttpEntity resEntity = responseGet.getEntity();

                    if(resEntity != null)
                    {
                        // 요청에 대한 결과 값
                        String responseString = EntityUtils.toString(resEntity);

                        Message msg = Message.obtain();
                        msg.what = 100;
                        msg.obj = responseString.trim();
                        mHandler2.sendMessage(msg);
                    }
                }
                catch (Exception e) {
                }
            }
        });
        thread.start();
    }

    private Handler mHandler2 = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 100) {
                try {
                    //오류 시작
                    JSONObject jbj = new JSONObject((String)msg.obj);
                    JSONArray jr = jbj.getJSONArray("result");

                    if(jr.length()==1) {
                        JSONObject jsonObject = jr.getJSONObject(0);

                        if (jsonObject.getString("id") != null) {
                            editor.putString("id_temp",jsonObject.getString("id"));
                            editor.commit();

                            Intent intent = new Intent(Main11Activity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else
                        Toast.makeText(Main11Activity.this, "ID와 PW를 확인해주세요.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main11, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = new Intent(Main11Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main11Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main11Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main11Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main11Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main11Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main11Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            Intent intent = new Intent(Main11Activity.this, Main11Activity.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            Intent intent = new Intent(Main11Activity.this, Main11Activity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
