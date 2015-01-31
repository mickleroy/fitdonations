package au.com.shinetech.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * A Device.
 */
@Entity
@Table(name = "T_DEVICE")
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "secret_token")
    private String secretToken;

    @Column(name = "date_added")
    @Temporal(TemporalType.DATE)
    private Date dateAdded;
    
    @OneToOne(mappedBy = "device")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Device device = (Device) o;

        if (id != null ? !id.equals(device.id) : device.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", accessToken='" + accessToken + "'" +
                ", secretToken='" + secretToken + "'" +
                '}';
    }
}
