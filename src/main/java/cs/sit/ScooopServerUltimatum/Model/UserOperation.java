package cs.sit.ScooopServerUltimatum.Model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Base64;

import cs.sit.ScooopServerUltimatum.Utils.DBConnection;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserOperation {
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    Login login = null;
    /***
     * Make sure hash in DB is generated under this same options
     ***/
    public static final int PBKDF2_ITERATION = 65536;
    public static final int PBKDF2_KEY_LENGTH = 256;
    public static final int PBKDF2_SALT_LENGTH = 16;

    public Login checkLogin(String username, String secret){
        try {
            login = new Login(null);
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE usr LIKE ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                if(compareHash(secret, resultSet.getString("pwd"), resultSet.getString("salt")))
                {
                    login = new Login(resultSet);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return login;
    }

    public static String[] generateHash(String secret)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[PBKDF2_SALT_LENGTH];
        random.nextBytes(salt);
        String salt64 = Base64.getEncoder().encodeToString(salt);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, PBKDF2_ITERATION, PBKDF2_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = factory.generateSecret(spec);
        String[] result = { Base64.getEncoder().encodeToString(key.getEncoded()), salt64 };
        return result;
    }

    public static String generateHashFromSalt(String secret, String salt64)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = Base64.getDecoder().decode(salt64);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, PBKDF2_ITERATION, PBKDF2_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = factory.generateSecret(spec);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public boolean compareHash(String usrSecret, String goodHash, String goodSalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String usrHash = generateHashFromSalt(usrSecret, goodSalt);
//        System.out.println(usrHash + " vs " + goodHash + " via " + goodSalt);
        return usrHash.equals(goodHash);
    }

    public void updateHash(int uid, String goodHash, String goodSalt) {
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("UPDATE account SET pwd = ?, salt = ? WHERE uid = ?");
            preparedStatement.setString(1, goodHash);
            preparedStatement.setString(2, goodSalt);
            preparedStatement.setInt(3, uid);
            preparedStatement.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
    }

    public boolean validateSecret(String username, String secret) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] userHash = getUserHash(username);
        return compareHash(secret, userHash[0], userHash[1]);
    }

    public Login getUserDetails(String username) {
        try {
            login = new Login(null);
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE usr LIKE ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                login = new Login(resultSet);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return login;
    }

    public Login getUserDetails(int uid) {
        try {
            login = new Login(null);
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM account WHERE uid = ?");
            preparedStatement.setInt(1, uid);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                login = new Login(resultSet);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return login;
    }

    public String[] getUserHash(String username){
        String[] s = new String[2];
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT pwd, salt FROM account WHERE usr LIKE ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                s[0] = resultSet.getString("pwd");
                s[1] = resultSet.getString("salt");
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return s;
    }

    public int getUserPrivilege(String username) {
        int p = -1;
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT privilege FROM account WHERE usr LIKE ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                p = Integer.parseInt(resultSet.getString("privilege"));
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return p;
    }

    public int getUserPrivilege(int uid) {
        int p = -1;
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT privilege FROM account WHERE uid = ?");
            preparedStatement.setInt(1, uid);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                p = Integer.parseInt(resultSet.getString("privilege"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return p;
    }

    public int addUser(String username, String goodHash, String goodSalt64, int privilege) {
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO account VALUES (0, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, goodHash);
            preparedStatement.setString(3, goodSalt64);
            preparedStatement.setInt(4, privilege);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 0)
            {
                return -1;
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if(generatedKeys.next())
                {
                    return generatedKeys.getInt(1);
                }
                else
                {
                    return -1;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }  finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return -1;
    }

    public boolean removeUser(int uid) {
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM account WHERE uid = ?");
            preparedStatement.setInt(1, uid);
            preparedStatement.execute();
            Statement stmt = connection.createStatement();
            stmt.execute("ALTER TABLE account AUTO_INCREMENT=1");
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }  finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return false;
    }
}