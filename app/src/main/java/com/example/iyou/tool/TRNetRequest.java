package com.example.iyou.tool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by cyhaha on 2017/1/12.
 */

public class TRNetRequest {
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果(这里得到的数组)
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString;
            URL realUrl;
            if (param != null) {
                urlNameString = url + "?" + param;
                realUrl = new URL(urlNameString);
            } else{
                urlNameString = url;
                realUrl = new URL(urlNameString);
            }

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;

            // 建立连接以后一直读取传送过来的数据 ，为空的话
            // 挂起线程，使得CPU被主线程使用，不影响用户与UI交互

            while ((line = in.readLine()) != null) { // 建立连接以后一直读取传送过来的数据
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            String contentLenString = String.format("%d", param.getBytes().length);
            conn.setRequestProperty("Content-Length", contentLenString);
            conn.setRequestMethod("POST");

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 建立实际的连接
            conn.connect();

            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();


            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 通过socket传送数据
     * @throws IOException
     * @throws UnknownHostException
     */
    public static void socketData(String IP, int port, String param) throws UnknownHostException, IOException {
        Socket client = new Socket(InetAddress.getByName(IP), port);
        OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
        // 注意！！！   这里的字符串绝对不能变
        out.write("Post  "+param + "  HTTP/1.0\r\n");
        out.flush();

        InputStreamReader reader = new InputStreamReader(client.getInputStream(), "UTF-8");
        int c;
        StringBuffer bf = new StringBuffer();
        while (true) {
            c = reader.read();
            if (c == -1) {
                break;
            }
            bf.append((char)c);
        }
        System.out.println(bf);


        reader.close();
        client.close();
    }

    /**
     * 获取景点信息
     * */
    static void GetScenerysInfo() throws IOException{
        String urlString = "http://" + InetAddress.getLocalHost().getHostAddress() +
                ":13569?type=queryScenery&limit=20&keyWordType=city&keyWord=峨眉山";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent",
                "TRClient 1.0");
        connection.setRequestMethod("GET");

        connection.connect();

        int code = connection.getResponseCode();
        System.out.println("响应头： " + code);

        InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
        int tem;
        StringBuffer boBuffer = new StringBuffer();
        while (true) {
            tem = reader.read();
            if (tem == -1) {
                break;
            }
            boBuffer.append((char)tem);
        }

        System.out.println("服务器数据" + boBuffer);
        reader.close();
    }

    /**
     * 发送get请求获得字节文件
     * */
    public static byte[] sendGetByte(String url, String param) {
        byte[] data = null;

        BufferedInputStream in = null;
        try {
            String urlNameString;
            URL realUrl;
            if (param != null) {
                urlNameString = url + "?" + param;
                realUrl = new URL(urlNameString);
            } else{
                urlNameString = url;
                realUrl = new URL(urlNameString);
            }

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
//            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();

            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "TRClient 1.0");
            // 建立实际的连接
            connection.connect();

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=in.read(buffer)) != -1 ) {
                // 将读取len长度的字节写入ByteArrayOutputStream
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();

        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return data;
    }
}
