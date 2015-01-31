package au.com.shinetech.web.rest;

import au.com.shinetech.config.WebConfigurer;
import com.braintreegateway.*;
import com.codahale.metrics.annotation.Timed;
import au.com.shinetech.domain.Authority;
import au.com.shinetech.domain.User;
import au.com.shinetech.repository.UserRepository;
import au.com.shinetech.security.SecurityUtils;
import au.com.shinetech.service.MailService;
import au.com.shinetech.service.UserService;
import au.com.shinetech.web.rest.dto.DeviceDTO;
import au.com.shinetech.web.rest.dto.UserDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    /**
     * POST  /register -> register the user.
     */
    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        return userRepository.findOneByLogin(userDTO.getLogin())
            .map(user -> new ResponseEntity<>("login already in use", HttpStatus.BAD_REQUEST))
            .orElseGet(() -> userRepository.findOneByEmail(userDTO.getEmail())
                .map(user -> new ResponseEntity<>("e-mail address already in use", HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    CustomerRequest customerRequest = new CustomerRequest()
                            .firstName(userDTO.getFirstName())
                            .lastName(userDTO.getLastName())
                            .email(userDTO.getEmail())
                            .paymentMethodNonce(userDTO.getPaymentMethodNonce());
                    Result<Customer> result = WebConfigurer.gateway.customer().create(customerRequest);

                    if(result.isSuccess()) {
                        User user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                                userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(),
                                userDTO.getLangKey(), result.getTarget().getId());
                        String baseUrl = request.getScheme() + // "http"
                                "://" +                                // "://"
                                request.getServerName() +              // "myhost"
                                ":" +                                  // ":"
                                request.getServerPort();               // "80"

                        mailService.sendActivationEmail(user, baseUrl);
                        return new ResponseEntity<>(HttpStatus.CREATED);
                    } else {
                        log.error("************* BrainTree validation failed!");
                        for(ValidationError err : result.getErrors().getAllValidationErrors()) {

                            log.error(err.getCode() + " - " + err.getAttribute() + " - " + err.getMessage());

                        }
                        log.error("************* /end");


                        return new ResponseEntity<>(result.getTransaction().getStatus().toString() + " - could not validate with payment gateway", HttpStatus.BAD_REQUEST);
                    }


                })
        );
    }
    /**
     * GET  /activate -> activate the registered user.
     */
    @RequestMapping(value = "/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        return Optional.ofNullable(userService.activateRegistration(key))
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * GET  /authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account -> get the current user.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(
                    new UserDTO(
                            user.getId(),
                            user.getLogin(),
                            null,
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            user.getLangKey(),
                            user.getDevice(),
                            user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList())
                    ),
                    HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * GET  /account/getClientToken -> get the current user's client token.
     */
    @RequestMapping(value = "/account/getClientToken",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public String getClientToken() {
        ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
//                .customerId(SecurityUtils.getCurrentLogin());
        return WebConfigurer.gateway.clientToken().generate(clientTokenRequest);
    }

    /**
     * POST  /account -> update the current user information.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
        return userRepository
            .findOneByLogin(userDTO.getLogin())
            .filter(u -> u.getLogin().equals(SecurityUtils.getCurrentLogin()))
            .map(u -> {
                userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
                return new ResponseEntity<String>(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /change_password -> changes the current user's password
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (StringUtils.isEmpty(password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
