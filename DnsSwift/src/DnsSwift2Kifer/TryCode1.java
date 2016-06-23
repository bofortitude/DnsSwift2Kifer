package DnsSwift2Kifer;

import org.xbill.DNS.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bofei on 6/21/2016.
 */
public class TryCode1 {

    public static void main(String [] args){

        LookupBuilder abc = null;
        abc = new LookupBuilder("fds", "fdsa");
        System.out.println(abc);
        abc = new LookupBuilder("fds", "fdsa");
        System.out.println(abc);


    }

}
