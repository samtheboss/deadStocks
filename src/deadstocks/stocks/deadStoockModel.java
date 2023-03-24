/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks.stocks;

import deadstocks.dbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.Notifications;

/**
 *
 * @author samue
 */
public class deadStoockModel {

    String item_name;
    String item_code;
    String item_location;
    String supplier_name;
    double qty;
    String suom;
    String date;
    double purch_qty;
    double sales_qty;
    double stock_value;
    double buy_price;
    double sale_price;
    double stock_price;
    double sale_value;
    double purchase_value;
    String trn_date;

    public deadStoockModel() {
    }

    public deadStoockModel(String item_name, String item_code, String item_location,
            String supplier_name, double qty, String suom, String date,
            double purch_qty, double sales_qty, double stock_value, double buy_price,
            double sale_price, double stock_price, double sale_value, double purchase_value,String trn_date) {
        this.item_name = item_name;
        this.item_code = item_code;
        this.item_location = item_location;
        this.supplier_name = supplier_name;
        this.qty = qty;
        this.suom = suom;
        this.date = date;
        this.purch_qty = purch_qty;
        this.sales_qty = sales_qty;
        this.stock_value = stock_value;
        this.buy_price = buy_price;
        this.sale_price = sale_price;
        this.stock_price = stock_price;
        this.sale_value = sale_value;
        this.purchase_value = purchase_value;
        this.trn_date = trn_date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getItem_location() {
        return item_location;
    }

    public void setItem_location(String item_location) {
        this.item_location = item_location;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getSuom() {
        return suom;
    }

    public void setSuom(String suom) {
        this.suom = suom;
    }

    public double getPurch_qty() {
        return purch_qty;
    }

    public void setPurch_qty(double purch_qty) {
        this.purch_qty = purch_qty;
    }

    public double getSales_qty() {
        return sales_qty;
    }

    public void setSales_qty(double sales_qty) {
        this.sales_qty = sales_qty;
    }

    public double getStock_value() {
        return stock_value;
    }

    public void setStock_value(double stock_value) {
        this.stock_value = stock_value;
    }

    public double getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(double buy_price) {
        this.buy_price = buy_price;
    }

    public double getSale_price() {
        return sale_price;
    }

    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }

    public double getStock_price() {
        return stock_price;
    }

    public void setStock_price(double stock_price) {
        this.stock_price = stock_price;
    }

    public double getSale_value() {
        return sale_value;
    }

    public void setSale_value(double sale_value) {
        this.sale_value = sale_value;
    }

    public double getPurchase_value() {
        return purchase_value;
    }

    public void setPurchase_value(double purchase_value) {
        this.purchase_value = purchase_value;
    }

    public String getTrn_date() {
        return trn_date;
    }

    public void setTrn_date(String trn_date) {
        this.trn_date = trn_date;
    }

    public static ObservableList<deadStoockModel> ListDeadStock(int days, String location) {
        PreparedStatement pst;
        Connection conn = new dbConnection().connection();
        ResultSet rs;
        ObservableList<deadStoockModel> data = FXCollections.observableArrayList();
        deadStoockModel dModel = new deadStoockModel();
        List<String> item_codes = new ArrayList();
        try {
            String sql;
            sql = sql(location, days);

            if (location.equals("ALL")) {
                sql += "group by s.item_code,i.item_name,i.suom,s.item_location,i.supplier,SU.ledger_name,itb.closing"
                        + ",i.buy_price,i.sale_price,stock_price,(i.stock_price*itb.closing),(i.buy_price*itb.closing),(i.sale_price*itb.closing) order by itb.closing desc";
            } else {
                sql += "and itb.item_location ='" + location + "' group by s.item_code,i.item_name,i.suom,s.item_location,i.supplier,SU.ledger_name,itb.closing"
                        + ",i.buy_price,i.sale_price,stock_price,(i.stock_price*itb.closing),(i.buy_price*itb.closing),(i.sale_price*itb.closing) order by itb.closing desc";
            }

            System.out.println(sql);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String item_code = rs.getString("item_code");
                dModel.setItem_code(rs.getString("item_code"));
                dModel.setItem_name(rs.getString("item_name"));
                dModel.setItem_location(rs.getString("item_location"));
                dModel.setQty(rs.getDouble("closing"));
                dModel.setSupplier_name(rs.getString("SUPPLIER_NAME"));
                dModel.setSuom(rs.getString("SUOM"));
                dModel.setStock_price(rs.getDouble("stock_price"));
                dModel.setBuy_price(rs.getDouble("buy_price"));
                dModel.setStock_value(rs.getDouble("stock_value"));
                dModel.setPurchase_value(rs.getDouble("purchase_value"));
                dModel.setSale_price(rs.getDouble("sale_price"));
                dModel.setSale_value(rs.getDouble("sale_value"));
                dModel.setPurch_qty(rs.getDouble("purchase_qty"));
                dModel.setSales_qty(rs.getDouble("sale_qty"));
                data.add(dModel);
                item_codes.add(item_code);
                dModel = new deadStoockModel();

            }

        } catch (SQLException ex) {
            Logger.getLogger(deadStoockModel.class.getName()).log(Level.SEVERE, null, ex);
        }

//        List<String> selectStatements = new ArrayList<>();
//        item_codes.forEach((t) -> {
//            String sql = "select max(trn_date) as trn_DAte from stock_ledger_combined where item_code ='" + t + "' and trn_type ='CSO'";
//            selectStatements.add(sql);
//        });
//        Statement stmt;
//        try {
//            stmt = conn.createStatement();
//            
//             for (String selectStatement : selectStatements) {
//                 rs = stmt.executeQuery(selectStatement);
//          
//                while (rs.next()) {
//                    
//                    System.out.println(rs.getString("trn_date"));
//                    dModel.setTrn_date(rs.getString("trn_date"));
//                      dModel = new deadStoockModel();
//             
//            }}
//        } catch (SQLException ex) {
//            Logger.getLogger(deadStoockModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        

    return data ;
}

public static String sql(String itemlocation, int days) {

        String sql = "select s.item_code,i.item_name,i.supplier,i.suom,"
                + "i.buy_price,i.sale_price,stock_price,"
                + "(i.stock_price*closing) as stock_value,"
                + "(i.buy_price*closing) as purchase_value,"
                + "(i.sale_price*closing)as sale_value,"
                + "COALESCE(SU.LEDGER_NAME,'NO SUPPLIER') AS SUPPLIER_NAME,"
                + "s.item_location,sum(s.sale_quantity) "
                + "as sale_qty,sum(s.sale_amount) as sale_amount,sum(s.purchase_quantity) "
                + "as purchase_qty,sum(s.purchase_amount) as purchase_amount,coalesce(decimal(itb.closing,20,2),2) as closing\n"
                + " from STOCK_SALES_PURCHASES s join item_master i on (s.item_code=i.item_code)"
                + " left join virtual_item_balances itb on (s.item_code=itb.item_code and "
                + "s.item_location=itb.item_location) "
                + "LEFT JOIN SUPPLIERS SU ON I.SUPPLIER =SU.LEDGER_NUMBER"
                + " where coalesce(decimal(itb.closing,20,2),2)>0 \n"
                + "and s.item_code not in (select item_code from stock_ledger_combined "
                + "where trn_date >=current timestamp - " + days + " days and order_type in ('CSO','ISO','ISS','CUS'))";

        return sql;
    }

    public static ObservableList<deadStoockModel> lastSaledate(String sql) {
        //   String sql = "select max(trn_date) as trn_DAte from stock_ledger_combined where item_code ='" + item_Code + "'";

        //  System.out.println(sql);
        ObservableList<deadStoockModel> data = FXCollections.observableArrayList();

        List<String> sqls = new ArrayList<>();
        sqls.add(sql);
        System.out.println(sqls.size());

        deadStoockModel dModel = new deadStoockModel();
        Connection conn = new dbConnection().connection();
        PreparedStatement pst;
        ResultSet rs;
      
        sqls.add(sql);
        
        sqls.forEach((t) -> {
            System.out.println(t);
        });
        try {

            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                dModel.setDate(rs.getString("trn_date"));
                System.out.println(rs.getString("trn_date"));
                data.add(dModel);
            }
        } catch (SQLException ex) {

            ProgressDialog pd = new ProgressDialog();
            pd.close();
            Logger

.getLogger(deadStoockModel.class
.getName()).log(Level.SEVERE, null, ex);

        }
        return data;
    }

}
