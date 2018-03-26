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
	
	public static final String ERROR = "error";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ERROR_CODE = "errorCode";

    public static final String CONNECTOR_NAME = "zabbix-sender";
    public static final String NAMESPACE = "ns";
    
    public static final String CONNECTOR_NAMESPACE = "http://org.wso2.esbconnectors.zabbix-sender";
    public static final String RESULT = "result";
    public static final String RESPONSE = "response";
    public static final String INFO = "info";
    public static final String PROCESSED = "processed";
    public static final String FAILED = "failed";
    public static final String TOTAL = "total";
    public static final String SECONDSSPENT = "secondsspent";
    public static final String SECONDS_SPENT = "secondsSpent";

    public static final int ERROR_SEND = 100001;


}
