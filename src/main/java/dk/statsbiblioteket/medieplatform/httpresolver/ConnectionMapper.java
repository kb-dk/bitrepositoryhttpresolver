package dk.statsbiblioteket.medieplatform.httpresolver;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectionMapper {

    private static Map<String, InputStream> connectionMapping = 
            Collections.synchronizedMap(new HashMap<String, InputStream>());
    
    /**
     * Add a token to the connection mapping
     * @param token The token for the connection 
     */
    public void addMapping(String token) {
        synchronized (connectionMapping) {
            connectionMapping.put(token, null);
        }
    }
    
    /**
     * Cancel a connection mapping represented by token
     * @param token The mapping to cancel. 
     */
    public void cancelMapping(String token) {
        synchronized (connectionMapping) {
            connectionMapping.remove(token);
            connectionMapping.notifyAll();
        }
    }
    
    /**
     * Add a connection (InputStream) to the mapping represented by token
     * @param token The connection to map the InputStream to
     * @param is The InputStream to be mapped
     */
    public void addConnection(String token, InputStream is) throws ConnectionMapperException {
        synchronized (connectionMapping) {
            if(connectionMapping.containsKey(token)) {
                connectionMapping.put(token, is);
                connectionMapping.notifyAll();
            } else {
                throw new ConnectionMapperException("No mapping exists for token: " + token);
            }
        }
    }
    
    /**
     * Get the connection mapped to token. The method will block until a connection is available
     * or the mapping is canceled.
     * @param token The token to retrieve the connection from
     * @return InputStream for the connection, or null if the mapping was canceled.
     * @throws InterruptedException is the thread got interrupted while waiting for a connection.
     */
    public InputStream getConnection(String token) throws InterruptedException {
        InputStream is;
        synchronized (connectionMapping) {
            while(connectionMapping.containsKey(token) && connectionMapping.get(token) == null) {
                connectionMapping.wait();
            }
            is = connectionMapping.get(token);
            connectionMapping.remove(token);    
        }
        return is;
    }
    
}
