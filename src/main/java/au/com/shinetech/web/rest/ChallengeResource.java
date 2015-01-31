package au.com.shinetech.web.rest;

import au.com.shinetech.config.WebConfigurer;
import au.com.shinetech.domain.User;
import au.com.shinetech.repository.CharityRepository;
import au.com.shinetech.repository.UserRepository;
import au.com.shinetech.security.SecurityUtils;
import au.com.shinetech.web.rest.dto.ChallengeDTO;
import au.com.shinetech.web.rest.dto.ProgressDTO;
import com.braintreegateway.*;
import com.codahale.metrics.annotation.Timed;
import au.com.shinetech.domain.Challenge;
import au.com.shinetech.repository.ChallengeRepository;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Challenge.
 */
@RestController
@RequestMapping("/api")
public class ChallengeResource {

    private final Logger log = LoggerFactory.getLogger(ChallengeResource.class);

    @Inject
    private ChallengeRepository challengeRepository;
    @Inject
    private CharityRepository charityRepository;
    @Inject
    private UserRepository userRepository;

    /**
     * POST  /challenges -> Create a new challenge.
     */
    @RequestMapping(value = "/challenges",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Challenge challenge) {
        log.debug("REST request to save Challenge : {}", challenge);
        challengeRepository.save(challenge);
    }

    @RequestMapping(value = "/challenges/new", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void startChallenge(@RequestBody ChallengeDTO challengeDTO) {
        Challenge challenge = new Challenge();
        challenge.setAmount(new BigDecimal(challengeDTO.getAmount()));
        challenge.setDistance(challengeDTO.getDistance());
        challenge.setStartDate(DateTime.now());
        //challenge.setEndDate(new DateTime(challengeDTO.getEndDate()));
        challenge.setEndDate(new DateTime(getEndDate(challengeDTO)));
        challenge.setCharity(charityRepository.findOne(challengeDTO.getCharityId()));
        challenge.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get());
        challengeRepository.save(challenge);
        log.info("Save challenge [" + challenge + "]");
    }

    @RequestMapping(value = "/challenges/donate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void donate(@RequestParam(value="payment_method_nonce") String paymentMethodNonce) {


        TransactionRequest request = new TransactionRequest()
                .amount(new BigDecimal("10.00"))
                .paymentMethodNonce(paymentMethodNonce)
                .customerId("customerLogin");

        Result<Transaction> result = WebConfigurer.gateway.transaction().sale(request);

    }

    private Date getEndDate(ChallengeDTO challengeDTO) {
        int addDays = 0;
        switch (challengeDTO.getPeriod()) {
            case THREE_DAYS:
                addDays = 3;
                break;
            case ONE_WEEK:
                addDays = 7;
                break;
            case TWO_WEEKS:
                addDays = 14;
                break;
            case ONE_MONTH:
                addDays = 31;
                break;
        }
        return DateUtils.addDays(new Date(), addDays);
    }

    @RequestMapping(value = "/challenges/progress", method = RequestMethod.GET)
    public List<ProgressDTO> getProgress() {
        return challengeRepository.findByUserLoginOrderByStartDateDesc(SecurityUtils.getCurrentLogin()).
            stream().map(this::progress).collect(Collectors.toList());
    }

    private ProgressDTO progress(Challenge c) {
        ProgressDTO progress = new ProgressDTO();
        progress.setDistanceTotal(c.getDistance());
        progress.setDistanceDone(c.getProgress() == null ? 0 : c.getProgress());
        progress.setPercentsDone(c.getProgress() == null ? 0 : (int) ( ( (double) c.getProgress() / (double)c.getDistance()) * 100) );
        progress.setDaysLeft( (int) ( (c.getEndDate().getMillis() - new Date().getTime()) / (24 * 60 * 60 * 1000)));
        progress.setDistanceLeft(c.getProgress() == null ? c.getDistance() : c.getDistance() - c.getProgress());
        progress.setAmount(c.getAmount());
        return progress;
    }

    /**
     * GET  /challenges -> get all the challenges.
     */
    @RequestMapping(value = "/challenges",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Challenge> getAll() {
        log.debug("REST request to get all Challenges");
        return challengeRepository.findAll();
    }

    /**
     * GET  /challenges/:id -> get the "id" challenge.
     */
    @RequestMapping(value = "/challenges/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Challenge> get(@PathVariable Long id) {
        log.debug("REST request to get Challenge : {}", id);
        return Optional.ofNullable(challengeRepository.findOne(id))
            .map(challenge -> new ResponseEntity<>(
                challenge,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /challenges/:id -> delete the "id" challenge.
     */
    @RequestMapping(value = "/challenges/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Challenge : {}", id);
        challengeRepository.delete(id);
    }
}
