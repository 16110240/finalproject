package com.pam.rra.projectcataloguemovie;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pam.rra.projectcataloguemovie.Category.FavoriteActivity;
import com.pam.rra.projectcataloguemovie.Db.DatabaseContract;

import static com.pam.rra.projectcataloguemovie.Db.DatabaseContract.FavoriteColumns.CONTENT_URI;

public class DetailMovieActivity extends AppCompatActivity {

    String img, judul, desc, tgl;
    ImageView tvImg;
    FloatingActionButton fvFav;
    TextView tvJudul, tvDesc, tvTgl;
    CoordinatorLayout coordinatorLayout;

    private static final int NOTIFICATION_ID_TAMBAH = 1;
    private static final int NOTIFICATION_ID_HAPUS = 2;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        tvImg = findViewById(R.id.poster);
        tvJudul = findViewById(R.id.judul);
        tvDesc = findViewById(R.id.desc);
        tvTgl = findViewById(R.id.tgl);
        fvFav = findViewById(R.id.love);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);


        setMovie();
        setFavorite();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Movie");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setMovie(){
        img = getIntent().getStringExtra("poster_path");
        judul = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("overview");
        tgl = getIntent().getStringExtra("release_date");

        Glide.with(getApplicationContext())
                .load("http://image.tmdb.org/t/p/w185"+img)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(tvImg);
        tvJudul.setText(judul);
        tvDesc.setText(desc);
        tvTgl.setText(tgl);
        fvFav.setImageResource(R.drawable.ic_star_unchecked);
    }

    public boolean setFavorite(){
        Uri uri = Uri.parse(CONTENT_URI+"");
        boolean favorite = false;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        String getTitle;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getLong(0);
                getTitle = cursor.getString(1);
                if (getTitle.equals(getIntent().getStringExtra("title"))){
                    fvFav.setImageResource(R.drawable.ic_star_checked);
                    favorite = true;
                }
            } while (cursor.moveToNext());

        }

        return favorite;

    }

    public void favorite (View view) {
        if(setFavorite()){
            Uri uri = Uri.parse(CONTENT_URI+"/"+id);
            getContentResolver().delete(uri, null, null);
            fvFav.setImageResource(R.drawable.ic_star_unchecked);
            tampilNotifikasiHapus();

        }
        else{
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.FavoriteColumns.NAME, judul);
            values.put(DatabaseContract.FavoriteColumns.POSTER, img);
            values.put(DatabaseContract.FavoriteColumns.RELEASE_DATE, tgl);
            values.put(DatabaseContract.FavoriteColumns.DESCRIPTION, desc);

            getContentResolver().insert(CONTENT_URI, values);
            setResult(101);

            fvFav.setImageResource(R.drawable.ic_star_checked);

            tampilNotifikasiTambah();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home : {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        refreshActivity();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void tampilNotifikasiHapus() {
        // membuat komponen pending intent
        NotificationManager notificationManager = (NotificationManager) DetailMovieActivity.this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // membuat komponen
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder
                //.setChannel(NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{100, 200, 100, 200})
                .setAutoCancel(true)
                .setContentTitle("INFO NOTIFIKASI")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(DetailMovieActivity.this.getResources()
                        , R.mipmap.ic_launcher))
                .setContentText("Data Berhasil Dihapus dari Favorite")
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID_HAPUS, notification);
    }

    private void tampilNotifikasiTambah() {
        // membuat komponen pending intent
        NotificationManager notificationManager = (NotificationManager) DetailMovieActivity.this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // membuat komponen
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder
                //.setChannel(NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{100, 200, 100, 200})
                .setAutoCancel(true)
                .setContentTitle("INFO NOTIFIKASI")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(DetailMovieActivity.this.getResources()
                        , R.mipmap.ic_launcher))
                .setContentText("Data Berhasil Ditambahkan Menjadi Favorite")
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID_TAMBAH, notification);
    }

    public void refreshActivity() {
        Intent i = new Intent(this, FavoriteActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }
}