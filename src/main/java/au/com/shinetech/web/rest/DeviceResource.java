
package au.com.shinetech.web.rest;

import au.com.shinetech.config.FitBitConfiguration;
import au.com.shinetech.domain.Activity;
import au.com.shinetech.domain.Device;
import au.com.shinetech.domain.User;
import au.com.shinetech.repository.DeviceRepository;
import au.com.shinetech.repository.UserRepository;
import au.com.shinetech.service.DeviceService;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


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
    
    @Inject
    private DeviceService deviceService;
    
    @Inject
    private FitBitConfiguration.FitBitConfig fitBitConfig;
    
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
            signer.clientSharedSecret = fitBitConfig.getClientSecret();

            OAuthGetTemporaryToken tempTokenRequest = new OAuthGetTemporaryToken(fitBitConfig.getApiUrl()+fitBitConfig.getRequestTokenPath());
            tempTokenRequest.consumerKey = fitBitConfig.getClientConsumerKey();
            tempTokenRequest.transport = transport;
            tempTokenRequest.signer = signer;
            tempTokenRequest.callback = fitBitConfig.getWebAppUrl() + "/api/device/authorize?userId="+userId;
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
            OAuthAuthorizeTemporaryTokenUrl authTempTokenRequest = new OAuthAuthorizeTemporaryTokenUrl(fitBitConfig.getBaseUrl()+fitBitConfig.getAuthorizePath());
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
            signer.clientSharedSecret = fitBitConfig.getClientSecret();
            signer.tokenSharedSecret = user.getDevice().getSecretToken();

            OAuthGetAccessToken accessTokenRequest = new OAuthGetAccessToken(fitBitConfig.getApiUrl()+fitBitConfig.getAccessTokenPath());
            accessTokenRequest.consumerKey = fitBitConfig.getClientConsumerKey();
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
    
    @RequestMapping(value = "/device/activities/{startDate}/{endDate}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getActivities(@RequestParam String userId,
                                            @PathVariable String startDate,
                                          @PathVariable String endDate,
                                          HttpServletRequest request) {
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            // get the user
            User user = userRepository.findOne(Long.valueOf(userId));
            
            JSONObject json = deviceService.getActivities(user, 
                                            Activity.Distance, 
                                            formatter.parseDateTime(startDate), 
                                            formatter.parseDateTime(endDate));

            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        } catch(IOException ex) {
            log.error("Could not retrieve device information");
        } catch (JSONException ex) {
            log.error("Invalid JSON response", ex);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
