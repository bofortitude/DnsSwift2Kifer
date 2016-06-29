package DnsSwift2Kifer;

import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

import java.io.IOException;

/**
 * Created by bofei on 6/22/2016.
 */
public class DnsRunFull extends DnsRun {
    public DnsRunFull(Message requestMessage, Resolver resolver) {
        super(requestMessage, resolver);
    }

    public void run(){
        for (int i=1; i<=totalRequests; i++){
            if (this.debugMode == true){BaseFunction.dumpInfo("Sending the DNS request...");}
            try {
                Message responseMessage = this.resolver.send(this.requestMessage);
                BaseFunction.dumpInfo(responseMessage.toString()+"\n----------------------------------------------------------------------------");
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
