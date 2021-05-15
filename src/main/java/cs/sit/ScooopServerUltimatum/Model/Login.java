package cs.sit.ScooopServerUltimatum.Model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    int uid;
    int privilege;
    String usr = null;
    String err = null;

    public Login(int uid, String usr) {
        this.uid = uid;
        this.usr = usr;
    }
    public Login(ResultSet resultSet) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        if(resultSet == null)
        {
            this.uid = -1;
        }
        else
        {
            this.uid = Integer.parseInt(resultSet.getString("uid"));
            this.privilege = Integer.parseInt(resultSet.getString("privilege"));
            this.usr = resultSet.getString("usr");
        }
    }

    public int getUid() {
        return uid;
    }

    public String getUsr() {
        return usr;
    }

    public int getPrivilege() {
        return privilege;
    }
}