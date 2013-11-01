/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.component;

import enumeration.component.ComponentType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import persistence.component.method.Method;
import persistence.component.parameter.ParameterDefinition;
import persistence.registry.RegistryItem;

/**
 *
 * @author Meng
 */
@Entity
public class Component implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Class provider;
    
    @Enumerated(EnumType.STRING)
    private ComponentType type;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "registry_id", unique=true)
    private RegistryItem registration;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "component_parameter",
            joinColumns = @JoinColumn(name="component_id"),
            inverseJoinColumns = @JoinColumn(name="parameter_id", unique=true)
            )
    private List<ParameterDefinition> parameters = new ArrayList<>();

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "component_requiredInput",
            joinColumns = @JoinColumn(name="component_id"),
            inverseJoinColumns = @JoinColumn(name="requiredInput_id", unique=true)
            )
    private List<ParameterDefinition> requiredInputs = new ArrayList<>();    

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "component_generatedOutput",
            joinColumns = @JoinColumn(name="component_id"),
            inverseJoinColumns = @JoinColumn(name="generatedOutput_id", unique=true)
            )
    private List<ParameterDefinition> generatedOutputs = new ArrayList<>(); 
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "component_function",
            joinColumns = @JoinColumn(name="component_id"),
            inverseJoinColumns = @JoinColumn(name="function_id", unique=true)
            )
    private List<Method> methods = new ArrayList<>();     

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Class getProvider(){
        return provider;
    }
    
    public void setProvider(Class provider){
        this.provider = provider;
    }
    
    public void setType(ComponentType type){
        this.type = type;
    }
    
    public ComponentType getType(){
        return type;
    }
    
    public void setRegistration(RegistryItem registration){
        this.registration = registration;
    }
    
    public void addParameter(ParameterDefinition parameter){
        parameters.add(parameter);
    }
    
    public List<ParameterDefinition> getParameters(){
        return parameters;
    }

    public void addRequiredInput(ParameterDefinition input){
        requiredInputs.add(input);
    }
    
    public List<ParameterDefinition> getRequiredInputs(){
        return requiredInputs;
    }

    public void addGeneratedOutput(ParameterDefinition output){
        generatedOutputs.add(output);
    }
    
    public List<ParameterDefinition> getGeneratedOutputs(){
        return generatedOutputs;
    }        

    public void addMethod(Method function){
        methods.add(function);
    }
    
    public List<Method> getMethods(){
        return methods;
    }  
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return registration.getName();
    }
    
}
