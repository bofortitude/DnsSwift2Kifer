package DnsSwift2Kifer;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.github.jgonian.ipmath.*;
import org.xbill.DNS.Rcode;

/**
 * Created by bofei on 6/28/2016.
 */
public class TryCode1 {
    public static void main(String [] args){


        String filePath = IpLibHandler.getPath(DnsKicker.class);
        String countryMapFilePath = filePath+"/IpLibrary/country_map.txt";
        File file = new File(countryMapFilePath);
        System.out.println(file.isDirectory());












    }
}
