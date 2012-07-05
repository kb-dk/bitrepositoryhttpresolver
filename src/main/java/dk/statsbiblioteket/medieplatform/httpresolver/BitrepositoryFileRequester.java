package dk.statsbiblioteket.medieplatform.httpresolver;

import org.bitrepository.access.AccessComponentFactory;
import org.bitrepository.access.getfile.GetFileClient;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.protocol.security.SecurityManager;

/**
 * Class to handle the request of a file stored in the Bitrepository. 
 */
public class BitrepositoryFileRequester {

    private GetFileClient getFileClient;
    private final ConnectionMapper connectionMapper;
    
    /**
     * Constructor 
     */
    public BitrepositoryFileRequester(Settings settings, SecurityManager securityManager, String clientID) {
        connectionMapper = new ConnectionMapper();
        getFileClient = AccessComponentFactory.getInstance().createGetFileClient(settings, securityManager, clientID);
    }
    
    /**
     * Method to request a file in the Bitrepository. 
     * Returns a token for use in the connection mapper to retrieve an input stream providing the file data.  
     */    
    public String requestFile(String fileID) {
        String token = RequestTokenGenerator.generateToken();
        return token;
    }
    
    /**
     * Get the ConnectionMapper used for the BitrepositoryFileRequester
     * @return ConnectionMapper, the ConnectionMapper in use. 
     */
    public ConnectionMapper getConnectionMapper() {
        return connectionMapper;
    }
    
    /**
     * Shutdown the BitrepositoryFileRequester in a graceful manner.  
     */
    public void shutdown() {
        getFileClient.shutdown();
    }
    
}
