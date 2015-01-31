
package au.com.shinetech.service;

import au.com.shinetech.config.FitBitConfiguration;
import au.com.shinetech.domain.Activity;
import au.com.shinetech.domain.User;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import java.io.IOException;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author michael
 */
@Service
@Transactional
public class DeviceService {
    
    @Inject
    private FitBitConfiguration.FitBitConfig fitBitConfig;
        
    public Long getActivityTotal(User user, Activity activity, 
                                   DateTime startDate, DateTime endDate) throws IOException, JSONException {
        JSONObject json = getActivities(user, activity, startDate, endDate);
        JSONArray jsonArray = json.getJSONArray("activities-"+activity.name().toLowerCase());
         
        Long total = 0L;
        for(int i = 0 ; i < jsonArray.length(); i++) {
            total += jsonArray.getJSONObject(i).getLong("value");
        }
        
        return total * 1000;
    }
    
    public JSONObject getActivities(User user, Activity activity, 
                                   DateTime startDate, DateTime endDate) throws IOException, JSONException {
        DateTimeFormatter pattern = DateTimeFormat.forPattern("yyyy-MM-dd");
        String startDateFormat = pattern.print(startDate);
        String endDateFormat = pattern.print(endDate);
        
        // build request to retrieve activities from fitbit
        OAuthHmacSigner signer = new OAuthHmacSigner();
        signer.clientSharedSecret = fitBitConfig.getClientSecret();
        signer.tokenSharedSecret = user.getDevice().getSecretToken();

        OAuthParameters params = new OAuthParameters();
        params.signer = signer;
        params.consumerKey = fitBitConfig.getClientConsumerKey();
        params.token = user.getDevice().getAccessToken();

        HttpRequestFactory factory = new ApacheHttpTransport().createRequestFactory(params);
        HttpRequest httpReq = factory.buildGetRequest(
                new GenericUrl(fitBitConfig.getApiUrl()+"/1/user/-/activities/"+
                        activity.name().toLowerCase()+"/date/"+startDateFormat+"/"+endDateFormat+".json"));
        HttpResponse response = httpReq.execute();
        
        String jsonResponse = IOUtils.toString(response.getContent());
        JSONObject json = new JSONObject(jsonResponse);
        
        return json;
    }
}
