
package au.com.shinetech.web.rest;

import au.com.shinetech.domain.Device;
import au.com.shinetech.domain.User;
import au.com.shinetech.repository.DeviceRepository;
import au.com.shinetech.repository.UserRepository;
import au.com.shinetech.service.MailService;
import au.com.shinetech.service.UserService;
import com.codahale.metrics.annotation.Timed;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCallbackUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;


@RestController
@RequestMapping("/api")
public class DeviceResource {
    
    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;
    
    @Inject
    private DeviceRepository deviceRepository;
    
    private static final String CLIENT_CONSUMER_KEY = "2f4ac84d9282434084fff5c23b2aeb92";
    private static final String CLIENT_SECRET = "6564439417104201a920b4dd3bf0cd83";
    
    private static final String FITBIT_BASE_URL = "https://www.fitbit.com";
    private static final String FITBIT_API_URL = "https://api.fitbit.com";
    private static final String WEBAPP_URL = "http://www.fitdonate.co";
    
    private static final String FITBIT_REQUEST_TOKEN = "/oauth/request_token";
    private static final String FITBIT_AUTHORIZE = "/oauth/authorize";
    private static final String FITBIT_ACCESS_TOKEN = "/oauth/access_token";
    
    /**
     * POST  /devices -> Create a new device.
     */
    @RequestMapping(value = "/devices",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Device device) {
        log.debug("REST request to save Device : {}", device);
        deviceRepository.save(device);
    }

    /**
     * GET  /devices -> get all the devices.
     */
    @RequestMapping(value = "/devices",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Device> getAll() {
        log.debug("REST request to get all Devices");
        return deviceRepository.findAll();
    }

    /**
     * GET  /devices/:id -> get the "id" device.
     */
    @RequestMapping(value = "/devices/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Device> get(@PathVariable Long id) {
        log.debug("REST request to get Device : {}", id);
        return Optional.ofNullable(deviceRepository.findOne(id))
            .map(device -> new ResponseEntity<>(
                device,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /devices/:id -> delete the "id" device.
     */
    @RequestMapping(value = "/devices/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Device : {}", id);
        
        Device device = deviceRepository.findOne(id);
        device.getUser().setDevice(null);
        userRepository.save(device.getUser());
        
        deviceRepository.delete(id);
    }
    
    /**
     * GET  /device/link -> initiates the linking of a device to a user.
     * @param userId
     * @param builder
     * @return 
     */
    @RequestMapping(value = "/device/link",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> linkDevice(@RequestParam String userId, 
                                        UriComponentsBuilder builder) {
        try {
            // retrieve temporary token from fitbit
            HttpTransport transport = new ApacheHttpTransport();
            OAuthHmacSigner signer = new OAuthHmacSigner();
            signer.clientSharedSecret = CLIENT_SECRET;

            OAuthGetTemporaryToken tempTokenRequest = new OAuthGetTemporaryToken(FITBIT_API_URL+FITBIT_REQUEST_TOKEN);
            tempTokenRequest.consumerKey = CLIENT_CONSUMER_KEY;
            tempTokenRequest.transport = transport;
            tempTokenRequest.signer = signer;
            tempTokenRequest.callback = WEBAPP_URL + "/api/device/authorize?userId="+userId;
            OAuthCredentialsResponse credResponse = tempTokenRequest.execute();

            // store secret token in database for HMAC-SHA1 signing
            User user = userRepository.findOne(Long.valueOf(userId));
            Device device = null;
            if(user.getDevice() == null) {
                device = new Device();
                device.setSecretToken(credResponse.tokenSecret);
                device.setDateAdded(new Date());
                user.setDevice(device);
            } else {
                device = user.getDevice();
                user.getDevice().setSecretToken(credResponse.tokenSecret);
            }
            deviceRepository.save(device);
            userRepository.save(user);

            // redirect user to fitbit for authorization
            OAuthAuthorizeTemporaryTokenUrl authTempTokenRequest = new OAuthAuthorizeTemporaryTokenUrl(FITBIT_BASE_URL+FITBIT_AUTHORIZE);
            authTempTokenRequest.temporaryToken = credResponse.token;
                        
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(authTempTokenRequest.build()));
            
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch(URISyntaxException ex) {
            log.error("Could not build redirect url", ex);
        } catch (IOException ex) {
            log.error("Could not execute request", ex);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "/device/authorize",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> postAuthorisation(UriComponentsBuilder uriBuilder,
                                                @RequestParam String userId, 
                                                HttpServletRequest request) {
        try {
            // parse callback url
            String callbackUrl = request.getRequestURL().toString()+"?"+request.getQueryString();
            OAuthCallbackUrl callbackParser = new OAuthCallbackUrl(callbackUrl);

            // retrieve user
            User user = userRepository.findOne(Long.valueOf(userId));

            // exchange temporary token for access token
            HttpTransport transport = new ApacheHttpTransport();
            OAuthHmacSigner signer = new OAuthHmacSigner();
            signer.clientSharedSecret = CLIENT_SECRET;
            signer.tokenSharedSecret = user.getDevice().getSecretToken();

            OAuthGetAccessToken accessTokenRequest = new OAuthGetAccessToken(FITBIT_API_URL+FITBIT_ACCESS_TOKEN);
            accessTokenRequest.consumerKey = CLIENT_CONSUMER_KEY;
            accessTokenRequest.transport = transport;
            accessTokenRequest.signer = signer;
            accessTokenRequest.temporaryToken = callbackParser.token;
            accessTokenRequest.verifier = callbackParser.verifier;                
            OAuthCredentialsResponse credResponse = accessTokenRequest.execute();

            // store access token and new secret in session
            user.getDevice().setAccessToken(credResponse.token);
            user.getDevice().setSecretToken(credResponse.tokenSecret);
            user.getDevice().setDateAdded(new Date());
            deviceRepository.save(user.getDevice());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI("/#/settings"));
            
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch(IOException ioEx) {
            log.error("Could not execute request", ioEx);
        } catch (NumberFormatException numEx) {
            log.error("Invalid user id specified", numEx);
        } catch (URISyntaxException uriEx) {
            log.error("Could not build url", uriEx);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @RequestMapping(value = "/device/activities/{date}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getActivities(@RequestParam String userId, 
                                          @PathVariable String date,
                                          HttpServletRequest request) {
        try {
            // get the user
            User user = userRepository.findOne(Long.valueOf(userId));
            
            // build request to retrieve activities from fitbit
            OAuthHmacSigner signer = new OAuthHmacSigner();
            signer.clientSharedSecret = CLIENT_SECRET;
            signer.tokenSharedSecret = user.getDevice().getSecretToken();

            OAuthParameters params = new OAuthParameters();
            params.signer = signer;
            params.consumerKey = CLIENT_CONSUMER_KEY;
            params.token = user.getDevice().getAccessToken();

            HttpRequestFactory factory = new ApacheHttpTransport().createRequestFactory(params);
            HttpRequest httpReq = factory.buildGetRequest(
                    new GenericUrl(FITBIT_API_URL+"/1/user/-/activities/date/"+date+".json"));
            HttpResponse response = httpReq.execute();
            String jsonResponse = IOUtils.toString(response.getContent());

            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch(IOException ex) {
            log.error("Could not retrieve device information");
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
