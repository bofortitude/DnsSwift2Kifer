package DnsSwift2Kifer;

import com.github.jgonian.ipmath.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by bofei on 6/29/2016.
 */
public class IpNetUtils {

    public static String getSubnetAndPrefix(String subnetAndPrefix, boolean isIpv6){
        String result = null;
        String [] subnetStringArray = subnetAndPrefix.split("/");
        BigInteger subnetNum = null;
        if (isIpv6 == false){
            // IPv4
            subnetNum = Ipv4.of(subnetStringArray[0]).asBigInteger();
        }else {
            // IPv6
            subnetNum = Ipv6.of(subnetStringArray[0]).asBigInteger();
        }
        int prefixNum = Integer.parseInt(subnetStringArray[1]);
        if (isIpv6 == false){
            // IPv4
            int untilPrefix = 31-prefixNum;
            for (int i=0; i<=untilPrefix; i++){
                subnetNum = subnetNum.clearBit(i);
            }

        }else {
            // IPv6
            int untilPrefix = 127-prefixNum;
            for (int j=0; j<=untilPrefix; j++){
                subnetNum = subnetNum.clearBit(j);
            }
        }

        if (isIpv6 == false){
            // IPv4
            result = Ipv4.of(subnetNum).toString()+"/"+subnetStringArray[1];

        }else {
            // IPv6
            result = Ipv6.of(subnetNum).toString()+"/"+subnetStringArray[1];
        }
        return result;
    }

    public static List<String> getWholeIpAddresses(String start, String end){
        List<String> result = new ArrayList<String>();
        boolean isIpv6 = false;
        if (start.indexOf(":") != -1 && end.indexOf(":") != -1){isIpv6 = true;}
        else if (start.indexOf(":") == -1 && end.indexOf(":") == -1){isIpv6 = false;}
        else {return null;}

        try {
            if (isIpv6 == false) {
                // IPv4
                Ipv4 startIp = Ipv4.of(start);
                Ipv4 endIp = Ipv4.of(end);
                Ipv4Range ipv4Range = Ipv4Range.from(startIp).to(endIp);
                Iterator it = ipv4Range.iterator();
                while (it.hasNext()) {
                    result.add(it.next().toString());
                }


            } else {
                // IPv6
                Ipv6 startIp6 = Ipv6.of(start);
                Ipv6 endIp6 = Ipv6.of(end);
                Ipv6Range ipv6Range = Ipv6Range.from(startIp6).to(endIp6);
                Iterator it = ipv6Range.iterator();
                while (it.hasNext()) {
                    result.add(it.next().toString());
                }
            }
        }catch (Exception e){return null;}

        return result;
    }

    public static List<String> genIpRangeList(String ipRangeString){
        //ip range string like: "10.76.1.1,10.77.2.1,10.88.1.1-10.88.2.10,10.100.1.3"
        List<String> result = new ArrayList<String>();
        if (ipRangeString.equals("")){return result;}
        if (ipRangeString == null){return result;}

        String [] splitedComma = ipRangeString.split(",");
        for (String i : splitedComma){
            String [] splitedBar = i.split("-");
            if (splitedBar.length == 1){
                result.add(i);
            }else {
                List<String> tmpRange = IpNetUtils.getWholeIpAddresses(splitedBar[0], splitedBar[1]);
                if (tmpRange != null){result.addAll(tmpRange);}
            }
        }

        return result;
    }

    public static List<String> genIpPrefixRandomList(String ipPrefixString, int total){
        List<String> result = new ArrayList<String>();
        if (ipPrefixString.indexOf(":") == -1){
            // IPv4

            String modifiedPrefixString = IpNetUtils.getSubnetAndPrefix(ipPrefixString, false);
            Ipv4Range subnet = Ipv4Range.parse(modifiedPrefixString);
            BigInteger startIpBigInteger = subnet.start().asBigInteger();
            BigInteger endIpBigInteger = subnet.end().asBigInteger();
            BigInteger rangeValueBigInteger = endIpBigInteger.subtract(startIpBigInteger);

            for (int i=1;i<=total;i++) {
                Random rnd = new Random();
                do {
                    BigInteger j = new BigInteger(rangeValueBigInteger.bitLength(), rnd);
                    if (j.compareTo(rangeValueBigInteger) <= 0) {
                        result.add(Ipv4.of(startIpBigInteger.add(j)).toString());
                        break;
                    }
                } while (true);
            }

        }else {
            // IPv6

            String modifiedPrefixString = IpNetUtils.getSubnetAndPrefix(ipPrefixString, true);
            Ipv6Range subnet = Ipv6Range.parse(modifiedPrefixString);
            BigInteger startIpNumValue = subnet.start().asBigInteger();
            BigInteger endIpNumValue = subnet.end().asBigInteger();
            //BigInteger minusValue = endIpNumValue.add(startIpNumValue.negate());
            BigInteger minusValue = endIpNumValue.subtract(startIpNumValue);
            for (int j=1;j<=total;j++){
                Random random = new Random();
                BigInteger r;
                do{
                    r = new BigInteger(minusValue.bitLength(), random);
                }while (r.compareTo(minusValue) >= 0);
                result.add(Ipv6.of(r.add(startIpNumValue)).toString());

            }

        }

        return result;
    }

    public static String genIpv4AddressFromRange(String startIpNum, String endIpNum){
        String result = null;
        Long startValue = Long.parseLong(startIpNum);
        Long endValue = Long.parseLong(endIpNum);
        Random random = new Random();
        Long selectedIpValue = Math.abs(random.nextLong())%(endValue-startValue)+startValue;
        result = Ipv4.of(selectedIpValue).toString();
        return result;
    }

    public static String genIpv6AddressFromRange(String startIpString, String endIpString){
        String result = null;
        BigInteger startIpValue = Ipv6.of(startIpString).asBigInteger();
        BigInteger endIpValue = Ipv6.of(endIpString).asBigInteger();
        BigInteger minusValue = endIpValue.add(startIpValue.negate());
        BigInteger r;
        Random random = new Random();
        do{
            r = new BigInteger(minusValue.bitLength(), random);
        }while (r.compareTo(minusValue) >= 0);
        result = Ipv6.of(r.add(startIpValue)).toString();

        return result;
    }





}
