package au.com.shinetech.web.rest.dto;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 *
 */
public class ChallengeDTO {
    public enum Period {
        THREE_DAYS, ONE_WEEK, TWO_WEEKS, ONE_MONTH
    }

    @NotNull
    private int distance;
    @NotNull
    private Date endDate;
    private int amount;
    @NotNull
    private Long charityId;

    private Period period;

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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }
}
