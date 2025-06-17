/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

//Some parts of this code were generated or referred by ChatGpt
package com.mycompany.jupiterscompletechatapp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
/**
 *
 * @author boitu
 */


/**
 * Main chat application class declaring user registration, login, 
 * sending messages, and managing a message database via JSON.
 */
public class JupitersCompleteChatApp {
    // storage for user credentials: username -> [password, phone]
 private static HashMap<String, String[]> users = new HashMap<>();
 
 // A place for all messages sent during session
    private static ArrayList<Message> messages = new ArrayList<>();
    
    //A plcae for Successfully sent messages
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    
    // Messages disregarded messages due to invalid input
    private static ArrayList<Message> disregardedMessages = new ArrayList<>();
    
    // Stored Messages are loaded from the JSON file
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    
    // Message hashes and IDs for tracking
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIds = new ArrayList<>();
    
    // Used for counting the total of all messages sent
    private static int totalMessages = 0;

    public static void main(String[] args) {
        boolean loggedIn = false;
        String currentUser = null;
        loadMessagesFromJSON();

        // Loop for Main app menu
        while (true) {
            String[] options = {"Register", "Login", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Choose an option", "The Orbit",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) {
                registerUser();  // Registering new user
            } else if (choice == 1) {
                if (loginUser()) {
                    loggedIn = true;
                    currentUser = JOptionPane.showInputDialog("Please re-enter your username:");
                    JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");
                    int limit = Integer.parseInt(JOptionPane.showInputDialog("How many messages do you wish to send?"));
                    chatMenu(limit); //Enter the chat menu
                }
            } else {
                break; //Exit App
            }
        }
    }
 
     // Carries out the registration of the user
private static void registerUser() {
        String userName = JOptionPane.showInputDialog("Enter a Username:");
        String password = JOptionPane.showInputDialog("Enter a Password:");
        String saPhoneNumber = JOptionPane.showInputDialog("Enter a South African phone number:");

        if (userName != null && password != null && saPhoneNumber != null) {
            if (checkUserName(userName)) {
                if (checkPasswordComplexity(password)) {
                    if (checkCellPhoneNumber(saPhoneNumber)) {
                        if (!users.containsKey(userName)) {
                            users.put(userName, new String[]{password, saPhoneNumber});
                            JOptionPane.showMessageDialog(null, "Registration successful!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Username already exists.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid SA phone number.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Password is too weak.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Username must contain underscore and be ≤ 5 chars.");
            }
        }
    }

//Carries out the Login of the User
 private static boolean loginUser() {
        String userName = JOptionPane.showInputDialog("Enter your username:");
        String password = JOptionPane.showInputDialog("Enter your password:");
        String saPhoneNumber = JOptionPane.showInputDialog("Enter your SA Phone Number:");

        if (userName != null && password != null && saPhoneNumber != null) {
            if (users.containsKey(userName)) {
                String[] userDetails = users.get(userName);
                if (userDetails[0].equals(password) && userDetails[1].equals(saPhoneNumber)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials or phone number.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User does not exist.");
            }
        }
        return false;
    }

 //Displays the menu for sending/searching/deleting messages
    private static void chatMenu(int messageLimit) {
        while (true) {
            String menu = "Choose an option:\n" +
                          "1) Send Message\n" +
                          "2) Show Recent Messages\n" +
                          "3) Quit\n" +
                          "4) Search by Recipient\n" +
                          "5) Delete by Message Hash\n" +
                          "6) Display Message Report";
            String input = JOptionPane.showInputDialog(menu);
            if (input == null) continue;

            switch (input) {
                case "1":
                    if (totalMessages < messageLimit) {
                        sendMessage();
                    } else {
                        JOptionPane.showMessageDialog(null, "Message limit reached.");
                    }
                    break;
                case "2":
                    JOptionPane.showMessageDialog(null, "Coming Soon.");
                    break;
                case "3":
                    JOptionPane.showMessageDialog(null, "Total messages sent: " + totalMessages);
                    return;
                case "4":
                    String recipient = JOptionPane.showInputDialog("Enter recipient to search:");
                    searchMessagesByRecipient(recipient);
                    break;
                case "5":
                    String hash = JOptionPane.showInputDialog("Enter message hash to delete:");
                    deleteMessageByHash(hash);
                    break;
                case "6":
                    displayMessageReport();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option.");
            }
        }
    }

    //Method for sending messages to a recipient
    private static void sendMessage() {
        String recipient = JOptionPane.showInputDialog("Enter recipient number (+27 or 0...):");
        if (!checkCellPhoneNumber(recipient) || recipient.length() > 10) {
            JOptionPane.showMessageDialog(null, "Invalid recipient number.");
            disregardedMessages.add(new Message(recipient, "Invalid message (recipient rejected)"));
            return;
        }

        String content = JOptionPane.showInputDialog("Enter your message (max 250 characters):");
        if (content.length() > 250) {
            JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.");
            disregardedMessages.add(new Message(recipient, content));
            return;
        }

        Message msg = new Message(recipient, content);
        messages.add(msg);
        sentMessages.add(msg);
        messageHashes.add(msg.getMessageHash());
        messageIds.add(msg.getMessageId());
        totalMessages++;

        JOptionPane.showMessageDialog(null,
                "Message sent!\nMessage ID: " + msg.getMessageId() +
                        "\nMessage Hash: " + msg.getMessageHash() +
                        "\nRecipient: " + msg.getRecipient() +
                        "\nMessage: " + msg.getMessage());

        saveMessagesToJSON(); //Save each message to the JSON File
    }

    //Validation of Users Name conditions: must have underscore and <=5 chars
    private static boolean checkUserName(String userName) {
        return userName.contains("_") && userName.length() <= 5;
    }

    //Validation of Password conditions: Passoword must be 8+ chars, 1 uppercase, 1 digit, 1 symbol
    private static boolean checkPasswordComplexity(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$";
        return Pattern.matches(regex, password);
    }

    //Validation of South African cell phone number format: must contain a 0 or a +27 at the beginning
    private static boolean checkCellPhoneNumber(String number) {
        String regex = "(0\\d{9}|\\+27\\d{9})$";
        return Pattern.matches(regex, number);
    }

    //Save all messages to a JSON File manually
    private static void saveMessagesToJSON() {
    try (FileWriter writer = new FileWriter("messages.json")) {
        writer.write("[\n");
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            String json = "  {\n" +
                    "    \"messageId\": \"" + msg.getMessageId() + "\",\n" +
                    "    \"messageHash\": \"" + msg.getMessageHash() + "\",\n" +
                    "    \"recipient\": \"" + msg.getRecipient() + "\",\n" +
                    "    \"message\": \"" + msg.getMessage().replace("\"", "\\\"") + "\"\n" +
                    "  }";
            if (i < messages.size() - 1) {
                json += ",";
            }
            writer.write(json + "\n");
        }
        writer.write("]");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

//Load messages from JSON File manually
    private static void loadMessagesFromJSON() {
    storedMessages.clear();
    File file = new File("messages.json");
    if (!file.exists()) {
        System.out.println("messages.json does not exist.");
        return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line.trim());
        }

        String content = sb.toString();
        if (content.length() < 5) return;

        // Remove the outer [ ] brackets
        content = content.substring(1, content.length() - 1).trim();

        String[] entries = content.split("\\},\\s*\\{");

        for (String entry : entries) {
            entry = entry.trim();
            if (!entry.startsWith("{")) entry = "{" + entry;
            if (!entry.endsWith("}")) entry = entry + "}";

            String messageId = extractValue(entry, "messageId");
            String messageHash = extractValue(entry, "messageHash");
            String recipient = extractValue(entry, "recipient");
            String message = extractValue(entry, "message");

            if (messageId != null && messageHash != null && recipient != null && message != null) {
                storedMessages.add(new Message(messageId, messageHash, recipient, message));
            }
        }

        System.out.println("Loaded " + storedMessages.size() + " messages manually.");

    } catch (IOException e) {
        e.printStackTrace();
    }
}


//Locate all messages that have been sent to the recipient
    private static void searchMessagesByRecipient(String recipient) {
        StringBuilder sb = new StringBuilder("Messages to " + recipient + ":\n");
        for (Message msg : sentMessages) {
            if (msg.getRecipient().equals(recipient)) {
                sb.append("ID: ").append(msg.getMessageId())
                        .append(", Hash: ").append(msg.getMessageHash())
                        .append(", Message: ").append(msg.getMessage())
                        .append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    //Delete messages using their Hash keys
    private static void deleteMessageByHash(String hash) {
        Message toRemove = null;
        for (Message msg : sentMessages) {
            if (msg.getMessageHash().equals(hash)) {
                toRemove = msg;
                break;
            }
        }
        if (toRemove != null) {
            sentMessages.remove(toRemove);
            messageHashes.remove(hash);
            messageIds.remove(toRemove.getMessageId());
            messages.remove(toRemove);
            JOptionPane.showMessageDialog(null, "Message deleted.");
        } else {
            JOptionPane.showMessageDialog(null, "Message hash not found.");
        }
    }

    //Show a full report of all sent messages
    private static void displayMessageReport() {
        StringBuilder sb = new StringBuilder("Full Sent Message Report:\n");
        for (Message msg : sentMessages) {
            sb.append("ID: ").append(msg.getMessageId())
                    .append(", Hash: ").append(msg.getMessageHash())
                    .append(", Recipient: ").append(msg.getRecipient())
                    .append(", Message: ").append(msg.getMessage())
                    .append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private static String extractValue(String entry, String Key) {
        String pattern = "\"" + Key + "\"\s*\"(.*?)\"";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(entry);
        return m.find() ? m.group(1) : null;
    }
}

class Message {
    private String messageId;
    private String messageHash;
    private String recipient;
    private String message;

    public Message(String recipient, String message) {
        this.messageId = generateMessageId();
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    public Message(String messageId, String messageHash, String recipient, String message) {
        this.messageId = messageId;
        this.messageHash = messageHash;
        this.recipient = recipient;
        this.message = message;
    }

    private String generateMessageId() {
        return String.valueOf((long) (Math.random() * 1_000_000_0000L));
    }

    private String createMessageHash() {
        return Integer.toHexString((messageId + recipient + message).hashCode());
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }
}    
