package com.itfitness.templatedemo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int TEMPLATE_ONE = 1;
    private static final int TEMPLATE_TWO = 2;
    private static final int TEMPLATE_THREE = 3;

    private ImageView imgOne;
    private ImageView imgOneDst;
    private ImageView imgTwo;
    private ImageView imgTwoDst;
    private ImageView imgThree;
    private ImageView imgThreeDst;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TEMPLATE_ONE:
                    imgOneDst.setImageBitmap((Bitmap)msg.obj);
                    break;
                case TEMPLATE_TWO:
                    imgTwoDst.setImageBitmap((Bitmap)msg.obj);
                    break;
                case TEMPLATE_THREE:
                    imgThreeDst.setImageBitmap((Bitmap)msg.obj);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initOpenCv();
        initView();
        initListener();
    }
    /**
     * 加载OpenCv库
     */
    private void initOpenCv() {
        boolean b = OpenCVLoader.initDebug();
        if(b){
            Toast.makeText(this, "加载成功", Toast.LENGTH_SHORT).show();
        }
    }
    private void initListener() {
        imgOne.setOnClickListener(this);
        imgTwo.setOnClickListener(this);
        imgThree.setOnClickListener(this);
    }

    private void initView() {
        imgOne = (ImageView) findViewById(R.id.img_one);
        imgOneDst = (ImageView) findViewById(R.id.img_one_dst);
        imgTwo = (ImageView) findViewById(R.id.img_two);
        imgTwoDst = (ImageView) findViewById(R.id.img_two_dst);
        imgThree = (ImageView) findViewById(R.id.img_three);
        imgThreeDst = (ImageView) findViewById(R.id.img_three_dst);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_one:
//             第一张图片
                templateImage(TEMPLATE_ONE,BitmapFactory.decodeResource(getResources(),R.drawable.mv));
                break;
            case R.id.img_two:
//             第二张图片
                templateImage(TEMPLATE_TWO,BitmapFactory.decodeResource(getResources(),R.drawable.mv2));
                break;
            case R.id.img_three:
//             第三张图片
                templateImage(TEMPLATE_THREE,BitmapFactory.decodeResource(getResources(),R.drawable.mv3));
                break;
        }
    }
    /**
     * 模板匹配图像
     */
    private void templateImage(int imgFlag,Bitmap bitmapSrc) {
        Bitmap bitmapTemplate = BitmapFactory.decodeResource(getResources(), R.drawable.temple3);
//        获取Bitmap对应的Mat
        Mat img = new Mat();//创建Mat对象用于存放转换后的Bitmap对象
        Mat templ = new Mat();
        Utils.bitmapToMat(bitmapSrc,img);//将Bitmap转化为Mat
        Utils.bitmapToMat(bitmapTemplate,templ);
        int match_method = Imgproc.TM_CCOEFF;//模板匹配的计算方法
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);//创建模板匹配结果的Mat
        Imgproc.matchTemplate(img, templ, result, match_method);//进行模板匹配

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);//获取匹配结果的区域
        Point matchLoc;
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
//        绘制匹配结果区域
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(255, 0, 0),4,8,0);
        Utils.matToBitmap(img,bitmapSrc);//将绘制结果Mat转化为Bitmap
        Message obtain = Message.obtain();
        obtain.obj = bitmapSrc;
        obtain.what = imgFlag;
        handler.sendMessage(obtain);
//        释放资源
        img.release();
        templ.release();
        result.release();
        bitmapTemplate.recycle();
    }
}
