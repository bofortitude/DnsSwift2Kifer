package DnsSwift2Kifer;

/**
 * Created by bofei on 6/19/2016.
 * The thread to be run.
 */

import jdk.internal.org.objectweb.asm.*;
import org.xbill.DNS.*;
import org.xbill.DNS.Type;

import java.io.IOException;

public class DnsRun implements Runnable {
    Message requestMessage;
    Resolver resolver;
    int totalRequests = 1;
    long sleepTime = 1000;
    String runMode = "Normal";
    boolean debugMode = false;

    public DnsRun(Message requestMessage, Resolver resolver){
        this.requestMessage = requestMessage;
        this.resolver = resolver;
    }

    public void setTotalRequests(int totalRequests){this.totalRequests = totalRequests;}

    public void setSleepTime(int sleepTime){this.sleepTime = sleepTime;}

    public void setRunMode(String runMode){
        if (runMode != "Normal" && runMode != "Statistics"){
            runMode = "Normal";
        }
        this.runMode = runMode;
    }

    public void setDebugMode(boolean debugMode){this.debugMode=debugMode;}

    private void normalHandleResponse(Message responseMessage){
        if (responseMessage.getRcode() == Rcode.NOERROR){
            RRset [] myRRset = responseMessage.getSectionRRsets(Section.ANSWER);
            for (RRset i: myRRset){
                System.out.println(Type.string(i.getType()));


            }
        }else {

        }
    }

    @Override
    public void run() {

        for (int i=1; i<=totalRequests; i++){
            try {
                Message responseMessage = this.resolver.send(this.requestMessage);
                if (this.runMode == "Normal"){
                    this.normalHandleResponse(responseMessage);
                }else if (this.runMode == "Statistics"){

                }

            } catch (IOException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The DNS sending meets IOException!");
            }

            try {
                Thread.sleep(this.sleepTime);
            } catch (InterruptedException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The thread sleep meets InterruptedException!");
            }
        }

    }
}
