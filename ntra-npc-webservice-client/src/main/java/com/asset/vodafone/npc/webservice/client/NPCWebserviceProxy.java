package com.asset.vodafone.npc.webservice.client;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsg;
import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsgResponse;

public class NPCWebserviceProxy extends WebServiceGatewaySupport {
	private static final  Logger loggerObj =LoggerFactory.getLogger(NPCWebserviceProxy.class.getName());
	NPCWebserviceProxy() {
		
	}

	static {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				 throw new UnsupportedOperationException();
				
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				
				 throw new UnsupportedOperationException();
			
		}
		}};

		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		
		} catch (Exception e) {
			loggerObj.error(e.getMessage());
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		
	}

	/**
	 * processNPCMsg Method return ProcessNPCMsgResponse
	 * 
	 * @param processNPCMsg
	 * 
	 * 
	 * @return ProcessNPCMsgResponse
	  
	 */
	public ProcessNPCMsgResponse processNPCMsg(ProcessNPCMsg processNPCMsg)  {
		try {
			
			loggerObj.debug("Start Connecting to NTRA Web Service ");
			
			
			
			return (ProcessNPCMsgResponse) ((JAXBElement<?>) getWebServiceTemplate().marshalSendAndReceive(processNPCMsg))
					.getValue();
	
	}catch(WebServiceIOException e) {
			
			
			loggerObj.error(e.getMessage());
			throw new WebServiceIOException(e.getMessage());
			
		}

		
	}
}
