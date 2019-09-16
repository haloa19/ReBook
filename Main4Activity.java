package com.example.hyejin.hh;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main4Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    ArrayList<ListItem> item;
    ArrayList<ListItem> things = new ArrayList<ListItem>();
    ListItem temp;

    SharedPreferences test;
    SharedPreferences.Editor editor;
    String id_temp;

    String imgURL;
    String title;
    String author;
    String publisher;
    String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main4Activity.this, MainActivity.class);
                startActivity(intent);
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

        listView = (ListView) findViewById(R.id.listView);

        readServer();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Main4Activity.this, Main5Activity.class);
                intent.putExtra("image", imgURL);
                intent.putExtra("title", title);
                intent.putExtra("author", author);
                intent.putExtra("publisher", publisher);
                intent.putExtra("price", price);
                startActivity(intent);
                finish();
            }
        });
    }

    public class CustomList extends ArrayAdapter<ListItem> {

        public CustomList(Context context, int textViewResourceId, ArrayList<ListItem> items){
            super(context, textViewResourceId, items);
            item = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v==null){
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.book_info, null);
            }
            final ListItem p = item.get(position);

            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            TextView bookName = (TextView) v.findViewById(R.id.bookName);
            TextView bookAuthor = (TextView) v.findViewById(R.id.bookAuthor);
            TextView prices = (TextView)v.findViewById(R.id.price);
            TextView publishers = (TextView)v.findViewById(R.id.publisher);
            final Bitmap[] bitmap = {null};

            // 네트워크를 통하여 데이터를 받아올때는 '반드시' 쓰레드를 사용하여야 한다고함
            Thread mThread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL url = new URL(p.getImgURL());

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
            bookName.setText("책 제목: "+ p.getD_titl());
            bookAuthor.setText("저자: "+ p.getD_auth());
            prices.setText("가격: "+ p.getPrice());
            publishers.setText("출판사:" + p.getPublisher());

            return v;
        }
    }

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
        getMenuInflater().inflate(R.menu.main4, menu);
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
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/xml4.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id", id_temp));
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
                    things.clear();

                    JSONObject jbj = new JSONObject((String)msg.obj);
                    JSONArray jr = jbj.getJSONArray("result");

                    for(int i=0; i<jr.length(); i++)
                    {
                        temp = new ListItem();
                        JSONObject jsonObject = jr.getJSONObject(i);

                        imgURL = jsonObject.getString("image");
                        title = jsonObject.getString("title");
                        author = jsonObject.getString("author");
                        publisher = jsonObject.getString("publisher");
                        price = jsonObject.getString("price");

                        temp.setImgURL(imgURL);
                        temp.setD_titl(title);
                        temp.setD_auth(author);
                        temp.setPublisher(publisher);
                        temp.setPrice(price);

                        things.add(temp);

                        System.out.println(jr.length());
                        System.out.println("i : " + i);
                        System.out.println(jsonObject.getString("image"));
                        System.out.println(jsonObject.getString("title"));
                        System.out.println(jsonObject.getString("author"));
                        System.out.println(jsonObject.getString("publisher"));
                        System.out.println(jsonObject.getString("price"));


                    }
                    CustomList adapter = new CustomList(Main4Activity.this, R.layout.book_info, things);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = new Intent(Main4Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main4Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main4Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main4Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main4Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main4Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main4Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            id_temp = test.getString("id_temp", "0");
            if(id_temp.equals("0")){
                Intent intent = new Intent(Main4Activity.this, Main11Activity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(Main4Activity.this, "현재 로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.logout) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0"))
                Toast.makeText(Main4Activity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            else{
                editor.clear();
                editor.commit();
                Toast.makeText(Main4Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
