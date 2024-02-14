package nxhel_sql9;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    private final static Scanner scan= new Scanner (System.in);
    public static void main( String[] args )
    {
        greetUser();
        IJLSecutiry jlSecurity = new JLSecurity();
        boolean userExit=false;
        while(!userExit){
            int userTask= userTask();
            switch(userTask){
                case 1:
                    loginTheUser(jlSecurity);
                    break;
                case 2:
                    createTheUser(jlSecurity);
                    break;
                case 3:
                    userExit=true;
                    break;
                default :
                    System.out.println("Invalid Input");
            }
        }
        (jlSecurity).closeConnection();
    }

     /**
     * Creates a new user account.
     * @param {IJLSecurity}
     * @returns {void}
     */
    public static void createTheUser(IJLSecutiry jlSecurity){
        System.out.println("FOLLOW THE STEPS TO CREATE YOUR ACCOUNT");
        String user = new String(System.console().readLine("[STEP1]. CREATE AN USERNAME: "));
        String password = new String(System.console().readPassword("[STEP2] CREATE A PASSWORD : "));
        jlSecurity.CreateUser(user, password);
        System.out.println("\nSTATUS : [ACCOUNT CREATED SUCCESFULLY]");
    }

    /**
     * logins an existingt user
     * @param {IJLSecurity}
     * @returns {void}
     */

    public static void loginTheUser(IJLSecutiry jlSecurity){
        while (true) {
            try{
                System.out.println("\nENTER YOUR CREDENTIALS TO LOGIN");
                String user = new String(System.console().readLine("ENTER your Username: "));
                String password = new String(System.console().readPassword("ENTER Your Password "));
                boolean loggedIn = jlSecurity.Login(user, password);
                if (loggedIn)
                {
                    System.out.println("\nSTATUS:[LOGGED IN SUCCESFULLY]");
                    return;
                }
                displayFailedLogin();
                int choice = scan.nextInt();
                if (choice==2){
                    return;
                }
            }
            catch (IllegalArgumentException e){
               e.printStackTrace();
            }
        }
    }

     public static int userTask() {
        displayActions();
        int userChosenTask = 0;
        while(true){
            try {
                System.out.println("ENTER NUMBER NEXT TO YOUR TASK");
                userChosenTask=scan.nextInt();

                if (!(userChosenTask >= 1&& userChosenTask <= 3)) {
                    throw new IllegalArgumentException();
                }
                break;
            } 
            catch(IllegalArgumentException e){
                System.err.println("ENTER A VALID NUMBER [Between 1 & 3]");
            }
            catch (InputMismatchException e) {
                System.err.println("ENTER NUMBERS ONLY [Between 1 & 3]");
                scan.nextLine();
            }
        }
        scan.nextLine(); 
        return userChosenTask;
    }

    public static void greetUser(){
        System.out.println("\nWELCOME TO THE DATABSE SYSTEM");
        System.out.println("ENTER YOUR SQL CREDENTIALS TO LOGIN [MAIN SYSTEM]");
    }

    public static void displayActions() {
        System.out.println("\nWHAT IS YOUR [TASK] FOR TODAY");
        System.out.println("For Login Press 1.");
        System.out.println("To Sign Up Press 2.");
        System.out.println("To Exit Press 3.");
    }
     public static void displayFailedLogin() {
        System.out.println("\nSTATUS: [INVALID LOGIN]");
        System.out.println("\nUsername Or Password Incorrect");
        System.out.println("Press 1 To Try Again.");
        System.out.println("Press 2 To Exit.");
    }
}
