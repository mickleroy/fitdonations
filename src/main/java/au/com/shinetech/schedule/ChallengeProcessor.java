
package au.com.shinetech.schedule;

import au.com.shinetech.domain.Activity;
import au.com.shinetech.domain.Challenge;
import au.com.shinetech.repository.ChallengeRepository;
import au.com.shinetech.service.ChallengeService;
import au.com.shinetech.service.DeviceService;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author michael
 */
@Component
public class ChallengeProcessor {
            
    @Inject
    private ChallengeRepository challengeRepository;
    
    @Inject
    private ChallengeService challengeService;
    
    @Inject
    private DeviceService deviceService;
    
    private final Logger log = LoggerFactory.getLogger(ChallengeProcessor.class);
    
    /**
     * Every 5 minutes, process completed challenges.
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void processChallenges() {
        log.info("Start --> Processing challenges");
        
        List<Challenge> challengesToProcess = challengeRepository.findAllForProcessing();
        
        for(Challenge challenge : challengesToProcess) {
            
            try {
                log.info("Processing challenge {}", challenge.getId());
                int targetDistance = challenge.getDistance();
                Long totalDistanceCovered = deviceService.getActivityTotal(challenge.getUser(), 
                                                                        Activity.Distance,
                                                                        challenge.getStartDate(),
                                                                        challenge.getEndDate());
                if(targetDistance > totalDistanceCovered) {
                    // make a donation
                    Result<Transaction> result = challengeService.makePayment(challenge.getAmount(), 
                                                                              challenge.getUser());
                    challenge.setPaymentMessage(result.getTarget().getProcessorResponseCode() + " - " + 
                                                result.getTarget().getProcessorSettlementResponseText());
                    
                    if(result.isSuccess()){
                        challenge.setPayed(true);
                    }
                }
                challenge.setFinished(true);
                challengeRepository.save(challenge);
            } catch (IOException | JSONException ex) {
                log.error("An error occurred processing a challenge", ex);
                continue;
            }
        }
        
        log.info("End --> Processing challenges");
    }
}
