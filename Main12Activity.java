package com.example.hyejin.hh;

import android.content.Intent;
import android.graphics.Color;
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

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class Main12Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean idcheck = false;
    boolean inputcheck = false;
    String id, passwdStr, mname, personStr, phoneStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main12);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        TextView checkID = (TextView)findViewById(R.id.checkID);
        checkID.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                readServer();
            }
        });
        SpannableString content = new SpannableString("중복확인");    //아이디 찾기 밑줄
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        checkID.setText(content);
        checkID.setBackgroundColor(Color.parseColor(strColor));

        Button okButton = (Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(idcheck == false)
                    Toast.makeText(Main12Activity.this, "중복확인을 확인해주세요.", Toast.LENGTH_SHORT).show();
                else{
                    EditText idEdit = (EditText)findViewById(R.id.idEdit);
                    EditText passwdEdit = (EditText)findViewById(R.id.passwdEdit);
                    EditText nameEdit = (EditText)findViewById(R.id.nameEdit);
                    EditText personEdit = (EditText)findViewById(R.id.personEdit);
                    EditText phoneEdit = (EditText)findViewById(R.id.phoneEdit);

                    id = idEdit.getText().toString();
                    passwdStr = passwdEdit.getText().toString();
                    mname = nameEdit.getText().toString();
                    personStr = personEdit.getText().toString();
                    phoneStr = phoneEdit.getText().toString();

                    if(passwdStr.equals(""))
                        Toast.makeText(Main12Activity.this, "input password please", Toast.LENGTH_SHORT).show();
                    else if(!(passwdStr.length()>=8&&passwdStr.length()<=12)){
                        Toast.makeText(Main12Activity.this, "input password length from 8 to 12", Toast.LENGTH_SHORT).show();
                    }
                    else if(mname.equals(""))
                        Toast.makeText(Main12Activity.this, "input name please", Toast.LENGTH_SHORT).show();
                    else if(personStr.equals(""))
                        Toast.makeText(Main12Activity.this, "input idcardNumber please", Toast.LENGTH_SHORT).show();
                    else if(phoneStr.equals(""))
                        Toast.makeText(Main12Activity.this, "input phoneNumber please", Toast.LENGTH_SHORT).show();
                    else
                        inputcheck = true;

                    if(inputcheck==true)
                        memberAddServer();
                }
            }
        });
    }

    private void readServer(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/checkid.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    //params.add(new BasicNameValuePair("data1", "값1")); //데이터는 이걸로 받기
                    post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //Get 방식의 요청
                    try{
                        HttpResponse responseGet = new DefaultHttpClient().execute(post);
                        HttpEntity resEntity = responseGet.getEntity();
                        if(resEntity!=null)
                        {
                            // 요청에 대한 결과 값
                            String responseString = EntityUtils.toString(resEntity);
                            Message msg = Message.obtain();
                            msg.what=100;
                            msg.obj=responseString.trim();

                            mHandler.sendMessage(msg);
                        }
                    }
                    catch (ConnectException ex) {
                    }
                }
                catch (Exception e) {
                }
            }
        });
        thread.start();
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            EditText idEdit = (EditText)findViewById(R.id.idEdit);

            if(msg.what == 100)
            {
                try {
                    //오류 시작
                    JSONObject jbj = new JSONObject((String)msg.obj);
                    JSONArray jr = jbj.getJSONArray("result");

                    for(int i=0; i<jr.length(); i++)
                    {
                        JSONObject jsonObject = jr.getJSONObject(i);

                        if(jsonObject.getString("id").equals(idEdit.getText().toString())){
                            Toast.makeText(Main12Activity.this, "ID가 중복됩니다!\n다른 ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                            idcheck = false;
                            idEdit.setText("");
                            break;
                        }

                        if(jr.length()==(i+1)){
                            Toast.makeText(Main12Activity.this, "중복된 ID가 없습니다.", Toast.LENGTH_SHORT).show();
                            idcheck = true;
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    };

    private void memberAddServer(){
        Thread thread = new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/insertmemberinfo.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("passwd", passwdStr));
                    params.add(new BasicNameValuePair("mname", mname));
                    params.add(new BasicNameValuePair("personalnum", personStr));
                    params.add(new BasicNameValuePair("contacts", phoneStr));
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
                //Toast.makeText(Main12Activity.this, ""+msg.obj, Toast.LENGTH_SHORT).show();
                System.out.println((String)msg.obj);

                if (((String)msg.obj).equals("\uFEFFsuccess")) {
                    Toast.makeText(Main12Activity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Main12Activity.this, Main11Activity.class);
                    startActivity(intent);
                    finish();
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
        getMenuInflater().inflate(R.menu.main12, menu);
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
            Intent intent = new Intent(Main12Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main12Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main12Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main12Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main12Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main12Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main12Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            Intent intent = new Intent(Main12Activity.this, Main11Activity.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            Intent intent = new Intent(Main12Activity.this, Main12Activity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
