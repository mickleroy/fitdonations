package au.com.shinetech.service;

import au.com.shinetech.config.WebConfigurer;
import au.com.shinetech.domain.User;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChallengeService {

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    public Result<Transaction> makePayment(BigDecimal amount, User user) {
        log.info("Making a donation of {} for {}", amount, user.getId());
        
        TransactionRequest request = new TransactionRequest()
                .customerId(user.getUuid())
                .amount(amount);

        return WebConfigurer.gateway.transaction().sale(request);
    }
}
