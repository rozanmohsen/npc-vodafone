package com.asset.vodafone.npc.core.handler.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.asset.vodafone.npc.core.handler.NPCWSHandler;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.service.NPCService;
import com.asset.vodafone.npc.webservice.wsdl.ObjectFactory;
import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsg;
import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsgResponse;

public class TestNPCWSHandler {

	NPCWSHandler handler = null;
	ObjectFactory factory = new ObjectFactory();
	ProcessNPCMsgResponse response = factory.createProcessNPCMsgResponse();
	String DatabaseConnectionFailed=null;
	NPCService npcService=null;
	ResourceBundle npcProperties=null;
	String databaseConnectionfailure="SOP0001E: Internal error: Could not connect to database, Please contact system administrator";
	String dataBaseFailureMessage="SOP0006E: Database Exception in processing message";
	NPCMessageModel npcMessageModel=new NPCMessageModel();
	String message="";
	
	
	
	@Before
	public void init() throws Exception {
		handler = NPCWSHandler.getInstance();
	npcProperties=NPCWSHandler.loadNPCPropertiesFile("npc_web_service.properties");
	npcService=NPCService.initiateDBConnection(npcProperties);
	message =new String (Files.readAllBytes( Paths.get("message.xml") ) );

try {
 		
 		Class<NPCWSHandler> reflectclass = NPCWSHandler.class;
 		
 		Method method = reflectclass.getDeclaredMethod("parseMessage", String.class);
 		method.setAccessible(true);
 		npcMessageModel=(NPCMessageModel) method.invoke(reflectclass, npcService,message);
 		
 		}catch(Exception e) {
 			e.getMessage();
 		}
	}
	
	@Test
	public void testprocessNPCMessage() {

		String userName = "voda";
		byte[] password = {-66, -121, 90};
		String result="success";
		
		DatabaseConnectionFailed=handler.intializeDataBaseConnection();
		if(DatabaseConnectionFailed!=null) {
			Assert.assertEquals(result, handler.processNPCMessage(userName, password, message));
			}else
			Assert.assertEquals(databaseConnectionfailure, handler.processNPCMessage(userName, password, message) );
		
		
	}
	@Test
	public void testprocessNPCMsg() {

		ProcessNPCMsg request = new ProcessNPCMsg();
		byte[] password = {-66, -121, 90};
		
	
	    request.setString1("voda");
		request.setArrayOfbyte2(password);
		
	
		request.setString3(message);

		String expectedResult = "success";
		
		DatabaseConnectionFailed=handler.intializeDataBaseConnection();
		String result = handler.processNPCMessage(request.getString1(), request.getArrayOfbyte2(),
				request.getString3());
		
		if(DatabaseConnectionFailed!=null) {
		Assert.assertEquals(expectedResult,result );
		}else
		Assert.assertEquals(databaseConnectionfailure,result );
		

	}
	@Test
	public void testcheckWSCredentials() throws UnsupportedEncodingException {
		
		String userName = "voda";
		byte[] password = {-66, -121, 90};
	
		String result2  = "Valid credentials";
	Assert.assertEquals(result2, handler.checkWSCredentials(userName, password));
	}
	
	
 	
 
 	
 	
	
	
}
