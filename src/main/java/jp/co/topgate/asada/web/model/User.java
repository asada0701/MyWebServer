package jp.co.topgate.asada.web.model;

/**
 * Created by yusuke-pc on 2017/05/02.
 */
public class User {

    private int userID;
    private String pw;
    private String name;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
