package DnsSwift2Kifer;

import org.xbill.DNS.Message;
import org.xbill.DNS.Resolver;

/**
 * Created by bofei on 6/19/2016.
 * Thre main starter.
 */
public class DnsOperator {

    public static void main(String [] args) {
        //LookupBuilder myLookup = new LookupBuilder("172.30.154.52", "app1.testglb.com");

        LookupBuilder myLookup = new LookupBuilder("8.8.8.8", "www.163.com");
        Message myMessage = myLookup.buildMessage();
        Resolver myResolver = myLookup.buildResolver();

        DnsRun a = new DnsRunNormal(myMessage, myResolver);
        Thread [] myThreads = new Thread[1];
        for (int aa=0; aa<1; aa++){
            myThreads[aa] = new Thread(a);
        }

        for (Thread nn: myThreads){
            nn.start();
        }

        for (Thread nn: myThreads){
            try {
                nn.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }






    }


}
