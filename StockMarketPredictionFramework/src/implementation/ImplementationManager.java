/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package implementation;

import annotation.component.input.RequiredInput;
import annotation.component.output.GeneratedOutput;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistence.component.Component;
import annotation.component.parameter.Parameter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.reflections.Reflections;
import persistence.component.parameter.ParameterDefinition;

/**
 *
 * @author Meng
 */
public class ImplementationManager {
    private Properties config;
    
    public static final String IMPLEMENTATION_CONFIG = "settings/implementation/config.properties";  

    public ImplementationManager() {
        try {
            config = new Properties();
            config.load(new FileInputStream(IMPLEMENTATION_CONFIG));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ImplementationManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImplementationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Class> scanForComponent(){
        List<Class> result = new ArrayList<>();
        
        Reflections reflections = new Reflections("");
        result.addAll(reflections.getTypesAnnotatedWith(annotation.component.Component.class));
        
        return result;
    }
    
    public Component createComponentFromClass(Class provider){
        
        Component component = new Component();

        component.setProvider(provider);
        
        annotation.component.Component componentAnnotation =  (annotation.component.Component) provider.getAnnotation(annotation.component.Component.class);
        component.setType(componentAnnotation.type());
        
        for(Field field : provider.getDeclaredFields()){

            if(field.isAnnotationPresent(Parameter.class)){
                Parameter parameter = field.getAnnotation(Parameter.class);
                ParameterDefinition parameterDefinition = new ParameterDefinition();
                parameterDefinition.setFormat(parameter.format());
                parameterDefinition.setName(parameter.name());
                parameterDefinition.setFieldName(field.getName());
                
                component.addParameter(parameterDefinition);
            } else if(field.isAnnotationPresent(RequiredInput.class)){
           
                RequiredInput requiredInput = field.getAnnotation(RequiredInput.class);
                ParameterDefinition requiredInputDefinition = new ParameterDefinition();
                requiredInputDefinition.setFormat(requiredInput.format());
                requiredInputDefinition.setName(requiredInput.name());
                requiredInputDefinition.setFieldName(field.getName());
                
                component.addRequiredInput(requiredInputDefinition);
            } else if(field.isAnnotationPresent(GeneratedOutput.class)){
           
                GeneratedOutput generatedOutput = field.getAnnotation(GeneratedOutput.class);
                ParameterDefinition generatedOutputDefinition = new ParameterDefinition();
                generatedOutputDefinition.setFormat(generatedOutput.format());
                generatedOutputDefinition.setName(generatedOutput.name());
                generatedOutputDefinition.setFieldName(field.getName());
                
                component.addGeneratedOutput(generatedOutputDefinition);                
            }
        }    
        
        for(Method method : provider.getDeclaredMethods()){
            if(method.isAnnotationPresent(annotation.component.method.Method.class)){
                annotation.component.method.Method methodAnnotation = method.getAnnotation(annotation.component.method.Method.class);
                persistence.component.method.Method methodEntity = new persistence.component.method.Method();
                methodEntity.setName(methodAnnotation.name());
                methodEntity.setMethodName(method.getName());
                
                component.addMethod(methodEntity);
            }
        }
        
        return component;
    }
    
}
