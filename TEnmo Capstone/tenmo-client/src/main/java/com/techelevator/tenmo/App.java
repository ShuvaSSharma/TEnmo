package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.util.BasicLogger;
import java.math.BigDecimal;
import java.text.NumberFormat;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();

    private final UserService userService = new UserService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private NumberFormat currency = NumberFormat.getCurrencyInstance();

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        String token = currentUser.getToken();
        accountService.setAuthToken(token);
        transferService.setAuthToken(token);
        userService.setAuthToken(token);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            BasicLogger.log("User logged in");
        }
    }
    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                //continue;
                System.out.println("Thank you for using TENMO. We will be happy to have you back!");
                System.exit(0);
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance();
        BasicLogger.log(String.format("%s checked their balance. Balance: %s",
                currentUser.getUser().getUsername(), currency.format(balance)));
        System.out.println("Your current account balance is: " + currency.format(balance));
    }

    private void viewTransferHistory() {
        TransferDto[] transfers = transferService.getTransfers();
        consoleService.printTransfers(transfers, currentUser.getUser().getUsername());
        int choice;
        while ((choice = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ")) != 0) {
            if (isValidTransferId(choice, transfers)) {
                consoleService.printTransferDetails(transferService.getTransfersById(choice));
            } else {
                consoleService.printMessage("Invalid transfer ID. Please try again.");
            }
        }
    }

    private void viewPendingRequests() {
        TransferPendingDto[] pendingTransfers = transferService.getPending();
        consoleService.printPendingTransfers(pendingTransfers);

        if (pendingTransfers.length == 0) {
            return;
        }

        int choice;
        while ((choice = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ")) != 0) {
            if (isValidTransferId(choice, pendingTransfers)) {
                handleTransferApprovalOrRejection(choice);
            } else {
                consoleService.printMessage("Invalid transfer ID. Please try again.");
            }
        }
    }

    private void sendBucks() {
        User[] users = userService.getUsers();
        consoleService.printUsers(users);
        int userId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if (userId == 0) {
            return;
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        String response = transferService.sendTeBucks(userId, amount);
        if (response != null) {
            System.out.println(response);
        }
    }

    private void requestBucks() {
        User[] users = userService.getUsers();
        consoleService.printUsers(users);
        int userId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        if (userId == 0) {
            return;
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        System.out.println(transferService.requestTeBucks(userId, amount));
    }

    private boolean isValidTransferId(int transferId, TransferPendingDto[] pendingTransfers) {
        for (TransferPendingDto transfer : pendingTransfers) {
            if (transfer.getTransferId() == transferId) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidTransferId(int transferId, TransferDto[] transfers) {
        for (TransferDto transfer : transfers) {
            if (transfer.getTransferId() == transferId) {
                return true;
            }
        }
        return false;
    }

    private void handleTransferApprovalOrRejection(int transferId) {
        consoleService.printApproveRejectMenu();
        int option = consoleService.promptForInt("Please choose an option: 1 to approve, 2 to reject (0 to cancel):");
        switch (option) {
            case 1:
                TransferStatus approveStatus = new TransferStatus(2, "Approved");
                String approvedResponse = transferService.approveOrRejectTransfer(transferId, approveStatus);
                if(approvedResponse.contains("Insufficient")){
                    consoleService.printMessage(approvedResponse);
                } else {
                    consoleService.printMessage(approvedResponse);
                }


                break;
            case 2:
                TransferStatus rejectStatus = new TransferStatus(3, "Rejected");
                String rejectResponse = transferService.approveOrRejectTransfer(transferId, rejectStatus);
                consoleService.printMessage(rejectResponse);

                break;
            case 0:
                consoleService.printMessage("Operation canceled.");
                break;
            default:
                consoleService.printMessage("Invalid option. Please try again.");
        }
    }
}


