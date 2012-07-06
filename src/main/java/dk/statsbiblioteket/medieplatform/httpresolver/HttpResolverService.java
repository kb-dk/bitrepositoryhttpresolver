package dk.statsbiblioteket.medieplatform.httpresolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;


/**
 * Class to provide look up capabilities for a file in the media collection bitrepository and stream the file to the caller. 
 * The class provides two webservice calls. 
 * First a getfile, that requests the bitrepository for the given file and waits for an incoming stream. 
 * Second a uploadProxy call to receive the input stream and pipe to a waiting getfile.
 * 
 * 
 * 
 */
@Path("/resolver")
public class HttpResolverService {

    private BitrepositoryFileRequester requester;
    
    public HttpResolverService() {
        requester = BitrepositoryFileRequesterFactory.getInstance();
    }

    /**
     * Method to request the delivery of a file from the bitrepository, blocking until a inputstream becomes available 
     */
    @GET
    @Path("/getfile/{fileID}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getFile(@PathParam("fileID") String fileID) throws Exception {
        final InputStream is;
        String token = requester.requestFile(fileID);
        RequestContext requestContext = requester.getRequestContextMapper().getRequestContext(token);
        synchronized (requestContext) {
            requestContext.wait();
            is = requestContext.getInputStream();
        }
        
        if(is != null) {
            requester.getRequestContextMapper().removeRequestContext(token);
            return new StreamingOutput() {
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    try {
                        int i;
                        byte[] data = new byte[4096];
                        synchronized (is) {
                            while((i = is.read(data)) >= 0) {
                               output.write(data, 0, i);
                            }
                            is.close();
                            is.notifyAll();
                        }
                    } catch (Exception e) {
                        throw new WebApplicationException(e);
                    }
                }
            };
        } else {
            throw new WebApplicationException(Response.status(requestContext.getResponse())
                    .entity(requestContext.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
        
    }
    
    /**
     *  Method to receive a requested file via an input stream. 
     *  Adds its input steam to the connection mapping, notifies threads waiting for the input stream
     *  and waits to the transfer is finished.
     *  @param id The id of the file transfer. 
     *  @param is The input stream providing the data that is to be transfered.  
     */
    @PUT
    @Path("/uploadProxy/{ID}")
    public void uploadFile(@PathParam("ID") String id, InputStream is) throws InterruptedException, IOException {
        try {
            RequestContext requestContext = requester.getRequestContextMapper().getRequestContext(id);
            synchronized (requestContext) {
                requestContext.setInputStream(is);
                requestContext.notifyAll();
            }
            synchronized (is) {
                is.wait();
            }
        } catch (RequestContextMapperException e) {
            is.close();
        }
    }
    
}
