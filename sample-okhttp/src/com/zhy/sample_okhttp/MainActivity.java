package com.zhy.sample_okhttp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
{

    private String mBaseUrl = "http://192.168.253.1/ScreenRecorder/";

    private static final String TAG = "MainActivity";

    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            Log.e(TAG, "onResponse：complete");
            mTv.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    Toast.makeText(MainActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(MainActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    public void getHtml(View view)
    {
        String url = "http://192.168.253.1/meiting/getmusic.php";
        //String url="http://www.baidu.com";
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }


    public void postString(View view)
    {
        String url = mBaseUrl + "user!postString";
        OkHttpUtils
                .postString()
                .url(url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(new Gson().toJson(new User("zhy", "123")))
                .build()
                .execute(new MyStringCallback());

    }

    public void postFile(View view)
    {
    	int width = 1280;
        int height = 720;
    	/*File file = new File(Environment.getExternalStorageDirectory()
    			, "/ScreenRecorder-" + width + "x" + height + "-" 
                        + ".mp4");*/
        File file = new File(Environment.getExternalStorageDirectory()
    			, "su" + ".jpg");
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = mBaseUrl + "user!postFile";
        OkHttpUtils
                .postFile()
                .url(url)
                .file(file)
                .build()
                .execute(new MyStringCallback());


    }

    public void getUser(View view)
    {
        String url = mBaseUrl + "user!getUser";
        OkHttpUtils
                .post()//
                .url(url)//
                .addParams("username", "hyman")//
                .addParams("password", "123")//
                .build()//
                .execute(new GenericsCallback<User>(new JsonGenericsSerializator())
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(User response, int id)
                    {
                        mTv.setText("onResponse:" + response.username);
                    }
                });
    }


    public void getUsers(View view)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "zhy");
        String url = mBaseUrl + "user!getUsers";
        OkHttpUtils//
                .post()//
                .url(url)//
//                .params(params)//
                .build()//
                .execute(new ListUserCallback()//
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(List<User> response, int id)
                    {
                        mTv.setText("onResponse:" + response);
                    }
                });
    }


    public void getHttpsHtml(View view)
    {
        String url = "http://192.168.253.1/meiting/getmusic.php";
        OkHttpUtils
                .get()//
                .url(url)//
                .id(101)
                .build()//
                .execute(new MyStringCallback());

    }

    public void getImage(View view)
    {
        mTv.setText("");
        String url = mBaseUrl + "myimg.jpg";
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id)
                    {
                        Log.e("TAG", "onResponse：complete");
                        mImageView.setImageBitmap(bitmap);
                    }
                });
    }


    public void uploadFile(View view)
    {
    	int width = 1280;
        int height = 720;
        //File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file = new File(Environment.getExternalStorageDirectory()
    			, "/ScreenRecorder-" + width + "x" + height + "-" + ".mp4"); 
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "root");
        params.put("password", "passwd");

        Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        //String url = mBaseUrl + "user!uploadFile";
        String url = mBaseUrl;
        OkHttpUtils.post()//
                .addFile("mFile", "/ScreenRecorder-" + width + "x" + height + "-" + ".mp4", file)//
                .url(url)//
                .params(params)//
                .headers(headers)//
                .build()//
                .execute(new MyStringCallback());
    }

    public void multiFileUpload(View view)
    {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1#.txt");
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void downloadFile(View view)
    {
        String url = mBaseUrl + "myimg.jpg";
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "myimg.jpg")//
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                        mProgressBar.setProgress((int) (100 * progress));
                        Log.e(TAG, "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


    public void otherRequestDemo(View view)
    {
        //also can use delete ,head , patch
        /*
        OkHttpUtils
                .put()//
                .url("http://11111.com")
                .requestBody
                        ("may be something")//
                .build()//
                .execute(new MyStringCallback());



        OkHttpUtils
                .head()//
                .url(url)
                .addParams("name", "zhy")
                .build()
                .execute();

       */


    }

    public void clearSession(View view)
    {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl)
        {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
