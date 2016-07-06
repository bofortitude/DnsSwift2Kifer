package DnsSwift2Kifer;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by bofei on 7/3/2016.
 */
public class IpLibHandler {
    static String filePath = IpLibHandler.getPath(DnsKicker.class)+"/IpLibrary";
    static String countryMapFilePath = filePath+"/country_map.txt";
    static String ipv4MapFilePath = filePath+"/ipv4_map.txt";
    static String ipv6MapFilePath = filePath+"/ipv6_map.txt";
    static String preDefineIspFilePath = filePath+"/pre-define-isp";
    static String geoSubFolder = filePath+"/geo_ip_sub";
    static String ipv4GeoIpSub = geoSubFolder+"/ipv4";
    static String ipv6GeoIpSub = geoSubFolder+"ipv6";

    public static String getPath(Class dstClass) {
        // Run this method like: IpLibHandler.getPath(DnsKicker.class)
        URL url = dstClass.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;

        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    public static boolean isFileExists(String pathName){
        File file = new File(pathName);
        if (file.exists()){
            return true;
        }else {
            return false;
        }

    }

    public static boolean isFolderExists(String pathName){
        File file = new File(pathName);
        if (file.isDirectory() == true){
            return  true;
        }else {
            return false;
        }

    }

    public static ConcurrentHashMap<String, String> getIndexToCountryMap(){

        ConcurrentHashMap<String, String> indexToCountryMap = new ConcurrentHashMap<String, String>();
        File myFile = new File(countryMapFilePath);

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(myFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(reader);
        String line = "";
        try {
            buffer.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null){
            try {
                line = buffer.readLine();
                if (line != null){
                    String countryIndex = line.split(";")[0].trim();
                    String countryName = line.split(";")[4].trim();
                    indexToCountryMap.put(countryIndex, countryName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return indexToCountryMap;
    }

    public static ConcurrentHashMap<String, String> getCountryToIndexMap(){
        ConcurrentHashMap<String, String> countryToIndexMap = new ConcurrentHashMap<String, String>();
        File myFile = new File(countryMapFilePath);

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(myFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(reader);
        String line = "";
        try {
            buffer.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null){
            try {
                line = buffer.readLine();
                if (line != null){
                    String countryIndex = line.split(";")[0].trim();
                    String countryName = line.split(";")[4].trim();
                    countryToIndexMap.put(countryName, countryIndex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return countryToIndexMap;
    }

    public static String getCountryId(String countryName){
        return IpLibHandler.getCountryToIndexMap().get(countryName);
    }

    public static ConcurrentHashMap<String, List<String>>[] getIspProvinceMap(){
        ConcurrentHashMap<String, List<String>> ispMap = new ConcurrentHashMap<String, List<String>>();
        ConcurrentHashMap<String, List<String>> provinceMap = new ConcurrentHashMap<String, List<String>>();
        ConcurrentHashMap<String, List<String>> ispProvinceMap = new ConcurrentHashMap<String, List<String>>();

        File myFile = new File(preDefineIspFilePath);

        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(myFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(reader);
        String line = "";
        String currentIsp = "ispNull";
        String currentProvince = "provinceNull";
        while (line != null){
            try {
                line = buffer.readLine();
                if (line == null){continue;}
                if (line.indexOf("Version") != -1){continue;}
                if (line.length() == 0){continue;}


                if (line.indexOf("ISP name") != -1){
                    currentIsp = line.split(":")[1];
                }else if(line.indexOf("Province") != -1){
                    currentProvince = line.split(":")[1];
                }else {
                    if (ispMap.containsKey(currentIsp) == false){
                        List<String> newIspArray = new ArrayList<String>();
                        newIspArray.add(line);
                        ispMap.put(currentIsp, newIspArray);
                    }else {
                        ispMap.get(currentIsp).add(line);
                    }
                    if (provinceMap.containsKey(currentProvince) == false){
                        List<String> newProvinceArray = new ArrayList<String>();
                        newProvinceArray.add(line);
                        provinceMap.put(currentProvince, newProvinceArray);
                    }else {
                        provinceMap.get(currentProvince).add(line);
                    }
                    if (ispProvinceMap.containsKey(currentIsp+","+currentProvince) == false){
                        List<String> newIspProvinceArray = new ArrayList<String>();
                        newIspProvinceArray.add(line);
                        ispProvinceMap.put(currentIsp+","+currentProvince, newIspProvinceArray);
                    }else {
                        ispProvinceMap.get(currentIsp+","+currentProvince).add(line);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ConcurrentHashMap<String, List<String>> [] resultMapArray = new ConcurrentHashMap[3];
        resultMapArray[0] = ispMap;
        resultMapArray[1] = provinceMap;
        resultMapArray[2] = ispProvinceMap;
        return resultMapArray;

    }

    public static List<String> getIspIpList(String category, int total, boolean isIpv6){
        List<String> result = new ArrayList<String>();
        if (isIpv6 == true){
            return result;
        }
        // category format:  'china-telecom,Anhui', 'china-telecom,any', 'any,Anhui', 'any,any'
        String [] categorySplitedArray = category.split(",");
        if (categorySplitedArray.length < 2){return result;}
        if (categorySplitedArray[0].equals("any") && categorySplitedArray[1].equals("any")){return result;}

        ConcurrentHashMap<String, List<String>> [] ispMaps = IpLibHandler.getIspProvinceMap();
        ConcurrentHashMap<String, List<String>> ispMap = ispMaps[0];
        ConcurrentHashMap<String, List<String>> provinceMap = ispMaps[1];
        ConcurrentHashMap<String, List<String>> ispProvinceMap = ispMaps[2];
        if (ispMap.isEmpty() && provinceMap.isEmpty() && ispProvinceMap.isEmpty()){return result;}

        if (categorySplitedArray[0].equals("any") && !categorySplitedArray[1].equals("any")){
            // "any Anhui"
            if (provinceMap.containsKey(categorySplitedArray[1]) == false){
                return result;
            }else {
                Random random = new Random();
                for (int i=1; i<=total; i++){
                    String selectedSubnet = "";
                    int listLength = provinceMap.get(categorySplitedArray[1]).size();
                    int randomSequence = random.nextInt(listLength-1)%(listLength);
                    selectedSubnet = provinceMap.get(categorySplitedArray[1]).get(randomSequence);
                    result.add(IpNetUtils.genIpPrefixRandomList(selectedSubnet, 1).get(0));
                }
                return result;
            }

        }else if(!categorySplitedArray[0].equals("any") && categorySplitedArray[1].equals("any")){
            // "china-telecom any"
            if (ispMap.containsKey(categorySplitedArray[0]) == false){
                return result;
            }else {
                Random random = new Random();
                for (int j = 1; j <= total; j++) {
                    String selectedSubnet = "";
                    int listLength = ispMap.get(categorySplitedArray[0]).size();
                    int randomSequence = random.nextInt(listLength - 1) % (listLength);
                    selectedSubnet = ispMap.get(categorySplitedArray[0]).get(randomSequence);
                    result.add(IpNetUtils.genIpPrefixRandomList(selectedSubnet, 1).get(0));
                }
            }
            return result;

        }else {
            // "china-telecom Anhui"
            if (ispProvinceMap.containsKey(categorySplitedArray[0]+","+categorySplitedArray[1]) == false){
                return result;
            }else {
                Random random = new Random();
                for (int k = 1; k <= total; k++) {
                    String selectedSubnet = "";
                    int listLength = ispProvinceMap.get(categorySplitedArray[0] + "," + categorySplitedArray[1]).size();
                    int randomSequence = random.nextInt(listLength - 1) % (listLength);
                    selectedSubnet = ispProvinceMap.get(categorySplitedArray[0] + "," + categorySplitedArray[1]).get(randomSequence);
                    result.add(IpNetUtils.genIpPrefixRandomList(selectedSubnet, 1).get(0));
                }
            }
            return result;
        }

    }

    public static void splitGeoFiles(){
        File tmpfile = new File(geoSubFolder);
        if (tmpfile.isDirectory() == false){
            tmpfile.mkdir();
        }

        System.out.println(ipv4MapFilePath);
        File myFile = new File(ipv4MapFilePath);
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(myFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffer = new BufferedReader(reader);
        String line = "";
        int count = 1;
        while (line != null){
            try {
                line = buffer.readLine();
                System.out.println(line);
                count ++;
                if (count >= 20){
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





    }

    public static List<String> getCountryIpList(String category, int total, boolean isIpv6){
        List<String> result = new ArrayList<String>();
        String countryId = IpLibHandler.getCountryId(category);
        if (countryId == null){return result;}

        if (isIpv6 == false){
            // IPv4
            if (IpLibHandler.isFolderExists(geoSubFolder) == false){

            }


        }else {
            // IPv6

        }




        return result;
    }




    public static List<String> getIpList(String libType, String category, int total, boolean isIpv6){
        List<String> result = new ArrayList<String>();
        if (libType.equals("ISP")){
            if (IpLibHandler.isFileExists(preDefineIspFilePath) == false){return result;}
            return IpLibHandler.getIspIpList(category, total, isIpv6);
        }else if(libType.equals("Country")){
            if (IpLibHandler.isFileExists(countryMapFilePath) == false
                    || IpLibHandler.isFileExists(ipv4MapFilePath) == false
                    || IpLibHandler.isFileExists(ipv6MapFilePath) == false){
                return result;
            }
            return IpLibHandler.getCountryIpList(category, total, isIpv6);

        }else {
            return result;
        }
    }








    public static void main(String [] args){

        IpLibHandler.splitGeoFiles();



    }


}
