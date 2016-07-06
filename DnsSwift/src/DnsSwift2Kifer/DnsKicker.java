package DnsSwift2Kifer;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bofei on 6/19/2016.
 * The CLI entrance of the package.
 */
public class DnsKicker {

    String [] args = null;
    Options myOptions = new Options();
    CommandLine myCLI = null;
    List<String> optionNeedArg = new ArrayList<String>();
    List<String> optionNoArg = new ArrayList<String>();
    List<String > optionWithoutHyphen = new ArrayList<String>();
    DnsOperator myDnsOperator = null;


    public DnsKicker(String [] args){this.args = args;}

    private void addOptionAndClassify(String shortName, String longName, boolean hasArg, String description){
        this.myOptions.addOption(shortName, longName, hasArg, description);
        if (hasArg == true){
            this.optionNeedArg.add("-"+shortName);
            this.optionNeedArg.add("--"+longName);
        }else {
            this.optionNoArg.add("-"+shortName);
            this.optionNoArg.add("--"+longName);
        }
    }

    private void addOptionAndClassify(String shortName, boolean hasArg, String description){
        this.myOptions.addOption(shortName, hasArg, description);
        if (hasArg == true){
            this.optionNeedArg.add("-"+shortName);
        }else {
            this.optionNoArg.add("-"+shortName);
        }
    }

    private int getIntArgValue(String argName, int defaultValue){
        if (this.myCLI.hasOption(argName)){
            String stringResult = this.myCLI.getOptionValue(argName);
            return Integer.valueOf(stringResult).intValue();
        }else {return defaultValue;}
    }

    private boolean getBooleanArgValue(String argName){
        if (this.myCLI.hasOption(argName)){
            return true;
        }else {
            return false;
        }
    }

    private String getStringArgValue(String argName, String defaultValue){
        if (this.myCLI.hasOption(argName)){
            return this.myCLI.getOptionValue(argName);
        }else {
            return defaultValue;
        }
    }

    private List<String> getSrcIpAddress(){
        String argName = "source-address";
        List<String> result = new ArrayList<String>();
        if (this.myCLI.hasOption(argName)){
            String originalSrcAddressStr = this.myCLI.getOptionValue(argName);
            if (originalSrcAddressStr.indexOf("=") == -1){
                // type -> 10.76.1.1,10.77.2.1,10.88.1.1-10.88.2.10,10.100.1.3
                result = IpNetUtils.genIpRangeList(originalSrcAddressStr);

            }else {
                // type -> 10.76.1.0/24=10
                String [] subnetAndPrefixArray = originalSrcAddressStr.split("=");
                String mySubnet = subnetAndPrefixArray[0];
                int prefixNum = Integer.parseInt(subnetAndPrefixArray[1]);
                result = IpNetUtils.genIpPrefixRandomList(mySubnet, prefixNum);
            }
        }else {return result;}
        return result;
    }

    private List<Integer> genConsecutivePort(int start, int end){
        List<Integer> result = new ArrayList<Integer>();
        if (end < start){
            return result;
        }else {
            for (int i=start; i<=end; i++){
                result.add(i);
            }
        }
        return result;
    }

    private List<Integer> getSrcPort(){
        String argName = "source-port";
        List<Integer> result = new ArrayList<Integer>();
        if (this.myCLI.hasOption(argName)){
            String originalSrcPortString = this.myCLI.getOptionValue(argName);
            String [] portFieldArray = originalSrcPortString.split(",");
            for (String i : portFieldArray){
                if (i.indexOf("-") == -1){
                    result.add(Integer.parseInt(i));
                }else {
                    int startNum = Integer.parseInt(i.split("-")[0]);
                    int endNum = Integer.parseInt(i.split("-")[1]);
                    result.addAll(this.genConsecutivePort(startNum, endNum));
                }
            }
        }else {
            return result;
        }
        return result;
    }

    private void addOptions(){

        addOptionAndClassify("h", "help", false, "Print help for DnsSwift2Kifer.");
        addOptionAndClassify("c", "concurrent", true, "Specify the concurrent threads number.");
        addOptionAndClassify("r", "requests", true, "Specify the requests number per thread.");
        addOptionAndClassify("i", "interval", true, "Specify the interval between reuqests, default is 1.");
        addOptionAndClassify("s", "source-address", true, "Specify the source address. "+"\nFormat:\n \"10.76.1.0/24=10\" "+" \n\"10.76.1.1,10.77.2.1,10.88.1.1-10.88.2.10,10.100.1.3\".");
        addOptionAndClassify("p", "source-port", true, "Specify the source port. Format: \"2234,4532,5423-5490,2452\".");
        addOptionAndClassify("d", "dns-port", true, "Specify the dns port, default is 53.");
        addOptionAndClassify("n", "timeout", true, "Timeout or every request, default is 5s.");
        addOptionAndClassify("t", "tcp", false, "Use TCP as transport protocol once it is taken.");
        addOptionAndClassify("o", "record-type", true, "Specify the record type, default is \"A\".");
        //addOptionAndClassify("m", "reuse-session", false, "Reuse the session to send requests once it is taken.");
        addOptionAndClassify("f", "show-full", false, "Show full response info once it is taken.");
        addOptionAndClassify("w", "watch-statistics", false, "Show statistics info only once it is taken.");
        addOptionAndClassify("optionCode", true, "Specify the option code, default is \"QUERY\".");
        addOptionAndClassify("ignoreTruncation", false, "The \"TC\" flag will be ignored once this option is set.");
        addOptionAndClassify("debug", false, "Enable the debug mode.");
        //addOptionAndClassify("ispAddress", true, "Specify the source addresses from ISP predefined base. Format: \"china-telecom Anhui\" or \"any Henan\".");
        //addOptionAndClassify("geoAddress", true, "Specify the source addresses from GEO IP library. Format: --geo-address \"Andorra\".");
        //addOptionAndClassify("totalAddress", true, "Specify total isp address or total geo address, default is 1.");
        addOptionAndClassify("id", true, "Specify the request message ID.");
        addOptionAndClassify("noRecurse", false, "Unset the recurse bit once it is taken.");
        addOptionAndClassify("noAdditional", false, "Unset the additional record for request.");
        addOptionAndClassify("rdclass", true, "Specify the rdclass, default is \"IN\".");
        addOptionAndClassify("aaonly", false, "Set the aa bit once it is taken.");
        addOptionAndClassify("adflag", false, "Set the ad bit once it is taken.");
        addOptionAndClassify("cdflag", false, "Set the cd bit once it is taken.");


    }

    private String [] stripOptionWithoutHyphen(){
        List<String> result = new ArrayList<String>();
        String lastArg = null;
        String currentArg = null;

        for (String i: this.args){
            currentArg = i;
            if (lastArg == null){
                if (currentArg.indexOf("-") == 0){
                    result.add(currentArg);
                }else {
                    this.optionWithoutHyphen.add(currentArg);
                }
            }else if (this.optionNeedArg.contains(lastArg)){
                result.add(currentArg);
            }else {
                if (currentArg.indexOf("-") == 0){
                    result.add(currentArg);

                }else {
                    this.optionWithoutHyphen.add(currentArg);
                }
            }
            lastArg = currentArg;
        }

        return (String[]) result.toArray(new String [result.size()]);
    }

    private void showHelp(){
        HelpFormatter helpMessage = new HelpFormatter();
        //helpMessage.printHelp("\n./DnsSwift2Kifer.sh <DNS Server IP> <Domain Name> [Options] ", this.myOptions);
        helpMessage.printHelp(100,
                "\n./DnsSwift2Kifer.sh <DNS Server IP> <Domain Name> [Options]\n\n",
                "Options:",
                this.myOptions,
                "\nMaintainer: bofei@fortinet.com | FortiADC QA",
                false);
        System.exit(1);
    }

    public void parseArgs(){
        if (this.args == null){this.showHelp();}

        this.addOptions();
        String [] strippedArgs = this.stripOptionWithoutHyphen();

        if (this.optionWithoutHyphen.size() < 2){this.showHelp();}

        CommandLineParser systemParser = new DefaultParser();
        try {
            this.myCLI = systemParser.parse(this.myOptions, strippedArgs);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (this.myCLI.hasOption("h")){this.showHelp();}
    }

    public void setArgs(){

        String [] myRegularOption = (String []) this.optionWithoutHyphen.toArray(new String [this.optionWithoutHyphen.size()]);

        String dnsServerIp = myRegularOption[0];
        String dnsQuestion = myRegularOption[1];
        int dnsServerPort = 53;
        int dnsTimeout = 10;
        boolean tcpEnable = false;
        boolean ignoreTruncation = false;
        int requestId = 0;
        String recordType = "A";
        String requestDclass = "IN";
        String requestOpcode = "QUERY";
        boolean requestFlagRD = true;
        boolean requestFlagAA = false;
        boolean requestFlagAD = false;
        boolean requestFlagCD = false;
        boolean requestWithAdditional = true;
        List<String> srcIpAddress = new ArrayList<String>();
        List<Integer> srcPort = new ArrayList<Integer>();
        int threadsTotal = 1;
        int totalRequests = 1;
        long sleepTime = 1000;
        boolean debugMode = false;
        String runMode = "Normal";  // Valid: "Normal", "Statistics", "Full"

        this.myDnsOperator = new DnsOperator(dnsServerIp, dnsQuestion);
        dnsServerPort = this.getIntArgValue("dns-port", dnsServerPort);
        dnsTimeout = this.getIntArgValue("timeout", dnsTimeout);
        tcpEnable = this.getBooleanArgValue("tcp");
        ignoreTruncation = this.getBooleanArgValue("ignoreTruncation");
        requestId = this.getIntArgValue("id", requestId);
        recordType = this.getStringArgValue("record-type", recordType);
        requestDclass = this.getStringArgValue("rdclass", requestDclass);
        requestOpcode = this.getStringArgValue("optionCode", requestOpcode);
        if (this.getBooleanArgValue("noRecurse") == true){requestFlagRD = false;}
        requestFlagAA = this.getBooleanArgValue("aaonly");
        requestFlagAD = this.getBooleanArgValue("adflag");
        requestFlagCD = this.getBooleanArgValue("cdflag");
        if (this.getBooleanArgValue("noAdditional") == true){requestWithAdditional = false;}
        srcIpAddress = this.getSrcIpAddress();
        srcPort = this.getSrcPort();
        threadsTotal = this.getIntArgValue("concurrent", threadsTotal);
        totalRequests = this.getIntArgValue("requests", totalRequests);
        if (this.myCLI.hasOption("interval")){
            Double doubleSleep = Double.valueOf(this.myCLI.getOptionValue("interval"));
            sleepTime = new Double(doubleSleep*1000).longValue();
        }
        debugMode = this.getBooleanArgValue("debug");
        if (this.myCLI.hasOption("watch-statistics")){runMode = "Statistics";}
        if (this.myCLI.hasOption("show-full")){runMode = "Full";}

        this.myDnsOperator = new DnsOperator(dnsServerIp, dnsQuestion);
        this.myDnsOperator.setDnsServerPort(dnsServerPort);
        this.myDnsOperator.setDnsTimeout(dnsTimeout);
        this.myDnsOperator.setTcpEnable(tcpEnable);
        this.myDnsOperator.setIgnoreTruncation(ignoreTruncation);
        this.myDnsOperator.setRequestId(requestId);
        this.myDnsOperator.setRecordType(recordType);
        this.myDnsOperator.setRequestDclass(requestDclass);
        this.myDnsOperator.setRequestOpcode(requestOpcode);
        this.myDnsOperator.setRequestFlagRD(requestFlagRD);
        this.myDnsOperator.setRequestFlagAA(requestFlagAA);
        this.myDnsOperator.setRequestFlagAD(requestFlagAD);
        this.myDnsOperator.setRequestFlagCD(requestFlagCD);
        this.myDnsOperator.setRequestWithAdditional(requestWithAdditional);
        this.myDnsOperator.setSrcIpAddress(srcIpAddress);
        this.myDnsOperator.setSrcPort(srcPort);
        this.myDnsOperator.setThreadsTotal(threadsTotal);
        this.myDnsOperator.setTotalRequests(totalRequests);
        this.myDnsOperator.setSleepTime(sleepTime);
        this.myDnsOperator.setDebugMode(debugMode);
        this.myDnsOperator.setRunMode(runMode);

    }

    public void generateThreads(){
        this.myDnsOperator.generateThreads();
    }

    public void startThreads(){
        this.myDnsOperator.startThreads();
    }

    public void joinThreads(){
        this.myDnsOperator.joinThreads();
    }


    public static void main(String [] args){

        String[] myArgs = new String[]{"8.8.8.8", "www.163.com", "-h"};
        DnsKicker kicker = new DnsKicker(myArgs);

        //DnsKicker kicker = new DnsKicker(args);

        kicker.parseArgs();
        kicker.setArgs();
        if (kicker.myCLI.hasOption("debug")){
            BaseFunction.dumpInfo("\nHere are all the parameters in DnsOperator:");
            kicker.myDnsOperator.showAllParameters();
            BaseFunction.dumpInfo("");
        }

        kicker.generateThreads();
        kicker.startThreads();
        kicker.joinThreads();

    }

}
