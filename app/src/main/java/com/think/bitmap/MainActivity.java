package com.think.bitmap;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.demonstrate.DemonstrateUtil;
import com.example.demonstrate.DialogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

/*
1,质量压缩法
    1.1质量压缩不会减少图片的像素
    1.2在像素不变的前提下改变图片的位深及透明度等，来达到压缩图片的目的。
    1.3压缩的图片文件大小会有改变，但是导入成bitmap后占得内存是不变的
2,采样率压缩法
    2.1内存的使用少,不会过多的占用内存
    2.2可以先只读取图片的边，通过宽和高设定好取样率后再加载图片
    2.3
3,缩放法
    3.1通过缩放图片像素来减少图片占用内存大小
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String IMG_URL_0 = "http://pic.xiami.net/images/artistlogo/77/14677816895777.jpg";
    protected TextView tvOld;
    protected TextView tv3;
    private String baseUrl = "http://pic.xiami.net";
    private int contentLength;
    private String IMG_URL_1 = "http://pic9.nipic.com/20100823/4361515_000842599423_2.jpg";
    private String IMG_URL_2 = "http://attachments.gfan.com/forum/attachments2/201302/03/11281446n2st1its4152n5.jpg";
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmaps/";

    private void showList() {
        DialogUtil.showListDialog(this, "Android中图片压缩方案", new String[]{
                "0,加载原图",
                "1,下载原图",
                "2,质量压缩法,不压缩保存到本地",
                "3,质量压缩法,质量压缩保存到本地",
                "4,采样率压缩法,保存",
                "5,缩放法,方式一!",
                "6,缩放法,方式二!",
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        //加载原图,并且展示!
                        load0();
                        break;
                    case 1:
                        //下载原图,并且保存本地,从本加载展示!
                        load1();
                        break;
                    case 2:
                        //
                        load2();
                        break;
                    case 3:
                        load3();
                        break;
                    case 4:
                        load4();
                        break;
                    case 5:
                        load5();
                        break;
                    case 6:
                        load6();
                        break;
                }
            }
        });
    }

    private void load6() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ResponseBody body = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .build()
                        .create(NetServerInterface.class)
                        .getBitmap(IMG_URL_1)
                        .execute()
                        .body();
                contentLength = (int) body.contentLength() / 1024;
                InputStream stream = body
                        .byteStream();
                Bitmap image = BitmapFactory.decodeStream(stream);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 85, out);
                int size = contentLength;
                //--原大小/内存小小后,开平方根.
                float zoom = (float)Math.sqrt(size * 1024 / (float)out.toByteArray().length);
//
                Matrix matrix = new Matrix();
                matrix.setScale(zoom, zoom);

                Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                //清空.
                out.reset();
                result.compress(Bitmap.CompressFormat.JPEG, 85, out);
                while(out.toByteArray().length > size * 1024){
                    DemonstrateUtil.showLogResult(out.toByteArray().length+"***");
                    matrix.setScale(0.9f, 0.9f);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                    out.reset();
                    result.compress(Bitmap.CompressFormat.JPEG, 85, out);
                }

                File file = new File(Environment.getExternalStorageDirectory(), "load6.jpg");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(out.toByteArray());
                fos.flush();
                fos.close();
                String filePath = file.getPath();
                e.onNext(filePath);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        tvOld.setText("原始大小:" + contentLength + "kb");
                        File file = new File(path);
                        int fileLenth = (int) (file.length() / 1024);
                        tv.setText("保存后大小:" + fileLenth + "kb");

                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        int byteCount = bitmap.getByteCount() / 1024;
                        tv3.setText("加载到内存的大小:" + byteCount);
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    private void load5() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ResponseBody body = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .build()
                        .create(NetServerInterface.class)
                        .getBitmap(IMG_URL_1)
                        .execute()
                        .body();
                contentLength = (int) body.contentLength() / 1024;
                InputStream stream = body
                        .byteStream();
                Bitmap bmp = BitmapFactory.decodeStream(stream);

                // 尺寸压缩倍数,值越大，图片尺寸越小
                int ratio = 2;
                // 压缩Bitmap到对应尺寸
                //int width,宽 int height,高 Config config,决定图片质量.
                Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
                canvas.drawBitmap(bmp, null, rect, null);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 把压缩后的数据存放到baos中
                result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                File file = new File(Environment.getExternalStorageDirectory(), "load5.jpg");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                String filePath = file.getPath();
                e.onNext(filePath);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        tvOld.setText("原始大小:" + contentLength + "kb");
                        File file = new File(path);
                        int fileLenth = (int) (file.length() / 1024);
                        tv.setText("保存后大小:" + fileLenth + "kb");

                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        int byteCount = bitmap.getByteCount() / 1024;
                        tv3.setText("加载到内存的大小:" + byteCount);
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    private void load4() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ResponseBody body = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .build()
                        .create(NetServerInterface.class)
                        .getBitmap(IMG_URL_2)
                        .execute()
                        .body();
                contentLength = (int) body.contentLength() / 1024;
                InputStream stream = body
                        .byteStream();
                File file = new File(Environment.getExternalStorageDirectory(), "load4.jpg");
                FileOutputStream out = new FileOutputStream(file);
                out.write(body.bytes());
                out.close();
                String filePath = file.getPath();
                e.onNext(filePath);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        tvOld.setText("原始大小:" + contentLength + "kb");
                        File file = new File(path);
                        int fileLenth = (int) (file.length() / 1024);
                        tv.setText("保存后大小:" + fileLenth + "kb");

                        //使用采样率进行压缩
                        BitmapFactory.Options newOpts = new BitmapFactory.Options();
                        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
                        newOpts.inJustDecodeBounds = true;
                        //此时返回bitmap为空
                        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);

                        newOpts.inJustDecodeBounds = false;
                        int w = newOpts.outWidth;
                        int h = newOpts.outHeight;
                        //现在主流手机比较多是1280*720分辨率，所以高和宽我们设置为
                        //这里设置高度为1280f
                        float hh = 1280f;
                        //这里设置宽度为720f
                        float ww = 720f;
                        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                        //be=1表示不缩放
                        int be = 1;
                        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                            be = (int) (newOpts.outWidth / ww);
                        } else if (w < h && h > hh) {//如果高度高的话根据高度固定大小缩放
                            be = (int) (newOpts.outHeight / hh);
                        }
                        if (be <= 0) {
                            be = 1;
                        }

                        //设置缩放比例
                        newOpts.inSampleSize = be;
                        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
                        bitmap = BitmapFactory.decodeFile(path, newOpts);

                        //压缩好比例大小后再进行质量压缩
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                        int options = 100;
                        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                            //重置baos即清空baos
                            baos.reset();
                            //这里压缩options%，把压缩后的数据存放到baos中
                            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                            options -= 10;//每次都减少10
                        }

                        int len = baos.toByteArray().length / 1024;
                        tv3.setText("内存中的大小:" + len + "kb");
                        //把压缩后的数据baos存放到ByteArrayInputStream中
                        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                        //把ByteArrayInputStream数据生成图片
                        Bitmap bitmap3 = BitmapFactory.decodeStream(isBm, null, null);
                        tv.setText("保存后大小:" + fileLenth + "kb"+"bitmap:"+bitmap3.getByteCount()/1024+"--");
                        iv.setImageBitmap(bitmap3);

                        /*File file5 = new File(Environment.getExternalStorageDirectory(), "load5.jpg");
                        FileOutputStream out = new FileOutputStream(file5);
                        out.write(baos.toByteArray());
                        out.close();
                        baos.close();*/
                    }
                });
    }

    private void load3() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ResponseBody body = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .build()
                        .create(NetServerInterface.class)
                        .getBitmap(IMG_URL_1)
                        .execute()
                        .body();
                contentLength = (int) body.contentLength() / 1024;
                InputStream stream = body
                        .byteStream();

                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                int options = 100;
                while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    //重置baos即清空baos
                    baos.reset();
                    //这里压缩options%，把压缩后的数据存放到baos中
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    options -= 10;//每次都减少10
                }

                File file = new File(Environment.getExternalStorageDirectory(), "load3.jpg");
                FileOutputStream out = new FileOutputStream(file);
                out.write(baos.toByteArray());
                out.close();
                baos.close();
                String filePath = file.getPath();
                e.onNext(filePath);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        tvOld.setText("原始大小:" + contentLength + "kb");
                        File file = new File(path);
                        int fileLenth = (int) (file.length() / 1024);
                        tv.setText("保存后大小:" + fileLenth + "kb");
                        Bitmap bitmap = BitmapFactory.decodeFile(path);

                        int byteCount = bitmap.getByteCount() / 1024;
                        tv3.setText("加载到内存的大小:" + byteCount);
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    private void load2() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ResponseBody body = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .build()
                        .create(NetServerInterface.class)
                        .getBitmap(IMG_URL_1)
                        .execute()
                        .body();
                contentLength = (int) body.contentLength() / 1024;
                InputStream stream = body
                        .byteStream();
                //压缩100,图片并将Bitmap保存到本地
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                File file = new File(Environment.getExternalStorageDirectory(), "load2.jpg");
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                String filePath = file.getPath();
                e.onNext(filePath);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        tvOld.setText("原始大小:" + contentLength + "kb");
                        File file = new File(path);
                        int fileLenth = (int) (file.length() / 1024);
                        tv.setText("保存后大小:" + fileLenth + "kb");
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        int byteCount = bitmap.getByteCount() / 1024;
                        tv3.setText("加载到内存的大小:" + byteCount);
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    private void load1() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                ResponseBody body = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .build()
                        .create(NetServerInterface.class)
                        .getBitmap(IMG_URL_1)
                        .execute()
                        .body();
                contentLength = (int) body.contentLength() / 1024;
                InputStream stream = body
                        .byteStream();
                //压缩图片并将Bitmap保存到本地
//                Bitmap bitmap = BitmapFactory.decodeStream(stream);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                File file = new File(Environment.getExternalStorageDirectory(), "load1.jpg");
                FileOutputStream out = new FileOutputStream(file);
                out.write(body.bytes());
                out.close();
                String filePath = file.getPath();
                e.onNext(filePath);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        tvOld.setText("原始大小:" + contentLength + "kb");
                        File file = new File(path);
                        int fileLenth = (int) (file.length() / 1024);
                        tv.setText("保存后大小:" + fileLenth + "kb");
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        int byteCount = bitmap.getByteCount() / 1024;
                        tv3.setText("加载到内存的大小:" + byteCount);
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    private void load0() {
        Observable
                .create(new ObservableOnSubscribe<Bitmap>() {
                    @Override
                    public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                        Response<ResponseBody> response = new Retrofit.Builder()
                                .baseUrl(baseUrl)
                                .build()
                                .create(NetServerInterface.class)
                                .getBitmap(IMG_URL_0)
                                .execute();
                        contentLength = (int) (response.body().contentLength() / 1024);
                        InputStream stream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        e.onNext(bitmap);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        tvOld.setText("原图片大小为:" + contentLength + "kb");
                        iv.setImageBitmap(bitmap);
                    }
                });
    }

    protected Button btn;
    protected TextView tv;
    protected ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            showList();
        }
    }


    private void initView() {
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(MainActivity.this);
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        tvOld = (TextView) findViewById(R.id.tv_old);
        tv3 = (TextView) findViewById(R.id.tv3);
    }
}
