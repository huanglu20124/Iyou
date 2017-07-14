package com.example.iyou.my.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.example.iyou.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by road on 2017/2/3.
 */


public class MyRegisterActivity extends AppCompatActivity {



    //拍照取头像设定的常数
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private ImageView head;
    private Bitmap headBitmap;


    //用户要保存的四条属性
    private AVFile userImage;
    private String userAccount;
    private String userEmail;
    private String userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("用户注册");
        actionBar.setDisplayHomeAsUpEnabled(true);


        head = (ImageView) findViewById(R.id.reg_head);
        final EditText account = (EditText) findViewById(R.id.reg_account);
        final EditText email = (EditText) findViewById(R.id.reg_email);
        final EditText password = (EditText) findViewById(R.id.reg_password);
        Button submit = (Button) findViewById(R.id.success_button);


        //先生成默认头像
        head.setImageResource(R.drawable.default_head_pic);
        BitmapDrawable tempBitmap = (BitmapDrawable) getResources().getDrawable(R.drawable.default_head_pic);
        headBitmap = tempBitmap.getBitmap();


        //设置头像模块
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(MyRegisterActivity.this);

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



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAccount = account.getText().toString();
                userEmail = email.getText().toString();
                userPassword = password.getText().toString();
                boolean isCanSub = true;

                if(userAccount == null )
                    Toast.makeText(MyRegisterActivity.this,"用户名不能为空",Toast.LENGTH_LONG).show();
                if(userEmail == null )
                    Toast.makeText(MyRegisterActivity.this,"邮箱不能为空",Toast.LENGTH_LONG).show();
                if(userPassword == null )
                    Toast.makeText(MyRegisterActivity.this,"密码不能为空",Toast.LENGTH_LONG).show();

                if(isSimple(userPassword)){
                    isCanSub = false;
                    Toast.makeText(MyRegisterActivity.this,"密码长度至少为4，不能为全数字",Toast.LENGTH_LONG).show();
                }

                if(!checkEmail(userEmail)){
                    isCanSub = false;
                    Toast.makeText(MyRegisterActivity.this,"邮箱格式不正确",Toast.LENGTH_LONG).show();
                }

                if(isCanSub){
                    //把头像图片存储
                    byte[] temp = Bitmap2Bytes(headBitmap);
                    userImage = new AVFile("head.jpg",temp);
                    //String imageUrl = imageFile.getUrl();
                    userImage.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            Log.d("tip","用户头像保存成功");
                        }
                    });

                    AVUser user = new AVUser();
                    user.setUsername(userAccount);
                    user.setPassword(userPassword);
                    user.setEmail(userEmail);
                    user.put(ContentMy.USER_HEAD_IMAGE,userImage);
                    user.put(ContentMy.USER_SIGNATURE,"点击设置");
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                // 注册成功
                                Toast.makeText(MyRegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MyRegisterActivity.this,RegisterSucessActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // 失败的原因可能有多种，常见的是用户名已经存在。
                                Log.d("ERROR",e.getMessage());
                                if(e.getMessage().contains("此电子邮箱已经被占用"))
                                    Toast.makeText(MyRegisterActivity.this,"注册失败，邮箱已被占用",Toast.LENGTH_SHORT).show();
                                if(e.getMessage().contains("Username has already been taken"))
                                    Toast.makeText(MyRegisterActivity.this,"注册失败，用户名已被占用",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }


            }
        });




    }

    boolean isSimple(String str){
        if(str.length() <= 3) return true;
        int numSum = 0;
        int letterSum = 0;
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) >= '0'&& str.charAt(i) <= '9') numSum++;
            if(str.charAt(i) >= 'a'&& str.charAt(i) <= 'z') letterSum++;
            if(str.charAt(i) >= 'A'&& str.charAt(i) <= 'Z') letterSum++;
        }
        if(letterSum <= 1 || numSum == str.length())return true;
        return false;
    }


    //判断邮箱格式是否正确
    public boolean checkEmail(String email) {

        Pattern pattern = Pattern
                .compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
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
                Toast.makeText(MyRegisterActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                headBitmap = data.getParcelableExtra("data");
                head.setImageBitmap(headBitmap);
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

    //bitmap转化成bytes[]
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


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
}
