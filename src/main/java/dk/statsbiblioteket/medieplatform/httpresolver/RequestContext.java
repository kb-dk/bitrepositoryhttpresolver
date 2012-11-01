package dk.statsbiblioteket.medieplatform.httpresolver;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bitrepository.bitrepositoryelements.ResponseCode;

public class RequestContext {

    private InputStream is = null;
    private String message;
    private ResponseCode responseCode;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Status getResponse() {
        Status resp;
        switch(responseCode) {
        case OPERATION_COMPLETED:
            resp = Response.Status.OK;
            break; 
        case FAILURE:
            resp = Response.Status.BAD_REQUEST;
            break;
        case FILE_NOT_FOUND_FAILURE:
            resp = Response.Status.NOT_FOUND;
            break;
        default:
            resp = Response.Status.INTERNAL_SERVER_ERROR;
        } 
        return resp;
    }
    
    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
    
    public InputStream getInputStream() {
        return is;
    }
    
    public void setInputStream(InputStream is) {
        this.is = is;
    }
    
}
