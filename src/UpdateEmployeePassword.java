import java.sql.*;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class UpdateEmployeePassword {
    /*
     *
     * This program updates your existing moviedb customers table to change the
     * plain text passwords to encrypted passwords.
     *
     * You should only run this program **once**, because this program uses the
     * existing passwords as real passwords, then replace them. If you run it more
     * than once, it will treat the encrypted passwords as real passwords and
     * generate wrong values.
     *
     */
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "CS122Bupup!";
        String loginUrl = "jdbc:mysql://localhost:3306/movieDB";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection conn = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

        String alterQuery = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        PreparedStatement prepStmt = conn.prepareStatement(alterQuery);
        int alterResult = prepStmt.executeUpdate();
        System.out.println("altering employees table schema completed, " + alterResult + " rows affected");


        // get the Email and password for each employee
        String query = "SELECT email, password from employees";
        prepStmt = conn.prepareStatement(query);
        ResultSet rs = prepStmt.executeQuery();

        // we use the StrongPasswordEncryptor from jasypt library (Java Simplified Encryption)
        //  it internally use SHA-256 algorithm and 10,000 iterations to calculate the encrypted password
        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

        ArrayList<String> updateQueryList = new ArrayList<>();

        System.out.println("encrypting employees' password (this might be quick)");
        while (rs.next()) {
            // get the ID and plain text password from current table
            String email = rs.getString("email");
            String password = rs.getString("password");

            // encrypt the password using StrongPasswordEncryptor
            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            // generate the update query
            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email='%s';", encryptedPassword, email);
            updateQueryList.add(updateQuery);
        }
        rs.close();

        // execute the update queries to update the password
        System.out.println("updating password");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            prepStmt = conn.prepareStatement(updateQuery);
            int updateResult = prepStmt.executeUpdate();
            count += updateResult;
        }
        System.out.println("updating password completed, " + count + " rows affected");

        System.out.println("finished");

    }

}
