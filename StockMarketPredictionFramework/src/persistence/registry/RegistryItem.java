/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence.registry;

import enumeration.registry.RegistryItemType;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author Meng
 */
@Entity
public class RegistryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Enumerated(EnumType.STRING)
    private RegistryItemType type;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parent_id", nullable=true)
    private RegistryItem parent;
    
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="parent")
    private List<RegistryItem> children = new ArrayList<>();

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }    

    public RegistryItem getParent(){
        return parent;
    }
    
    public List<RegistryItem> getChildren(){
        return children;
    }

    public RegistryItemType getType(){
        return type;
    }
    
    public void setType(RegistryItemType type){
        this.type = type;
    }
    
    
    public void registerParent(RegistryItem parent){
        this.parent = parent;
        parent.children.add(this);
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
        if (!(object instanceof RegistryItem)) {
            return false;
        }
        RegistryItem other = (RegistryItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
