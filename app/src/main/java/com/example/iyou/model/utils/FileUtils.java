package com.example.iyou.model.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {
	private String SDPATH;
	
	public String getSDPATH(){
		return SDPATH;
	}
	
	public FileUtils(){
		//得到当前外部存储设备的目录
		//SDCARD
		SDPATH= Environment.getExternalStorageDirectory()+"/";//位置在storage-emulated-0-MyRoute.txt文件里面
		System.out.println("FileUtils---->"+SDPATH);
	}

	//在SD卡上创建文件
	public File creatSDFile(String fileName)throws IOException {
		File file=new File(SDPATH+fileName);
		file.createNewFile();
		return file;
	}

	//在SD卡上创建目录
	public File creatSDDir(String dirName){
		File dir=new File(SDPATH+dirName);
		dir.mkdir();
		return dir;
	}

	//判断文件在SD卡上是否存在
	public boolean isFileExist(String fileName){
		File file=new File(SDPATH+fileName);
		return file.exists();
	}

	public boolean deleteFile(String path){
		File file = new File(path);
		if(file.exists()){
			return file.delete();
		}
		return false;
	}


	//从本地获取我的路线数据
	public List<Object> load(String path) {
		List<Object> objs=new ArrayList<Object>();
		Object obj = null;
		File file = new File(path);
		try {
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				try {
					while(fis.available()>0){ //代表文件还有内容
						obj = ois.readObject();
						objs.add(obj);
					}
				}
				catch (ClassNotFoundException e) {
				}
				ois.close();
			}
		}catch (IOException e) {
		}
		return objs;
	}


	//保存我的路线数据，解决了序列化文件对象追加内容
	public void save(Object obj, String path, boolean isUpdate) {
		try {
			File f = new File(path);
			boolean isexist=false;//定义一个用来判断文件是否需要截掉头aced 0005的
			if(f.exists()){    //文件是否存在
				isexist=true;
				FileOutputStream fo=new FileOutputStream(f,isUpdate);//实现对象文件追加
				ObjectOutputStream oos = new ObjectOutputStream(fo);
				long pos=0;
				if(isexist && isUpdate){
					pos=fo.getChannel().position()-4;//追加的时候去掉头部aced 0005
					fo.getChannel().truncate(pos);
				}
				oos.writeObject(obj);//进行序列化
				System.out.println("追加成功");
			}else{//文件不存在
				f.createNewFile();
				FileOutputStream fo=new FileOutputStream(f);
				ObjectOutputStream oos = new ObjectOutputStream(fo);
				oos.writeObject(obj);//进行序列化
				oos.close();
				System.out.println("首次对象序列化成功！");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
