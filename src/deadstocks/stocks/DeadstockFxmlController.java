/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks.stocks;

import com.jfoenix.controls.JFXTextField;
import deadstocks.dbConnection;
import static deadstocks.stocks.deadStoockModel.ListDeadStock;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author samue
 */
public class DeadstockFxmlController implements Initializable {

    @FXML
    private TextField TTX_NUMBER_DAYS;
    @FXML
    private ComboBox<String> CMB_LOCATION;
    @FXML
    private Button BTN_LOAD_DATA;
    @FXML
    private Button BTN_EXPORT;
    @FXML
    private Button BTN_PRINT;
    @FXML
    private JFXTextField TXT_FILTER;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_AVG_PRICE;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_STOCK_PRICE;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_BUY_PRICE;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_PURCHASES_VALUE;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_SALES_PRICE;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_SALE_VALUE;

    @FXML
    private TableColumn<deadStoockModel, ?> COL_ITEM_CODE;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_ITEM_NAME;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_ITEM_LOCATION;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_QTY;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_SUPPLIER_NAME;
    @FXML
    private TableView<deadStoockModel> tbl_deadStock;
    @FXML

    private TableColumn<deadStoockModel, ?> COL_SUOM;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_SALE_QTY;
    @FXML
    private TableColumn<deadStoockModel, ?> COL_PURCHASE_QRY;

    @FXML
    private AnchorPane main;
    ObservableList<String> locations = FXCollections.observableArrayList();
    PreparedStatement pst;
    ResultSet rs;
    Connection conn;
    LazyDataLoaderTask task;

    ProgressDialog progressDialog = new ProgressDialog();
    @FXML
    private RadioButton rd30days;
    @FXML
    private ToggleGroup ptions;
    @FXML
    private RadioButton rd60days;
    @FXML
    private RadioButton rd120days;
    @FXML
    private RadioButton rdcustom;
    @FXML
    private RadioButton rd90days;
    @FXML
    private Label txtamaount;
    @FXML
    private Label lbsaleValue;
    @FXML
    private Label lbstockvalue;

    int numberOfDays = 90;
    @FXML
    private TableColumn<?, ?> COL_LAST_SALE_DATE;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initComp();
        inittable();
       filter();
        loadloactions();
        calculateSum();
        //  TTX_NUMBER_DAYS.setText("90");

    }

    public void initComp() {
        setDays();
        TTX_NUMBER_DAYS.setDisable(true);
        BTN_LOAD_DATA.setOnAction((ActionEvent e) -> {
            progressDialog.show();

            if (TTX_NUMBER_DAYS.getText().isEmpty() && rdcustom.isSelected()) {
                TTX_NUMBER_DAYS.setDisable(false);
                TTX_NUMBER_DAYS.requestFocus();
                TTX_NUMBER_DAYS.clear();
                progressDialog.close();
            } else {
                loadDataToTable(numberOfDays);
                calculateSum();

                progressDialog.close();
            }
        });
        BTN_EXPORT.setOnAction(e -> {
            FileChooser();
        });
        TXT_FILTER.setOnKeyTyped(e -> {
            filter();

        });
        TXT_FILTER.setOnKeyReleased(e -> {
            calculateSum();
        });
        BTN_PRINT.setOnAction(e -> {
            System.out.println(e.getEventType());
            

        });

        rdcustom.setOnAction(e -> {
            TTX_NUMBER_DAYS.setDisable(false);
            TTX_NUMBER_DAYS.requestFocus();
            setDays();

        });
        rd120days.setOnAction(e -> {
            TTX_NUMBER_DAYS.setDisable(true);
            setDays();
        });
        rd90days.setOnAction(e -> {
            TTX_NUMBER_DAYS.setDisable(true);
            setDays();
        });
        rd60days.setOnAction(e -> {
            TTX_NUMBER_DAYS.setDisable(true);
            setDays();
        });
        rd30days.setOnAction(e -> {
            TTX_NUMBER_DAYS.setDisable(true);
            setDays();
        });

    }

    public void calculateSum() {
        double totalPurchaseValue = 0.00;
        totalPurchaseValue = tbl_deadStock.getItems().stream().map(
                (item) -> (item.getPurchase_value())).reduce(totalPurchaseValue, (accumulator, _item) -> accumulator + _item);
        String pattern = "###,###,###.##";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        txtamaount.setText(decimalFormat.format(totalPurchaseValue));

        double totalStockValue = 0.00;
        totalStockValue = tbl_deadStock.getItems().stream().map(
                item -> (item.getStock_value())).reduce(totalStockValue, (accumulator, _item) -> accumulator + _item);
        lbstockvalue.setText(decimalFormat.format(totalStockValue));

        double totalSaleValue = 0.00;
        totalSaleValue = tbl_deadStock.getItems().stream().map(
                (item) -> (item.getSale_value())).reduce(totalSaleValue, (accumulator, _item) -> accumulator + _item);

        lbsaleValue.setText(decimalFormat.format(totalSaleValue));

//        double total = tbl_deadStock.getItems().stream()
//                .mapToDouble(item -> Double.parseDouble(COL_PURCHASES_VALUE.getCellData(item).toString()))
//                .sum(); 
//        lbstockvalue.setText(String.valueOf(total));
    }

    public void inittable() {

        COL_ITEM_CODE.setCellValueFactory(new PropertyValueFactory("item_code"));
        COL_ITEM_NAME.setCellValueFactory(new PropertyValueFactory("item_name"));
        COL_ITEM_LOCATION.setCellValueFactory(new PropertyValueFactory("item_location"));
        COL_SUPPLIER_NAME.setCellValueFactory(new PropertyValueFactory("supplier_name"));
        COL_QTY.setCellValueFactory(new PropertyValueFactory("qty"));
        COL_SUOM.setCellValueFactory(new PropertyValueFactory("suom"));
        COL_SALE_QTY.setCellValueFactory(new PropertyValueFactory("sales_qty"));
        COL_PURCHASE_QRY.setCellValueFactory(new PropertyValueFactory("purch_qty"));
        COL_AVG_PRICE.setCellValueFactory(new PropertyValueFactory("sale_price"));
        COL_BUY_PRICE.setCellValueFactory(new PropertyValueFactory("buy_price"));
        COL_SALE_VALUE.setCellValueFactory(new PropertyValueFactory("sale_value"));
        COL_PURCHASES_VALUE.setCellValueFactory(new PropertyValueFactory("purchase_value"));
        COL_STOCK_PRICE.setCellValueFactory(new PropertyValueFactory("stock_value"));
        COL_SALES_PRICE.setCellValueFactory(new PropertyValueFactory("sale_price"));
        COL_LAST_SALE_DATE.setCellValueFactory(new PropertyValueFactory("trn_date"));
    }

    private void loadDataToTable(int number) {
        tbl_deadStock.getItems().clear();
        if (CMB_LOCATION.getSelectionModel().getSelectedItem() == null) {
            tbl_deadStock.setItems(ListDeadStock(number, "ALL"));
            tbl_deadStock.refresh();
        } else {
            tbl_deadStock.setItems(ListDeadStock(number, CMB_LOCATION.getSelectionModel().getSelectedItem()));
            tbl_deadStock.refresh();
        }

    }

    void FileChooser() {

        Window stage = main.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Records");
        fileChooser.setInitialFileName("dead stock details");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel", "xls"));
        try {
            File file = fileChooser.showSaveDialog(stage);
            fileChooser.setInitialDirectory(file.getParentFile());
           // exportToexcel(file.toString());
            exportTable(file.toString());
            System.out.println(file);
        } catch (Exception e) {
        }

    }

    public void exportToexcel(String dir) {
        FileOutputStream fileOut = null;
        try {
            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("Data");
            Row row = spreadsheet.createRow(0);
            for (int j = 0; j < tbl_deadStock.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tbl_deadStock.getColumns().get(j).getText());
            }
            for (int i = 0; i < tbl_deadStock.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < tbl_deadStock.getColumns().size(); j++) {
                    if (tbl_deadStock.getColumns().get(j).getCellData(i) != null) {
                        row.createCell(j).setCellValue(tbl_deadStock.getColumns().get(j).getCellData(i).toString());
                    } else {
                        row.createCell(j).setCellValue("");
                        //  spreadsheet.setC
                    }
                }
            }
            fileOut = new FileOutputStream(dir + ".xls");
            workbook.write(fileOut);
            fileOut.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DeadstockFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeadstockFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileOut.close();
            } catch (IOException ex) {
                Logger.getLogger(DeadstockFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void filter() {

        FilteredList<deadStoockModel> filteredData = new FilteredList<>(tbl_deadStock.getItems(), p -> true);
        TXT_FILTER.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(data -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return Stream.of(data.getItem_code(), data.getItem_location(), data.getItem_name(), data.getSupplier_name())
                        .anyMatch(s -> s != null && s.toLowerCase().contains(lowerCaseFilter));
            });
        });
        SortedList<deadStoockModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tbl_deadStock.comparatorProperty());
        tbl_deadStock.setItems(sortedData);

    }

    public void loadloactions() {
        conn = new dbConnection().connection();
        try {
            locations.clear();
            String getloactions = "SELECT * FROM locations ";

            pst = conn.prepareStatement(getloactions);
            rs = pst.executeQuery();
            String ALL = "ALL";
            locations.add(ALL);
            while (rs.next()) {
                String sup = rs.getString("MAIN_LOCATION");

                locations.add(sup);

            }

        } catch (SQLException ex) {

            Logger.getLogger(DeadstockFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
        CMB_LOCATION.setItems(locations);
    }

    private void setDays() {

        if (rd30days.isSelected()) {
            TTX_NUMBER_DAYS.setDisable(true);
            numberOfDays = 30;

        } else if (rd60days.isSelected()) {
            TTX_NUMBER_DAYS.setDisable(true);
            numberOfDays = 60;
        } else if (rd90days.isSelected()) {
            TTX_NUMBER_DAYS.setDisable(true);
            numberOfDays = 90;
        } else if (rd120days.isSelected()) {
            TTX_NUMBER_DAYS.setDisable(true);
            numberOfDays = 120;
        } else if (rdcustom.isSelected()) {
            TTX_NUMBER_DAYS.setDisable(false);
            TTX_NUMBER_DAYS.setText("0");
            numberOfDays = Integer.parseInt(TTX_NUMBER_DAYS.getText());

        }
        TTX_NUMBER_DAYS.setText(String.valueOf(numberOfDays));
    }

    private void exportTable(String dir) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Table Data");

        // Add the data from the table to the sheet
        for (int i = 0; i < tbl_deadStock.getItems().size(); i++) {
            for (int j = 0; j < tbl_deadStock.getColumns().size(); j++) {
                sheet.createRow(i).createCell(j).setCellValue(tbl_deadStock.getColumns().get(j).getCellData(i).toString());
            }
        }

        // Handle the merged columns
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        // Write the workbook to the file
        try {
            FileOutputStream fileOut = new FileOutputStream(dir +".xlsx");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
