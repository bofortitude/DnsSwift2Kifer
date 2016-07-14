package DnsSwift2Kifer;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;


/**
 * Created by bofei on 6/23/2016.
 */
public class TalkToSystem2 {

    public static String getOsName(){
        return System.getProperty("os.name");
        /*
        Linux
        Windows
         */
    }

    public static String runCmdLinux(String cmd, boolean debug){

        StringBuffer result = new StringBuffer();
        String[] cmds = {"/bin/sh","-c",cmd};
        Process myProcess = null;
        try {
            myProcess = Runtime.getRuntime().exec(cmds);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int resultCode = myProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        InputStream in = myProcess.getInputStream();
        InputStream error = myProcess.getErrorStream();
        BufferedReader readOutput = new BufferedReader(new InputStreamReader(in));
        BufferedReader readError = new BufferedReader(new InputStreamReader(error));

        String line = null;
        String line2 = null;

        try {

            while((line = readOutput.readLine())!=null){
                result.append(line+"\n");
                if (debug){BaseFunction.dumpInfo(line);}
            }
            while ((line2 = readError.readLine()) != null){if (debug){BaseFunction.dumpInfo(line2);}}
            readOutput.close();
            readError.close();
            in.close();
            error.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (debug){
            if (myProcess.exitValue() == 0){
                BaseFunction.dumpInfoFmt("CMD \""+cmd+"\" [OK]!");
            }else {
                BaseFunction.dumpInfoFmt("CMD \""+cmd+"\" [Error]!");
            }
        }

        return result.toString();
    }

    public static String runCmdWindows(String cmd, boolean debug){
        String result = null;

        return result;
    }

    public static String runCmd(String cmd, boolean debug){
        String result = null;
        String OsName = TalkToSystem2.getOsName().toLowerCase();
        BaseFunction.dumpInfoFmt("Run cmd \""+cmd+"\"...");
        if (OsName.indexOf("linux") >= 0){result = TalkToSystem2.runCmdLinux(cmd, debug);}
        else if(OsName.indexOf("windows") >=0){result = TalkToSystem2.runCmdWindows(cmd, debug);}
        return result;
    }

    public static String getOutInterface(String dstIp, boolean debug){
        String outInterface = null;
        String OsName = TalkToSystem2.getOsName().toLowerCase();
        if (OsName.indexOf("linux") >= 0){
            String getResult = TalkToSystem2.runCmdLinux("ip route get "+dstIp, debug);
            String [] firstSplited = getResult.split("dev");
            outInterface = firstSplited[1].trim().split("\\s+")[0];

        }else if(OsName.indexOf("windows") >=0){}

        return outInterface;
    }

    public static String addIpAddress(String srcIpAddress, String dstIpAddress, boolean debug){
        // ipAddress -->  1.1.1.1
        String delCmd = null;
        String OsName = TalkToSystem2.getOsName().toLowerCase();
        if (OsName.indexOf("linux") >= 0){
            String outInt = TalkToSystem2.getOutInterface(dstIpAddress, debug);
            TalkToSystem2.runCmdLinux("ip address add "+srcIpAddress+"/32 dev "+outInt, debug);
            delCmd = "ip address del "+srcIpAddress+"/32 dev "+outInt;
        }else if(OsName.indexOf("windows") >=0){}
        return delCmd;
    }

    public static String configIpAddress(String ipAddress, String outInterface, boolean debug){
        String delCmd = null;
        String OsName = TalkToSystem2.getOsName().toLowerCase();
        if (OsName.indexOf("linux") >= 0){
            TalkToSystem2.runCmdLinux("ip address add "+ipAddress+"/32 dev "+outInterface, debug);
            delCmd = "ip address del "+ipAddress+"/32 dev "+outInterface;
        }else if(OsName.indexOf("windows") >=0){}
        return delCmd;
    }

    public static String getPath(Class dstClass) {
        // Run this method like: TalkToSystem2.getPath(DnsKicker.class)
        URL url = dstClass.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;

        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }


}
