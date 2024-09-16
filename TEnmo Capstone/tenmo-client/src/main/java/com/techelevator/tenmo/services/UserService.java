package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class UserService {
    public static final String API_BASE_URL = "http://localhost:8080/user";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;


    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public User[] getUsers() {
        User[] users = null;
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(API_BASE_URL + "/", HttpMethod.GET, makeAuthEntity(), User[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                users = response.getBody();
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return users;
    }



    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        } else {
            throw new IllegalStateException("Auth token not set");
        }
        return new HttpEntity<>(headers);
    }
}
