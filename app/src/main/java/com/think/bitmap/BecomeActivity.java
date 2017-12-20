package com.think.bitmap;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.demonstrate.DialogUtil;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.MaskTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class BecomeActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button btn;
    protected ImageView iv;
    private String IMG_URL_0 = "http://zhanhui.3158.cn/data/attachment/exhibition/data/attachment/exhibition/article/2016/02/17/0d6437a313155f933c13971a0ba22cf4.jpg";
    private String IMG_URL_1 = "http://img2015.zdface.com/20171115/89eef31e783ccd13f2cdf12ab04298e3.jpg";
    private String IMG_URL_2 = "http://img003.21cnimg.com/photos/album/20161114/m600/6CDB31F812CC273DD89E4AA58594A217.jpeg";
    private String IMG_URL_3 = "http://p0.so.qhimgs1.com/t0195918c00ff0b8c1c.jpg";
    private String IMG_URL_4 = "http://himg2.huanqiu.com/attachment2010/2016/1026/15/06/20161026030615719.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_become);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            showDialog();
        }
    }

    private void showDialog() {
        DialogUtil.showListDialog(this, "图片的形状的变换!", new String[]{
                "0,将图像剪切成圆",
                "1,将图像剪切成三角形",
                "2,将图像剪切成多角形",
                "3,将图像剪切成曲线形",
                "4,将图像剪切成圆角形",
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Glide.with(BecomeActivity.this)
                                .load(IMG_URL_0)
                                .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                                .into(iv);
                        break;
                    case 1:
                        MultiTransformation<Bitmap> transformation
                                = new MultiTransformation<>(new CenterCrop(), new MaskTransformation(R.mipmap.mask_trangle));

                        Glide.with(BecomeActivity.this)
                                .load(IMG_URL_1)
                                .apply(bitmapTransform(transformation))
                                .into(iv);
                        break;
                    case 2:
                        Glide.with(BecomeActivity.this)
                                .load(IMG_URL_2)
                                .apply(bitmapTransform(new MultiTransformation<>(new CenterCrop(), new MaskTransformation(R.mipmap.mask_starfish))))
                                .into(iv);
                        break;
                    case 3:
                        Glide.with(BecomeActivity.this)
                                .load(IMG_URL_3)
                                .apply(bitmapTransform(new MultiTransformation<>(new CenterCrop(), new MaskTransformation(R.drawable.mask_chat_right))))
                                .into(iv);
                        break;
                    case 4:
                        Glide.with(BecomeActivity.this)
                            .load(IMG_URL_4)
                            .apply(bitmapTransform(new RoundedCornersTransformation(45, 0,
                                    RoundedCornersTransformation.CornerType.BOTTOM)))
                            .into(iv);
                    break;
                }
            }
      });
    }

    private void initView() {
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(BecomeActivity.this);
        iv = (ImageView) findViewById(R.id.iv);
    }
}
