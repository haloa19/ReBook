package com.example.hyejin.hh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main6Activity extends AppCompatActivity
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
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
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
        imgURL = intent.getStringExtra("image");
        title = intent.getStringExtra("title");
        author = intent.getStringExtra("author");
        publisher = intent.getStringExtra("publisher");
        price = intent.getStringExtra("price");
        pagenum = intent.getStringExtra("pagenum");
        pagecontent = intent.getStringExtra("pagecontent");
        content = intent.getStringExtra("content");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView bookName = (TextView) findViewById(R.id.bookName);
        TextView bookAuthor = (TextView) findViewById(R.id.bookAuthor);
        TextView bookPublisher = (TextView) findViewById(R.id.publisher);
        TextView bookPrice = (TextView) findViewById(R.id.price);
        TextView edit1 = (TextView) findViewById(R.id.edit1);
        TextView edit2 = (TextView) findViewById(R.id.edit2);
        EditText feelingText = (EditText) findViewById(R.id.feelingEdit);

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
        edit1.setText(pagenum);
        edit2.setText(pagecontent);
        feelingText.setText(content);

        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            TextView edit1 = (TextView) findViewById(R.id.edit1);
            TextView edit2 = (TextView) findViewById(R.id.edit2);
            EditText feelingText = (EditText) findViewById(R.id.feelingEdit);
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                pagenum = edit1.getText().toString();
                pagecontent = edit2.getText().toString();
                content = feelingText.getText().toString();
                intent.putExtra("image", imgURL);
                intent.putExtra("title", title);
                intent.putExtra("author", author);
                intent.putExtra("publisher", publisher);
                intent.putExtra("price", price);
                intent.putExtra("pagenum", pagenum);
                intent.putExtra("pagecontent", pagecontent);
                intent.putExtra("content", content);
                setResult(300, intent);
                finish();
            }
        });
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
        getMenuInflater().inflate(R.menu.main6, menu);
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
            Intent intent = new Intent(Main6Activity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.diary) {
            Intent intent = new Intent(Main6Activity.this, Main4Activity.class);
            startActivity(intent);

        } else if (id == R.id.recommend) {
            Intent intent = new Intent(Main6Activity.this, Main10Activity.class);
            startActivity(intent);

        }else if (id == R.id.bulletin) {
            Intent intent = new Intent(Main6Activity.this, Main7Activity.class);
            startActivity(intent);

        } else if (id == R.id.qrcode) {
            Intent intent = new Intent(Main6Activity.this, Main9Activity.class);
            startActivity(intent);

        }else if (id == R.id.barcode){
            Intent intent = new Intent(Main6Activity.this, Main14Activity.class);
            startActivity(intent);

        } else if (id == R.id.join) {
            Intent intent = new Intent(Main6Activity.this, Main12Activity.class);
            startActivity(intent);

        } else if (id == R.id.login) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0")){
                Intent intent = new Intent(Main6Activity.this, Main11Activity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(Main6Activity.this, "현재 로그인 상태입니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.logout) {
            id_temp = test.getString("id_temp", "0");

            if(id_temp.equals("0"))
                Toast.makeText(Main6Activity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            else{
                editor.clear();
                editor.commit();
                Toast.makeText(Main6Activity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
