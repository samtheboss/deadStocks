/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks;

/**
 *
 * @author samue
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class ExcelImport extends Application {

    private TableView<RowData> table = new TableView<>();
    private ObservableList<RowData> data = FXCollections.observableArrayList();

    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Excel Import");

        // create a file chooser
        FileChooser fileChooser = new FileChooser();

        // set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // show open file dialog
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                // read the file
                FileInputStream fileInputStream = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheetAt(0);
                boolean firstRow = true;
                for (Row row : sheet) {
                    RowData rowData = new RowData();
                    for (Cell cell : row) {
                        if (firstRow) {
                            // create a new table column for the header
                            TableColumn column = new TableColumn(cell.getStringCellValue());
                            column.setCellValueFactory(new PropertyValueFactory<RowData, String>(cell.getStringCellValue()));
                            table.getColumns().add(column);
                        } else {
                            // add the cell value to the RowData object
                            rowData.addData(cell.getStringCellValue());
                        }
                    }
                    if (!firstRow) {
                        data.add(rowData);
                    }
                    firstRow = false;
                }
                fileInputStream.close();
                workbook.close();

                // set the items of the table to the data list
                table.setItems(data);

                // add the table to the scene
                primaryStage.setScene(new Scene(table));
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public class RowData {

        private List<String> data;

        public RowData() {
            data = new ArrayList<>();
        }

        public void addData(String value) {
            data.add(value);
        }

        public List<String> getData() {
            return data;
        }
    }
}
