package dk.statsbiblioteket.medieplatform.httpresolver;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.JMSException;

import org.bitrepository.access.AccessComponentFactory;
import org.bitrepository.access.getfile.GetFileClient;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.protocol.messagebus.MessageBusManager;
import org.bitrepository.protocol.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to handle the request of a file stored in the Bitrepository. 
 */
public class BitrepositoryFileRequester {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private GetFileClient getFileClient;
    private final String hostUrl;
    private final RequestContextMapper contextMapper;
    private final Settings settings;
    
    public BitrepositoryFileRequester(Settings settings, SecurityManager securityManager, String clientID, String hostUrl) {
    	this.settings = settings;
    	contextMapper = new RequestContextMapper();
        getFileClient = AccessComponentFactory.getInstance().createGetFileClient(settings, securityManager, clientID);
        if(!hostUrl.endsWith("/")) {
            this.hostUrl = hostUrl + "/";
        } else {
            this.hostUrl = hostUrl;
        }
    }
    
    /**
     * Method to request a file in the Bitrepository. 
     * @param fileID The ID of the file to retrieve
     * @return String A token for use in the connection mapper to retrieve an input stream providing the file data.  
     */    
    public String requestFile(String fileID) {
        String token = RequestTokenGenerator.generateToken();
        RequestContext requestContext = new RequestContext();
        try {
            URL url = new URL(hostUrl + "resolver/uploadProxy/" + token);
            FileRequestHandler handler = new FileRequestHandler(requestContext);
            contextMapper.addRequestContext(token, requestContext);
            getFileClient.getFileFromFastestPillar(fileID, null, url, handler);
        } catch (MalformedURLException e) {
        	log.warn(e.toString());
        }
        return token;

    }
    
    /**
     * Get the RequestContextMapper used for the BitrepositoryFileRequester
     * @return RequestContextMapper, the RequestContextMapper in use. 
     */
    public RequestContextMapper getRequestContextMapper() {
        return contextMapper;
    }
    
    /**
     * Shutdown the BitrepositoryFileRequester in a graceful manner.  
     */
    public void shutdown() {
    	try {
			MessageBusManager.getMessageBus(settings.getCollectionID()).close();
		} catch (JMSException e) {
        	log.warn(e.toString());
		}
    }
    
}
