/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.senario.source;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import persistence.dataset.Dataset;
import persistence.flow.node.Node;
import persistence.senario.Senario;

/**
 *
 * @author Meng
 */
@Entity
public class Source implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String filename;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "node_id", unique=true)    
    private Node node;    

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "dataset_id", unique=true)    
    private Dataset dataset;        
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "senario_id") 
    private Senario senario;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNode(Node node){
        this.node = node;
    }
    
    public Node getNode(){
        return node;
    }
    
    public void setDataset(Dataset dataset){
        this.dataset = dataset;
    }
    
    public Dataset getDataset(){
        return dataset;
    } 
    
    public void setSenario(Senario senario){
        this.senario = senario;
    }
    
    public Senario getSenario(){
        return senario;
    }
    
    public String getFilename(){
        return filename;
    }
    
    public void setFilename(String filename){
        this.filename = filename;
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
        if (!(object instanceof Source)) {
            return false;
        }
        Source other = (Source) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return node.getName();
    }
    
}
