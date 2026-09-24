package org.dahllab.opsservicedoc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


// @ConfigurationProperties: bindet alle Properties mit dem mit dem Prefix "glpi."
// automatisch an die Felder dieser Klasse (glpi.api-url -> apiUrl, usw.).
// Vorteil gegenüber einzelnen @Value-Annotationen: typsicher, zentral an
// einer Stelle gebündelt, und IntelliJ bietet Autovervolsständigung dafür
// (DRY: eine Config-Quelle statt vieler verstreuter Value-Aufrufe).
@Configuration
@ConfigurationProperties(prefix = "glpi")
public class GlpiConfig {

    private String apiUrl;
    private String appToken;
    private String userToken;


    // Getter/Setter werden von Spring Boot für die automatische Befüllung
    // benötigt (kein Lombok hier, da @ConfigurationProperties klassische
    // Java-Bean-Konventionen erwartet.)
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    // Stellt den fertig konfigurierten RestClient als Spring-Bean bereit.
    // Wird automatisch in GlpiClient injiziert (Constructor Injection).
    // Im Test wird stattdessen manuell ein RestClient mit
    // MockRestServiceServer gebaut, ohne dass diese Bean-Methode
    // überhaupt aufgerufen wird.
    @Bean
    public RestClient glpiRestClient() {
        return RestClient.builder()
                .baseUrl(getApiUrl())
                .build();
    }

}



