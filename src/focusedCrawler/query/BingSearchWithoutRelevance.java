package focusedCrawler.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.binary.Base64;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import focusedCrawler.util.Page;
import focusedCrawler.util.string.PorterStemmer;
import focusedCrawler.util.string.StopList;
import focusedCrawler.util.string.StopListArquivo;
import focusedCrawler.util.vsm.VSMElement;
import focusedCrawler.util.vsm.VSMVector;

public class BingSearchWithoutRelevance {

	
	private HashSet<String> usedURLs = new HashSet<String>();
	private StopList stoplist = null;
	private int numberOfResults = 250;
	private String nextPageLink = null;
	
	public BingSearchWithoutRelevance(StopList stoplist){
		this.stoplist = stoplist;
	}

	private Boolean validUrl(String url){
		return !url.endsWith("pdf") && !url.endsWith("pps");
	}
	
	private Boolean urlNeverUsed(String url){
		return !usedURLs.contains(url);
	}
	
	private void addUrl(String url){
		System.out.println(url);
		usedURLs.add(url);
	}
	
	public void execute(String initialQuery) throws MalformedURLException, IOException, SAXException{
		String keyword = "%27" + initialQuery + "%27";
		nextPageLink = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=" + keyword + "&$top=" + numberOfResults;
		do{
			Page page =  downloadResults(nextPageLink);
			String[] urls = parseXMLPage(page);
			for (String url : urls) {
				if (validUrl(url) && urlNeverUsed(url)){
					addUrl(url);
				}
			} 
		}while (nextPageLink != null);
		
	}
	
	private Page downloadResults(String keyword){
		keyword = keyword.replaceAll(" ", "%20");
		String accountKey="d9zIG4ICwyPiUzBz0pDB9fvGr/UKDqk82fYBlJlXmhc";
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);
		URL url = null;
		StringBuffer output = new StringBuffer();
		try {
			url = new URL(nextPageLink);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String line;
			while ((line = br.readLine()) != null) {
				output = output.append(line);
			}
			conn.disconnect();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Page(url, output.toString());
	}
	
	private String[] parseXMLPage(Page page) throws SAXException, IOException{
	    DOMParser parser = new DOMParser();
	    Vector<String> urls = new Vector<String>();
	    parser.parse(new InputSource(new BufferedReader(new StringReader(page.getContent()))));
	    Document doc = parser.getDocument();
	    NodeList list = doc.getElementsByTagName("d:Url");
	    NodeList linkNode = doc.getElementsByTagName("link");
	    nextPageLink = null;
	    if (linkNode.getLength() > 0){
	    	Node node = linkNode.item(0);
	    	nextPageLink =  node.getAttributes().getNamedItem("href").getTextContent();
	    }
	    	
	    for (int j = 0; j < list.getLength(); j++) {
	    	Node node = list.item(j);
	    	NodeList children = node.getChildNodes();
	    	Node child = children.item(0);
	    	urls.add(child.getTextContent());
	    }
	    String[] res = new String[urls.size()];
	    urls.toArray(res);
	    return res;
	}


	public static void main(String[] args) {
		try {
			StopList stoplist = new StopListArquivo(args[0]);
			BingSearchWithoutRelevance search = new BingSearchWithoutRelevance(stoplist);
			search.execute(args[1]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	}
