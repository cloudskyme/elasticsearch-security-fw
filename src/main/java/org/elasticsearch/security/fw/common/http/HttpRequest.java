package org.elasticsearch.security.fw.common.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    /**
     * 
     * @param url
     * @param param  name1=value1&name2=value2
     * @param headerMap
     * @return 
     */
	public static String sendGet(String url, String param, Map<String, String> headerMap) throws Exception{
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            
            URLConnection connection = realUrl.openConnection();
            
            connection.setConnectTimeout(5);
            connection.setReadTimeout(500);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if(headerMap!=null){
            	for (String key : headerMap.keySet()){
            		connection.setRequestProperty(key, headerMap.get(key));
            	}
            }
            
            connection.connect();

            Map<String, List<String>> map = connection.getHeaderFields();
            
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
            	result += line+"\n";
            }
        } catch(Exception ex){
        	throw ex;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 
     * @param url
     * @param param  name1=value1&name2=value2
     * @param headerMap
     * @return 
     */
    public static String sendPost(String url, String param, Map<String,String> headerMap)throws Exception {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            
            conn.setConnectTimeout(5);
            conn.setReadTimeout(500);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            
            if(headerMap!=null){
            	for (String key : headerMap.keySet()){
            		conn.setRequestProperty(key, headerMap.get(key));
            	}
            }

            conn.setDoOutput(true);
            conn.setDoInput(true);
            if(param!=null){
	            out = new PrintWriter(conn.getOutputStream());
	            out.print(param);
	            out.flush();
            }
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line+"\n";
            }
        }
        catch(Exception ex){
        	ex.printStackTrace();
        	throw ex;
        }

        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(Exception ex){
            }
        }
        return result;
    }  
}

