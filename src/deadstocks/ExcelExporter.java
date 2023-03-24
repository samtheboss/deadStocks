//import deadstocks.ColumnState;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import javafx.scene.control.TableView;
//import java.util.HashMap;
//import javafx.scene.control.TableColumn;
//import org.apache.poi.ss.util.CellRangeAddress;
//
//public class ExcelExporter {
//    HashMap<TableColumn, ColumnState> columnStates = new HashMap<>();
//
//    public static void exportTable(TableView table, String fileName) {
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("Table Data");
//
//        // Add the data from the table to the sheet
//        for (int i = 0; i < table.getItems().size(); i++) {
//            for (int j = 0; j < table.getColumns().size(); j++) {
//                ColumnState state = columnStates.get(table.getColumns().get(j));
//                if (state.isVisible()) {
//                    sheet.createRow(i).createCell(j).setCellValue(table.getColumns().get(j).getCellData(i).toString());
//}
//}
//}    // Handle the merged columns
//    for(int i = 0; i < table.getItems().size(); i++) {
//        for (int j = 0; j < table.getColumns().size(); j++) {
//            ColumnState state = columnStates.get(table.getColumns().get(j));
//            if(state.isVisible() && state.isGrouped()){
//                sheet.addMergedRegion(new CellRangeAddress(i, i + state.getSpan(), j, j));
//            }
//        }
//    }
//
//    // Write the workbook to the file
//    try {
//        FileOutputStream fileOut = new FileOutputStream(fileName);
//        workbook.write(fileOut);
//        fileOut.close();
//        workbook.close();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
//}