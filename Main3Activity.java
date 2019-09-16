package com.example.hyejin.hh;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.NotificationCompat;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button saveButton;
    EditText feelingEdit;
    String imgURL;
    String title;
    String author;
    String publisher;
    String price;
    ScrollView scrollC, scrollA;
    ListView listview1;

    SharedPreferences test;
    SharedPreferences.Editor editor;
    String id_temp;

    ListView listView;
    ArrayList<String> list=new ArrayList<String>();
    ImageButton addPage, subPage, rePage;
    ArrayAdapter<String> adapter;
    int pos, i, j=0, k;
    String str;
    boolean flag=true;

    public final static int MY_NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
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
        id_temp = test.getString("id_temp", "0");

        Intent intent = getIntent();



        imgURL = intent.getStringExtra("covoer_s_url");
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        publisher = intent.getStringExtra("pub_nm");
        price = intent.getStringExtra("sale_price");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView bookName = (TextView) findViewById(R.id.bookName);
        TextView bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        TextView bookPublisher = (TextView) findViewById(R.id.publisher);
        TextView bookPrice = (TextView) findViewById(R.id.price);
        saveButton = (Button) findViewById(R.id.saveButton);

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



        listView = (ListView) findViewById(R.id.listview1);
        addPage = (ImageButton) findViewById(R.id.plusPage);
        subPage = (ImageButton) findViewById(R.id.subPage);
        rePage = (ImageButton) findViewById(R.id.rePage);
        final EditText edit1=(EditText)findViewById(R.id.edit1);
        final EditText edit2=(EditText)findViewById(R.id.edit2);




        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?>parent, View v, int position, long id){
                String item=list.get(position);
                String[] sep=item.split("페이지: ");
                edit1.setText(sep[0]);
                edit2.setText(sep[1]);
            }
        });

        addPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                for(i=0; i<list.size(); i++){
                    str=list.get(i);
                    String[] sep=str.split("페이지:");
                    if(edit1.getText().toString().equals(sep[0])){
                        Toast.makeText(Main3Activity.this, "there is a page in the list", Toast.LENGTH_SHORT).show();
                        flag=false;
                    }
                }
                if(flag==true){
                    str=edit1.getText().toString()+"페이지: "+edit2.getText().toString();
                    list.add(str);
                    adapter.notifyDataSetChanged();
                    edit1.setText("");
                    edit2.setText("");
                }
            }
        });

        rePage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                pos=listView.getCheckedItemPosition();

                for(i=0; i<list.size(); i++){
                    str=list.get(i);
                    String[] sep=str.split("페이지:");
                    if(edit1.getText().toString().equals(sep[0])){
                        if(pos==i)
                            flag=true;
                        else{
                            Toast.makeText(Main3Activity.this, "there is a page in the list", Toast.LENGTH_SHORT).show();
                            flag=false;
                        }
                    }
                }
                if(flag==true){
                    list.remove(pos);
                    str=edit1.getText().toString()+"페이지: "+edit2.getText().toString();
                    list.add(pos, str);
                    adapter.notifyDataSetChanged();
                    edit1.setText("");
                    edit2.setText("");
                }
            }
        });

        subPage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                pos=listView.getCheckedItemPosition();
                list.remove(pos);
                adapter.notifyDataSetChanged();
                listView.clearChoices();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Main3Activity.this);
                mBuilder.setSmallIcon(R.drawable.ic_alarm_on_black_24dp);
                mBuilder.setContentTitle(getResources().getString(R.string.notif_title));
                mBuilder.setContentText(getResources().getString(R.string.notif_body));

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // MY_NOTIFICATION_ID allows you to update the notification later on.
                mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());

                for(k=0; k<list.size(); k++) {
                    String str_temp = list.get(k).toString();
                    String[] sep = str_temp.split("페이지:");

                    idPresentCheckServer(sep);
                }

                Intent intent = new Intent(Main3Activity.this, Main4Activity.class);
                startActivity(intent);

            }
        });

        scrollC=(ScrollView)findViewById(R.id.scrollC);
        scrollA=(ScrollView)findViewById(R.id.scrollA);
        listview1=(ListView)findViewById(R.id.listview1) ;


        scrollC.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollA.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listview1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollA.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void idPresentCheckServer(final String[] temp){
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/xml3check.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id_temp));
                    params.add(new BasicNameValuePair("image", imgURL));
                    params.add(new BasicNameValuePair("title", title));
                    params.add(new BasicNameValuePair("pagenum", temp[0]));
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
                        msg.obj = responseString.trim() + "A:" +temp[0] + "B:" + temp[1];
                        mHandler.sendMessage(msg);
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
            if(msg.what == 100) {
                System.out.println(msg.obj);
                String str_temp = (String)msg.obj;
                String[] sep = str_temp.split("A:");
                str_temp = sep[1];
                String[] sep2 = str_temp.split("B:");

                try {
                    //오류 시작
                    JSONObject jbj = new JSONObject((String)msg.obj);
                    JSONArray jr = jbj.getJSONArray("result");

                    if(jr.length()==1) {
                        JSONObject jsonObject = jr.getJSONObject(0);

                        if (jsonObject.getString("id") != null) {
                            Toast.makeText(Main3Activity.this, "" + jsonObject.getString("id"), Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        inputMyDiaryServer(sep2);
                        System.out.println("input sep2 : " + sep2[0] + " / " + sep2[1]);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    };

    private void inputMyDiaryServer(final String[] temp2){
        feelingEdit = (EditText) findViewById(R.id.feelingEdit);
        System.out.println("temp2 : " + temp2[0] + " / " + temp2[1]);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/xml3.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id_temp));
                    params.add(new BasicNameValuePair("image", imgURL));
                    params.add(new BasicNameValuePair("title", title));
                    params.add(new BasicNameValuePair("author", author));
                    params.add(new BasicNameValuePair("publisher", publisher));
                    params.add(new BasicNameValuePair("price", price));
                    params.add(new BasicNameValuePair("pagenum", temp2[0]));
                    params.add(new BasicNameValuePair("pagecontent", temp2[1]));
                    params.add(new BasicNameValuePair("content", feelingEdit.getText().toString()));
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
                j++;
                System.out.println("j : " + j);
                System.out.println("list size : " + list.size());
                if(j==list.size()){
                    j=0;
                    if (((String)msg.obj).equals("\uFEFFsuccess")) {
                        Toast.makeText(Main3Activity.this, "완료", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Main3Activity.this, Main4Activity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                        Toast.makeText(Main3Activity.this, "WRONG", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.main3, menu);
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
            Intent intent = new Intent(Main3Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main3Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main3Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main3Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main3Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main3Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main3Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0")){
                Intent intent = new Intent(Main3Activity.this, Main11Activity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(Main3Activity.this, "현재 로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.logout) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0"))
                Toast.makeText(Main3Activity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            else{
                editor.clear();
                editor.commit();
                Toast.makeText(Main3Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
