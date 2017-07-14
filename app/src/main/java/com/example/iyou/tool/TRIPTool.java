package com.example.iyou.tool;

/**
 * Created by cyhaha on 2017/1/15.
 */

public class TRIPTool {
    private String ip = "119.29.121.44";
//    private String ip = "172.18.92.209";
    private String port = "8349";
//    private String port = "13569";

    public String getPort(){
        return port;
    }

    public String getIp() {
        return ip;
    }


    public String byte2hex(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1){
                sb.append("0" + stmp);
            }else{
                sb.append(stmp);
            }

        }
        return sb.toString();
    }
}
