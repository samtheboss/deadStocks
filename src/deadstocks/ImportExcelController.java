/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks;

import deadstocks.stocks.ProgressDialog;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author samue
 */
public class ImportExcelController implements Initializable {

    @FXML
    private TableView<ObservableList<String>> table;

    @FXML
    private Button importFile;

    @FXML
    private Button savetoDb;
    @FXML
    private JFXTextField tableName;
    private ProgressBar progressBar = new ProgressBar();
    @FXML
    private TextArea queryTextArea;
    @FXML
    private Button exceutesq;
    ProgressDialog pro = new ProgressDialog();
    @FXML
    private Button exporttoExcel;
    @FXML
    private Button btnupdatedb;
    @FXML
    private Label itemcount;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initCompnent();
    }

    void upd() {

        // Create a map to store the old column names and their corresponding TableColumn objects
        Map<String, TableColumn<ObservableList<String>, String>> columnMap = new HashMap<>();

// Loop through the table columns and store the old column names and their TableColumn objects in the map
        for (TableColumn<ObservableList<String>, ?> column : table.getColumns()) {
            columnMap.put(column.getText(), (TableColumn<ObservableList<String>, String>) column);
        }

// Show a dialog box that allows the user to enter new column names
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Update Column Names");

// Create text fields for each column name
        List<TextField> textFields = new ArrayList<>();
        for (String oldColumnName : columnMap.keySet()) {
            TextField textField = new TextField(oldColumnName);
            textFields.add(textField);
            dialog.getDialogPane().setContent(textField);
        }

// Add OK and Cancel buttons to the dialog box
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

// Set the result converter to map the old column names to their new names
        dialog.setResultConverter(buttonType -> {
            if (buttonType == buttonTypeOk) {
                Map<String, String> result = new HashMap<>();
                for (TextField textField : textFields) {
                    TableColumn<ObservableList<String>, String> column = columnMap.get(textField.getPromptText());
                    result.put(column.getText(), textField.getText());
                }
                return result;
            }
            return null;
        });

// Show the dialog box and update the column names if the user clicked OK
        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(newColumnNames -> {
            for (TableColumn<ObservableList<String>, ?> column : table.getColumns()) {
                String oldColumnName = column.getText();
                String newColumnName = newColumnNames.get(oldColumnName);
                column.setText(newColumnName);
            }
        });

    }

    void bbbb() {
        // Assuming 'table' is the TableView object that displays the imported data
        ObservableList<TableColumn<ObservableList<String>, ?>> columns = table.getColumns();
// Iterate through the columns and add an event listener to each column header
        for (TableColumn<ObservableList<String>, ?> column : columns) {
            column.setOnEditStart(event -> {
                // Get the current column name
                String currentName = column.getText();
                // Prompt the user to enter a new column name
                TextInputDialog dialog = new TextInputDialog(currentName);
                dialog.setTitle("Rename Column");
                dialog.setHeaderText("Enter a new name for this column:");
                dialog.setContentText("New name:");
                Optional<String> result = dialog.showAndWait();
                // If the user entered a new name, update the column name
                if (result.isPresent()) {
                    column.setText(result.get());
                } // Otherwise, cancel the edit
                else {
                   // event.cancelEdit();
                }
            });
        }
    }

    void importFromExcel(File selectedFile) {
        table.getItems().clear();
        table.getColumns().clear();
        if (selectedFile != null) {
            try {
                FileInputStream file = new FileInputStream(selectedFile);
                // Create Workbook instance holding reference to .xlsx file
                XSSFWorkbook workbook = new XSSFWorkbook(file);

                // Get first/desired sheet from the workbook
                XSSFSheet sheet = workbook.getSheetAt(0);
                //Iterate through each rows one by one
                Iterator<Row> rowIterator = sheet.iterator();
                if (rowIterator.hasNext()) {
                    Row headerRow = rowIterator.next();
                    //For each row, iterate through all the columns
                    Iterator<Cell> cellIterator = headerRow.cellIterator();
                    int i = 0;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        TableColumn<ObservableList<String>, String> column = new TableColumn<>(cell.getStringCellValue());
                        final int colIndex = i;
                        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex)));
                        table.getColumns().add(column);
                        i++;
                    }
                }
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    //
                    //For each row, iterate through all the columns
                    Iterator<Cell> cellIterator = row.cellIterator();
                    ObservableList<String> rowData = FXCollections.observableArrayList();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();

                        switch (cell.getCellTypeEnum()) {
                            case NUMERIC:
                                rowData.add(String.valueOf(cell.getNumericCellValue()));
                                break;
                            case STRING:
                                rowData.add(cell.getStringCellValue());
                                break;
                            case BLANK:
                                rowData.add("-");

                            default:
                                rowData.add(cell.toString());
                                break;
                        }

                    }
                    table.getItems().add(rowData);
                    itemcount.setText(String.valueOf(table.getItems().size()));
                }
            } catch (IOException e) {
                e.getLocalizedMessage();
            }
        }
    }

    public void initCompnent() {
        table.setOnMouseClicked(e ->{
            bbbb();
        });
        
        removeColumn(table);
        importFile.setOnAction(e -> {
            table.getColumns().clear();
            table.getItems().clear();
            progress();
        });
        savetoDb.setOnAction(e -> {
            updateforeach(table, tableName.getText());

        });
        btnupdatedb.setOnAction(e -> {
            p(tableName.getText());
        });
        exceutesq.setOnAction((ActionEvent e) -> {
            String query = queryTextArea.getText().toLowerCase().trim();

            if (query.startsWith("show")) {
                int firstSpaceIndex = query.indexOf(" ");
                String firstWord = query.substring(0, firstSpaceIndex);
                String replacementWord = "select * from ";
                query = query.replaceFirst(firstWord, replacementWord);
                System.out.println(query);
            }
            if (query.startsWith("select")) {

                executeQuery(query);
            } else {
                executeUpdateOrDeleteQuery(query);
            }
        });
    }

    private void saveDataToDatabse(TableView<ObservableList<String>> tableView, String tableName) throws SQLException {
        // Get the column headers
        pro.show();
        try ( // Connect to the database
                Connection connection = new dbConnection().connection()) {
            // Get the column headers
            List<TableColumn<ObservableList<String>, ?>> columns = tableView.getColumns();

            for (ObservableList<String> listofdata : tableView.getItems()) {
                // Build the SQL insert statement
                StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
                for (int i = 0; i < columns.size(); i++) {
                    sql.append(columns.get(i).getText());
                    if (i < columns.size() - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(") VALUES (");
                for (int i = 0; i < listofdata.size(); i++) {
                    sql.append("'").append(listofdata.get(i)).append("'");
                    if (i < listofdata.size() - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(")");

                // Execute the SQL insert statement
                Statement statement = connection.createStatement();

                if (statement.executeUpdate(sql.toString()) >= 1) {
                    System.out.println("successful");
                    pro.close();
                } else {
                    System.out.println("fail");
                    pro.close();
                }

            }
            // Close the database connection
        }
    }

    void removeColumn(TableView<ObservableList<String>> tableView) {
        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {

                TableColumn<ObservableList<String>, ?> selectedColumn
                        = table.getSelectionModel().getSelectedCells().get(0).getTableColumn();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to remove the selected column?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    table.getColumns().remove(selectedColumn);
                }
            }
        });

    }

    void InsertForEach(TableView<ObservableList<String>> tableView, String tableName) {
        List<TableColumn<ObservableList<String>, ?>> columns = tableView.getColumns();
        Connection connection = new dbConnection().connection();
        tableView.getItems().stream().forEach(listofdata -> {
            // Build the SQL insert statement
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
            IntStream.range(0, columns.size()).forEach(i -> {
                sql.append(columns.get(i).getText());
                if (i < columns.size() - 1) {
                    sql.append(", ");
                }
            });
            sql.append(") VALUES (");
            IntStream.range(0, listofdata.size()).forEach(i -> {
                sql.append("'").append(listofdata.get(i)).append("'");
                if (i < listofdata.size() - 1) {
                    sql.append(", ");
                }
            });
            sql.append(")");
            System.out.println(sql);
            // Execute the SQL insert statement
            try (Statement statement = connection.createStatement()) {
                if (statement.executeUpdate(sql.toString()) >= 1) {
                    System.out.println("successful");
                } else {
                    System.out.println("fail");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    void progress() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Excel File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        File file = fileChooser.showOpenDialog(importFile.getScene().getWindow());

        if (file != null) {
            importFromExcel(file);
        }
    }

    public void p(String tablename) {
        Connection conn = new dbConnection().connection();

        List<String> Sqls = new ArrayList<>();
        for (int i = 0; i < table.getItems().size(); i++) {
            String itemcode = table.getItems().get(i).get(0);
            String orginal = table.getItems().get(i).get(1);
            String parentitem = orginal.replace("'", "").trim();

            String sql = "update " + tablename + " set description = '" + parentitem + "' where item_code ='" + itemcode + "'";
            Sqls.add(sql);

        }
        table.getItems().clear();
        executeupdateSqls(Sqls, conn);
    }

    void updateforeach(TableView<ObservableList<String>> tableView, String tableName) {
        List<TableColumn<ObservableList<String>, ?>> columns = tableView.getColumns();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Connection connection = new dbConnection().connection();
        List<String> sqls = new ArrayList();
        tableView.getItems().stream().forEach(listofdata -> {
            // Build the SQL update statement
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (int i = 1; i < columns.size(); i++) {
                String columnName = columns.get(i).getText();
                String columnValue = listofdata.get(i).replace("'", "").trim();
                if (i > 0) {
                    sql.append(" ");
                }
                sql.append(columnName).append(" = '").append(columnValue).append("'");
            }
            // Use the first column as the primary key for the WHERE condition
            String primaryKeyColumn = columns.get(0).getText();
            String primaryKeyValue = listofdata.get(0);
            sql.append(" WHERE ").append(primaryKeyColumn).append(" = '").append(primaryKeyValue).append("'");
            sqls.add(sql.toString());
            System.out.println(sql);
            //Execute the SQL update statement
        });

        executeupdateSqls(sqls, connection);
    }

    public boolean executeupdateSqls(List<String> sqls, Connection con) {
        boolean batchLock = false;
        if (!batchLock) {
            batchLock = true;
            if (sqls == null) {
                return false;
            }
            if (sqls.isEmpty()) {
                return false;
            }
            Statement stmt = null;
            try {
                stmt = con.createStatement();
                int numberOfSqlTOExcute = 0;
                for (String sql : sqls) {
                    stmt.addBatch(sql);
                    System.out.println(numberOfSqlTOExcute + ": " + sql);
                    if (numberOfSqlTOExcute % 10000 == 0) {
                        stmt.executeBatch();
                        stmt.clearBatch();
                    }
                    numberOfSqlTOExcute++;
                }

                con.setAutoCommit(false);
                stmt.executeBatch();
                con.commit();
                con.setAutoCommit(true);
                stmt.clearBatch();

                batchLock = true;
            } catch (SQLException ex) {

                batchLock = false;
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex1) {
                    Logger.getLogger(ImportExcelController.class.getName()).log(Level.INFO, null, ex1);
                    return false;
                }
                Logger.getLogger(ImportExcelController.class.getName()).log(Level.INFO, null, ex.getNextException());
                return false;
            } finally {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ImportExcelController.class.getName()).log(Level.INFO, null, ex);
                }
            }
        }

        return true;
    }

    // Define a method that updates multiple rows in the database
    private void updateRows() {
        try {
            Connection conn = new dbConnection().connection();
            PreparedStatement pst = conn.prepareStatement("UPDATE item_master SET description=? WHERE item_code=?");
            for (int i = 0; i < table.getItems().size(); i++) {

                // Get the values from the table
                String itemcode = table.getItems().get(i).get(0);
                String idValue = table.getItems().get(i).get(1);
                String orginal = table.getItems().get(i).get(2);

                String parentitem = orginal.replace("'", "");
                if (parentitem.length() < 240) {
                    pst.setString(1, parentitem);
                    pst.setString(2, itemcode);
                    pst.addBatch();
                    if (i % 1000 == 0) {
                        pst.executeBatch();
                        pst.clearBatch();
                    }
                } else {
                    System.out.println(parentitem);
                }

                // Execute the update statement 
            }
            pst.executeBatch();
            table.getItems().clear();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(ImportExcelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void executeUpdateOrDeleteQuery(String query) {

        pro.show();
        Connection connection = new dbConnection().connection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            pro.close();
            alert.setContentText("Query Executed Successfully!");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while executing the query!\n" + e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    private void executeQuery(String query) {
//        pro.show();
        try {
            Connection conn = new dbConnection().connection();

            if (query.isEmpty()) {
                queryTextArea.requestFocus();
                Notifications notifications = Notifications.create()
                        .title("Error")
                        .text("sql is reuired")
                        .position(Pos.TOP_LEFT);
                notifications.showError();
            } else {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Clear any existing columns and data from the result table
                table.getColumns().clear();
                table.getItems().clear();

                // Create columns in the result table for each column in the query result
                for (int i = 1; i <= columnCount; i++) {
                    TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnLabel(i));
                    final int j = i - 1;
                    column.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(j)));
                    table.getColumns().add(column);
//                    pro.close();
                }       // Add data to the result table for each row in the query result
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getString(i));
                    }
                    table.getItems().add(row);

//                    pro.close();
                }
            }
        } catch (SQLException e) {
            Notifications notifications = Notifications.create()
                    .title("Sql Error")
                    .text(e.getLocalizedMessage());
            notifications.showError();
            pro.close();
        }

    }

}
