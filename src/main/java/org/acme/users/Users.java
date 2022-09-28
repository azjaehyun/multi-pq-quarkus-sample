package org.acme.users;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Cacheable
//@PersistenceUnit("users")
public class Users extends PanacheEntity {

    @Column(length = 40, unique = true)
    public String name;

    public Users() {
    }

    public Users(String name) {
        this.name = name;
    }

}
