/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks.stocks;

/**
 *
 * @author samue
 */
import deadstocks.dbConnection;
import javafx.concurrent.Task;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LazyDataLoaderTask extends Task<ObservableList<deadStoockModel>> {
    
    private final String location;
    private  final int days;

    public LazyDataLoaderTask(String location, int days) {
        this.location = location;
        this.days = days;
    }

  
    
    @Override
    protected ObservableList<deadStoockModel> call() throws Exception {
        PreparedStatement pst;
        Connection conn = new dbConnection().connection();
        ResultSet rs;
        ObservableList<deadStoockModel> data = FXCollections.observableArrayList();
        deadStoockModel dModel = new deadStoockModel();
        try {

            String sql = "SELECT B.* ,I.ITEM_NAME,coalesce(I.SUPPLIER,'N/A') AS SUPPLIER,"
                    + "COALESCE(S.LEDGER_NAME,'NO SUPPLIER')AS SUPPLIER_NAME FROM STOCK_OPEN_BALANCES B \n"
                    + "INNER JOIN ITEM_MASTER I ON \n"
                    + "(I.ITEM_CODE =B.ITEM_CODE) \n"
                    + "LEFT JOIN SUPPLIERS S ON S.LEDGER_NUMBER =I.SUPPLIER WHERE B.ITEM_CODE  NOT IN "
                    + "(SELECT ITEM_CODE FROM(select s.item_code,b.item_location,sum(QUANTITY * conversion) "
                    + "as qty_sold,b.closing,trn_Date,\n"
                    + "(days(CURRENT_TIMESTAMP)- days(TRN_DATE)) as dayss from STOCK_LEDGER_COMBINED s left join "
                    + "virtual_item_balances b on \n"
                    + "s.item_code =b.item_code\n"
                    + "where ORDER_TYPE in('CSO','CUS','ISS','ISO')and (days(CURRENT_TIMESTAMP)- days(TRN_DATE))>=" + days + " "
                    + "GROUP BY s.item_code,b.item_location,(days(CURRENT_TIMESTAMP)- days(TRN_DATE)),b.closing,trn_Date "
                    + "))";

          
            if (location.equals("ALL")) {
                sql += "order by OPEN_QTY desc";
            } 
            else {
                sql += "and item_location ='" + location + "' order by OPEN_QTY desc";
            }
            System.out.println(sql);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dModel.setItem_code(rs.getString("item_code"));
                dModel.setItem_name(rs.getString("item_name"));
                dModel.setItem_location(rs.getString("item_location"));
                dModel.setQty(rs.getDouble("OPEN_QTY"));
                dModel.setSupplier_name(rs.getString("SUPPLIER_NAME"));
                data.add(dModel);
                dModel = new deadStoockModel();
            }

        } catch (SQLException ex) {
            Logger.getLogger(deadStoockModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    
    }
}