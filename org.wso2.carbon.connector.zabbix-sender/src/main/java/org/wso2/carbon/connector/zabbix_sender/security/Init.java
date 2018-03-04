package org.wso2.carbon.connector.zabbix_sender.security;

import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.zabbix_sender.ZabbixConstants;
import org.wso2.carbon.connector.zabbix_sender.ZabbixUtils;

/**
 * Class to connect in Zabbix with socket.
 * It is necessary only hostname of server and port with the socket is listening.
 * 
 * @author Marcelo Godinho Wolff
 *
 */
public class Init extends AbstractConnector{

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		String server = (String) getParameter(messageContext, ZabbixConstants.SERVER);
		String port = (String) getParameter(messageContext, ZabbixConstants.PORT);
		ZabbixUtils.storeHostDatails(messageContext, server, port);
	}

}
