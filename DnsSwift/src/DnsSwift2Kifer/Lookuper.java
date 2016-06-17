package DnsSwift2Kifer;

/**
 * Created by root on 6/16/16.
 */

import org.xbill.DNS.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Lookuper {
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



    private SimpleResolver myResolver = null;
    private Message requestMessage =null;


    public Lookuper(String dnsServerIp, String dnsQuestion){
        this.dnsServerIp = dnsServerIp;
        this.dnsQuestion = dnsQuestion;
    }

    public Lookuper(String dnsServerIp, String dnsQuestion, String recordType){
        this.dnsServerIp = dnsServerIp;
        this.dnsQuestion = dnsQuestion;
        this.recordType = recordType;
    }

    public void setDnsServerIp(String dnsServerIp){
        this.dnsServerIp = dnsServerIp;
        /*
        if (this.myResolver != null){
            this.myResolver.setAddress(new InetSocketAddress(this.dnsServerIp, this.dnsServerPort));
        }
        */
    }

    public void setDnsServerPort(int dnsServerPort){
        this.dnsServerPort = dnsServerPort;
        /*
        if (this.myResolver != null){
            this.myResolver.setAddress(new InetSocketAddress(this.dnsServerIp, this.dnsServerPort));
        }
        */
    }

    public void setSrcIpAddress(String srcIpAddress){
        this.srcIpAddress = srcIpAddress;
        /*
        if (this.myResolver != null){
            this.myResolver.setLocalAddress(new InetSocketAddress(this.srcIpAddress, this.srcPort));
        }
        */
    }

    public void setSrcPort(int srcPort){
        this.srcPort = srcPort;
        /*
        if (this.myResolver != null){
            this.myResolver.setLocalAddress(new InetSocketAddress(this.srcIpAddress, this.srcPort));
        }
        */
    }

    public void setDnsTimeout(int dnsTimeout){
        this.dnsTimeout = dnsTimeout;
        /*
        if (this.myResolver != null){
            this.myResolver.setTimeout(this.dnsTimeout);
        }
        */
    }

    public void setTcpEnable(boolean tcpEnable){
        this.tcpEnable = tcpEnable;
        /*
        if (this.myResolver != null){
            this.myResolver.setTCP(this.tcpEnable);
        }
        */
    }

    public void setIgnoreTruncation(boolean ignoreTruncation){
        this.ignoreTruncation = ignoreTruncation;
        /*
        if (this.myResolver != null){
            this.myResolver.setIgnoreTruncation(this.ignoreTruncation);
        }
        */
    }

    public void setDnsQuestion(String dnsQuestion){
        this.dnsQuestion = dnsQuestion;
    }

    public void setRequestId(int requestId){
        this.requestId = requestId;
    }

    public void setRecordType(String recordType){
        this.recordType = recordType;
    }

    public void setRequestDclass(String requestDclass){
        this.requestDclass = requestDclass;
    }

    public void setRequestFlagRD(boolean requestFlagRD){
        this.requestFlagRD = requestFlagRD;
    }

    public void setRequestFlagAA(boolean requestFlagAA){
        this.requestFlagAA = requestFlagAA;
    }

    public void setRequestFlagAD(boolean requestFlagAD){
        this.requestFlagAD = requestFlagAD;
    }

    public void setRequestFlagCD(boolean requestFlagCD){
        this.requestFlagCD = requestFlagCD;
    }

    private Record genRecord(){
        try {
            Name domainName = new Name(this.dnsQuestion);
            int myRecordType = Type.value(this.recordType);
            if (myRecordType == -1){
                myRecordType = Type.value("A");
            }
            int myDclass = DClass.value(this.requestDclass);
            if (myDclass == -1){
                myDclass = DClass.value("IN");
            }

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

    private Message genMessage(){
        Message requestMessage;
        if (this.requestId != 0){
            requestMessage = new Message(this.requestId);
        }else {
            requestMessage = new Message();
        }

        Record myRecord = this.genRecord();
        if (myRecord == null){return null;}
        requestMessage.setHeader(this.genHeader());
        requestMessage.addRecord(myRecord, 0);
        return requestMessage;
    }

    public void buildMessage(){
        Message myMessage = this.genMessage();
        if (myMessage != null){this.requestMessage = myMessage;}
    }

    public void buildResolver(){
        try {
            if (this.myResolver == null){this.myResolver = new SimpleResolver();}

            this.myResolver.setAddress(new InetSocketAddress(this.dnsServerIp, this.dnsServerPort));
            this.myResolver.setLocalAddress(new InetSocketAddress(this.srcIpAddress, this.srcPort));
            this.myResolver.setTimeout(this.dnsTimeout);
            this.myResolver.setTCP(this.tcpEnable);
            this.myResolver.setIgnoreTruncation(this.ignoreTruncation);


        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.myResolver = null;
        }

    }

    public void send(){



    }



}





