package com.smrahmadi.viraluncher.activitys;

import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Fade;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smrahmadi.viraluncher.R;
import com.smrahmadi.viraluncher.adapters.ApplicationAdapter;
import com.smrahmadi.viraluncher.models.Application;
import com.smrahmadi.viraluncher.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.smrahmadi.easydialog.EasyDialog;

public class Apps extends AppCompatActivity {

    @BindView(R.id.lin_apps) protected LinearLayout lin_apps ;
    @BindView(R.id.apps_Recycler) protected RecyclerView apps_Recycler;
    @BindView(R.id.apps_Search) protected EditText apps_Search;

     private ApplicationAdapter adapter ;
     private ArrayList<Application> apps = new ArrayList<>();
     private PackageManager manager;

     private String deletePackage ;
     private int deleteItem ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupWindowAnimations();

        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(this);

        Drawable myWallpaper = myWallpaperManager.getDrawable() ;

        Bitmap blurredBitmap = ((BitmapDrawable)myWallpaper).getBitmap();

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            lin_apps.setBackgroundDrawable(new BitmapDrawable(getResources(),blurredBitmap));
        } else {
            lin_apps.setBackground(new BitmapDrawable(getResources(),blurredBitmap));
        }


        getApps();

        int numberOfColumns = 4 ;
        apps_Recycler.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


        apps_Recycler.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), apps_Recycler, new ApplicationAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                StartApp(position);
            }

            @Override
            public void onLongClick(View view, final int position) {

                EasyDialog easyDialog = new EasyDialog();

                if(!apps.get(position).isSysytem()) {

                    easyDialog.threeItem(Apps.this,
                            "Choose options",
                            null,
                            0,
                            "App info",
                            "Uninstall",
                            "Toast Info",
                            new EasyDialog.showClickThreeItem() {
                                @Override
                                public void firstItem() {

                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + apps.get(position).getPackageName()));
                                    startActivity(intent);
                                }

                                @Override
                                public void secondItem() {

                                    Intent intent = new Intent(Intent.ACTION_DELETE);
                                    intent.setData(Uri.parse("package:" + apps.get(position).getPackageName()));
                                    deletePackage = apps.get(position).getPackageName();
                                    deleteItem = position;
                                    startActivityForResult(intent, 1);

                                }

                                @Override
                                public void thirdItem() {

                                    toastAppInfo(position);
                                }


                            });

                }else{
                    easyDialog.twoItem(
                            Apps.this,
                            "Choose options",
                            null,
                            0,
                            "App info",
                            "Toast Info",
                            new EasyDialog.showClickTwoItem() {
                                @Override
                                public void firstItem() {

                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + apps.get(position).getPackageName()));
                                    startActivity(intent);

                                }

                                @Override
                                public void secondItem() {
                                    toastAppInfo(position);
                                }
                            }


                    );
                }


            }
        }));



        apps_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                getApps();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
            PackageManager pm = getApplicationContext().getPackageManager();
            if(!isPackageInstalled(deletePackage,pm)) {
                apps.remove(deleteItem);
                adapter.notifyDataSetChanged();
            }

        }
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void getApps()  {

        manager = getPackageManager();
        apps = new ArrayList<Application>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities) {


            if (getApplicationContext().getPackageName().equals(ri.activityInfo.packageName))
                continue;


            Application app = new Application();

            app.setName(ri.loadLabel(manager).toString());

            app.setPackageName(ri.activityInfo.packageName);

            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(ri.activityInfo.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            app.setVersion(packageInfo.versionName);

            app.setIcon(ri.activityInfo.loadIcon(manager));

            ApplicationInfo ai = null;
            try {
                 ai = manager.getApplicationInfo(ri.activityInfo.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            app.setSysytem(false);
            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                app.setSysytem(true);
            }






            if (!TextUtils.isEmpty(apps_Search.getText().toString()) && ri.loadLabel(manager).toString().toLowerCase().contains(apps_Search.getText().toString().toLowerCase())) {
                apps.add(app);
            }else if (TextUtils.isEmpty(apps_Search.getText().toString())){
                apps.add(app);
            }

        }

        adapter  = new ApplicationAdapter(apps) ;
        apps_Recycler.setAdapter(adapter);

        if(apps.size()==0){
            apps_Recycler.setAdapter(null);
        }

        }

    private void toastAppInfo(int position){

        String yesNo = "No" ;

        if(apps.get(position).isSysytem())
            yesNo="Yes";

        String appInfo = "Name : " + apps.get(position).getName() +
                "\n Version : " + apps.get(position).getVersion() +
                "\n Package : " + apps.get(position).getPackageName() +
                "\n Is System App : "+yesNo;

        Toast.makeText(Apps.this, appInfo, Toast.LENGTH_LONG).show();
    }


//    public void searchAndGetApps(String s) {
//
//
//
//        manager = getPackageManager();
//        apps = new ArrayList<Application>();
//
//        Intent i = new Intent(Intent.ACTION_MAIN, null);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
//        for(ResolveInfo ri:availableActivities){
//
//
//
//            if (getApplicationContext().getPackageName().equals(ri.activityInfo.packageName))
//                continue;
//
//            if(!ri.loadLabel(manager).toString().toLowerCase().contains(s.toLowerCase())) {
//                continue;
//            }
//
//
//            Application app = new Application();
//            app.setName(ri.loadLabel(manager).toString());
//            app.setPackageName(ri.activityInfo.packageName);
//            app.setIcon(ri.activityInfo.loadIcon(manager));
//            apps.add(app);
//
//        }
//
//        adapter  = new ApplicationAdapter(apps) ;
//        apps_Recycler.setAdapter(adapter);
//
//        if(apps.size()==0){
//            apps_Recycler.setAdapter(null);
//        }
//    }
//
//    public void getApps(){
//
//        manager = getPackageManager();
//        apps = new ArrayList<Application>();
//
//        Intent i = new Intent(Intent.ACTION_MAIN, null);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
//        for(ResolveInfo ri:availableActivities){
//
//            if(getApplicationContext().getPackageName().equals(ri.activityInfo.packageName))
//                continue;
//
//
//            Application app = new Application();
//            app.setName( ri.loadLabel(manager).toString());
//            app.setPackageName(ri.activityInfo.packageName);
//            app.setIcon(ri.activityInfo.loadIcon(manager));
//            apps.add(app);
//
//        }
//
//        adapter  = new ApplicationAdapter(apps) ;
//        apps_Recycler.setAdapter(adapter);
//
//    }

    private void StartApp(int p){

        PackageManager pg = getPackageManager();
        Intent i = pg.getLaunchIntentForPackage(apps.get(p).getPackageName());
        startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
        getWindow().setReturnTransition(fade);
    }
}
