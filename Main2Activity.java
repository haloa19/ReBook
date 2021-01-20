package com.example.hyejin.hh;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button newButton, showListButton;
    String imgURL;
    String title;
    String author;
    String description;
    String publisher;
    String price;

    SharedPreferences test;
    SharedPreferences.Editor editor;
    String id_temp;

    private static final int DIALOG_YES_NO_MESSAGE = 1;
    int i = 0;
    int j = 0;

    ListView listView2;
    Dialog dialog;
    TextView contentText;   //다이얼로그 후기내용

    private ArrayList<Item> m_arr;
    private ListAdapter adapter;

    String[] gradeStrings = {
            "평점",
            "1", "2", "3", "4", "5", "4", "3", "2", "1", "0"

    };
    String[] contentString = {
            "후기",
            "라바는 최고입니다.", "라바 너무너무재미있어요.", "라바 짱짱맨", "라바라바라바", "아쓸말이너무없다.",
            "라바얘기만잔뜩이넹...", "라바책을 추천합니다.", "남주언니는 숙대에갔습니다.돌아와요", "우와 이제끝이보인다.", "마지막글입니다."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
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

        imgURL = intent.getStringExtra("covoer_s_url");
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        description = intent.getStringExtra("description");
        publisher = intent.getStringExtra("pub_nm");
        price = intent.getStringExtra("sale_price");

        newButton = (Button) findViewById(R.id.newButton);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_temp = test.getString("id_temp", "0");

                if(id_temp.equals("0"))
                    Toast.makeText(Main2Activity.this, "로그인이 되어있지 않습니다.\n로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                else{
                    Intent intent = new Intent(Main2Activity.this, Main3Activity.class);

                    intent.putExtra("covoer_s_url", imgURL);
                    intent.putExtra("title", title);
                    intent.putExtra("author", author);
                    intent.putExtra("pub_nm", publisher);
                    intent.putExtra("sale_price", price);

                    startActivityForResult(intent, 200);
                }
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView bookName = (TextView) findViewById(R.id.bookName);
        TextView bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        TextView bookPublisher = (TextView) findViewById(R.id.publisher);
        TextView bookPrice = (TextView) findViewById(R.id.price);
        TextView bookInfoText = (TextView) findViewById(R.id.bookinfoText);
        newButton = (Button) findViewById(R.id.newButton);
        showListButton = (Button) findViewById(R.id.showListButton);
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
        bookInfoText.setText(description);

        readServer();

        newButton = (Button) findViewById(R.id.newButton);
        listView2 = (ListView) findViewById(R.id.listView2);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                j= position;
                showDialog(DIALOG_YES_NO_MESSAGE);

                dialog = new Dialog(Main2Activity.this);
                dialog.setContentView(R.layout.book_info_dialog);
                dialog.setTitle("후기");

                contentText = (TextView)dialog.findViewById(R.id.contentText);
                Item temp = m_arr.get(position);
                contentText.setText(temp.contentTexts);

                dialog.show();
            }
        });
    }

    public class Item {
        public String gradeTexts;
        public String contentTexts;

        public Item(String gradeTexts, String contentTexts) {
            this.gradeTexts = gradeTexts;
            this.contentTexts = contentTexts;
        }
    }

    public class ListAdapter extends BaseAdapter {
        private Activity m_activity;
        private LayoutInflater mInflater;
        private ArrayList<Item> arr;

        public ListAdapter(Activity act, ArrayList<Item> arr_item) {
            this.m_activity = act;
            arr = arr_item;
            mInflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return arr.size();
        }

        public Object getItem(int position) {
            return arr.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            i = position;
            View rowView = mInflater.inflate(R.layout.book_info_list, null, true);
            if (convertView == null) {
                int res = 0;
            }

            TextView gradeText=(TextView)rowView.findViewById(R.id.gradeText);
            TextView contentText=(TextView)rowView.findViewById(R.id.content);
            Item temp = m_arr.get(position);

            gradeText.setText(temp.gradeTexts);
            contentText.setText(temp.contentTexts);

            return rowView;
        }
    }


    private void readServer(){
        System.out.println("image : " + imgURL);
        System.out.println("title : " + title);
        System.out.println("author : " + author);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub

                try {
                    String postURL = "http://pj9039.ipdisk.co.kr:8080/namju/xml2.php";
                    HttpPost post = new HttpPost(postURL);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("image", imgURL));
                    params.add(new BasicNameValuePair("title", title));
                    params.add(new BasicNameValuePair("author", author));
                    post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //Get 방식의 요청
                    try{
                        HttpResponse responseGet = new DefaultHttpClient().execute(post);
                        HttpEntity resEntity = responseGet.getEntity();
                        if(resEntity!=null)
                        {
                            // 요청에 대한 결과 값
                            String responseString = EntityUtils.toString(resEntity);

                            System.out.println("response : " + responseString);
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
                    m_arr = new ArrayList<Item>();

                    for(int i=0; i<jr.length(); i++)
                    {
                        JSONObject jsonObject = jr.getJSONObject(i);

                        System.out.println(jsonObject.getString("rating"));
                        System.out.println(jsonObject.getString("content"));

                        m_arr.add(new Item(jsonObject.getString("rating"), jsonObject.getString("content")));
                    }

                    adapter = new ListAdapter(Main2Activity.this, m_arr);
                    listView2.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.main2, menu);
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
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main2Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main2Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main2Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main2Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main2Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main2Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0")){
                Intent intent = new Intent(Main2Activity.this, Main11Activity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(Main2Activity.this, "현재 로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.logout) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0"))
                Toast.makeText(Main2Activity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            else{
                editor.clear();
                editor.commit();
                Toast.makeText(Main2Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
