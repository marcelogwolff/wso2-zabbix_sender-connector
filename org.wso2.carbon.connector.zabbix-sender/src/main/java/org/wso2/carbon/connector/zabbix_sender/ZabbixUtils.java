package org.wso2.carbon.connector.zabbix_sender;

import java.util.Iterator;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPBody;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.transport.nhttp.NhttpConstants;


public class ZabbixUtils {
	
	private static final OMFactory fac = OMAbstractFactory.getOMFactory();
	private static OMNamespace ns = fac.createOMNamespace(ZabbixConstants.CONNECTOR_NAME, ZabbixConstants.NAMESPACE);


	    
	public static void storeHostDatails(MessageContext ctxt, String server, String port) {
		ctxt.setProperty(ZabbixConstants.PORT, port);
		ctxt.setProperty(ZabbixConstants.SERVER, server);
	}
	
    public static void preparePayload(MessageContext messageContext, OMElement element) {
        SOAPBody soapBody = messageContext.getEnvelope().getBody();
        for (Iterator itr = soapBody.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            child.detach();
        }
        soapBody.addChild(element);
        
    }

    public static void preparePayload(MessageContext messageContext, Exception e, int errorCode) {
        OMElement omElement = fac.createOMElement(ZabbixConstants.ERROR, ns);
        OMElement message = fac.createOMElement(ZabbixConstants.ERROR_MESSAGE, ns);
        OMElement code = fac.createOMElement(ZabbixConstants.ERROR_CODE, ns);
        message.addChild(fac.createOMText(omElement, e.getMessage()));
        code.addChild(fac.createOMText(omElement, errorCode + ""));
        omElement.addChild(message);
        omElement.addChild(code);
        preparePayload(messageContext, omElement);
    }

    public static void handleErrorResponse(MessageContext messageContext, int errorCode,
                                           Exception e) {
        org.apache.axis2.context.MessageContext axis2MessageContext =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        String errorMessage = e.getMessage();
        axis2MessageContext.setProperty(NhttpConstants.HTTP_SC, 500);
        messageContext.setProperty(SynapseConstants.ERROR_CODE, errorCode); // This doesn't work
        messageContext.setProperty(SynapseConstants.ERROR_MESSAGE, errorMessage);
        messageContext.setProperty(SynapseConstants.ERROR_EXCEPTION, e);
        messageContext.setFaultResponse(true);
        preparePayload(messageContext, e, errorCode);
    }

}
