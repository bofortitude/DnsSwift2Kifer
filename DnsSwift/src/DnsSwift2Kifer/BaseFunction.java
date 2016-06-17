package DnsSwift2Kifer;

/**
 * Created by root on 6/16/16.
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseFunction {

    public static void dumpInfo(String message){
        System.out.println(message);

    }
    public static void dumpInfoFmt(String message){
        Date date=new Date();
        SimpleDateFormat myDateFormat=new SimpleDateFormat("hh:mm:ss MM/dd/yyyy");
        String time = myDateFormat.format(date);
        System.out.println("["+time+"] "+message);

    }


}
