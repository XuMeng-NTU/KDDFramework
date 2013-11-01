/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components.preprocessing.indicator;

import abstraction.component.NodeComponent;
import annotation.component.Component;
import annotation.component.method.Method;
import annotation.component.input.RequiredInput;
import annotation.component.output.GeneratedOutput;
import annotation.component.parameter.Parameter;
import java.util.List;
import java.util.Map;
import enumeration.component.ComponentType;

/**
 *
 * @author Meng
 */
@Component(type=ComponentType.CALCULATOR)
public class MACD extends NodeComponent{
    @Parameter(name="FAST_DAYS", format="java.lang.Integer")
    private int FAST_DAYS;
    @Parameter(name="SLOW_DAYS", format="java.lang.Integer")
    private int SLOW_DAYS;
    @Parameter(name="SIGNAL_DAYS", format="java.lang.Integer")
    private int SIGNAL_DAYS;
    
    @RequiredInput(name="CLOSE", format="java.lang.Double")
    private String CLOSE = "CLOSE";
    
    @GeneratedOutput(name="MACD_LINE", format="java.lang.Double")
    private String MACD_LINE = "MACD_LINE";
    @GeneratedOutput(name="MACD_SIGNAL", format="java.lang.Double")
    private String MACD_SIGNAL = "MACD_SIGNAL";
    @GeneratedOutput(name="MACD_DIFF", format="java.lang.Double")
    private String MACD_DIFF = "MACD_DIFF";    
    
    @Method(name="INDICATE")
    public List<Map<String, Object>> indicate(List<Map<String, Object>> data){
        Double[] fastEMA = new Double[data.size()];
        Double[] slowEMA = new Double[data.size()];
        Double[] diff = new Double[data.size()];
        Double[] signal = new Double[data.size()];        
        
        int position=0;
        
        while(position<data.size()){
            
            if(position<FAST_DAYS){
                fastEMA[position] = null;
            } else{
                if(fastEMA[position-1]==null){
                    fastEMA[position-1] = calculateAverage(data, CLOSE, position - FAST_DAYS, position);
                }
                fastEMA[position] = Util.calculateEMA(FAST_DAYS, (Double)data.get(position).get(CLOSE), fastEMA[position-1]);              
            }
            
            if(position<SLOW_DAYS){
                slowEMA[position] = null;
            } else{
                if(slowEMA[position-1]==null){
                    slowEMA[position-1] = calculateAverage(data, CLOSE, position - SLOW_DAYS, position);
                }
                slowEMA[position] = Util.calculateEMA(SLOW_DAYS, (Double)data.get(position).get(CLOSE), slowEMA[position-1]);             
            }
            
            if(fastEMA[position]==null || slowEMA[position]==null){
                diff[position] = null;
                signal[position] = null;
            } else{
                diff[position] = fastEMA[position] - slowEMA[position];
                if(position<SLOW_DAYS+SIGNAL_DAYS){
                    signal[position]=  null;
                } else{
                    if(signal[position-1]==null){
                        signal[position] = Util.calculateEMA(SIGNAL_DAYS, diff[position], calculateAverage(diff, position-SIGNAL_DAYS, position));
                    } else{
                        signal[position] = Util.calculateEMA(SIGNAL_DAYS, diff[position], signal[position-1]);
                    }
                }
            }
            position++;
        }        
  
        Map<String, Object> temp;
        for(int i=0;i<data.size();i++){
            
            data.get(i).put(MACD_LINE, diff[i]);
            data.get(i).put(MACD_SIGNAL, signal[i]);
            if(diff[i]==null || signal[i]==null){
                data.get(i).put(MACD_DIFF, null);
            } else{
                data.get(i).put(MACD_DIFF, diff[i] - signal[i]);
            }
        }        
        
        return data;
    }
    
    private static Double calculateAverage(List<Map<String, Object>> data, String key, int start, int end){
        double average = 0;
        for(int i=start;i<end;i++){
            average = ((double)data.get(i).get(key)) + average;
        }
        return average / (end-start);
    } 
    
    private static Double calculateAverage(Double[] data, int start, int end){
        double average = 0;
        for(int i=start;i<end;i++){
            average = data[i]+average;
        }
        return average/(end-start);
    }
            
}
