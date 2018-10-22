package com.example.android.loginfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;


    public static int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        setNavigationViewListner();

      //  getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void init() {
        drawerLayout = (DrawerLayout) findViewById(R.id.profileDrawer);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                id = 0;
                break;
            case R.id.camera:
                id = 1;
                Intent camera = new Intent(this, MainActivity.class);
                startActivity(camera);
                return true;
            case R.id.logout:
                id=2;
                Intent logout=new Intent(this,SignOutActivity.class);
                startActivity(logout);
                return true;
            case R.id.gallery:
                id=3;
                Intent openGallery=new Intent(this,OpenGalleryActivity.class);
                startActivity(openGallery);
                return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
