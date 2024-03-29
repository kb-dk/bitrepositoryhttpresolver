package dk.statsbiblioteket.medieplatform.httpresolver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.bitrepository.protocol.utils.LogbackConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Listener has two intentions
 * 1) Acquire necessary information at startup to locate configuration files and create the first instance 
 * 		of the basic client, so everything is setup before the first users start using the webservice. 
 * 2) In time shut the service down in a proper manner, so no threads will be orphaned.   
 */
public class HttpresolverContextListener implements ServletContextListener {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Do initialization work  
     */
    @SuppressWarnings("unused")
    @Override
    public void contextInitialized(ServletContextEvent sce) {
       String confDir = sce.getServletContext().getInitParameter("HttpResolverConfigDir");
        if(confDir == null) {
        	throw new RuntimeException("No configuration directory specified!");
        }
        log.debug("Configuration dir = " + confDir);
        try {
			new LogbackConfigLoader(confDir + "/logback.xml");
		} catch (Exception e) {
			log.info("Failed to read log configuration file. Falling back to default.");
		} 
        BitrepositoryFileRequesterFactory.init(confDir);
        BitrepositoryFileRequester requester = BitrepositoryFileRequesterFactory.getInstance();
        log.debug("Bitrepository http resolver initialized");
    }

    /**
     * Does work of shutting the http resolver down in a graceful manner. 
     * This is done by calling BitepositoryFileRequester's shutdown method.  
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        BitrepositoryFileRequester requester = BitrepositoryFileRequesterFactory.getInstance();
        requester.shutdown(); 
        log.debug("Bitrepository http resolver destroyed");
    }

}
