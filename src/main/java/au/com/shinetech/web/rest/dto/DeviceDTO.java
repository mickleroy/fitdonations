
package au.com.shinetech.web.rest.dto;

import au.com.shinetech.domain.Device;
import java.util.Date;

/**
 *
 * @author michael
 */
public class DeviceDTO {
    
    private Date dateAdded;
    private String description;
    private String accessToken;

    public DeviceDTO(Device device) {
        if(device != null) {
            this.dateAdded = new Date();
            this.description = "Fitbit";
            this.accessToken = device.getAccessToken();
        } else {
            this.description = "";
            this.accessToken = "";
        }
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
}
