package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {
    public static final String API_BASE_URL = "http://localhost:8080/account";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;
    private BasicLogger BasicLoggerException;


    public void setAuthToken(String token) {
        this.authToken = token;
    }
    public BigDecimal getBalance() {
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(API_BASE_URL + "/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                balance = response.getBody();
            } else {
                System.out.println("Sorry, we couldn't retrieve your balance at this time. Please try again later.");
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            String errorMessage = "Error retrieving balance: " + e.getMessage();
            BasicLoggerException.log(errorMessage);
        }
        return balance;
    }
    public User[] getUsers() {
        User[] users = null;
        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(API_BASE_URL + "/user", HttpMethod.GET, makeAuthEntity(), User[].class);
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
