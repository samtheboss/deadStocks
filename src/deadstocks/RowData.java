/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadstocks;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author samue
 */
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
