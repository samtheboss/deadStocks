/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks;

import deadstocks.stocks.ProgressDialog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author smartApps
 */
public class dbConnection {

    public Connection conn;

    String url, user, pass, database;

    public Connection connection() {

        url = "jdbc:db2://192.168.1.16:50000/seven";
        user = "maliplus";
        pass = "pa55word";
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection successful");
            return conn;
        } catch (ClassNotFoundException | SQLException ex) {
//        ProgressDialog pd = new ProgressDialog();
         //   pd.close();
            Logger.getLogger(dbConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
