/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.senario;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import persistence.flow.Flow;
import persistence.registry.RegistryItem;
import persistence.senario.source.Source;

/**
 *
 * @author Meng
 */
@Entity
public class Senario implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "flow_id", unique=true)    
    private Flow flow;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="senario")
    private List<Source> sources = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "registry_id", unique=true)
    private RegistryItem registration;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFlow(Flow flow){
        this.flow = flow;
    }
    
    public Flow getFlow(){
        return flow;
    }
    
    public void addSource(Source source){
        sources.add(source);
        source.setSenario(this);
    }
    
    public List<Source> getSources(){
        return sources;
    }

    public void removeSource(Source source){
        sources.remove(source);
    }
    
    public void setRegistration(RegistryItem registration){
        this.registration = registration;
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
        if (!(object instanceof Senario)) {
            return false;
        }
        Senario other = (Senario) object;
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
