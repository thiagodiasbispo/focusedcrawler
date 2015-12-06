package focusedCrawler.util.download;

import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.io.*;

import org.mozilla.universalchardet.UniversalDetector;

import focusedCrawler.util.Page;

public class UniversalDownloader {
	
	private String lastDetectedEnconding;
	private UniversalDetector detector = new UniversalDetector(null);
	
	public HttpURLConnection getConnection(URL url) throws IOException{
		    HttpURLConnection httpurlconnection = null;
		    try {
		        URLConnection urlconnection = url.openConnection();
		        urlconnection.setConnectTimeout(5000);
		        urlconnection.setReadTimeout(5000);
		        urlconnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.0; WOW64; rv:24.0) Gecko/20100101 Firefox/24.0");
		        urlconnection.connect();

		        if (!(urlconnection instanceof HttpURLConnection)) {
		            return null;
		        }

		        if(urlconnection.getContentType()!= null && !urlconnection.getContentType().contains("text") || urlconnection.toString().endsWith("mp3") ){
			    	return null;
			    }
		        httpurlconnection = (HttpURLConnection) urlconnection;
		        int responsecode = httpurlconnection.getResponseCode();
		        switch (responsecode) {
		        case HttpURLConnection.HTTP_OK:
		        case HttpURLConnection.HTTP_MOVED_PERM:
		        case HttpURLConnection.HTTP_MOVED_TEMP:
		            break;
		        default:
		            System.err.println("Invalid response code: " +
		                responsecode + " " + url);
		            httpurlconnection.disconnect();
		            return null;
		        }
		    } catch (IOException ioexception) {
		        System.err.println("unable to connect: " + ioexception);
		        if (httpurlconnection != null) {
		            httpurlconnection.disconnect();
		        }
		        throw ioexception;
		    }
		    return httpurlconnection;
		}
	
	public InputStream getInputStream(HttpURLConnection connection){
	    InputStream inputstream = null;
	    for (int i = 0; i < 3; ++i) {
	        try {
	            if(connection != null){
	            	inputstream = connection.getInputStream();	
	            }
	        	
	            break;
	        } catch (IOException e) {
	            System.err.println("error opening connection " + e);
	        }
	    }
	    return inputstream;
	}
	
	public static final int STREAM_BUFFER_SIZE = 4096;
	public static final String DEFAULT_ENCODING = "utf-8";
	
	public String getContent(HttpURLConnection connection)	    
		throws IOException
	{
	    InputStream inputstream = null;
	    try {
	        
	    	LinkedList<byte[]> byteList = new LinkedList<byte[]>();
	        LinkedList<Integer> byteLength = new LinkedList<Integer>();
	        inputstream = getInputStream(connection);
	        
	        if (inputstream == null) {
	            return null;
	        }

	        byte[] buf = new byte[STREAM_BUFFER_SIZE];
	        int nread = 0, nTotal = 0;
	        detector.reset();
	        while ((nread = inputstream.read(buf, 0, STREAM_BUFFER_SIZE)) > 0) {
	        	byteList.add(buf.clone());	            
	            byteLength.add(nread);
	            nTotal += nread;
	            detector.handleData(buf, 0, nread);	            
	            if (detector.isDone())
	                break;
	        }
	        
	        detector.dataEnd();
	        
	        lastDetectedEnconding = detector.getDetectedCharset();
	        
	        if (null == lastDetectedEnconding){
	        	lastDetectedEnconding = DEFAULT_ENCODING;
	        }
	        
	        while ((nread = inputstream.read(buf, 0, STREAM_BUFFER_SIZE)) > 0) {
	            byteList.add(buf.clone());
	            byteLength.add(nread);
	            nTotal += nread;
	        }
	        byte[] contentByte = new byte[nTotal];
	        int offSet = 0, l = byteList.size();
	        for (int i = 0; i < l; ++i) {
	            byte[] bytes = byteList.get(i);
	            int length = byteLength.get(i);
	            System.arraycopy(bytes, 0, contentByte, offSet, length);
	            offSet += length;
	        }
	        return new String(contentByte, lastDetectedEnconding);
	    } catch (IOException ioe) {
	        throw ioe;
	    } finally {
	        if (inputstream != null) {
	            inputstream.close();
	        }
	    }
	}
	
	public Page downloadPage(String url) throws IOException {
		return downloadPage(new URL(url));
	}
	
	public Page downloadPage(URL urlCon) throws IOException {
		 HttpURLConnection connection = getConnection(urlCon);
		 String source = getContent(connection);
		 Page page = new Page(urlCon, source);
		 page.setEncoding(lastDetectedEnconding);
		 return page;
	}
	
	public static void main(String[] args)	{ 
	    try {
	    	UniversalDownloader universalDownloader = new UniversalDownloader();
	    	Page page = universalDownloader.downloadPage(args[0]);
	    	//String url = "http://www.msn.com/pt-br/noticias/fotos/atos-humanit%C3%A1rios-que-podem-restaurar-sua-f%C3%A9-na-humanidade/ss-BBmuLyd";
	    	//Page page = downloadPage(url);
	    	System.out.println("###### codificação: " + page.getEncoding());
	    	System.out.println(page.getContent());
	    	
//	    	 BufferedReader input = new BufferedReader(new FileReader(new File("C:\\Users\\IBM_ADMIN\\luciano\\nlp\\CETENFolha-1.0.clean.tokenized.v1.rnnlm.train.voc")));
//	         for (String line = input.readLine(); line != null;
//	                 line = input.readLine()) {
//	        	 String[] parts = line.split(" ");
//	        	 if ((parts[0].charAt(0) >= 'a' && parts[0].charAt(0) <= 'z') || (parts[0].charAt(0) >= 'A' && parts[0].charAt(0) <= 'Z')){
//	        		 String url = "http://www.linguee.com.br/portugues-ingles/search?query="+parts[0]+"&moreResults=1&source=portugues";
////	        		 String url = "http://www.dictionarist.com/"+parts[0];
//	        		 System.out.println(parts[0]);
//		        	 HttpURLConnection connection = UniversalDownloader.getConnection(new URL(url));
//		 	        if (connection != null) {
//		 	            String resource = UniversalDownloader.getContent(connection);
//		 	            if (resource != null) {
//		 	            	OutputStream fout= new FileOutputStream("C:\\Users\\IBM_ADMIN\\luciano\\nlp\\data\\" + parts[0],false);
//		 	            	OutputStream bout= new BufferedOutputStream(fout);
//		 	            	OutputStreamWriter outputFile = new OutputStreamWriter(bout);
//		 	            	outputFile.write(resource);
//		 	            	outputFile.close();
//		 	            }
//		 	        }
//		 	        connection.disconnect();
//		 	        Thread.sleep(1000);	 
//	        	 }
//	         }
//	         input.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
			// TODO: handle exception
		}
       
//		HttpURLConnection connection = null;
//	    try {
//	        connection = UniversalDownloader.getConnection(new URL(args[0]));
//	        if (connection != null) {
//	            String resource = UniversalDownloader.getContent(connection);
//	            if (resource != null) {
//	            	System.out.println(resource);
//	            }
//	        }
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    } finally {
//	        if (connection != null) {
//	            connection.disconnect();
//	        }
//	    }
	}
	
}
