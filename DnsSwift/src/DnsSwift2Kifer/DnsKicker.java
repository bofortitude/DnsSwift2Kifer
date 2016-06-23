package DnsSwift2Kifer;

/**
 * Created by bofei on 6/19/2016.
 * The CLI entrance of the package.
 */
public class DnsKicker {

    public static void main(String [] args){
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                System.out.println("Running the exit program.....");

            }
        });


        DnsOperator myDnsOperator = new DnsOperator("8.8.8.8", "www.163.com");
        myDnsOperator.setThreadsTotal(20);
        myDnsOperator.setTotalRequests(200);
        myDnsOperator.setRunMode("Normal");
        myDnsOperator.generateThreads();
        myDnsOperator.startThreads();
        myDnsOperator.joinThreads();

    }
}
