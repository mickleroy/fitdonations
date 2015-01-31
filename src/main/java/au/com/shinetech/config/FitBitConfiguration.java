
package au.com.shinetech.config;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 *
 * @author michael
 */
@Configuration
public class FitBitConfiguration implements EnvironmentAware {
    
    private RelaxedPropertyResolver propertyResolver;
    
    private static final String ENV_FITBIT = "fitbit.";
    
    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_FITBIT);
    }
    
    @Bean
    public FitBitConfig fitBitConfig() {
        return FitBitConfig.fromPropertyResolver(propertyResolver);
    }
    
    public static class FitBitConfig {
        
        public String consumerKey;
        public String clientSecret;
        public String baseUrl;
        public String apiUrl;
        public String webAppUrl;
        public String requestTokenPath;
        public String authorizePath;
        public String accessTokenPath;

        public static FitBitConfig fromPropertyResolver(RelaxedPropertyResolver property) {
            FitBitConfig config = new FitBitConfig();
            config.setClientConsumerKey(property.getProperty("clientConsumerKey"));
            config.setClientSecret(property.getProperty("clientSecret"));
            config.setBaseUrl(property.getProperty("baseUrl"));
            config.setApiUrl(property.getProperty("apiUrl"));
            config.setWebAppUrl(property.getProperty("webAppUrl"));
            config.setRequestTokenPath(property.getProperty("requestTokenPath"));
            config.setAuthorizePath(property.getProperty("authorizePath"));
            config.setAccessTokenPath(property.getProperty("accessTokenPath"));
            return config;
        }
        
        public String getClientConsumerKey() {
            return consumerKey;
        }

        private void setClientConsumerKey(String consumerKey) {
            this.consumerKey = consumerKey;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        private void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        private void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        private void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public String getWebAppUrl() {
            return webAppUrl;
        }

        private void setWebAppUrl(String webAppUrl) {
            this.webAppUrl = webAppUrl;
        }

        public String getRequestTokenPath() {
            return requestTokenPath;
        }

        private void setRequestTokenPath(String requestTokenPath) {
            this.requestTokenPath = requestTokenPath;
        }

        public String getAuthorizePath() {
            return authorizePath;
        }

        private void setAuthorizePath(String authorizePath) {
            this.authorizePath = authorizePath;
        }

        public String getAccessTokenPath() {
            return accessTokenPath;
        }

        private void setAccessTokenPath(String accessTokenPath) {
            this.accessTokenPath = accessTokenPath;
        }
    }
    
}
