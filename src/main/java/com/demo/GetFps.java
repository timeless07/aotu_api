package com.demo;

//import commons.Commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class GetFps {

    //清空之前采样的数据，防止统计重复的时间
    private static String clearCommand = "adb shell dumpsys SurfaceFlinger --latency-clear";
    private static float jumpingFrames = 0; //jank次数，跳帧数
    private static float totalFrames = 0;  //统计的总帧数
    private static float lostFrameRate = 0; //丢帧率
    private static float fps; //fps值

    public static float[] getInfo(String deviceName, String packageName){
        String gfxCMD = "adb -s " + deviceName + " shell dumpsys gfxinfo " + packageName;
        float[] info = new float[2];
        int vsyncOverTimes = 0; // 垂直同步次数
        try {
            Runtime.getRuntime().exec(clearCommand);
            Process process = Runtime.getRuntime().exec(gfxCMD);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line; boolean flag = false;
            while ((line = reader.readLine()) != null){
                if (line.length() > 0){
                    if (line.contains("Execute")){
                        flag = true;
                        continue;
                    }
                    if (line.contains("View hierarchy")){
                        break;
                    }
                    if (flag){
                        String[] times = line.trim().split("\\s+");
                        //计算一帧所花费的时间
                        float onceTime = Float.parseFloat(times[0]) + Float.parseFloat(times[1]) + Float.parseFloat(times[2]) + Float.parseFloat(times[3]);
                        totalFrames += 1; //统计总帧数
                        if (onceTime > 16.67){//以Android定义的60FPS为标准
                            jumpingFrames += 1; // 统计跳帧jank数
                            //统计额外花费垂直同步脉冲的数量
                            if (onceTime % 16.67 == 0){
                                vsyncOverTimes += onceTime / 16.67 - 1;
                            }else {
                                vsyncOverTimes += Math.floor(onceTime / 16.67); //向下取整即可
                            }
                        }
                    }
                }
            }
            if (totalFrames > 0){
                fps = totalFrames / (totalFrames + vsyncOverTimes) * 60;
                lostFrameRate = jumpingFrames / totalFrames;
                info[0] = fps;
                info[1] = lostFrameRate * 100;
            }else {
                System.err.println("【ERROR】无FPS信息，请确认手机正常连接或APP正常运行");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static double getFps(String deviceName,String packageName){
        return getInfo(deviceName,packageName)[0];
    }

    public static double getLostFrameRate(String deviceName,String packageName){
        return getInfo(deviceName,packageName)[1];
    }

    public static void main(String []arg){
        GetFps ff=new GetFps();
        double Fps=ff.getFps("TCWSKFDYBUYD9H4D","com.heytap.market");
        System.out.println("fps :"+Fps);

        double Fps2=ff.getLostFrameRate("TCWSKFDYBUYD9H4D","com.heytap.market");
        System.out.println(Fps2);


    }
}

