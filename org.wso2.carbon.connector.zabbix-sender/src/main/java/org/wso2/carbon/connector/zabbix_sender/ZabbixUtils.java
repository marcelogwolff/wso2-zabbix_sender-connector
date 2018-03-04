package org.wso2.carbon.connector.zabbix_sender;

import org.apache.synapse.MessageContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ZabbixUtils {
	
	public static void storeHostDatails(MessageContext ctxt, String server, String port) {
		ctxt.setProperty(ZabbixConstants.PORT, port);
		ctxt.setProperty(ZabbixConstants.SERVER, server);
	}
	
}
