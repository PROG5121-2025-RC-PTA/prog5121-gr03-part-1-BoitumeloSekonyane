/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatserverpoepart01;
import javax.swing.JOptionPane;
import java.util.HashMap;// This import helps my program to store data in pairs whilst associating them via a unique object , i.e. user names & passwords that belong to a specific user are paired together to verify the users login
import java.util.regex.Pattern;//This is to compile my characters and numbers into matching strings: the code uses regex to compile the international cellphone number that occurs or than once in the program
/**
 *
 * @author RC_Student_lab
 */

/**The following Program will create a registration and login
 * portal for a chat application called the Orbit
 * 
 * @author RC_Student_lab
 */ 
public class ChatServerPOEPart01 {

    //Store user information: user [password,phoneNumber]
    private static HashMap<String, String[]> users = new HashMap<>(); //The HashMap has been referred from the YouTube BroCode channel
    
    public static void main(String[] args) {
      while (true){
          String[] options = {"Register", "Login", "Exit"};
          int choice = JOptionPane.showOptionDialog(null, "Choose an option", "The Obrit",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
          
          if (choice==0){
          registerUser();
          }else if (choice==1){
            loginUser();  
          }else{
              break; //Exit the first program in the main method
          }
      }
          
        
    } //Make the Methods Private to restrict other programs from using this code, except for this specific program
    private static void registerUser(){
        String userName = JOptionPane.showInputDialog("Enter a Username;");
        String password = JOptionPane.showInputDialog("Enter a Password");
        String saPhoneNumber = JOptionPane.showInputDialog("Enter a South African phone number");
        
        if (userName != null && password != null && saPhoneNumber != null) {
            if (checkUserName(userName)){
                if(checkPasswordComplexity(password)){
                    if (checkCellPhoneNumber(saPhoneNumber)){
          if (!users.containsKey(userName)){
              users.put(userName, new String[]{password, saPhoneNumber});
              JOptionPane.showMessageDialog(null,"Registration successful!");
          }else{
              JOptionPane.showMessageDialog(null, "Username already Exists.");
          }
          } else{
                        JOptionPane.showMessageDialog(null, "Cell phone number incorrectly formatted or does not contain South African code.");
                    }
        } else {
                    JOptionPane.showMessageDialog(null, "Password is not correctly formatted, please ensure that the password contains at least eight characters, a special letter, a numner, and a special character.");
                }
        }else {
                  JOptionPane.showMessageDialog(null,"Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
         }
       }         
    }            
                
      private static void loginUser(){          
         String userName = JOptionPane.showInputDialog("Enter your username:");     
         String password = JOptionPane.showInputDialog("Enter your password:");       
         String saPhoneNumber = JOptionPane.showInputDialog("Enter your South African Phone Number");
     
         if ( userName!= null && password != null && saPhoneNumber != null) {
             if (users.containsKey(userName)){
                 String[] userDetails = users.get(userName);
                if (userDetails[0].equals(password) && userDetails[1].equals(saPhoneNumber)){
                  JOptionPane.showMessageDialog(null, "Login successful!");
                  JOptionPane.showMessageDialog(null,"Welcome"  +userName + "The Orbit is happy to see you again.");
                }else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials or phone number.");
                }
             }else {
                 JOptionPane.showMessageDialog(null, "User does not exist.");
             }
         }      
      }
      
      private static boolean checkUserName(String userName) {
          //UserName: Contains an underscore and is at most 5 characters long
                return userName.contains("_") && userName.length() <=5;
      }
       //The  String Regex code has been referred from Microsoft coPilot as an API tool that authenticates and matches numerical and string variables
      private static boolean checkPasswordComplexity(String password) {
          //Password: Contains at least 8 characters, one uppercase letter, one number, and one special character
          String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$"; //This is to include any special character and 8 numbers along with the uppercase letter that a user desires as their password      
           return Pattern.matches(regex,password);     
      }         
      private static boolean checkCellPhoneNumber(String number) {
          //Phone number: start with "0" or "+27", followed by 9 digits
          String regex = "(0\\d{9}|\\+27\\d{9})$"; //Matches SA numbers starting with "0" (followed by 9 digits) or "+27" (followed by 9 digits).
          return Pattern.matches(regex,number);
      }  
}     
                
              