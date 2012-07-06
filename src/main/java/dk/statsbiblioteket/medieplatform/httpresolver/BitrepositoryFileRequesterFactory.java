package dk.statsbiblioteket.medieplatform.httpresolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.bitrepository.common.settings.Settings;
import org.bitrepository.common.settings.SettingsProvider;
import org.bitrepository.common.settings.XMLFileSettingsLoader;
import org.bitrepository.protocol.security.BasicMessageAuthenticator;
import org.bitrepository.protocol.security.BasicMessageSigner;
import org.bitrepository.protocol.security.BasicOperationAuthorizor;
import org.bitrepository.protocol.security.BasicSecurityManager;
import org.bitrepository.protocol.security.MessageAuthenticator;
import org.bitrepository.protocol.security.MessageSigner;
import org.bitrepository.protocol.security.OperationAuthorizor;
import org.bitrepository.protocol.security.PermissionStore;
import org.bitrepository.protocol.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Factory class handling setup and creation of the BitrepositoryFileRequester. 
 * Prior to calling the getInstance method, the configuration dir must be set by calling the init method.  
 */
public class BitrepositoryFileRequesterFactory {

    private static final Logger log = LoggerFactory.getLogger(BitrepositoryFileRequesterFactory.class);

    private static String confDir;
    private static BitrepositoryFileRequester requester;
    private static String privateKeyFile;
    private static String clientID;
    private static String hostUrl;

    private static final String CONFIGFILE = "httpresolver.properties"; 
    private static final String PRIVATE_KEY_FILE = "dk.statsbiblioteket.medieplatform.httpresolver.privateKeyFile";
    private static final String CLIENT_ID = "dk.statsbiblioteket.medieplatform.httpresolver.clientID";
    private static final String HOST_URL = "dk.statsbiblioteket.medieplatform.httpresolver.hosturl";

    /**
     * Initialization method for setting the configuration directory for the deployment. 
     */
    public static synchronized void init(String configurationDir) {
        confDir = configurationDir;
        loadProperties();
    }

    /**
     * Factory method to acquire the BitrepositoryFileRequester instance.
     * Prior to calling this method the confDir member variable needs to be set by calling init, 
     * otherwise a RuntimeException will be thrown.    
     */
    public static synchronized BitrepositoryFileRequester getInstance() {
        if(requester == null) {
            if(confDir == null) {
                throw new RuntimeException("No configuration dir has been set!");
            }

            SettingsProvider settingsLoader = new SettingsProvider(new XMLFileSettingsLoader(confDir), clientID);
            Settings settings = settingsLoader.getSettings();
            PermissionStore permissionStore = new PermissionStore();
            MessageAuthenticator authenticator = new BasicMessageAuthenticator(permissionStore);
            MessageSigner signer = new BasicMessageSigner();
            OperationAuthorizor authorizer = new BasicOperationAuthorizor(permissionStore);
            SecurityManager securityManager = new BasicSecurityManager(settings.getCollectionSettings(), privateKeyFile,
                    authenticator, signer, authorizer, permissionStore, clientID);

            try {
                requester = new BitrepositoryFileRequester(settings, securityManager, clientID, hostUrl);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return requester;
    }

    /**
     * Load properties from configuration file 
     */
    private static void loadProperties() {
        Properties properties = new Properties();
        try {
            String propertiesFile = confDir + "/" + CONFIGFILE;
            BufferedReader reader = new BufferedReader(new FileReader(propertiesFile));
            properties.load(reader);

            privateKeyFile = properties.getProperty(PRIVATE_KEY_FILE);
            clientID = properties.getProperty(CLIENT_ID);
            hostUrl = properties.getProperty(HOST_URL);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
