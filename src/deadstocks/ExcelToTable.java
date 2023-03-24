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
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class ExcelToTable extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TableView<ObservableList<String>> table = new TableView<>();
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Excel File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
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
                    rowData.add(cell.toString());
                }
                table.getItems().add(rowData);
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        StackPane root = new StackPane();
        root.getChildren().add(table);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Excel to Table");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
