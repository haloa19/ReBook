package com.example.hyejin.hh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main5Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences test;
    SharedPreferences.Editor editor;
    String id_temp;

    String imgURL;
    String title;
    String author;
    String publisher;
    String price;
    String pagenum;
    String pagecontent;
    String content;

    Button updateButton;
    Button deleteButton;

    TextView bookName;
    TextView bookAuthor;
    TextView bookPublisher;
    TextView bookPrice;
    TextView pagenum1;
    TextView pagecontent1;
    TextView feelingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
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

        test = getSharedPreferences("test", MODE_PRIVATE);
        editor = test.edit();

        Intent intent = getIntent();
        imgURL = intent.getStringExtra("image");
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        publisher = intent.getStringExtra("publisher");
        price = intent.getStringExtra("price");

        readServer();

        updateButton = (Button)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main5Activity.this, Main6Activity.class);
                intent.putExtra("image", imgURL);
                intent.putExtra("title", title);
                intent.putExtra("author", author);
                intent.putExtra("publisher", publisher);
                intent.putExtra("price", price);
                intent.putExtra("pagenum", pagenum);
                intent.putExtra("pagecontent", pagecontent);
                intent.putExtra("content", content);
                startActivityForResult(intent, 500);
            }
        });

        deleteButton = (Button)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiaryFromServer();

                Toast.makeText(Main5Activity.this, "delete test", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Main5Activity.this, Main4Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode ,int resultCode, Intent data){
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView bookName = (TextView) findViewById(R.id.bookName);
        TextView bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        TextView bookPublisher = (TextView) findViewById(R.id.publisher);
        TextView bookPrice = (TextView) findViewById(R.id.price);
        TextView pagenum1 = (TextView) findViewById(R.id.pagenum1);
        TextView pagecontent1 = (TextView) findViewById(R.id.pagecontent1);
        TextView feelingText = (TextView) findViewById(R.id.feelingText);

        imgURL = data.getStringExtra("image");
        title = data.getStringExtra("title");
        author = data.getStringExtra("author");
        publisher = data.getStringExtra("publisher");
        price = data.getStringExtra("price");
        pagenum = data.getStringExtra("pagenum");
        pagecontent = data.getStringExtra("pagecontent");
        content = data.getStringExtra("content");

        if(requestCode==500){
            if(resultCode==300){

                final Bitmap[] bitmap = {null};

                // 네트워크를 통하여 데이터를 받아올때는 '반드시' 쓰레드를 사용하여야 한다고함
                Thread mThread = new Thread(){
                    @Override
                    public void run(){
                        try {
                            URL url = new URL(imgURL);

                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setDoInput(true);
                            conn.connect();

                            InputStream is = conn.getInputStream();
                            bitmap[0] = BitmapFactory.decodeStream(is);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mThread.start();
                try{
                    mThread.join();

                    imageView.setImageBitmap(bitmap[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bookName.setText("책 제목 : " + title);
                bookAuthor.setText("저자 : " + author);
                bookPublisher.setText("출판사 : " + publisher);
                bookPrice.setText("가격 : " + price);
                pagenum1.setText(pagenum);
                pagecontent1.setText(pagecontent);
                feelingText.setText(content);

                deleteDiaryFromServer();
                inputMyDiaryServer();
            }
        }
    }

    private void inputMyDiaryServer(){
        id_temp = test.getString("id_temp", "0");

        bookName = (TextView) findViewById(R.id.bookName);
        bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        bookPublisher = (TextView) findViewById(R.id.publisher);
        bookPrice = (TextView) findViewById(R.id.price);
        pagenum1 = (TextView) findViewById(R.id.pagenum1);
        pagecontent1 = (TextView) findViewById(R.id.pagecontent1);
        feelingText = (TextView)findViewById(R.id.feelingText);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/xml3.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id_temp));
                    params.add(new BasicNameValuePair("image", imgURL));
                    params.add(new BasicNameValuePair("title", bookName.getText().toString()));
                    params.add(new BasicNameValuePair("author", bookAuthor.getText().toString()));
                    params.add(new BasicNameValuePair("publisher", bookPublisher.getText().toString()));
                    params.add(new BasicNameValuePair("price", bookPrice.getText().toString()));
                    params.add(new BasicNameValuePair("pagenum", pagenum1.getText().toString()));
                    params.add(new BasicNameValuePair("pagecontent", pagecontent1.getText().toString()));
                    params.add(new BasicNameValuePair("content", feelingText.getText().toString()));
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

            System.out.println("msg.obj : " + msg.obj);

            if(msg.what==100) {

                if (((String)msg.obj).equals("\uFEFFsuccess")) {
                    Toast.makeText(Main5Activity.this, "완료", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Main5Activity.this, Main4Activity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(Main5Activity.this, "WRONG", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.main5, menu);
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

    private void readServer(){
        id_temp = test.getString("id_temp", "0");

        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/xml5.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    params.add(new BasicNameValuePair("id", id_temp));
                    params.add(new BasicNameValuePair("image", imgURL));
                    params.add(new BasicNameValuePair("title", title));
                    params.add(new BasicNameValuePair("publisher", publisher));
                    params.add(new BasicNameValuePair("price", price));
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
            if(msg.what == 100)
            {
                try {
                    //오류 시작
                    JSONObject jbj = new JSONObject((String)msg.obj);
                    JSONArray jr = jbj.getJSONArray("result");

                    for(int i=0; i<jr.length(); i++)
                    {
                        JSONObject jsonObject = jr.getJSONObject(i);

                        System.out.println(jsonObject.getString("title"));
                        System.out.println(jsonObject.getString("author"));
                        System.out.println(jsonObject.getString("publisher"));
                        System.out.println(jsonObject.getString("price"));
                        System.out.println(jsonObject.getString("pagenum"));
                        System.out.println(jsonObject.getString("pagecontent"));
                        System.out.println(jsonObject.getString("content"));
                        pagenum = jsonObject.getString("pagenum");
                        pagecontent = jsonObject.getString("pagecontent");
                        content = jsonObject.getString("content");




                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        TextView bookName = (TextView) findViewById(R.id.bookName);
                        TextView bookAuthor = (TextView) findViewById(R.id.bookAuthor);
                        TextView bookPublisher = (TextView) findViewById(R.id.publisher);
                        TextView bookPrice = (TextView) findViewById(R.id.price);
                        TextView pagenum1 = (TextView) findViewById(R.id.pagenum1);
                        TextView pagecontent1 = (TextView) findViewById(R.id.pagecontent1);
                        TextView feelingText = (TextView) findViewById(R.id.feelingText);

                        final Bitmap[] bitmap = {null};

                        // 네트워크를 통하여 데이터를 받아올때는 '반드시' 쓰레드를 사용하여야 한다고함
                        Thread mThread = new Thread(){
                            @Override
                            public void run(){
                                try {
                                    URL url = new URL(imgURL);

                                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                    conn.setDoInput(true);
                                    conn.connect();

                                    InputStream is = conn.getInputStream();
                                    bitmap[0] = BitmapFactory.decodeStream(is);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        mThread.start();
                        try{
                            mThread.join();

                            imageView.setImageBitmap(bitmap[0]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        bookName.setText("책 제목 : " + title);
                        bookAuthor.setText("저자 : " + author);
                        bookPublisher.setText("출판사 : " + publisher);
                        bookPrice.setText("가격 : " + price);
                        pagenum1.setText(pagenum);
                        pagecontent1.setText(pagecontent);
                        feelingText.setText(content);
                    }





                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    };

    private void deleteDiaryFromServer(){
        Thread thread = new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/delete.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id_temp));
                    params.add(new BasicNameValuePair("image", imgURL));
                    params.add(new BasicNameValuePair("title", title));

                    post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                    //Get 방식의 요청
                    HttpResponse responseGet = new DefaultHttpClient().execute(post);
                    HttpEntity resEntity = responseGet.getEntity();

                    if(resEntity != null)
                    {
                        // 요청에 대한 결과 값
                        String responseString = EntityUtils.toString(resEntity);

                        System.out.println("responseString : " + responseString);
                        System.out.println("id : " + id_temp);
                        System.out.println("image url : " + imgURL);
                        System.out.println("title : " + title);
                        /*
                        Message msg = Message.obtain();
                        msg.what = 100;
                        msg.obj = responseString.trim();
                        mHandler2.sendMessage(msg);
                        */
                    }
                }
                catch (Exception e) {
                }
            }
        });
        thread.start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = new Intent(Main5Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main5Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main5Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main5Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main5Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main5Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main5Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0")){
                Intent intent = new Intent(Main5Activity.this, Main11Activity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(Main5Activity.this, "현재 로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.logout) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0"))
                Toast.makeText(Main5Activity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            else{
                editor.clear();
                editor.commit();
                Toast.makeText(Main5Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
