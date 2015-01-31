package au.com.shinetech.web.rest.dto;

import java.math.BigDecimal;

public class ProgressDTO {
    private int daysLeft;
    private int percentsDone;
    private int distanceDone;
    private int distanceTotal;
    private BigDecimal amount;
    private int distanceLeft;

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public int getPercentsDone() {
        return percentsDone;
    }

    public void setPercentsDone(int percentsDone) {
        this.percentsDone = percentsDone;
    }

    public int getDistanceDone() {
        return distanceDone;
    }

    public void setDistanceDone(int distanceDone) {
        this.distanceDone = distanceDone;
    }

    public int getDistanceTotal() {
        return distanceTotal;
    }

    public void setDistanceTotal(int distanceTotal) {
        this.distanceTotal = distanceTotal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getDistanceLeft() {
        return distanceLeft;
    }

    public void setDistanceLeft(int distanceLeft) {
        this.distanceLeft = distanceLeft;
    }
}
