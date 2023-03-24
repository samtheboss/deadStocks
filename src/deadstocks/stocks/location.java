/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author samue
 */
public class location {

    String ledger_number;
    String ledger_name;

    public location() {
    }

    public String getLedger_number() {
        return ledger_number;
    }

    public void setLedger_number(String ledger_number) {
        this.ledger_number = ledger_number;
    }

    public String getLedger_name() {
        return ledger_name;
    }

    public void setLedger_name(String ledger_name) {
        this.ledger_name = ledger_name;
    }

    public static ObservableList<location> ListLocations() {
        PreparedStatement pst;
        Connection conn = new dbConnection().connection();
        ResultSet rs;
        ObservableList<location> data = FXCollections.observableArrayList();
        location loc = new location();
        try {

            String sql = "select * from locations";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                loc.setLedger_number(rs.getString("MAIN_LOCATION"));
                loc.setLedger_name(rs.getString("LOCATION_NAME"));
//                data.add(new location(rs.getString("MAIN_LOCATION")));
//                
//                data.add(new location(rs.getString("username")).ge());
                loc = new location();
            }
        } catch (SQLException ex) {
            Logger.getLogger(location.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

}
