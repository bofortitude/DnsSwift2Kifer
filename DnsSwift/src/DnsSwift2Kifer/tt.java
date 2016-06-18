package DnsSwift2Kifer;

import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.Flags;

/**
 * Created by root on 6/16/16.
 */
public class tt {

    public static void main(String [] args){

        System.out.println(Flags.value("AA"));
        System.out.println(Flags.value("RD"));
        System.out.println(Flags.value("AD"));
        System.out.println(Flags.value("CD"));


    }
}
