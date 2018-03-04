package org.wso2.carbon.connector.zabbix_sender;

import java.nio.charset.Charset;

/**
 * Constants used in the other classes
 * 
 * @author Marcelo Godinho Wolff
 *
 */
public class ZabbixConstants {
	public final static int CONNECT_TIMEOUT = 3000;
	public final static int SOCKET_TIMEOUT = 3000;
	public final static Charset UTF8 = Charset.forName("UTF-8");
    
	public static final String SERVER = "server";
	public static final String PORT = "port";
	public static final String HOST = "host";
	public static final String ITEM = "item";
	public static final String VALUE = "value";

}
