package org.wso2.carbon.connector.zabbix_sender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;



/**
 * Class to send data to Zabbix
 * 
 * Zabbix receive three strings:
 * host: name of host in trapper configuration.
 * item: name of key present in trapper configuration
 * value: value of alert.
 * 
 * @author Marcelo Godinho Wolff
 *
 */
public class Send extends AbstractConnector{

	@Override
	public void connect(MessageContext messageContext) throws ConnectException {
		try {
			OMFactory factory = OMAbstractFactory.getOMFactory();
			OMNamespace ns = factory.createOMNamespace(ZabbixConstants.CONNECTOR_NAMESPACE, ZabbixConstants.NAMESPACE);
			OMElement result = factory.createOMElement(ZabbixConstants.RESULT, ns);
			OMElement response = factory.createOMElement(ZabbixConstants.RESPONSE, ns);
			OMElement info = factory.createOMElement(ZabbixConstants.INFO, ns);
			OMElement processed = factory.createOMElement(ZabbixConstants.PROCESSED, ns);
			OMElement failed = factory.createOMElement(ZabbixConstants.FAILED, ns);
			OMElement total = factory.createOMElement(ZabbixConstants.TOTAL, ns);
			OMElement secondsSpent = factory.createOMElement(ZabbixConstants.SECONDS_SPENT, ns);

			String server = (String) messageContext.getProperty(ZabbixConstants.SERVER);
			Integer port = Integer.valueOf((String) messageContext.getProperty(ZabbixConstants.PORT));
			String host = (String) messageContext.getProperty(ZabbixConstants.HOST);
			String item = (String) messageContext.getProperty(ZabbixConstants.ITEM);
			String value = (String) messageContext.getProperty(ZabbixConstants.VALUE);
			String jsonString = send(server, port, host, item, value);
			
			//It was make this way to didn't use external library.
			String[] split = jsonString.replace("{", "").replace("}", "").replace(" ", "").split(",");
			String[] zabbixResponse = split[0].split(":");
			response.setText(zabbixResponse[1].replace("\"",""));
			
			List<String> list = Arrays.asList(split[1].replace("\"info\":","").split(";"));
			for(String element : list){
				String[] elementArray = element.split(":");

				switch(elementArray[0].replace("\"","")){
					case ZabbixConstants.PROCESSED:
						processed.setText(elementArray[1]);
						break;
					case ZabbixConstants.FAILED:
						failed.setText(elementArray[1]);
						break;
					case ZabbixConstants.TOTAL:
						total.setText(elementArray[1]);
						break;
					case ZabbixConstants.SECONDSSPENT:
						secondsSpent.setText(elementArray[1].replace("\"",""));
						break;
				}
			}
			result.addChild(response);
			info.addChild(processed);
			
			info.addChild(failed);
			info.addChild(total);
			info.addChild(secondsSpent);
			result.addChild(info);

			ZabbixUtils.preparePayload(messageContext, result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String send(String server, Integer port, String host, String item, String value) throws IOException {
		Socket socket = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		String jsonString = null;
		try {
			socket = new Socket();
			socket.setSoTimeout(ZabbixConstants.SOCKET_TIMEOUT);
			socket.connect(new InetSocketAddress(server, port), ZabbixConstants.CONNECT_TIMEOUT);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			String report = buildJSonString(host,item,value);
            writeMessage(outputStream, report.getBytes());
			byte[] responseData = new byte[512];
			int readCount = 0;
			while (true) {
				int read = inputStream.read(responseData, readCount, responseData.length - readCount);
				if (read <= 0) {
					break;
				}
				readCount += read;
			}
			jsonString = new String(responseData, 13, readCount - 13, ZabbixConstants.UTF8);
			log.info("Return of Zabbix: " + jsonString);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		} finally {
			if (socket != null) {
				socket.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
		return jsonString;
	}

	/*
	 * http://zabbix.org/wiki/Docs/protocols/zabbix_sender/1.8/java_example
	 */

	private  String buildJSonString(String host, String item, String value) {
		return "{" + "\"request\":\"sender data\",\n" + "\"data\":[\n" + "{\n" + "\"host\":\"" + host + "\",\n"
				+ "\"key\":\"" + item + "\",\n" + "\"value\":\"" + value.replace("\\", "\\\\") + "\"}]}\n";
	}

	protected  void writeMessage(OutputStream out, byte[] data) throws IOException {
		int length = data.length;

		out.write(new byte[] { 'Z', 'B', 'X', 'D', '\1', (byte) (length & 0xFF), (byte) ((length >> 8) & 0x00FF),
				(byte) ((length >> 16) & 0x0000FF), (byte) ((length >> 24) & 0x000000FF), '\0', '\0', '\0', '\0' });
		out.write(data);
	}

}
