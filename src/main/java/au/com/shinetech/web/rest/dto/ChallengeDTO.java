package au.com.shinetech.web.rest.dto;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 *
 */
public class ChallengeDTO {
    @NotNull
    private int distance;
    @NotNull
    private Date endDate;
    private int amount;
    @NotNull
    private Long charityId;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Long getCharityId() {
        return charityId;
    }

    public void setCharityId(Long charityId) {
        this.charityId = charityId;
    }
}
