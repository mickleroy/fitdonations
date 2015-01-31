
package au.com.shinetech.web.rest.dto;

import au.com.shinetech.domain.Device;
import java.util.Date;

/**
 *
 * @author michael
 */
public class DeviceDTO {
    
    private Long id;
    private Date dateAdded;
    private String description;
    private String accessToken;

    public DeviceDTO(Device device) {
        this.id = device.getId();
        this.dateAdded = device.getDateAdded();
        this.description = "Fitbit";
        this.accessToken = device.getAccessToken();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
