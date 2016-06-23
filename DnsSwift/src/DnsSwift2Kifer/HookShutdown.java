package DnsSwift2Kifer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Created by bofei on 6/23/2016.
 */
public class HookShutdown extends Thread{
    List<String> cmdList = new ArrayList<String>();

    public void addCmd(String cmd){this.cmdList.add(cmd);}

    public void runCmd(){
        Iterator<String> it = this.cmdList.iterator();
        while (it.hasNext()){

        }
        this.cmdList = new ArrayList<String>();
    }

    public void run(){
        this.runCmd();
    }

}
