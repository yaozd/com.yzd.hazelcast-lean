package com.yzd.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * java执行bat脚本和shell脚本并传入参数
 * https://blog.csdn.net/c0411034/article/details/81749915
 */
public class CommandUtil {
    private CommandUtil() {
    }

    public static String runCmd(String command) {
        String result = "";
        try {
            Process ps = Runtime.getRuntime().exec(command);
            ps.waitFor();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(ps.getInputStream(), Charset.forName(getSystemCharset())));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            result = "linux下运行完毕-ERROR!";
        }
        return result;
    }

    /**
     * @param command
     * @return
     */
    public static String runCmdNew(String command) {
        try {
            // 执行ping命令
            //Process process = Runtime.getRuntime().exec("cmd /c e:&dir");
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.forName(getSystemCharset())));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                System.out.println(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR!";
    }

    public static String getSystemCharset() {
        String osName = System.getProperty("os.name");
        System.out.println(osName);
        if (osName.toLowerCase().startsWith("windows")) {
            return "gbk";
        }
        return "utf-8";
    }

}
