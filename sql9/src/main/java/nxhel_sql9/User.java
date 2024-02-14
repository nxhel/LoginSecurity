package nxhel_sql9;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.Map;

public class User implements IUser, SQLData {
    public static final String TYPENAME = "JLUSER_TYPE";
    private String userId;
    private long FailedLoginCount;
    private byte[] salt;
    private byte[] passwordHash;

    public User(String userId, byte[] salt, byte[] passwordHash,long FailedLoginCount) {
        this.userId = userId;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.FailedLoginCount = FailedLoginCount;
   }

   @Override
    public String getUser() {
        return this.userId;
    }

    @Override
    public byte[] getHash() {
        return this.passwordHash;
    }

    @Override
    public byte[] getSalt() {
        return this.salt;
    }

    @Override
    public long getFailedLoginCount() {
        return this.FailedLoginCount;
   }

    @Override
    public void setFailedLoginCount(long newCount) {
        this.FailedLoginCount = newCount;
    }


    public void setUser(String newUserId) {
        this.userId=newUserId;
    }

    public void setSalt (byte[] newSalt){
        this.salt= newSalt;
    }

    public void setPasswordHash(byte[] newPasswordHash){
        this.passwordHash=newPasswordHash;
    }
 
   public String getSQLTypeName() throws SQLException {
        return TYPENAME;
   }

   public void readSQL(SQLInput stream, String typeName) throws SQLException {
        setUser(stream.readString());
        setSalt(stream.readBytes());
        setPasswordHash(stream.readBytes());
        setFailedLoginCount((stream.readLong()));
   }

    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeString(getUser());
        stream.writeBytes(getSalt());
        stream.writeBytes(getHash());
        stream.writeLong(getFailedLoginCount());
   }

    public void addUserToDatabase(Connection conn)throws SQLException, ClassNotFoundException{
        try{
            Map map = conn.getTypeMap();
            conn.setTypeMap(map);
            map.put("JLUSER_TYPE", Class.forName("nxhel_sql9.User"));
            User userToAdd = new User (this.userId, this.salt, this.passwordHash, this.FailedLoginCount);
            String sql = "{call nxhelAssignment9.addUser(?)}";
            try(CallableStatement stmt = conn.prepareCall(sql)){
                stmt.setObject (1, userToAdd);
                stmt.execute();
            }
        }
        catch (SQLException exception) {
            System.out.println("Error Occured in AddToDatabase User ");
        }
        catch (ClassNotFoundException e  ){
            System.out.println("Error Occured in AddToDatabase User (ClassNotFoundExpection)");
        }
    }
}






