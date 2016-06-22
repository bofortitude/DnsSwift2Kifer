package DnsSwift2Kifer;

/**
 * Created by bofei on 6/19/2016.
 * Generate Message and Resolver.
 */

import org.xbill.DNS.*;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class LookupBuilder {

    private String dnsServerIp = null;
    private String dnsQuestion = null;
    private int dnsServerPort = 53;
    private String srcIpAddress = "0.0.0.0";
    private int srcPort = 0;
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



    public LookupBuilder(String dnsServerIp, String dnsQuestion){
        this.dnsServerIp = dnsServerIp;
        this.dnsQuestion = dnsQuestion;
    }

    public LookupBuilder(String dnsServerIp, String dnsQuestion, String recordType){
        this.dnsServerIp = dnsServerIp;
        this.dnsQuestion = dnsQuestion;
        this.recordType = recordType;
    }

    public void setDnsServerIp(String dnsServerIp){this.dnsServerIp = dnsServerIp;}

    public void setDnsServerPort(int dnsServerPort){this.dnsServerPort = dnsServerPort;}

    public void setSrcIpAddress(String srcIpAddress){this.srcIpAddress = srcIpAddress;}

    public void setSrcPort(int srcPort){this.srcPort = srcPort;}

    public void setDnsTimeout(int dnsTimeout){this.dnsTimeout = dnsTimeout;}

    public void setTcpEnable(boolean tcpEnable){this.tcpEnable = tcpEnable;}

    public void setIgnoreTruncation(boolean ignoreTruncation){this.ignoreTruncation = ignoreTruncation;}

    public void setDnsQuestion(String dnsQuestion){this.dnsQuestion = dnsQuestion;}

    public void setRequestId(int requestId){this.requestId = requestId;}

    public void setRecordType(String recordType){this.recordType = recordType;}

    public void setRequestDclass(String requestDclass){this.requestDclass = requestDclass;}

    public void setRequestFlagRD(boolean requestFlagRD){this.requestFlagRD = requestFlagRD;}

    public void setRequestFlagAA(boolean requestFlagAA){this.requestFlagAA = requestFlagAA;}

    public void setRequestFlagAD(boolean requestFlagAD){this.requestFlagAD = requestFlagAD;}

    public void setRequestFlagCD(boolean requestFlagCD){this.requestFlagCD = requestFlagCD;}

    public void setRequestWithAdditional(boolean requestWithAdditional){this.requestWithAdditional = requestWithAdditional;}

    private Record genRecord(){
        try {
            Name domainName = new Name(this.dnsQuestion);
            if (domainName.isAbsolute() == false){
                this.dnsQuestion = this.dnsQuestion+".";
                domainName = new Name(this.dnsQuestion);
            }
            int myRecordType = Type.value(this.recordType);
            if (myRecordType == -1){myRecordType = Type.value("A");}
            int myDclass = DClass.value(this.requestDclass);
            if (myDclass == -1){myDclass = DClass.value("IN");}

            return Record.newRecord(domainName, myRecordType, myDclass);

        } catch (TextParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    private Header genHeader(){
        Header myHeader;
        if (this.requestId != 0){ myHeader = new Header(this.requestId); }else { myHeader = new Header(); }

        int myOpcode = Opcode.value(this.requestOpcode);
        if (myOpcode == -1){ myOpcode = Opcode.value("QUERY"); }

        int myFlags;
        if (this.requestFlagRD == true){ myFlags = Flags.value("RD"); }else { myFlags = 0; }
        if (this.requestFlagAA == true){ myFlags |= Flags.value("AA"); }
        if (this.requestFlagAD == true){ myFlags |= Flags.value("AD"); }
        if (this.requestFlagCD == true){ myFlags |= Flags.value("CD"); }

        myHeader.setOpcode(myOpcode);
        myHeader.setFlag(myFlags);

        return myHeader;
    }

    public Message buildMessage(){
        Message requestMessage;
        if (this.requestId != 0){
            requestMessage = new Message(this.requestId);
        }else {
            requestMessage = new Message();
        }

        Record myRecord = this.genRecord();
        if (myRecord == null){return null;}
        requestMessage.setHeader(this.genHeader());
        requestMessage.addRecord(myRecord, Section.QUESTION);
        if (this.requestWithAdditional == true){requestMessage.addRecord(new OPTRecord(4096, 0, 0), Section.ADDITIONAL);}

        return requestMessage;
    }

    public Resolver buildResolver(){
        try {
            SimpleResolver myResolver;
            myResolver = new SimpleResolver();

            myResolver.setAddress(new InetSocketAddress(this.dnsServerIp, this.dnsServerPort));
            myResolver.setLocalAddress(new InetSocketAddress(this.srcIpAddress, this.srcPort));
            myResolver.setTimeout(this.dnsTimeout);
            myResolver.setTCP(this.tcpEnable);
            myResolver.setIgnoreTruncation(this.ignoreTruncation);

            return myResolver;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }

    }


}
