package com.asset.vodafone.npc.webservice.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.handler.NPCWSHandler;
import com.asset.vodafone.npc.webservice.wsdl.ObjectFactory;
import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsg;
import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsgResponse;

@Endpoint
public class NPCWebServiceEndpoint {
	private static final String NAMESPACE_URI = "http://npc.gizasystems.com/";
	private static final String REQUEST_LOCAL_NAME = "processNPCMsg";
	private static final Logger logger = LoggerFactory.getLogger(NPCWebServiceEndpoint.class.getName());

	/**
	 * Method return Process NPC MEssage Response from NTRA Web service
	 * 
	 * @param request
	 * @return
	 */
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = REQUEST_LOCAL_NAME)
	@ResponsePayload
	public ProcessNPCMsgResponse processNPCMsg(@RequestPayload ProcessNPCMsg request) {
		String returnedMessage = "";
		ObjectFactory factory = new ObjectFactory();
		ProcessNPCMsgResponse response = factory.createProcessNPCMsgResponse();

		NPCWSHandler handler;
		try {
			handler = NPCWSHandler.getInstance();
			returnedMessage = handler.processNPCMessage(request.getString1(), request.getArrayOfbyte2(),
					request.getString3());

		} catch (NPCException e) {

			logger.error(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE, e.getMessage());
		}

		response.setResult(returnedMessage);

		return response;
	}
}
