package com.example.iyou.route.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetDataCallback;
import com.example.iyou.MainActivity;
import com.example.iyou.R;
import com.example.iyou.model.utils.SendPictureUtil;
import com.example.iyou.my.activity.CircleImageView;
import com.example.iyou.my.activity.ContentMy;
import com.example.iyou.route.model.bean.EditShareRouteParamters;
import com.example.iyou.tool.CustomImageView;
import com.example.iyou.tool.EditShareRouteActivityTool;
import com.example.iyou.tool.TRIPTool;
import com.example.iyou.tool.UploadImageTool;

import java.util.List;
import java.util.Map;

import static com.avos.avoscloud.AVUtils.getRandomString;

public class EditShareRouteActivity extends AppCompatActivity {

    private EditText editCommentText;
    private TextView textView;
    private CircleImageView head;
    private List<Map<String, Object>> bean;

    private ImageButton sendPictureBtn;
    private CustomImageView imageView;
    private ImageView deleteImage;
    private Bitmap bitmap=null;
    private String picturePath=null;

    private String idName=null;

    private ProgressDialog progDialog = null;// 上传时进度条


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_share_route);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("分享足迹贴");
        actionBar.setDisplayHomeAsUpEnabled(true);

        bean = (List<Map<String, Object>>) getIntent().getSerializableExtra("bean");
        textView=(TextView)findViewById(R.id.textView13);
        head = (CircleImageView) findViewById(R.id.write_route_head);
        AVFile file = AVUser.getCurrentUser().getAVFile("user_head_image");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                head.setImageBitmap(MainActivity.Bytes2Bimap(bytes));
            }
        });
        StringBuffer routeName = new StringBuffer();
        StringBuffer sceneriesIDName=new StringBuffer();
        for(Map map:bean){
            routeName.append(map.get("sceneryName") + "->");
            sceneriesIDName.append(map.get("sceneryId")+"+");
        }
        textView.setText((String) routeName.toString().substring(0,routeName.toString().length()-2));
        idName=(String) sceneriesIDName.toString().substring(0,sceneriesIDName.toString().length()-1);
        editCommentText=(EditText)this.findViewById(R.id.edit_comment_text);

        imageView=(CustomImageView)findViewById(R.id.picture_imageview);
        deleteImage=(ImageView)findViewById(R.id.delete_imageview);
        imageView.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        sendPictureBtn=(ImageButton)findViewById(R.id.send_picture_btn);
        sendPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到系统图片选择界面
                SendPictureUtil.toGalleryPage(EditShareRouteActivity.this);
            }
        });
        //点击删除上传图片事件
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                deleteImage.setVisibility(View.GONE);
                bitmap.recycle();//回首bitmap的内存
                bitmap=null;
            }
        });

        progDialog = new ProgressDialog(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case SendPictureUtil.REQUEST_IMAGE_PICK:
                    bitmap=SendPictureUtil.getSmallBitmap(SendPictureUtil.getRealPathFromURI(getBaseContext(), data.getData()));
                    picturePath=SendPictureUtil.getRealPathFromURI(getBaseContext(), data.getData());
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    deleteImage.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }


    //返回键事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_send:

                if(AVUser.getCurrentUser() ==null){
                    Toast.makeText(EditShareRouteActivity.this, "请先登陆账号", Toast.LENGTH_SHORT).show();
                    break;
                }

                Editable editContent=editCommentText.getText();
                //感想内容字符串
                String feelingContent=editContent.toString();
                if(feelingContent == null ||feelingContent.equals("")){
//                    Toast.makeText(EditShareRouteActivity.this, "请输入心得", Toast.LENGTH_SHORT).show();
                    //如果不写心得就传一个空字符
                    feelingContent=".";
                }
                String title = (String)textView.getText();

                EditShareRouteParamters data = new EditShareRouteParamters();
                data.setTitle(title);
                data.setContent(feelingContent);
                data.setSceneries(idName);
                data.setAccount(AVUser.getCurrentUser().getUsername());
                data.setNoteType("Route");

                TRIPTool iptool = new TRIPTool();
                String ip = iptool.getIp();
                String port = iptool.getPort();
                String imageUrl = "http://"+ip+":"+port+"/images/" + getRandomString(20)  + ".png";//图片在服务器存储位置
                data.setImageUrl(imageUrl);

                String path = picturePath;     //图片路径
                UploadImageTool imageTool = new UploadImageTool(path);
                imageTool.getBitmap(200);       //根据设定宽度按比例压缩读取图片
                byte[] image = imageTool.getImages();          //获得图片byte[]
                data.setImageArr(image);

                EditShareRouteActivityTool tool = new EditShareRouteActivityTool(data,this);
                tool.upload();
                int num = (int)AVUser.getCurrentUser().get(ContentMy.USER_ROUTE_NUM);
                num++;
                AVUser.getCurrentUser().put(ContentMy.USER_ROUTE_NUM,num);
                AVUser.getCurrentUser().saveInBackground();
//                Toast.makeText(EditShareRouteActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                showProgressDialog();
//                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editcomment, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null!=bitmap){
            //回收bitmap的内存
            bitmap.recycle();
            bitmap=null;
        }
    }


    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setCanceledOnTouchOutside(false);
        progDialog.setMessage("正在上传...");
        progDialog.show();
    }
    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    public void doCheckMessage(){
        dissmissProgressDialog();
        this.finish();
    }
}
