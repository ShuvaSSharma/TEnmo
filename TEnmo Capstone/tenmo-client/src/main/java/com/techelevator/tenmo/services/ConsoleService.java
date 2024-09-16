package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);
    private final NumberFormat currency = NumberFormat.getCurrencyInstance();
    private final int SPACE_SEPERATION1 = 10;
    private final int SPACE_SEPERATION2 = 22;
    private final int SPACE_SEPERATION3 = 37;

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("             WELCOME TO  ");
        System.out.println();
        System.out.println(" |__   __|  ____| \\ | |  \\/  |/ __ \\ ");
        System.out.println("    | |  | |__  |  \\| | \\  / | |  | |");
        System.out.println("    | |  |  __| | . ` | |\\/| | |  | |");
        System.out.println("    | |  | |____| |\\  | |  | | |__| |");
        System.out.println("    |_|  |______|_| \\_|_|  |_|\\____/ ");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("(1) Register");
        System.out.println("(2) Login");
        System.out.println("(0) Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("(1) View your current balance");
        System.out.println("(2) View your past transfers");
        System.out.println("(3) View your pending requests");
        System.out.println("(4) Send TE bucks");
        System.out.println("(5) Request TE bucks");
        System.out.println("(0) Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void printUsers(User[] users) {
        lineSeparator(SPACE_SEPERATION2);
        System.out.println("Users");
        System.out.printf(" %-10s%10s %n", "ID", "Username");
        lineSeparator(SPACE_SEPERATION2);
        for (User user : users) {
            System.out.printf(" %-10d%10s %n", user.getId(), user.getUsername());
        }
        lineSeparator(SPACE_SEPERATION1);
    }

    public void printApproveRejectMenu() {
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Exit");
        System.out.println();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printTransfers(TransferDto[] transfers, String currentUsername) {
        lineSeparator(SPACE_SEPERATION3);
        System.out.println("Transfers");
        System.out.printf(" %-10s%15s%15s %n", "ID", "From/To", "Amount");
        lineSeparator(SPACE_SEPERATION3);

        for (TransferDto transfer : transfers) {
            String fromUsername = transfer.getAccountFrom();
            String toUsername = transfer.getAccountTo();
            BigDecimal amount = transfer.getAmount();
            int transferId = transfer.getTransferId();
            String transferIdString = Integer.toString(transferId);

            if (currentUsername.equals(fromUsername)) {
                System.out.printf(" %-10d%15s%15s %n", transferId, "To: " + toUsername, currency.format(amount));
            } else if (currentUsername.equals(toUsername)) {
                System.out.printf(" %-10d%15s%15s %n", transferId, "From: " + fromUsername, currency.format(amount));
            }
        }

        lineSeparator(SPACE_SEPERATION3);
    }

    public void printTransferDetails(TransferDetailsDto transferDetailsDto) {
        lineSeparator(SPACE_SEPERATION3);
        System.out.println("Transfer Details");
        lineSeparator(SPACE_SEPERATION3);
        System.out.printf("Id: %d%n", transferDetailsDto.getTransferId());
        System.out.printf("From: %s%n", transferDetailsDto.getAccountFrom());
        System.out.printf("To: %s%n", transferDetailsDto.getAccountTo());
        System.out.printf("Type: %s%n", transferDetailsDto.getTransferType());
        System.out.printf("Status: %s%n", transferDetailsDto.getTransferStatus());
        System.out.printf("Amount: %s%n", currency.format(transferDetailsDto.getAmount()));
        lineSeparator(SPACE_SEPERATION3);
    }

    public void printPendingTransfers(TransferPendingDto[] pendingTransfers) {
        if (pendingTransfers == null || pendingTransfers.length == 0) {
            System.out.println("No pending transfers.");
            return;
        }

        lineSeparator(SPACE_SEPERATION3);
        System.out.println("Pending Transfers");
        System.out.printf(" %-10s%10s%15s %n", "ID", "From", "Amount" );
        lineSeparator(SPACE_SEPERATION3);

        for (TransferPendingDto transfer : pendingTransfers) {
            System.out.printf("  %-10d%10s%15s %n",
                    transfer.getTransferId(),
                    transfer.getAccountFrom(),
                    currency.format(transfer.getAmount()));
        }
        lineSeparator(SPACE_SEPERATION3);
    }

    private void lineSeparator(int length) {
        System.out.println("-".repeat(length));
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
