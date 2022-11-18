package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final UserService userService = new UserService();
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
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
        if (currentUser == null) {
            consoleService.printErrorMessage();
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
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    // VIEW BALANCE
    private void viewCurrentBalance() {
        System.out.println("Your current account balance is: $ " + accountService.getAccount(currentUser.getToken()).getBalance());
    }

    // SHOW LIST OF ALL USER IDS
    private void displayUserList() {
        System.out.println("-------------------------------------------");
        System.out.println("User ID\t\t Name");
        System.out.println("-------------------------------------------");

        // Creates an array of all users
        User[] allUsers = userService.getUsers(currentUser.getToken());

        for (User item : allUsers) {
            if (!item.getUsername().equals(currentUser.getUser().getUsername())) {
                System.out.println(item.getId() + " \t\t " + item.getUsername());
            }
        }
        System.out.println("-------------------------------------------");
    }

    // ADD NEW TRANSFER (SEND)
    private void sendBucks() {

        displayUserList();

        Transfer sendTransfer = new Transfer(); // set transferType, transferStatus, accountTo, and amount. Need transferId
        sendTransfer.setTransferType(2); //automatically setting transfer type to SEND
        sendTransfer.setTransferStatus(2); //automatically setting transfer status to APPROVED

        sendTransfer.setAccountFrom(currentUser.getUser().getId()); //NOTE: The accountFrom is the current USER ID, not account number
        int accountToID = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): "); //NOTE: The accountTo is the USER ID, not account number!

        if (accountToID == 0) {
            mainMenu();
        } else if (userService.getUserById(currentUser.getToken(), accountToID) == null) {////////////////////////////////////////////////////////////////////////////////////////
            System.out.println("Please enter a valid user ID. ");
        } else {
            sendTransfer.setAccountTo(accountToID);

            if (sendTransfer.getAccountTo() == sendTransfer.getAccountFrom()) {
                System.out.println("Error! Cannot send money to your own account.");
            } else {
                BigDecimal sendAmount = consoleService.promptForBigDecimal("How much do you want to send? ");
                sendTransfer.setAmount(sendAmount);

                if (sendTransfer.getAmount().compareTo(BigDecimal.valueOf(0.01)) == -1) {
                    System.out.println("Error! Amount must be greater than 0");
                } else {
                    BigDecimal currentBalance = accountService.getAccount(currentUser.getToken()).getBalance(); //retrieving balance of current user

                    //check for sufficient funds
                    if (sendAmount.compareTo(currentBalance) == -1 | sendAmount.compareTo(currentBalance) == 0) { //if send amount is less than current balance(-1) OR equal to current balance(0)
                        Transfer returnedTransfer = transferService.create(currentUser.getToken(), sendTransfer); //returns a "proper" transfer with account ids and transfer id
                        accountService.updateAccountBalance(currentUser.getToken(), returnedTransfer); //UPDATES BOTH ACCOUNTS
                        System.out.println("Money sent. Your current balance is now: $" + accountService.getAccount(currentUser.getToken()).getBalance()); //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    } else {
                        System.out.println("Insufficient funds. Please try again.");
                    }
                }
            }
        }
    }

    // ADD NEW TRANSFER (REQUEST)
    private void requestBucks() {

        displayUserList();

        Transfer requestTransfer = new Transfer(); // set transferType, transferStatus, accountTo, and amount. Need transferId
        requestTransfer.setTransferType(1); //automatically setting transfer type to REQUEST
        requestTransfer.setTransferStatus(1); //automatically setting transfer status to PENDING

        requestTransfer.setAccountTo(currentUser.getUser().getId()); //NOTE: The accountTo is the current USER ID, not account number
        int accountFromID = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): "); //NOTE: The accountFrom is the USER ID, not account number!

        if (accountFromID == 0) {
            mainMenu();
        } else if (userService.getUserById(currentUser.getToken(), accountFromID) == null) {
            System.out.println("Please enter a valid user ID. ");
        } else {
            requestTransfer.setAccountFrom(accountFromID);

            if (requestTransfer.getAccountTo() == requestTransfer.getAccountFrom()) {
                System.out.println("Error! Cannot request money from your own account.");
            } else {
                BigDecimal requestAmount = consoleService.promptForBigDecimal("How much do you want to request? ");
                requestTransfer.setAmount(requestAmount);

                if (requestTransfer.getAmount().compareTo(BigDecimal.valueOf(0.01)) == -1) {
                    System.out.println("Error! Amount must be greater than 0");
                } else {
                    Transfer returnedTransfer = transferService.create(currentUser.getToken(), requestTransfer); //returns a "proper" transfer with account ids and transfer id
                    System.out.println("Request sent. Pending approval from user.");
                }
            }
        }
    }

    private void viewTransferHistory() {

        Transfer[] transferList = transferService.list(currentUser.getToken());

        System.out.println("-------------------------------------------");
        System.out.println("\t\t\t    TRANSFERS");
        System.out.println("ID\t\t\t From/To\t\t Amount");
        System.out.println("-------------------------------------------");

        for (Transfer item : transferList) {

            //check that accountTo or accountFrom = currentUser account id
            int fromUserId = userService.getUserByAccount(currentUser.getToken(), item.getAccountFrom()).getId();
            int toUserId = userService.getUserByAccount(currentUser.getToken(), item.getAccountTo()).getId();
            int currentUserId = currentUser.getUser().getId();

            if (fromUserId == currentUserId | toUserId == currentUserId) {
                //stores recipient/sender usernames
                String recipient = userService.getUserByAccount(currentUser.getToken(), item.getAccountTo()).getUsername();
                String sender = userService.getUserByAccount(currentUser.getToken(), item.getAccountFrom()).getUsername();
                //stores recipient/sender strings to display with To:/From:
                String recipientString = "To: " + recipient;
                String senderString = "From: " + sender;

                if (!recipient.equals(currentUser.getUser().getUsername())) {
                    System.out.println(item.getTransferId() + "\t\t" + recipientString + "\t\t $" + item.getAmount());
                } else if (!sender.equals(currentUser.getUser().getUsername())) {
                    System.out.println(item.getTransferId() + "\t\t" + senderString + "\t\t $" + item.getAmount());
                }
            }
        }

        System.out.println("-------------------------------------------");

        int inputTransferId = consoleService.promptForInt("Please enter a transfer ID to view details (0 to cancel): ");

        if (inputTransferId == 0) {
            mainMenu();
        } else if (transferService.getTransfer(currentUser.getToken(), inputTransferId) == null) {
            System.out.println("Invalid transfer ID. Please try again.");
        } else {
            printTransferDetails(transferService.getTransfer(currentUser.getToken(), inputTransferId));
        }
    }

    private void viewPendingRequests() {

        System.out.println("-------------------------------------------");
        System.out.println("\t\t   PENDING TRANSFERS");
        System.out.println("ID\t\t\t To\t\t\t Amount");
        System.out.println("-------------------------------------------");

        List<Transfer> allPending = transferService.listPending(currentUser.getToken());

        for (Transfer item : allPending) {

            int fromUserId = userService.getUserByAccount(currentUser.getToken(), item.getAccountFrom()).getId();
            int toUserId = userService.getUserByAccount(currentUser.getToken(), item.getAccountTo()).getId();
            int currentUserId = currentUser.getUser().getId();

            if (fromUserId == currentUserId) {
                String recipient = userService.getUserByAccount(currentUser.getToken(), item.getAccountTo()).getUsername();
                System.out.println(item.getTransferId() + "\t\t" + recipient + "\t\t $" + item.getAmount());
            }
        }
        System.out.println("-------------------------------------------");
        int updateResponse = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");

        if (updateResponse == 0) {
            mainMenu();
        } else if (transferService.getTransfer(currentUser.getToken(), updateResponse) == null) {
            System.out.println("Invalid transfer ID. Please try again.");
        } else {
            updateTransferStatus(updateResponse);
        }
    }

    //TODO Update Transfer Status
    private void updateTransferStatus(int transferId){

        System.out.println("");
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("3: Don't approve or reject");
        System.out.println("-------------------------------------------");
        int status = consoleService.promptForInt("Please choose an option: ");

        Transfer updatedTransfer = transferService.getTransfer(currentUser.getToken(), transferId);
        updatedTransfer.setTransferStatus(status);

        //if approved and successful
        if (status == 1 && transferService.update(currentUser.getToken(), transferId, updatedTransfer)) {
            System.out.println("Transfer has been approved.");
            ////todo update users balances when approved
        } else if (status == 2 && transferService.update(currentUser.getToken(), transferId, updatedTransfer)) {
            System.out.println("Transfer has been rejected.");
        } else if (status < 1 | status > 3){
            System.out.println("Please enter a valid option.");
        } else {
            System.out.println("Unable to update transfer.");
        }
    }

    private void printTransferDetails(Transfer transfer){

        String transferType = new String();
        String transferStatus = new String();

        //Get transferType
        if (transfer.getTransferType() == 1) {
            transferType = "Request";
        } else if (transfer.getTransferType() == 2) {
            transferType = "Send";
        }

        //Get transferStatus
        if (transfer.getTransferStatus() == 1) {
            transferStatus = "Pending";
        } else if (transfer.getTransferStatus() == 2) {
            transferStatus = "Approved";
        } else if (transfer.getTransferStatus() == 3) {
            transferStatus = "Rejected";
        }

        System.out.println("-------------------------------------------");
        System.out.println("\t\t\t TRANSFER DETAILS");
        System.out.println("-------------------------------------------");
        System.out.println("Id: " + transfer.getTransferId());
        System.out.println("From: " + userService.getUserByAccount(currentUser.getToken(), transfer.getAccountFrom()).getUsername());
        System.out.println("To: " + userService.getUserByAccount(currentUser.getToken(), transfer.getAccountTo()).getUsername());
        System.out.println("Type: " + transferType);
        System.out.println("Status: " + transferStatus);
        System.out.println("Amount: $" + transfer.getAmount());

    }
}
