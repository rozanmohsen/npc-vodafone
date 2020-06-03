package com.asset.vodafone.npc.core.handler.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.handler.NPCProcessHandler;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.service.NPCService;

public class TestNPCProcessHandler {

	ResourceBundle npcProperties = null;
	NPCProcessHandler handler = null;
	NPCService npcService = null;
	NPCMessageModel npcMessageModel = new NPCMessageModel();

	@Before
	public void init() throws Exception {
		FileInputStream inputStream = new FileInputStream("npc_web_service.properties");
		npcProperties = new PropertyResourceBundle(inputStream);
		handler = NPCProcessHandler.getInstance();

		handler.initializeNPCService();
	}

	
	@Test
	public void testEncryption() throws Exception {

		NPCProcessHandler.main(new String[] { "enc", "voda" });

		File file = new File("Logs/NPC_Process_Debug.log");
		String[] words = null;
		FileReader filereader = new FileReader(file);
		BufferedReader br = new BufferedReader(filereader);
		String s;
		String input = "e41858c810005053";
		int count = 0;
		while ((s = br.readLine()) != null) {
			words = s.split(" ");
			for (String word : words) {
				if (word.equals(input)) {
					count++;
				}
			}
		}
		Assert.assertNotEquals(0, count);
		br.close();
	}

	@Test
	public void testgetUnsentMessages() throws NPCException, UnknownHostException {

		List<NPCMessageModel> unsentMessages = new ArrayList<>();
		npcService = NPCService.initiateDBConnection(npcProperties);

		unsentMessages = npcService.getUnsentMessages();
		boolean fetch = true;
		String jobId = "";
		jobId = InetAddress.getLocalHost().getHostAddress();
		if (unsentMessages.isEmpty()) {
			Assert.assertTrue(true);

		} else {
			for (int i = 0; i < unsentMessages.size(); i++) {
				npcMessageModel = unsentMessages.get(i);
				if (npcMessageModel.isSent() != false && npcMessageModel.getPickedBy() != jobId) {
					fetch = false;
				}

			}
		}

		Assert.assertEquals(true, fetch);
	}

	@Test
	public void testSendPendingMessage() throws Exception {
		String userName = "vodau1";
		byte[] Password = "vodau14NPC".getBytes();
		String xmlMessage = "";
		xmlMessage = FileUtils.readFileToString(new File("NPR.xml"), StandardCharsets.UTF_8);

		String expectedResponse = "Runner Done Successfully";

		String response = handler.sendMessage(userName, Password, xmlMessage, true);
		Assert.assertEquals(expectedResponse, response);

	}
}