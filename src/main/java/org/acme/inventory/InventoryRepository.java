package org.acme.inventory;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class InventoryRepository implements PanacheRepository<Inventory> {

}
