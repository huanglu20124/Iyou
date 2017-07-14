package com.example.iyou.my.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.iyou.MainActivity;
import com.example.iyou.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MyResetActivity extends AppCompatActivity {


    public static final int REQUESET_CHANGE_NAME = 1;
    public static final int REQUESET_CHANGE_SIGNATURE = 2;
    public static final int REQUEST_CHANGE_PASSWORD = 3;


    //拍照取头像设定的常数
    private static final int PHOTO_REQUEST_CAREMA = 5;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 6;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 7;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private Bitmap headBitmap;

    //云端的头像的key
    public static  final String USER_HEAD_IMAGE = "user_head_image";

    private AVUser user;
    private ImageView head;
    private TextView name;
    private TextView signature;
    private TextView email;
    private TextView changePassword;
    private AVFile headAVFile;

    private LinearLayout setHead;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reset);

        head = (ImageView) findViewById(R.id.userHead2);
        name = (TextView) findViewById(R.id.accountName2);
        signature = (TextView) findViewById(R.id.signature2);
        email = (TextView) findViewById(R.id.email2);
        changePassword = (TextView) findViewById(R.id.my_reset_password);
        setHead = (LinearLayout) findViewById(R.id.setHead);

        //显示信息
        user = AVUser.getCurrentUser();
        if(user != null){
            user.getAVFile(USER_HEAD_IMAGE).getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    head.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
                }
            });

            name.setText(user.getUsername());
            email.setText(user.getEmail());
            if(user.get(ContentMy.USER_SIGNATURE) != null)
            signature.setText(user.get(ContentMy.USER_SIGNATURE).toString());
        }

        findViewById(R.id.setSignature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyResetActivity.this,MyResetSignatureActivity.class);
                startActivityForResult(intent,REQUESET_CHANGE_SIGNATURE);
            }
        });

//        findViewById(R.id.setName).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyResetActivity.this,MySetNameActivity.class);
//                startActivityForResult(intent,REQUESET_CHANGE_NAME);
//
//            }
//        });


        findViewById(R.id.setPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyResetActivity.this);
                builder.setTitle("提示信息").setMessage("点击“确定”后，系统将会发送一封邮件到您的邮箱用于修改密码")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AVUser.requestPasswordResetInBackground(AVUser.getCurrentUser().getEmail().toString(), new RequestPasswordResetCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            Toast.makeText(MyResetActivity.this,"邮件已发送",Toast.LENGTH_SHORT).show();
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).show();
            }
        });

        setHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(MyResetActivity.this);

                //先设置默认的头像
//                BitmapDrawable defaultImage = (BitmapDrawable) head.getDrawable();
//                userImage = new AVFile("head.jpg",Bitmap2Bytes(defaultImage.getBitmap()));
                builder.setItems(new CharSequence[]{"图库","拍照"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            gallery();
                        }
                        else{
                            camera();
                        }
                    }
                }).show();
            }
        });



        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("我的个人信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    //更新云端数据显示
    public void updateData(){
        //头像部分
        AVFile file = new AVFile("head.jpg",Bitmap2Bytes(headBitmap));
        user.put(USER_HEAD_IMAGE,file);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Toast.makeText(MyResetActivity.this,"保存用户头像成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    /*
  * 从相册获取
  */
    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {

        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /*
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESET_CHANGE_NAME){
            name.setText(AVUser.getCurrentUser().getUsername());

        }
        if(requestCode == REQUESET_CHANGE_SIGNATURE){
            if(AVUser.getCurrentUser().get(ContentMy.USER_SIGNATURE) != null){
                signature.setText(AVUser.getCurrentUser().get(ContentMy.USER_SIGNATURE).toString());
                AVUser.getCurrentUser().saveInBackground();
            }
        }
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(MyResetActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                headBitmap = data.getParcelableExtra("data");
                head.setImageBitmap(headBitmap);
                updateData();
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //下面这三个函数提示MainActivity更新头像以及名字
    @Override
    protected void onDestroy() {
        setResult(1,getIntent());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        setResult(1,getIntent());
        super.onResume();
    }

    @Override
    protected void onStop() {
        setResult(1,getIntent());
        super.onStop();
    }

    //bitmap转化成bytes[]
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }



}
