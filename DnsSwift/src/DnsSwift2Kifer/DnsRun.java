package DnsSwift2Kifer;

/**
 * Created by bofei on 6/19/2016.
 * The thread to be run.
 */

import org.xbill.DNS.Message;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;

import java.io.IOException;

public class DnsRun implements Runnable {
    Message requestMessage;
    Resolver resolver;
    int totalRequests = 1;
    long sleepTime = 1000;

    public DnsRun(Message requestMessage, Resolver resolver){
        this.requestMessage = requestMessage;
        this.resolver = resolver;
    }

    public DnsRun(Message requestMessage, Resolver resolver, int totalRequests){
        this.requestMessage = requestMessage;
        this.resolver = resolver;
        this.totalRequests = totalRequests;
    }

    @Override
    public void run() {

        try {

            for (int i=1; i<=totalRequests; i++){
                Message responseMessage = this.resolver.send(this.requestMessage);
                RRset [] myRRset = responseMessage.getSectionRRsets(Section.ANSWER);
                System.out.println(myRRset[0].first());
                Thread.sleep(this.sleepTime);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
