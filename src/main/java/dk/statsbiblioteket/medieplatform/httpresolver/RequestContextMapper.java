package dk.statsbiblioteket.medieplatform.httpresolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestContextMapper {

    private static Map<String, RequestContext> contextMapping = 
            Collections.synchronizedMap(new HashMap<String, RequestContext>());
    
    
    /**
     * Add a RequestContext to the mapper
     * @param token The token for the connection
     * @param context The RequestContext to be mapped to the token 
     */
    public void addRequestContext(String token, RequestContext context) {
        synchronized (contextMapping) {
            contextMapping.put(token, context);
        }
    }
    
    /**
     * Get a RequestContext from the mapper, identified by token
     * @param token Identifier for the RequestContext 
     * @throws RequestContextMapperException if no mapping can be found
     */
    public RequestContext getRequestContext(String token) throws RequestContextMapperException {
        synchronized (contextMapping) {
            if(contextMapping.containsKey(token)) {
                return contextMapping.get(token);
            } else {
                throw new RequestContextMapperException();
            }
        }
    }

    /**
     * Remove the RequestContext from the mapper identified by token
     * @param token Identifier for the RequestContext to be removed. 
     * @throws RequestContextMapperException if no mapping can be found 
     */
    public void removeRequestContext(String token) throws RequestContextMapperException {
        synchronized (contextMapping) {
            if(contextMapping.containsKey(token)) {
                contextMapping.remove(token);
            } else {
                throw new RequestContextMapperException();
            }
        }
    }
    

    
}
