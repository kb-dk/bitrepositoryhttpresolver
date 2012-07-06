package dk.statsbiblioteket.medieplatform.httpresolver;

import java.net.MalformedURLException;
import java.net.URL;

import org.bitrepository.access.AccessComponentFactory;
import org.bitrepository.access.getfile.GetFileClient;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.protocol.security.SecurityManager;

/**
 * Class to handle the request of a file stored in the Bitrepository. 
 */
public class BitrepositoryFileRequester {

    private GetFileClient getFileClient;
    private final String hostUrl;
    private final RequestContextMapper contextMapper;
    
    /**
     * Constructor 
     */
    public BitrepositoryFileRequester(Settings settings, SecurityManager securityManager, String clientID, String hostUrl) {
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

            e.printStackTrace();
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
        getFileClient.shutdown();
    }
    
}
