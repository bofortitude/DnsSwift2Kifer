package DnsSwift2Kifer;

/**
 * Created by bofei on 6/19/2016.
 * The thread to be run.
 */


import org.xbill.DNS.*;


public abstract class DnsRun implements Runnable {
    Message requestMessage;
    Resolver resolver;
    int totalRequests = 1;
    long sleepTime = 1000;
    boolean debugMode = false;

    public DnsRun(Message requestMessage, Resolver resolver){
        this.requestMessage = requestMessage;
        this.resolver = resolver;
    }

    public void setTotalRequests(int totalRequests){this.totalRequests = totalRequests;}

    public void setSleepTime(int sleepTime){this.sleepTime = sleepTime;}

    public void setDebugMode(boolean debugMode){this.debugMode=debugMode;}

}
