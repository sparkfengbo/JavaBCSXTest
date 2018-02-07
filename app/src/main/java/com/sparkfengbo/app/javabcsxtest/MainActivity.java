package com.sparkfengbo.app.javabcsxtest;

import com.sparkfengbo.app.javabcsxtest.aidltest.Book;
import com.sparkfengbo.app.javabcsxtest.aidltest.IBookManagerInterface;
import com.sparkfengbo.app.javabcsxtest.annotations.FbAnnotInject;
import com.sparkfengbo.app.javabcsxtest.annotations.FBBindColor;
import com.sparkfengbo.app.javabcsxtest.annotations.FBBindContentView;
import com.sparkfengbo.app.javabcsxtest.annotations.FBBindString;
import com.sparkfengbo.app.javabcsxtest.annotations.FBBindView;
import com.sparkfengbo.app.libfbannotation.c21annotation.mydemo.FBEnumTest;
import com.sparkfengbo.app.libfbannotation.c21annotation.mydemo.FBFloatRange;
import com.sparkfengbo.app.javabcsxtest.annotations.FbBindListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@FBBindContentView(value = R.layout.activity_main)
public class MainActivity extends Activity {
    private static String TAG = "MainActivity";
    @FBBindView(R.id.tv_1)
    @FBBindColor(Color.GREEN)
    @FBBindString(R.string.test_1)
    private TextView mTextView1;

    /**
     * 注意，这里FBBindView，FBBindColor这两个注解都是添加了@Documented元注解，但FBBindString没有添加
     * 所以AS在mTextView2按F1查看注释的时候是看不到FBBindString信息的，但是能看到FBBindView，FBBindColor的信息
     */
    @FBBindView(R.id.tv_2)
    @FBBindColor(Color.BLACK)
    @FBBindString(R.string.test_2)
    private TextView mTextView2;

    @FBBindView(R.id.tv_3)
    @FBBindColor(Color.BLUE)
    @FBBindString(R.string.test_3)
    private TextView mTextView3;

    @FBFloatRange(from = 0, to = 10)
    private Float mFloatTest = 100f;

    @FBEnumTest
    private int color = 4;


    ThreadLocal<JsonTestMetaData> local = new ThreadLocal<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        FbAnnotInject.inject(this);

        JsonTestMetaData data = new JsonTestMetaData();
        data.json_str = "main_thread";
        local.set(data);

        Log.e(TAG, local.get().json_str);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                JsonTestMetaData data = new JsonTestMetaData();
//                data.json_str = "other_thread";
//                local.set(data);
//
//                Log.e(TAG, local.get().json_str);
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (local.get() != null) {
//                    Log.e(TAG, local.get().json_str);
//                } else {
//                    Log.e(TAG, "local.get() is null");
//
//                }
//            }
//        }).start();

//        SharedPreferences sharedPreferences = this.getSharedPreferences("setting", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("fengbo", "Hello fengbo");
//        editor.commit();
//        Log.e(TAG, local.get().json_str);
//
//        HandlerThread thread  = new HandlerThread("new_handler_thread");
//        Handler handler = new Handler(thread.getLooper(), new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                return false;
//            }
//        });


        Intent intent = new Intent("com.sparkfengbo.ng");


//        intent.setComponent(new ComponentName("com.sparkfengbo.app", "com.sparkfengbo.app.MyService"));
//        intent.setComponent(new ComponentName("com.sparkfengbo.app.javabcsxtest", "com.sparkfengbo.app.javabcsxtest.MyService"));

        boolean isSuccessConnect = getApplicationContext().bindService(createExplicitFromImplicitIntent(this, intent), mConnection, BIND_AUTO_CREATE);

        if (isSuccessConnect) {
            Log.d(TAG, "service connenct success");
        } else {
            Log.d(TAG, "service connenct fail");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences editor = this.getSharedPreferences("setting", MODE_PRIVATE);
        String testStr = editor.getString("fengbo", "null");
        Log.e("fengbo", testStr);
    }

    @FbBindListener(values = {R.id.tv_1, R.id.tv_2, R.id.tv_3})
    private void onClick(View view) {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "ANR???");

        Log.d(TAG, "onClick view.id : " + view.getId());
        if (view.getId() == R.id.tv_1) {
            Toast.makeText(this, "tv_1_click", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.tv_2) {
            Toast.makeText(this, "tv_2_click", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.tv_3) {
            Toast.makeText(this, "tv_3_click", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "service connencted " + name.getPackageName());

            IBookManagerInterface iBookManagerInterface = IBookManagerInterface.Stub.asInterface(service);

            try {
                List<Book> bookList = iBookManagerInterface.getList();
                if (bookList != null && bookList.size() > 0) {
                    for (Book book : bookList) {
                        Log.e(TAG, " " + book.content);
                    }
                } else {
                    Log.e(TAG, "service connencted : get null or empty booklist");
                }

                iBookManagerInterface.addBook(new Book(2, "哈哈哈哈😆"));

                bookList = iBookManagerInterface.getList();

                if (bookList != null && bookList.size() > 0) {
                    for (Book book : bookList) {
                        Log.e(TAG, " " + book.content);
                    }
                } else {
                    Log.e(TAG, "service connencted : get null or empty booklist");

                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.e("TAG", "onServiceDisconnected " + name.getPackageName());

        }
    };

    /**
     * http://blog.csdn.net/shenzhonglaoxu/article/details/42675287
     *
     * @param context
     * @param implicitIntent
     * @return
     */
    public Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
//         || resolveInfo.size() != 1
        if (resolveInfo == null || resolveInfo.size() == 0) {
            Log.e(TAG, "no explicit service");
            return implicitIntent;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }
}
