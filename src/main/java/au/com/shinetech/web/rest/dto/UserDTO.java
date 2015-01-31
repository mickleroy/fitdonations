package au.com.shinetech.web.rest.dto;

import au.com.shinetech.domain.Device;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import javax.annotation.Nullable;

public class UserDTO {

    private Long id;
    
    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    @NotNull
    @Size(min = 6, max = 100)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(max = 100)
    private String email;

    @Size(min = 2, max = 5)
    private String langKey;

    private List<String> roles;

    private String paymentMethodNonce;
    
    @Nullable
    private DeviceDTO device;

    public UserDTO() {
    }

    public UserDTO(Long id, String login, String password, String firstName, String lastName, String email, String langKey,
                   Device device, List<String> roles, String paymentMethodNonce) {
        this(id, login, password, firstName, lastName, email, langKey, device, roles);

        this.paymentMethodNonce = paymentMethodNonce;
    }

    public UserDTO(Long id, String login, String password, String firstName, String lastName, String email, String langKey,
                   Device device, List<String> roles) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.langKey = langKey;
        this.roles = roles;
        this.device = device != null ? new DeviceDTO(device) : null;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getLangKey() {
        return langKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    public DeviceDTO getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
        "login='" + login + '\'' +
        ", password='" + password + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", email='" + email + '\'' +
        ", langKey='" + langKey + '\'' +
        ", roles=" + roles + '\'' +
        ", paymentMethodNonce=" + paymentMethodNonce +
        '}';
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }
}
