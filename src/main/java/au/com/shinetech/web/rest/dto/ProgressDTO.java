package au.com.shinetech.web.rest.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgressDTO {
    private class MeterInDay {
        public String date;
        public Integer meters;

        public MeterInDay(String date, Integer meters) {
            this.date = date;
            this.meters = meters;
        }

        public String getDate() {
            return date;
        }

        public Integer getMeters() {
            return meters;
        }
    }

    private int daysLeft;
    private int percentsDone;
    private int distanceDone;
    private int distanceTotal;
    private BigDecimal amount;
    private int distanceLeft;
    private long endTime;
    private List<MeterInDay> metersInDays = new ArrayList<>();
    private Map<String, Integer> metersByDays;

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

    public void setMeterInDay(String day, Integer meters) {
        metersInDays.add(new MeterInDay(day, meters));
    }

    public List<MeterInDay> getMetersInDays() {
        return metersInDays;
    }

    public Map<String, Integer> getMetersByDays() {
        return metersByDays;
    }

    public void setMetersByDays(Map<String, Integer> metersByDays) {
        this.metersByDays = metersByDays;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
