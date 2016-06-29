package DnsSwift2Kifer;

import org.xbill.DNS.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by bofei on 6/22/2016.
 */

public class DnsRunNormal extends DnsRun {

    public DnsRunNormal(Message requestMessage, Resolver resolver) {super(requestMessage, resolver);}

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
                        if (answers.length >= 2){
                            remainAnswer.append("(");
                            for (int tmpIndexAnswer=1; tmpIndexAnswer<answers.length; tmpIndexAnswer++){
                                if (tmpIndexAnswer == 1){remainAnswer.append(answers[tmpIndexAnswer].rdataToString());}
                                else {remainAnswer.append(","+answers[tmpIndexAnswer].rdataToString());}
                            }
                            remainAnswer.append(")");
                        }else {remainAnswer.append("()");}
                    }else {remainAnswer.append("()");}
                    BaseFunction.dumpInfo("MsgID="+messageID+" Flags=("+responseFlags+") Primary="+primaryAnswer+" Remain="+remainAnswer);
                    break;
                }
            }
        }else {
            // The Rcode is NOT NOERROR!
            BaseFunction.dumpInfo(responseMessage.toString()+"\n");
        }
    }

    public void run(){
        for (int i=1; i<=totalRequests; i++){
            try {
                //if (this.debugMode == true){BaseFunction.dumpInfo("Sending the DNS request...");}
                Message responseMessage = this.resolver.send(this.requestMessage);
                //System.out.println("Sending DNS request over.");
                this.normalHandleResponse(responseMessage);
            } catch (IOException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The DNS sending meets IOException!");
            }

            try {
                if (i != totalRequests){Thread.sleep(this.sleepTime);}
            } catch (InterruptedException e) {
                if (this.debugMode == true){e.printStackTrace();}
                BaseFunction.dumpInfo("The thread sleep meets InterruptedException!");
            }
        }

    }

}

