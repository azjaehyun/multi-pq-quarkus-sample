package org.acme.inventory;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
//@PersistenceUnit("inventory")
public class Inventory extends PanacheEntity {

    @Column(length = 40, unique = true)
    public String name;

    public Inventory() {
    }

    public Inventory(String name) {
        this.name = name;
    }

}
