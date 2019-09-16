package com.example.hyejin.hh;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main7Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int DIALOG_YES_NO_MESSAGE = 1;
    int i = 0;
    int j = 0;

    ListView listView;
    Button newButton;
    Dialog dialog;

    SharedPreferences test;
    SharedPreferences.Editor editor;
    String id_temp;

    ImageView imageView;
    TextView bookNameText;
    TextView bookAuthorText;
    TextView publisherText;
    TextView priceText;
    TextView dcontent;
    TextView dlikenum;

    String imgURL_;
    String title_;
    String content_;

    int likeCheck;

    private ArrayList<Item> m_arr;
    private List_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main7Activity.this, Main8Activity.class);
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
        newButton = (Button) findViewById(R.id.newButton);

        m_arr = new ArrayList<Item>();

        readServer();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                j = position;
                showDialog(DIALOG_YES_NO_MESSAGE);
                String str;

                dialog = new Dialog(Main7Activity.this);
                dialog.setContentView(R.layout.bb_dialog);
                dialog.setTitle("작성글 확인");

                imgURL_ = m_arr.get(j).image;
                title_ = m_arr.get(j).bnameTexts;
                content_ = m_arr.get(j).content;

                imageView=(ImageView)dialog.findViewById(R.id.imageView);
                dlikenum=(TextView)dialog.findViewById(R.id.dlikenum);
                dlikenum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeCheck = Integer.parseInt(m_arr.get(j).likeit);
                        likeCheck++;
                        m_arr.get(j).likeit = String.valueOf(likeCheck);

                        dlikenum.setText(String.valueOf(likeCheck));
                    }
                });

                final Bitmap[] bitmap = {null};

                // 네트워크를 통하여 데이터를 받아올때는 '반드시' 쓰레드를 사용하여야 한다고함
                Thread mThread = new Thread(){
                    @Override
                    public void run(){
                        try {
                            URL url = new URL(m_arr.get(j).image);

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

                bookNameText = (TextView)dialog.findViewById(R.id.bookNameText);
                if(m_arr.get(position).bnameTexts.length()>13)
                    str = m_arr.get(position).bnameTexts.substring(0, 11) + "...";
                else
                    str = m_arr.get(position).bnameTexts;
                bookNameText.setText(str);

                bookAuthorText = (TextView)dialog.findViewById(R.id.bookAuthorText);
                if(m_arr.get(position).author.length()>13)
                    str = m_arr.get(position).author.substring(0, 11) + "...";
                else
                    str = m_arr.get(position).author;
                bookAuthorText.setText(str);

                publisherText = (TextView)dialog.findViewById(R.id.publisherText);
                publisherText.setText(m_arr.get(position).publisher);

                priceText = (TextView)dialog.findViewById(R.id.priceText);
                priceText.setText(m_arr.get(position).price);

                dcontent = (TextView)dialog.findViewById(R.id.dcontent);
                dcontent.setText("내용 : " + m_arr.get(position).content);

                dlikenum.setText(m_arr.get(position).likeit);
                dialog.show();

                TextView ok = (TextView)dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        likeAddServer(m_arr.get(j).numTexts, m_arr.get(j).dateTexts);
                    }
                });
            }
        });

    }

    private void readServer(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/allbbinfo.php";
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
            if(msg.what == 100)
            {
                try {
                    //오류 시작
                    JSONObject jbj = new JSONObject((String)msg.obj);
                    JSONArray jr = jbj.getJSONArray("result");

                    for(int i=0; i<jr.length(); i++) {
                        JSONObject jsonObject = jr.getJSONObject(i);

                        System.out.println("num : " + jsonObject.getString("num"));
                        System.out.println("title : " + jsonObject.getString("title"));
                        System.out.println("rating : " + jsonObject.getString("rating"));
                        System.out.println("author : " + jsonObject.getString("author"));
                        System.out.println("price : " + jsonObject.getString("price"));
                        System.out.println("likeit : " + jsonObject.getString("likeit"));

                       m_arr.add(new Item(jsonObject.getString("num"), jsonObject.getString("title"), jsonObject.getString("rating"),
                               jsonObject.getString("author"), jsonObject.getString("bdate"), jsonObject.getString("image"), jsonObject.getString("author"), jsonObject.getString("publisher"),
                               jsonObject.getString("price"), jsonObject.getString("content"), jsonObject.getString("likeit")));

                        if((jr.length()-1)==m_arr.size()){
                            adapter = new List_Adapter(Main7Activity.this, m_arr);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    };


    public class Item {
        public String numTexts;
        public String bnameTexts;
        public String ratingTexts;
        public String whoTexts;
        public String dateTexts;

        public String image;
        public String title;
        public String author;
        public String publisher;
        public String price;
        public String content;
        public String likeit;

        public Item(String numTexts, String bnameTexts, String ratingTexts, String whoTexts, String dateTexts, String image, String author, String publisher, String price, String content, String likeit) {
            this.numTexts = numTexts;
            this.bnameTexts = bnameTexts;
            this.ratingTexts = ratingTexts;
            this.whoTexts = whoTexts;
            this.dateTexts = dateTexts;
            this.image = image;
            this.author = author;
            this.publisher = publisher;
            this.price = price;
            this.content = content;
            this.likeit = likeit;
        }
    }

    public class List_Adapter extends BaseAdapter {
        private Activity m_activity;
        private LayoutInflater mInflater;
        private ArrayList<Item> arr;

        public List_Adapter(Activity act, ArrayList<Item> arr_item) {
            this.m_activity = act;
            arr = arr_item;
            mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public Object getItem(int position) {
            return arr.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            i = position;
            View rowView = mInflater.inflate(R.layout.bb_info, null, true);

            if (convertView == null) {
                int res = 0;
            }


            TextView bnameText = (TextView) rowView.findViewById(R.id.bnameText);
            TextView ratingText = (TextView) rowView.findViewById(R.id.ratingText);
            TextView whoText = (TextView) rowView.findViewById(R.id.whoText);

            bnameText.setText("제목 : " + m_arr.get(i).bnameTexts);
            ratingText.setText("평점 : " + m_arr.get(i).ratingTexts);
            whoText.setText("저자 : "  + m_arr.get(i).whoTexts);

            return rowView;
        }
    }

    private void likeAddServer(final String num_temp, final String bdate_temp){
        id_temp = test.getString("id_temp", "0");

        System.out.println(num_temp);
        System.out.println(id_temp);
        System.out.println(imgURL_);
        System.out.println(title_);
        System.out.println(bdate_temp);
        System.out.println(likeCheck);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/likeit.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("num", num_temp));
                    params.add(new BasicNameValuePair("id", id_temp));
                    params.add(new BasicNameValuePair("image", imgURL_));
                    params.add(new BasicNameValuePair("title", title_));
                    params.add(new BasicNameValuePair("bdate", bdate_temp));
                    //params.add(new BasicNameValuePair("content", content_));
                    params.add(new BasicNameValuePair("likeit", String.valueOf(likeCheck)));
                    post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //Get 방식의 요청
                    try{
                        HttpResponse responseGet = new DefaultHttpClient().execute(post);
                        HttpEntity resEntity = responseGet.getEntity();
                        if(resEntity!=null)
                        {
                            // 요청에 대한 결과 값
                            String responseString = EntityUtils.toString(resEntity);

                            System.out.println("responseString : " + responseString);

                            /*
                            Message msg = Message.obtain();
                            msg.what=100;
                            msg.obj=responseString.trim();

                            mHandler.sendMessage(msg);
                            */
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
        getMenuInflater().inflate(R.menu.main7, menu);
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
            Intent intent = new Intent(Main7Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main7Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main7Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main7Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main7Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main7Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main7Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0")){
                Intent intent = new Intent(Main7Activity.this, Main11Activity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(Main7Activity.this, "현재 로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.logout) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0"))
                Toast.makeText(Main7Activity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            else{
                editor.clear();
                editor.commit();
                Toast.makeText(Main7Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
