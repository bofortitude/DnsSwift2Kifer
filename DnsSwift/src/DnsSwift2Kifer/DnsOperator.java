package DnsSwift2Kifer;


import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bofei on 6/19/2016.
 * Thre main starter.
 */
public class DnsOperator {

    List<String> cmdList = new ArrayList<String>();
    public Thread shutdownHookThread = new HookShutdown(cmdList);

    private String dnsServerIp = null;
    private String dnsQuestion = null;
    private int dnsServerPort = 53;
    private int dnsTimeout = 10;
    private boolean tcpEnable = false;
    private boolean ignoreTruncation = false;
    private int requestId = 0;
    private String recordType = "A";
    private String requestDclass = "IN";
    private String requestOpcode = "QUERY";
    private boolean requestFlagRD = true;
    private boolean requestFlagAA = false;
    private boolean requestFlagAD = false;
    private boolean requestFlagCD = false;
    private boolean requestWithAdditional = true;

    private List<String> srcIpAddress = new ArrayList<String>();
    private List<Integer> srcPort = new ArrayList<Integer>();

    private int threadsTotal = 1;
    private int totalRequests = 1;
    private long sleepTime = 1000;
    private boolean debugMode = false;
    private String runMode = "Normal";  // Valid: "Normal", "Statistics", "Full"

    private List<Thread> threadList = new ArrayList<Thread>();
    private List<Thread> oldThreadList = new ArrayList<Thread>();
    private Thread threadCollectStatistics = null;
    private ConcurrentHashMap<String, Integer> statisticsPrimaryAnswer = new ConcurrentHashMap<>();


    public DnsOperator(String dnsServerIp, String dnsQuestion){
        this.dnsServerIp = dnsServerIp;
        this.dnsQuestion = dnsQuestion;
        Runtime.getRuntime().addShutdownHook(this.shutdownHookThread);
    }

    public void setDnsServerPort(int dnsServerPort){this.dnsServerPort = dnsServerPort;}

    public void setSrcIpAddress(List<String> srcIpAddress){this.srcIpAddress = srcIpAddress;}

    public void setSrcPort(List<Integer> srcPort){this.srcPort = srcPort;}

    public void setDnsTimeout(int dnsTimeout){this.dnsTimeout = dnsTimeout;}

    public void setTcpEnable(boolean tcpEnable){this.tcpEnable = tcpEnable;}

    public void setIgnoreTruncation(boolean ignoreTruncation){this.ignoreTruncation = ignoreTruncation;}

    public void setRequestId(int requestId){this.requestId = requestId;}

    public void setRecordType(String recordType){this.recordType = recordType;}

    public void setRequestDclass(String requestDclass){this.requestDclass = requestDclass;}

    public void setRequestOpcode(String requestOpcode){this.requestOpcode = requestOpcode;}

    public void setRequestFlagRD(boolean requestFlagRD){this.requestFlagRD = requestFlagRD;}

    public void setRequestFlagAA(boolean requestFlagAA){this.requestFlagAA = requestFlagAA;}

    public void setRequestFlagAD(boolean requestFlagAD){this.requestFlagAD = requestFlagAD;}

    public void setRequestFlagCD(boolean requestFlagCD){this.requestFlagCD = requestFlagCD;}

    public void setRequestWithAdditional(boolean requestWithAdditional){this.requestWithAdditional = requestWithAdditional;}

    public void setThreadsTotal(int threadsTotal){this.threadsTotal = threadsTotal;}

    public void setTotalRequests(int totalRequests){this.totalRequests = totalRequests;}

    public void setSleepTime(long sleepTime){this.sleepTime = sleepTime;}

    public void setDebugMode(boolean debugMode){this.debugMode = debugMode;}

    public void setRunMode(String runMode){
        if (runMode != "Normal" && runMode != "Statistics" && runMode != "Full"){
            this.runMode = "Normal";
        }else {this.runMode = runMode;}
    }

    private void addShutdownCmd(String cmd){this.cmdList.add(cmd);}

    private LookupBuilder generateLookuper(){
        LookupBuilder myLookup = myLookup = new LookupBuilder(this.dnsServerIp, this.dnsQuestion);;
        myLookup.setRecordType(this.recordType);
        myLookup.setTcpEnable(this.tcpEnable);
        myLookup.setDnsServerPort(this.dnsServerPort);
        myLookup.setDnsTimeout(this.dnsTimeout);
        myLookup.setIgnoreTruncation(this.ignoreTruncation);
        myLookup.setRequestDclass(this.requestDclass);
        myLookup.setRequestFlagAA(this.requestFlagAA);
        myLookup.setRequestFlagAD(this.requestFlagAD);
        myLookup.setRequestFlagCD(this.requestFlagCD);
        myLookup.setRequestFlagRD(this.requestFlagRD);
        myLookup.setRequestId(this.requestId);
        myLookup.setRequestWithAdditional(this.requestWithAdditional);
        myLookup.setRequestOpcode(this.requestOpcode);
        myLookup.setDebugMode(this.debugMode);
        return myLookup;
    }

    private DnsRun generateDnsRun(Message myMessage, Resolver myResolver){
        DnsRun myDnsRun = null;


        if (this.runMode.equals("Normal")) {
            myDnsRun = new DnsRunNormal(myMessage, myResolver);
        } else if (this.runMode.equals("Statistics")) {
            myDnsRun = new DnsRunStatistics(myMessage, myResolver, this.statisticsPrimaryAnswer);
        } else if (this.runMode.equals("Full")) {
            myDnsRun = new DnsRunFull(myMessage, myResolver);
        }

        myDnsRun.setDebugMode(this.debugMode);
        myDnsRun.setSleepTime(this.sleepTime);
        myDnsRun.setTotalRequests(this.totalRequests);
        return myDnsRun;
    }

    private void clearThreadList(){
        Iterator<Thread> it = this.threadList.iterator();
        while (it.hasNext()){this.oldThreadList.add(it.next());}
        this.threadList = new ArrayList<Thread>();
    }

    private void configIpAddress(){
        if (this.debugMode){BaseFunction.dumpInfoFmt("Starting to configuring the IP address...");}
        String outInt = TalkToSystem2.getOutInterface(this.dnsServerIp, this.debugMode);
        Iterator<String> srcIpIt = this.srcIpAddress.iterator();
        String delCmd = null;
        while (srcIpIt.hasNext()){
            delCmd = TalkToSystem2.configIpAddress(srcIpIt.next(), outInt, this.debugMode);
            this.addShutdownCmd(delCmd);
        }
    }

    public void showAllParameters(){
        BaseFunction.dumpInfo("dnsServerIp="+dnsServerIp);
        BaseFunction.dumpInfo("dnsQuestion="+dnsQuestion);
        BaseFunction.dumpInfo("dnsServerPort="+dnsServerPort);
        BaseFunction.dumpInfo("dnsTimeout="+dnsTimeout);
        BaseFunction.dumpInfo("tcpEnable="+tcpEnable);
        BaseFunction.dumpInfo("ignoreTruncation="+ignoreTruncation);
        BaseFunction.dumpInfo("requestId="+requestId);
        BaseFunction.dumpInfo("recordType="+recordType);
        BaseFunction.dumpInfo("requestDclass="+requestDclass);
        BaseFunction.dumpInfo("requestsOpcode="+requestOpcode);
        BaseFunction.dumpInfo("requestFlagRD="+requestFlagRD);
        BaseFunction.dumpInfo("requestFlagAA="+requestFlagAA);
        BaseFunction.dumpInfo("requestFlagAD="+requestFlagAD);
        BaseFunction.dumpInfo("requestFlagCD="+requestFlagCD);
        BaseFunction.dumpInfo("requestWithAdditional="+requestWithAdditional);

        BaseFunction.dumpInfo("srcIpAddress="+srcIpAddress);
        BaseFunction.dumpInfo("srcPort="+srcPort);

        BaseFunction.dumpInfo("threadsTotal="+threadsTotal);
        BaseFunction.dumpInfo("totalRequests="+totalRequests);
        BaseFunction.dumpInfo("sleepTime="+sleepTime);
        BaseFunction.dumpInfo("debugMode="+debugMode);
        BaseFunction.dumpInfo("runMode="+runMode);

        BaseFunction.dumpInfo("threadList="+threadList);
        BaseFunction.dumpInfo("oldThreadList="+oldThreadList);
    }

    public void generateThreads(){
        BaseFunction.dumpInfoFmt("Starting to generate threads...");
        this.clearThreadList();
        LookupBuilder myLookup = this.generateLookuper();
        Message myMessage = myLookup.buildMessage();
        boolean isSrcIpEmpty = this.srcIpAddress.isEmpty();
        boolean isSrcPortEmpty = this.srcPort.isEmpty();

        if (this.runMode.equals("Statistics")){
            CollectStatisticsThread myCollectStatisticsThread = new CollectStatisticsThread(this.statisticsPrimaryAnswer, this.threadList);
            this.threadCollectStatistics = new Thread(myCollectStatisticsThread);
        }

        if (isSrcIpEmpty == true && isSrcPortEmpty == true){
            // No IP,  No Port
            myLookup.setSrcIpAddress("0.0.0.0");
            myLookup.setSrcPort(0);
            DnsRun myDnsRun = this.generateDnsRun(myMessage, myLookup.buildResolver());
            for (int i=1; i<=this.threadsTotal; i++){
                this.threadList.add(new Thread(myDnsRun));
            }
        }else if(isSrcIpEmpty == false && isSrcPortEmpty == false){
            // Has IP, Has Port
            BaseFunction.dumpInfoFmt("Setting the IP addresses...");
            this.configIpAddress();
            BaseFunction.dumpInfoFmt("The IP addresses have been configured.");
            Iterator<String> srcIpIt = this.srcIpAddress.iterator();
            Iterator<Integer> srcPortIt = this.srcPort.iterator();
            DnsRun myDnsRun;
            while (srcIpIt.hasNext()){
                String currentSrcIp = srcIpIt.next();
                while (srcPortIt.hasNext()){
                    //myLookup.setSrcIpAddress(srcIpIt.next());
                    myLookup.setSrcIpAddress(currentSrcIp);
                    myLookup.setSrcPort(srcPortIt.next());
                    myDnsRun = this.generateDnsRun(myMessage, myLookup.buildResolver());
                    this.threadList.add(new Thread(myDnsRun));
                }
            }

        }else if(isSrcIpEmpty == true && isSrcPortEmpty == false){
            // No IP, Has Port
            myLookup.setSrcIpAddress("0.0.0.0");
            Iterator<Integer> srcPortIt = this.srcPort.iterator();
            DnsRun myDnsRun;
            while (srcPortIt.hasNext()){
                myLookup.setSrcPort(srcPortIt.next());
                myDnsRun = this.generateDnsRun(myMessage, myLookup.buildResolver());
                this.threadList.add(new Thread(myDnsRun));
            }

        }else if(isSrcIpEmpty == false && isSrcPortEmpty == true){
            // Has IP, No Port
            BaseFunction.dumpInfoFmt("Setting the IP addresses...");
            this.configIpAddress();
            BaseFunction.dumpInfoFmt("The IP addresses have been configured.");
            myLookup.setSrcPort(0);
            Iterator<String> srcIpIt = this.srcIpAddress.iterator();
            DnsRun myDnsRun;
            while (srcIpIt.hasNext()){
                myLookup.setSrcIpAddress(srcIpIt.next());
                myDnsRun = this.generateDnsRun(myMessage, myLookup.buildResolver());
                for (int i=1; i<=this.threadsTotal; i++){
                    this.threadList.add(new Thread(myDnsRun));
                }

            }

        }

        BaseFunction.dumpInfoFmt("All the threads have been generated.");

    }

    public void interruptAllThreads(){
        Iterator<Thread> itOld = this.oldThreadList.iterator();
        Iterator<Thread> itCurrent = this.threadList.iterator();
        while (itOld.hasNext()){itOld.next().interrupt();}
        while (itCurrent.hasNext()){itCurrent.next().interrupt();}
        this.threadList = new ArrayList<Thread>();
        this.oldThreadList = new ArrayList<Thread>();
    }

    public void interruptCurrentThreads(){
        Iterator<Thread> itCurrent = this.threadList.iterator();
        while (itCurrent.hasNext()){itCurrent.next().interrupt();}
        this.threadList = new ArrayList<Thread>();
    }

    public void interruptOldThreads(){
        Iterator<Thread> itOld = this.oldThreadList.iterator();
        while (itOld.hasNext()){itOld.next().interrupt();}
        this.oldThreadList = new ArrayList<Thread>();
    }

    public void startThreads(){
        BaseFunction.dumpInfoFmt("Start all the threads...");

        Iterator<Thread> it = this.threadList.iterator();
        while (it.hasNext()){
            Thread currentThread = it.next();
            if (this.debugMode == true){BaseFunction.dumpInfoFmt("Starting thread "+currentThread);}
            currentThread.start();
            if (this.debugMode == true){BaseFunction.dumpInfoFmt("Thread "+currentThread+" is started.");}
        }

        if (this.threadCollectStatistics != null){
            if (this.debugMode){BaseFunction.dumpInfoFmt("Starting thread collectStatistics...");}
            this.threadCollectStatistics.start();
            if (this.debugMode){BaseFunction.dumpInfoFmt("Thread collectStatistics is started.");}
        }
        if (this.debugMode){BaseFunction.dumpInfoFmt("All the threads have been started.");}

    }

    public void joinThreads()  {
        if (this.debugMode){BaseFunction.dumpInfoFmt("Join all the threads...");}

        Iterator<Thread> it = this.threadList.iterator();
        while (it.hasNext()){
            try {
                it.next().join();
            } catch (InterruptedException e) {
                if (this.debugMode == true) {e.printStackTrace();}
                BaseFunction.dumpInfoFmt("The main thread meets InterruptedException!");
            }
        }
    }


}
