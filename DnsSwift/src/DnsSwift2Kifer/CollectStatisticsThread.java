package DnsSwift2Kifer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bofei on 7/12/2016.
 */
public class CollectStatisticsThread implements Runnable{

    ConcurrentHashMap<String, Integer> statisticsPrimaryAnswer = null;
    List<Thread> threadList = null;

    public CollectStatisticsThread(ConcurrentHashMap<String, Integer> statisticsPrimaryAnswer, List<Thread> threadList){
        this.statisticsPrimaryAnswer = statisticsPrimaryAnswer;
        this.threadList = threadList;
    }

    @Override
    public void run() {

        while (true){


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String resultShown = "";
            int totalResponse = 0;
            synchronized (this){

                if (this.statisticsPrimaryAnswer.containsKey("Error")){
                    resultShown = "Exception="+this.statisticsPrimaryAnswer.get("Error")+" ";
                    this.statisticsPrimaryAnswer.remove("Error");
                }else {
                    resultShown = "";
                }
                for (Map.Entry<String,Integer> e: this.statisticsPrimaryAnswer.entrySet()){
                    totalResponse += e.getValue();
                }

                resultShown = "Response="+totalResponse+" "+resultShown+this.statisticsPrimaryAnswer;
                BaseFunction.dumpInfoFmt(resultShown);
                this.statisticsPrimaryAnswer.clear();
            }

            synchronized (this) {
                Iterator<Thread> it = this.threadList.iterator();
                boolean isAllThreadsOver = true;
                while (it.hasNext()) {
                    Thread currentThread = it.next();
                    if (currentThread.isAlive()) {
                        isAllThreadsOver = false;
                        break;
                    }
                }

                if (isAllThreadsOver) {
                    //System.out.println("All regular threads are dead!!");
                    break;
                }
            }



        }

    }
}
