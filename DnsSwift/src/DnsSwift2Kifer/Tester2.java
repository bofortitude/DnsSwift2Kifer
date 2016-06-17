package DnsSwift2Kifer;

/**
 * Created by root on 6/14/16.
 */

import org.xbill.DNS.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class Tester2 {




    public static void main(String [] args){

        String domainName = "app1.testglb.com";
        String dnsServer = "10.76.1.52";
        int dnsPort = 53;
        String srcIpAddr = "0.0.0.0";
        int srcPort = 0;
        int dnsTimeout = 10;
        boolean tcpEnable = false; //Sets whether TCP connections will be sent by default
        boolean ignoreTruncation = false;  //Sets whether truncated responses will be ignored. If not, a truncated response over UDP will cause a retransmission over TCP.
        int resultRcode;


        BaseFunction.dumpInfoFmt("Start the program...");

        try {
            SimpleResolver myResolver = new SimpleResolver();
            myResolver.setAddress(InetAddress.getByName(dnsServer));
            myResolver.setPort(dnsPort);
            InetSocketAddress srcIpPort = new InetSocketAddress(srcIpAddr, srcPort);
            myResolver.setLocalAddress(srcIpPort);
            myResolver.setIgnoreTruncation(ignoreTruncation);
            myResolver.setTCP(tcpEnable);
            myResolver.setTimeout(dnsTimeout);
            Lookup lookupER = new Lookup(domainName, Type.A, DClass.IN);
            lookupER.setResolver(myResolver);
            lookupER.run();



            resultRcode = lookupER.getResult();
            //resultReason = Rcode.string(resultRcode);

            if (resultRcode == Lookup.SUCCESSFUL){
                // The rcode is NOERROR.
                Record [] resultAnswers = lookupER.getAnswers();
                String primaryRecord = resultAnswers[0].rdataToString();

                StringBuffer remainRecord = new StringBuffer();
                if (resultAnswers.length > 1){
                    for (int i = 1; i<resultAnswers.length; i++){
                        remainRecord.append(resultAnswers[i].rdataToString()+" ");
                    }
                }

                BaseFunction.dumpInfo("primary="+primaryRecord+" remain="+"("+remainRecord+")");





            } else {
                // The rcode is not NOERROR.
                String resultReason = Rcode.string(resultRcode);



            }

        } catch (TextParseException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }



}
