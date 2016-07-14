package DnsSwift2Kifer;

import org.xbill.DNS.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bofei on 6/22/2016.
 */
public class DnsRunStatistics extends DnsRun{
    ConcurrentHashMap<String, Integer> statisticsPrimaryAnswer = null;
    public DnsRunStatistics(Message requestMessage, Resolver resolver, ConcurrentHashMap<String, Integer> statisticsPrimaryAnswer) {
        super(requestMessage, resolver);
        this.statisticsPrimaryAnswer = statisticsPrimaryAnswer;
    }


    private void normalHandleResponse(Message responseMessage){
        if (responseMessage.getRcode() == Rcode.NOERROR){
            // The Rcode is NOERROR.

            String primaryAnswer="";
            StringBuffer remainAnswer = new StringBuffer("");
            int messageID = responseMessage.getHeader().getID();
            String responseFlags = responseMessage.getHeader().printFlags();

            RRset[] myRRset = responseMessage.getSectionRRsets(Section.ANSWER);
            ArrayList e = new ArrayList();
            for (RRset tmpRRset: myRRset){
                int rrsetType = tmpRRset.getType();
                if (rrsetType != Type.A && rrsetType != Type.AAAA){
                    continue;
                }else {
                    Iterator it = tmpRRset.rrs();
                    while (it.hasNext()){e.add(it.next());}
                    Record [] answers = (Record[])((Record[])e.toArray(new Record[e.size()]));
                    if (answers.length >= 1){
                        primaryAnswer = answers[0].rdataToString();
                        synchronized (this){
                            if (this.statisticsPrimaryAnswer.containsKey(primaryAnswer)){
                                this.statisticsPrimaryAnswer.put(primaryAnswer, this.statisticsPrimaryAnswer.get(primaryAnswer)+1);
                            }else {
                                this.statisticsPrimaryAnswer.put(primaryAnswer, 1);
                            }
                        }
                        /*
                        if (answers.length >= 2){
                            remainAnswer.append("(");
                            for (int tmpIndexAnswer=1; tmpIndexAnswer<answers.length; tmpIndexAnswer++){
                                if (tmpIndexAnswer == 1){remainAnswer.append(answers[tmpIndexAnswer].rdataToString());}
                                else {remainAnswer.append(","+answers[tmpIndexAnswer].rdataToString());}
                            }
                            remainAnswer.append(")");
                        }else {remainAnswer.append("()");}
                        */
                    }//else {remainAnswer.append("()");}
                    //BaseFunction.dumpInfo("MsgID="+messageID+" Flags=("+responseFlags+") Primary="+primaryAnswer+" Remain="+remainAnswer);
                    break;
                }
            }
        }else {
            // The Rcode is NOT NOERROR!
            BaseFunction.dumpInfo(responseMessage.toString()+"\n");
        }
    }

    private void sendRegularRecordRequest(){
        for (int i=1; i<=totalRequests; i++){
            try {
                //if (this.debugMode == true){BaseFunction.dumpInfo("Sending the DNS request...");}
                Message responseMessage = this.resolver.send(this.requestMessage);
                //System.out.println("Sending DNS request over.");
                this.normalHandleResponse(responseMessage);
            } catch (IOException e) {
                if (this.debugMode == true){
                    BaseFunction.dumpInfo("The DNS sending meets IOException!");
                    e.printStackTrace();
                }

                synchronized (this){
                    if (this.statisticsPrimaryAnswer.containsKey("Error")){
                        this.statisticsPrimaryAnswer.put("Error", this.statisticsPrimaryAnswer.get("Error")+1);
                    }else {
                        this.statisticsPrimaryAnswer.put("Error", 1);
                    }
                }

            }

            try {
                if (i != totalRequests){Thread.sleep(this.sleepTime);}
            } catch (InterruptedException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The thread sleep meets InterruptedException!");
            }
        }
    }

    private void sendOtherRecordRequest(){
        for (int j=1; j <= totalRequests; j++){
            try {
                Message responseMessage = this.resolver.send(this.requestMessage);
                BaseFunction.dumpInfo(responseMessage.toString()+"\n");
            } catch (IOException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The DNS sending meets IOException!");
            }

            try {
                if (j != totalRequests){Thread.sleep(this.sleepTime);}
            } catch (InterruptedException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The thread sleep meets InterruptedException!");
            }

        }

    }

    public void run(){
        int currentRecordType = this.requestMessage.getQuestion().getType();
        if (currentRecordType != Type.value("A") && currentRecordType != Type.value("AAAA")){
            this.sendOtherRecordRequest();
        }else{
            this.sendRegularRecordRequest();
        }


    }
}
