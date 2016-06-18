package DnsSwift2Kifer;

/**
 * Created by root on 6/14/16.
 */
public class Tester1 {
    public static void main(String [] args){
        Lookuper myLookuper = new Lookuper("8.8.8.8", "www.google.com");
        myLookuper.setRecordType("A");
        myLookuper.setTcpEnable(true);
        myLookuper.send();

    }
}
