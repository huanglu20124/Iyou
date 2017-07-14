 package com.example.iyou.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by cyhaha on 2017/2/20.
 */

public class UploadImageTool {

    private byte[] images;
    private String imageUrl;
    private String path;
    private Bitmap bitmap;

    public UploadImageTool(String path){
        this.path = path;

        try{
            File f=new File(path);
            if(!f.exists()){
                System.out.println("======无无无======================");
                return;
            }

        }catch (Exception e) {
            // TODO: handle exception
            System.out.println("======有有有======================");
            return;
        }

    }

    //根据路径获得位图并压缩
    public void getBitmap(int Width){
        //获取位图长和宽
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        bitmap= BitmapFactory.decodeFile(path,options);

        options.outWidth = Width;       //按设定宽度进行比例压缩
        options.outHeight = options.outHeight * Width / options.outWidth;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path,options);

        //若图片无法读取成位图则退出
        if(bitmap == null){return;}


        String str = getImgStr(bitmap);
        bitmap = getimg(str);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        images = baos.toByteArray();

    }

    //将Bitmap转换成Base64
    public static String getImgStr(Bitmap bit){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 10, bos);//参数100表示不压缩
        byte[] bytes=bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    public static Bitmap getimg(String str){
        byte[] bytes;
        bytes=Base64.decode(str, 0);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

        public byte[] getImages() {
        return images;
    }
}
