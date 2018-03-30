package com.smrahmadi.viraluncher.activitys;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.smrahmadi.viraluncher.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.lin_main) protected LinearLayout linMain ;
    @BindView(R.id.lin_widget) protected ViewGroup lin_widget ;


    @BindView(R.id.imgCall) protected ImageView imgCall ;
    @BindView(R.id.imgSms) protected ImageView imgSms ;
    @BindView(R.id.imgMenu) protected ImageView imgMenu ;
    @BindView(R.id.imgContacts) protected ImageView imgContacts ;
    @BindView(R.id.imgBrowser) protected ImageView imgBrowser ;

    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        WallpaperManager myWallpaperManager = WallpaperManager.getInstance(this);

        Drawable myWallpaper = myWallpaperManager.getDrawable() ;

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linMain.setBackgroundDrawable(myWallpaper);
        } else {
            linMain.setBackground(myWallpaper);
        }


        imgCall.setOnClickListener(this);
        imgSms.setOnClickListener(this);
        imgMenu.setOnClickListener(this);
        imgContacts.setOnClickListener(this);
        imgBrowser.setOnClickListener(this);


        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, 0);






    }


    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, 2);
    }

    /**
     * This avoids a bug in the com.android.settings.AppWidgetPickActivity,
     * which is used to select widgets. This just adds empty extras to the
     * intent, avoiding the bug.
     *
     * See more: http://code.google.com/p/android/issues/detail?id=4272
     */
    void addEmptyData(Intent pickIntent) {
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    /**
     * If the user has selected an widget, the result will be in the 'data' when
     * this function is called.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                configureWidget(data);
            } else if (requestCode == 1) {
                createWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, 1);
        } else {
            createWidget(data);
        }
    }

    /**
     * Creates the widget and adds to our view layout.
     */
    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        lin_widget.addView(hostView);

    }

    /**
     * Registers the AppWidgetHost to listen for updates to any widgets this app
     * has.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    /**
     * Stop listen for updates for our widgets (saving battery).
     */
    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    /**
     * Removes the widget displayed by this AppWidgetHostView.
     */
    public void removeWidget(AppWidgetHostView hostView) {
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        lin_widget.removeView(hostView);
    }


    /**
     * Handle the 'Remove Widget' menu.
     */
    public void removeWidgetMenuSelected() {
        int childCount = lin_widget.getChildCount();
        if (childCount > 1) {
            View view = lin_widget.getChildAt(childCount - 1);
            if (view instanceof AppWidgetHostView) {
                removeWidget((AppWidgetHostView) view);
                Toast.makeText(this, R.string.widget_removed_popup, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, R.string.no_widgets_popup, Toast.LENGTH_SHORT).show();
    }









    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void onClick(final View v) {


        YoYo.with(Techniques.Landing)
                .duration(100).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                switch (v.getId()){

                    case R.id.imgCall:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        startActivityForResult(intent, 1);
                        break;

                    case R.id.imgSms:
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
                        startActivity(intent);
                        break;

                    case R.id.imgMenu:
                        startActivity(new Intent(Home.this,Apps.class));
                        break;

                    case R.id.imgContacts:
                        intent = new Intent(Intent.ACTION_DEFAULT, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, 1);
                        break;

                    case R.id.imgBrowser:
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_BROWSER);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(findViewById(v.getId()));
    }


}
