package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import client.Client;
import server.Server;

class ClientTest {
	
	Client client = new Client();

	@Test
	void testReadConfig() {
		client.readConfig();
		assertEquals(client.getPort(), 50000);
	}

	@Test
	void testLogin() {
		fail("Not yet implemented");
	}

	@Test
	void testSendMessage() {
		fail("Not yet implemented");
	}

	@Test
	void testSendMessagePath() {
		fail("Not yet implemented");
	}

	@Test
	void testConnect() {
		  Server server = new Server();
	      server.start();
		assertEquals(client.connect(), true);
	}

	@Test
	void testDisconnect() {
		fail("Not yet implemented");
	}

}
