package DnsSwift2Kifer;

import java.io.*;


/**
 * Created by bofei on 6/23/2016.
 */
public class TalkToSystem {

    public static String getOsName(){
        return System.getProperty("os.name");
        /*
        Linux
        Windows
         */
    }

    public static String runCmdLinux(String cmd){

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
                BaseFunction.dumpInfo(line);
            }
            while ((line2 = readError.readLine()) != null){
                BaseFunction.dumpInfo(line2);
            }
            readOutput.close();
            readError.close();
            in.close();
            error.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (myProcess.exitValue() == 0){BaseFunction.dumpInfoFmt("CMD \""+cmd+"\" [OK]!");}
        else {BaseFunction.dumpInfoFmt("CMD \""+cmd+"\" [Error]!");}
        return result.toString();
    }

    public static String runCmdWindows(String cmd){
        String result = null;

        return result;
    }

    public static String runCmd(String cmd){
        String result = null;
        String OsName = TalkToSystem.getOsName().toLowerCase();
        BaseFunction.dumpInfoFmt("Run cmd \""+cmd+"\"...");
        if (OsName.indexOf("linux") >= 0){result = TalkToSystem.runCmdLinux(cmd);}
        else if(OsName.indexOf("windows") >=0){result = TalkToSystem.runCmdWindows(cmd);}
        return result;
    }

    public static String getOutInterface(String dstIp){
        String outInterface = null;
        String OsName = TalkToSystem.getOsName().toLowerCase();
        if (OsName.indexOf("linux") >= 0){
            String getResult = TalkToSystem.runCmdLinux("ip route get "+dstIp);
            String [] firstSplited = getResult.split("dev");
            outInterface = firstSplited[1].trim().split("\\s+")[0];

        }else if(OsName.indexOf("windows") >=0){}

        return outInterface;
    }

    public static String addIpAddress(String srcIpAddress, String dstIpAddress){
        // ipAddress -->  1.1.1.1
        String delCmd = null;
        String OsName = TalkToSystem.getOsName().toLowerCase();
        if (OsName.indexOf("linux") >= 0){
            String outInt = TalkToSystem.getOutInterface(dstIpAddress);
            TalkToSystem.runCmdLinux("ip address add "+srcIpAddress+"/32 dev "+outInt);
            delCmd = "ip address del "+srcIpAddress+"/32 dev "+outInt;
        }else if(OsName.indexOf("windows") >=0){}
        return delCmd;
    }

    public static String configIpAddress(String ipAddress, String outInterface){
        String delCmd = null;
        String OsName = TalkToSystem.getOsName().toLowerCase();
        if (OsName.indexOf("linux") >= 0){
            TalkToSystem.runCmdLinux("ip address add "+ipAddress+"/32 dev "+outInterface);
            delCmd = "ip address del "+ipAddress+"/32 dev "+outInterface;
        }else if(OsName.indexOf("windows") >=0){}
        return delCmd;
    }


}
