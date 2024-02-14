package nxhel_sql9;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.*;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;


public class JLSecurity implements IJLSecutiry {
   private Connection conn;

   //constructor that sets up the connection as soon as the main instanciate IJLSECURITY
   public JLSecurity() {
    String user = new String(System.console().readLine("Enter your username: "));
    String pwd = new String(System.console().readPassword("Enter your password: "));
        try {
            String url = "jdbc:oracle:thin:@198.168.52.211:1521/pdbora19c.dawsoncollege.qc.ca";
            this.conn = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
      }
    }

    /**
     * Creates a new user in the database with the provided Username and password.
     * @param user The user name to be used when accessing data.
     * @param password The users password.
     * @implNote The password will not be stored as plain text.
     */
    @Override
    public void CreateUser(String user, String password) {

        byte[] userSalt= createNewSalt();
        byte[] userHash= hashedPassword(userSalt,password);
        User createdUser = new User(user, userSalt, userHash, 0);
        UpdateDb(createdUser);
    }
    
    /**
     * Creates a new random salt using SecureRandom.
     * @return The generated salt as a byte array.
     */
    public byte[] createNewSalt(){

        byte[] salt = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            salt = new byte[16];
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return salt;
    }


    /**
     * Generates a hashed password 
     * @param userSalt  user's Salt
     * @param password  The password in String Format
     * @return The hashed password as a byte array (hashed).
     */
    public byte[] hashedPassword(byte[] userSalt, String password){

        byte[] hash= null;
        try {
            char[] charPassword = password.toCharArray();
            PBEKeySpec spec = new PBEKeySpec(charPassword, userSalt, 7, 64 * 8);
            SecretKeyFactory skf;
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = skf.generateSecret(spec).getEncoded();
            
        } catch (NoSuchAlgorithmException e) {
            System.err.println("ERROR OCURED IN CREATED HASH [NOSUCHALGORI]");;
            e.printStackTrace();
        }catch (InvalidKeySpecException e) {
            System.err.println("ERROR OCURED IN CREATED HASH [INVALIDKEY]");;
            e.printStackTrace();
        }
        return hash;
        
    }

    /**
     * Retrieves the USER INFO , compares the PASSWORD with the stored HASH PASSWORD, 
     * keeps tracks of the failedlogins
     * @param user     The user's username 
     * @param password The user's password
     * @return Returns true if the login is successful; otherwise, false.
     */

    @Override
    public boolean Login(String user, String password) {
       
        User userFound = getUser(user);
        if (userFound == null) {
            return false;
        }
        long failedTimes= (userFound.getFailedLoginCount()+1);
        if(failedTimes>=5){
            System.out.println("Too Many Attempts, Temporarly Blocked");
            return false;
        }
        byte[] hashedPassword = hashedPassword(userFound.getSalt(), password);
        boolean passwordMatch = MessageDigest.isEqual(hashedPassword, userFound.getHash());

        if(!passwordMatch){
            UpdateDb(userFound);
        }
        return passwordMatch;
    }

    /**
     * @param username 
     * @return Returns the user Object From the Databse
     */
    private User getUser(String username) {
         User userFromDatabase = null;
        try{
            CallableStatement callableStatement = this.conn.prepareCall("{ ? = call nxhelAssignment9.getTheUser(?)}");
            callableStatement.registerOutParameter(1, Types.STRUCT, "JLUSER_TYPE");
            callableStatement.setString(2, username);
            callableStatement.execute();

            Struct userObj = (Struct) callableStatement.getObject(1);
            if (userObj == null) {
                System.out.println("STATUS : [USER NOT FOUND]");
                return null;
            }
            
            Object[] userObjAttributes = userObj.getAttributes();
            userFromDatabase = new User(
                                    (String) userObjAttributes[0],
                                    (byte[])userObjAttributes[1],
                                    (byte[])userObjAttributes[2],
                                    ((BigDecimal)userObjAttributes[3]).longValue());
            callableStatement.close();
        }catch(SQLException e){
            System.out.println("Error Occured in getUser");
             e.printStackTrace();
        }
         return userFromDatabase;
    }

    /**
     * Updates the user information in the database with the provided User object.
     * @param theUser The User object that is ncessary for the Update.
     */
    private void UpdateDb(IUser theUser) {
        try{
            CallableStatement callableStatement = this.conn.prepareCall("{ call nxhelAssignment9.UpdateUser(?)}");
            callableStatement.setObject(1, theUser);
            callableStatement.execute();
            callableStatement.close();
        }catch(SQLException e){
            System.out.println("Error Occured in UpdateUser");
             e.printStackTrace();
        }
    }

    /**
    * Closes the connection to the database.
    */
    public void closeConnection(){
        try {
            if(!this.conn.isClosed())
            {
                this.conn.close();
                System.out.println("CONNECTION CLOSED");
            }
        } catch (SQLException e) {
            System.out.println("Error Ocurred While Closing Connection");
            e.printStackTrace();
        }

    }
}


