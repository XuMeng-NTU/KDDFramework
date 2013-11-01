/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.reader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;
import persistence.dataset.Dataset;

/**
 *
 * @author Meng
 */
public class DatasetReader {

    public List<Map<String, Object>> read(Dataset format, String filename){
        try {
            CsvMapReader reader = new CsvMapReader(new FileReader(filename), CsvPreference.STANDARD_PREFERENCE);
            String[] header = reader.getHeader(true);
            
            CellProcessor[] processors = new CellProcessor[format.getAttributes().size()];
            String[] nameMapping = new String[format.getAttributes().size()];
            
            int i;
            
            for(i=0;i<format.getAttributes().size();i++){
                Class repr = Class.forName(format.getAttributes().get(i).getFormat());
                
                nameMapping[i] = format.getAttributes().get(i).getFieldName();
                
                if(repr.equals(ParseDate.class)){
                    processors[i] = (CellProcessor) repr.getConstructor(String.class).newInstance("yyyyMMdd");
                } else{
                    processors[i] = (CellProcessor) repr.newInstance();
                }
            }

            List<Map<String, Object>> data = new ArrayList<>();
            
            Map<String, Object> temp = reader.read(nameMapping, processors);
            
            while(temp!=null){
                data.add(temp);
                temp = reader.read(header, processors);
            }
         
            return data;            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DatasetReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return null;
    }
    
}
