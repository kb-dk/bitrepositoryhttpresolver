package dk.statsbiblioteket.medieplatform.httpresolver;

import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.client.eventhandler.EventHandler;
import org.bitrepository.client.eventhandler.OperationEvent;

public class FileRequestHandler implements EventHandler {

    private RequestContext context;
    
    public FileRequestHandler(RequestContext context) {
        this.context = context;
    }
    
    @Override
    public void handleEvent(OperationEvent event) {
        switch(event.getType()) {
        case IDENTIFY_REQUEST_SENT:
            break;
        case COMPONENT_IDENTIFIED:
            break;
        case IDENTIFICATION_COMPLETE:
            break;
        case REQUEST_SENT:
            break;
        case PROGRESS:
            break;
        case COMPONENT_COMPLETE:
            break;
        case COMPLETE:
            synchronized (context) {
                context.setMessage(event.getInfo());
                context.setResponseCode(ResponseCode.OPERATION_COMPLETED);
                context.notifyAll();
            }   
            break;
        case COMPONENT_FAILED:
            synchronized (context) {
                context.setMessage(event.getInfo());
                context.setResponseCode(ResponseCode.FILE_NOT_FOUND_FAILURE);
                context.notifyAll();
            }   
            break;
        case FAILED:
            synchronized (context) {
                context.setMessage(event.getInfo());
                context.setResponseCode(ResponseCode.FAILURE);
                context.notifyAll();
            }           
            break;
        case NO_COMPONENT_FOUND:
            break;
        case IDENTIFY_TIMEOUT: 
            break;
        case WARNING:
            break;
        }        
    }

    
}
