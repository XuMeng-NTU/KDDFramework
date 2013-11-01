/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.flow.edge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import persistence.component.Component;
import persistence.flow.Flow;
import persistence.component.method.Method;
import persistence.flow.node.Node;
import persistence.component.parameter.ParameterValue;

/**
 *
 * @author Meng
 */
@Entity
public class Edge implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "component_id")    
    private Component component;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "edge_parameter",
            joinColumns = @JoinColumn(name="node_id"),
            inverseJoinColumns = @JoinColumn(name="parameter_id", unique=true)
            )
    private List<ParameterValue> parameters = new ArrayList<>();       
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "upstream_id")
    private Node upstream;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "downstream_id")    
    private Node downstream;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "method_id")     
    private Method method;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "flow_id") 
    private Flow flow;    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setComponent(Component link){
        this.component = link;
    }
    
    public Component getComponent(){
        return component;
    }
    
    public void addParameter(ParameterValue parameter){
        parameters.add(parameter);
    }
    
    public List<ParameterValue> getParameters(){
        return parameters;
    }    
    
    public void setUpstream(Node upstream){
        this.upstream = upstream;
    }
    
    public Node getUpstream(){
        return upstream;
    }

    public void setDownstream(Node downstream){
        this.downstream = downstream;
    }
    
    public Node getDownstream(){
        return downstream;
    }    
    
    public void setFlow(Flow flow){
        this.flow = flow;
    }
 
    public void setMethod(Method method){
        this.method = method;
    }
    
    public Method getMethod(){
        return method;
    }   
    
    public Flow getFlow(){
        return flow;
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
        if (!(object instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return upstream.getName()+" TO "+downstream.getName();
    }
    
}
