
package au.com.shinetech.web.rest;

import au.com.shinetech.repository.UserRepository;
import au.com.shinetech.service.MailService;
import au.com.shinetech.service.UserService;
import com.codahale.metrics.annotation.Timed;
import java.util.HashMap;
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
import org.springframework.web.util.UriComponents;
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
import java.util.logging.Level;

/**
 *
 * @author michael
 */
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
    
    private static final String CLIENT_CONSUMER_KEY = "2f4ac84d9282434084fff5c23b2aeb92";
    private static final String CLIENT_SECRET = "6564439417104201a920b4dd3bf0cd83";
    
    private static final String FITBIT_BASE_URL = "https://www.fitbit.com";
    private static final String FITBIT_API_URL = "https://api.fitbit.com";
    private static final String WEBAPP_URL = "http://www.fitdonate.co";
    
    private static final String FITBIT_REQUEST_TOKEN = "/oauth/request_token";
    private static final String FITBIT_AUTHORIZE = "/oauth/authorize";
    private static final String FITBIT_ACCESS_TOKEN = "/oauth/access_token";
    
    /**
     * GET  /device/link -> initiates the linking of a device to a user.
     */
    @RequestMapping(value = "/device/link",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> linkDevice(UriComponentsBuilder builder) {
        try {
            // retrieve temporary token from fitbit
            HttpTransport transport = new ApacheHttpTransport();
            OAuthHmacSigner signer = new OAuthHmacSigner();
            signer.clientSharedSecret = CLIENT_SECRET;

            OAuthGetTemporaryToken tempTokenRequest = new OAuthGetTemporaryToken(FITBIT_API_URL+FITBIT_REQUEST_TOKEN);
            tempTokenRequest.consumerKey = CLIENT_CONSUMER_KEY;
            tempTokenRequest.transport = transport;
            tempTokenRequest.signer = signer;
            tempTokenRequest.callback = WEBAPP_URL + "/api/device/authorize";
            OAuthCredentialsResponse credResponse = tempTokenRequest.execute();

            // store secret token in session for HMAC-SHA1 signing
//            request.getSession(true).setAttribute("SECRET_TOKEN", credResponse.tokenSecret);

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
    public ResponseEntity<?> postAuthorisation() {
        // parse callback url
//        String callbackUrl = request.getRequestURL().toString()+"?"+request.getQueryString();
        OAuthCallbackUrl callbackParser = new OAuthCallbackUrl(callbackUrl);

        // exchange temporary token for access token
        HttpTransport transport = new ApacheHttpTransport();
        OAuthHmacSigner signer = new OAuthHmacSigner();
        signer.clientSharedSecret = CLIENT_SECRET;
//        signer.tokenSharedSecret = (String)request.getSession(true).getAttribute("SECRET_TOKEN");

        OAuthGetAccessToken accessTokenRequest = new OAuthGetAccessToken(FITBIT_API_URL+FITBIT_ACCESS_TOKEN);
        accessTokenRequest.consumerKey = CLIENT_CONSUMER_KEY;
        accessTokenRequest.transport = transport;
        accessTokenRequest.signer = signer;
        accessTokenRequest.temporaryToken = callbackParser.token;
        accessTokenRequest.verifier = callbackParser.verifier;                
        OAuthCredentialsResponse credResponse = accessTokenRequest.execute();

        System.out.println("ACCESS TOKEN: " + credResponse.token);

        // store access token and new secret in session
//        request.getSession(true).setAttribute("ACCESS_TOKEN", credResponse.token);
//        request.getSession(true).setAttribute("SECRET_TOKEN", credResponse.tokenSecret);

        return "redirect:"+returnPath;
        
        
        return new ResponseEntity<>("Authorisation complete!", HttpStatus.OK);
    }
}
