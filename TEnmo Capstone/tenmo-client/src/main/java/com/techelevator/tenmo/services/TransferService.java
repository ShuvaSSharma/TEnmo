package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;

import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/transfer";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String token) {
        this.authToken = token;
    }


    public TransferPendingDto[] getPending() {
        TransferPendingDto[] pendingTransfers = null;
        try {
            ResponseEntity<TransferPendingDto[]> response =
                    restTemplate.exchange(API_BASE_URL + "/pending", HttpMethod.GET, makeAuthEntity(), TransferPendingDto[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                pendingTransfers = response.getBody();

            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log("Exception: " + e.getMessage());
        }
        return pendingTransfers;
    }

    public TransferDto[] getTransfers() {
        TransferDto[] transfers = null;
        try {
            ResponseEntity<TransferDto[]> response =
                    restTemplate.exchange(API_BASE_URL + "/", HttpMethod.GET, makeAuthEntity(), TransferDto[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                transfers = response.getBody();
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log("Exception: " + e.getMessage());
        }
        return transfers;
    }

    public TransferDetailsDto getTransfersById(int transferId) {
        TransferDetailsDto transfer = null;
        try {
            ResponseEntity<TransferDetailsDto> response =
                    restTemplate.exchange(API_BASE_URL + "/" + transferId, HttpMethod.GET, makeAuthEntity(), TransferDetailsDto.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                transfer = response.getBody();
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log("Exception: " + e.getMessage());
        }
        return transfer;
    }


    public String requestTeBucks(int userId, BigDecimal amount) {
        TransferRequestDto transferRequestDto = new TransferRequestDto();
        transferRequestDto.setUserTo(userId);
        transferRequestDto.setAmount(amount);
        String responseMessage = null;
        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(API_BASE_URL + "/request", HttpMethod.POST, makeTransferRequestEntity(transferRequestDto), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                responseMessage = response.getBody();
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log("Exception: " + e.getMessage());
            //responseMessage = handleClientError(e);
        }
        return responseMessage;
    }

    public String sendTeBucks(int userId, BigDecimal amount) {
        TransferRequestDto transferRequestDto = new TransferRequestDto();
        transferRequestDto.setUserTo(userId);
        transferRequestDto.setAmount(amount);
        String responseMessage = null;
        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, makeTransferRequestEntity(transferRequestDto), String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                responseMessage = response.getBody();
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            responseMessage = handleClientError(e);
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            responseMessage = "Network error occurred. Please try again later.";
        }
        return responseMessage;
    }

    public String approveOrRejectTransfer(int transferId, TransferStatus transferStatus) {
        String responseMessage = null;
        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(API_BASE_URL + "/" + transferId + "/update_transfer", HttpMethod.PUT, makeTransferStatus(transferStatus), String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                responseMessage = response.getBody();
            } else if (response.getStatusCode().is4xxClientError()) {
                responseMessage = response.getBody();
            }
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getMessage());
            responseMessage = handleClientError(e);
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            responseMessage = "Network error occurred. Please try again later.";
        }
        return responseMessage;
    }

    private String handleClientError(RestClientResponseException e) {
        if (e.getRawStatusCode() == HttpStatus.BAD_REQUEST.value()) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("Insufficient funds")) {
                return "Insufficient funds for the transfer.";
            } else {
                return "There was an error with your request. Please check your input.";
            }
        }
        return "An unexpected error occurred. Please try again later.";
    }

    private HttpEntity<TransferRequestDto> makeTransferRequestEntity(TransferRequestDto transferRequestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferRequestDto, headers);
    }

    private HttpEntity<TransferStatus> makeTransferStatus(TransferStatus transferStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferStatus, headers);
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
