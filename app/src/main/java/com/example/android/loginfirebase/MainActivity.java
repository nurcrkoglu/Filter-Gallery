package com.example.android.loginfirebase;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCV";
    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";
    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;

    Toolbar toolbar;

    public static final int VIEW_MODE_RGBA = 0;
    public static final int VIEW_MODE_HIST = 1;
    public static final int VIEW_MODE_NEGATIVE = 2;
    public static final int VIEW_MODE_LOG = 8;
    public static final int VIEW_MODE_POWER_LAW = 9;

    private CameraBridgeViewBase mOpenCvCameraView;

    Mat rgba;
    public static int viewMode = VIEW_MODE_RGBA;

    public MainActivity() {
        Log.i(TAG, "new" + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        init();
        if (null == savedInstanceState) {
            mNavItemId = R.id.rgb;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }
        drawerLayoutSetup();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        rgba = new Mat(width, height, CvType.CV_8UC4);
    }


    public void onCameraViewStopped() {

    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        rgba = inputFrame.rgba();
        switch (MainActivity.viewMode) {
            case MainActivity.VIEW_MODE_RGBA:
                break;
            case MainActivity.VIEW_MODE_NEGATIVE:
           //     Core.copyMakeBorder(rgba,rgba,256,256,256,256,Core.BORDER_REFLECT_101);
               Imgproc.boxFilter(rgba,rgba,-1, new Size(25,25));
              //  Imgproc.Sobel(rgba,rgba,5,5,5);
             //  Imgproc.medianBlur(rgba, rgba, 15);
               // Imgproc.Canny(inputFrame.rgba(),rgba,90,90);
                break;
            case MainActivity.VIEW_MODE_LOG:
                Imgproc.cvtColor(inputFrame.rgba(), rgba, Imgproc.COLOR_BGR2HSV);
                break;
            case MainActivity.VIEW_MODE_POWER_LAW:
                Imgproc.cvtColor(inputFrame.rgba(), rgba, Imgproc.COLOR_RGB2GRAY);
                break;
        }
        return rgba;
    }

    private void init() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewMode = VIEW_MODE_RGBA;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.rgb));
    }

    private void drawerLayoutSetup() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(mNavItemId);
    }

    private void navigate(final int itemId) {
        Fragment f = null;
        switch (itemId) {
            case R.id.rgb:
                viewMode = VIEW_MODE_RGBA;
                getSupportActionBar().setTitle(getString(R.string.rgb));
                break;
            case R.id.negative:
                viewMode = VIEW_MODE_NEGATIVE;
                getSupportActionBar().setTitle(getString(R.string.negative));
                break;
            case R.id.log_transform:
                viewMode = VIEW_MODE_LOG;
                getSupportActionBar().setTitle(getString(R.string.log_transform));
                break;
            case R.id.power_law_tranform:
                viewMode = VIEW_MODE_POWER_LAW;
                getSupportActionBar().setTitle(getString(R.string.power_law_transform));
                break;
            default:
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}
