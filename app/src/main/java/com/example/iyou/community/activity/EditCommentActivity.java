package com.example.iyou.community.activity;

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
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.example.iyou.R;
import com.example.iyou.community.model.bean.EditParamters;
import com.example.iyou.model.utils.SendPictureUtil;
import com.example.iyou.my.activity.ContentMy;
import com.example.iyou.tool.CustomImageView;
import com.example.iyou.tool.TREditCommentTool;
import com.example.iyou.tool.TRIPTool;
import com.example.iyou.tool.UploadImageTool;

import java.util.Map;

import static com.avos.avoscloud.AVUtils.getRandomString;

public class EditCommentActivity extends AppCompatActivity {

    private EditText editCommentText;

    //点击回复贴子的指定帖子的map对象，可以通过这获取相应的数据
    private Map<String, Object> bean;

    private ImageButton sendPictureBtn;
    private CustomImageView imageView;
    private ImageView deleteImage;
    private Bitmap bitmap=null;
    private String picturePath=null;

    private ProgressDialog progDialog = null;// 上传时进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_edit);

        //设置工具栏，返回按钮
        android.support.v7.app.ActionBar actionBar=this.getSupportActionBar();
        actionBar.setTitle("发评论");
        actionBar.setDisplayHomeAsUpEnabled(true);

        editCommentText=(EditText)this.findViewById(R.id.edit_comment_text);

        //获取传递过来的数据
        bean= (Map<String, Object>) getIntent().getSerializableExtra("bean");


        imageView=(CustomImageView)findViewById(R.id.picture_imageview);
        deleteImage=(ImageView)findViewById(R.id.delete_imageview);
        imageView.setVisibility(View.GONE);
        deleteImage.setVisibility(View.GONE);
        sendPictureBtn=(ImageButton)findViewById(R.id.send_picture_btn);
        sendPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到系统图片选择界面
                SendPictureUtil.toGalleryPage(EditCommentActivity.this);
            }
        });
        //点击删除上传图片事件
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.GONE);
                deleteImage.setVisibility(View.GONE);
                bitmap.recycle();//回收bitmap的内存
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
                Editable editable=editCommentText.getText();
                //这就是你要的编辑字符串
                String commentStr=editable.toString();

                if(commentStr==null||commentStr.equals("")){
                    Toast.makeText(EditCommentActivity.this, "请输入评论"+commentStr, Toast.LENGTH_SHORT).show();
                }
                else{

                    if(AVUser.getCurrentUser() == null){
                        Toast.makeText(EditCommentActivity.this, "请先登陆账号"+commentStr, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    EditParamters par = new EditParamters();

                    String commentType = "Route";
                    par.setAccount(AVUser.getCurrentUser().getUsername());
                    par.setCommentType(commentType);
                    par.setContent(commentStr);
                    par.setNoteId((String)bean.get("noteId"));

                    //设置图片路径
                    TRIPTool iptool = new TRIPTool();
                    String ip = iptool.getIp();
                    String port = iptool.getPort();
                    String imageUrl = "http://"+ip+":"+port+"/images/" + getRandomString(20)  + ".png";//图片在服务器存储位置
                    par.setImageUrl(imageUrl);

                    String path = picturePath;     //图片路径
                    UploadImageTool imageTool = new UploadImageTool(path);
                    imageTool.getBitmap(200);       //根据设定宽度按比例压缩读取图片
                    byte[] image = imageTool.getImages();          //获得图片byte[]
                    par.setImageArr(image);

                    TREditCommentTool tool = new TREditCommentTool(par,this);
                    tool.senddata();
//                    Toast.makeText(EditCommentActivity.this, "发送:"+commentStr, Toast.LENGTH_SHORT).show();
                    int num = (int)AVUser.getCurrentUser().get(ContentMy.USER_COMMENT_COUNT);
                    num++;
                    AVUser.getCurrentUser().put(ContentMy.USER_COMMENT_COUNT,num);
                    AVUser.getCurrentUser().saveInBackground();
                    showProgressDialog();
//                    this.finish();
                }
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
