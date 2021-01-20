package com.example.hyejin.hh;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Main13Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText searchEdit;
    ImageButton searchButton;
    ListView listView;

    ArrayList<ListItem> listItems;
    CustomList adapter;
    String[] bookNames = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main13);
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

        searchEdit = (EditText) findViewById(R.id.searchEdit);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new CustomList(Main13Activity.this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 로그인이 안되어있으면 Toast 메세지로 "핸드폰의 메뉴버튼을 눌러서 회원가입을 누른 뒤, 로그인해주세요"
                Intent intent = new Intent(Main13Activity.this, Main8Activity.class);
                startActivity(intent);
            }
        });

        // 검색 버튼을 눌렀을 때
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyWord = searchEdit.getText().toString();
                //한글이 깨지지 않게 하기 위해
                String encodedK = null;
                try {
                    encodedK = URLEncoder.encode(keyWord, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                StringBuffer buffer = new StringBuffer();
                buffer.append("https://apis.daum.net/search/book?");
                //한글일 경우 인코딩 필요!(영어로 가정한다)
                buffer.append("apikey=e05fd88c1cf79a5be21c603212e143bb");
                buffer.append("&q=" + encodedK);
                buffer.append("&output=json");

                String url = buffer.toString();
                //스레드 객체를 생성해서 다운로드 받는다.
                GetJSONThread thread = new GetJSONThread(handler, null, url);
                thread.start();
            }
        });
    }


    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;

        public CustomList(Activity context) {
            super(context, R.layout.book_info, bookNames);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.book_info, null, true);

            final Bitmap[] bitmap = {null};
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            TextView bookName = (TextView) rowView.findViewById(R.id.bookName);
            TextView bookAuthor = (TextView) rowView.findViewById(R.id.bookAuthor);
            TextView bookPublisher = (TextView) rowView.findViewById(R.id.publisher);
            TextView bookPrice = (TextView) rowView.findViewById(R.id.price);

            final ListItem list = listItems.get(position);

            // 네트워크를 통하여 데이터를 받아올때는 '반드시' 쓰레드를 사용하여야 한다고함
            Thread mThread = new Thread(){
                @Override
                public void run(){
                    try {
                        URL url = new URL(list.getImgURL());

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

            bookName.setText("책 제목 : " + list.getD_titl());
            bookAuthor.setText("저자 : " + list.getD_auth());
            bookPublisher.setText("출판사 : " + list.getPublisher());
            bookPrice.setText("가격 : " + list.getPrice());

            return rowView;
        }
    }

    //핸들러
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0: //success
                    //json문자열을 ㅡ읽어오기
                    String jsonStr = (String) msg.obj;
                    listItems = new ArrayList<ListItem>();
                    try {
                        listItems.clear();
                        //문자열을 json 객체로 변환
                        //1. channel이라는 키값으로 {} jsonObject가 들어있다)
                        //2. jsonObject안에는 item이라는 키값으로 [] jsonArray 벨류값을 가지고 있다.
                        JSONObject jsonObj = new JSONObject(jsonStr);
//1.
                        JSONObject channel = jsonObj.getJSONObject("channel");
//2.
                        JSONArray items = channel.getJSONArray("item");
//3.반복문 돌면서 필요한 정보만 얻어온다.
                        for (int i = 0; i < items.length(); i++) {
//4. 검색결과 값을 얻어온다.
                            JSONObject tmp = items.getJSONObject(i);
                            ListItem listitem = new ListItem();

                            listitem.setImgURL(tmp.getString("cover_s_url"));
                            listitem.setD_titl(tmp.getString("title").replaceAll("&lt;b&gt;", "").replaceAll("&lt;/b&gt;", ""));
                            listitem.setD_auth(tmp.getString("author"));
                            listitem.setDescription(tmp.getString("description"));
                            listitem.setPublisher(tmp.getString("pub_nm"));
                            listitem.setPrice(tmp.getString("sale_price"));

                            listItems.add(listitem);
                        }
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent_ = new Intent();
                                intent_.putExtra("covoer_s_url", listItems.get(position).getImgURL());
                                intent_.putExtra("title", listItems.get(position).getD_titl().replaceAll("&lt;", "<").replaceAll("&gt;", ">"));
                                intent_.putExtra("author", listItems.get(position).getD_auth());
                                intent_.putExtra("description", listItems.get(position).getDescription());
                                intent_.putExtra("pub_nm", listItems.get(position).getPublisher());
                                intent_.putExtra("sale_price", listItems.get(position).getPrice());

                                setResult(RESULT_OK, intent_);
                                finish();
                            }
                        });
                        //모델의 데이터가 바뀌었다고 아답타 객체에 알린다.
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                    break;
                case 1: //fail
                    break;
            }
        }
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
        getMenuInflater().inflate(R.menu.main13, menu);
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
            Intent intent = new Intent(Main13Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main13Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main13Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main13Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main13Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main13Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main13Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            Intent intent = new Intent(Main13Activity.this, Main11Activity.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            Intent intent = new Intent(Main13Activity.this, Main13Activity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
