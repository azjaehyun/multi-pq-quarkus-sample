package org.acme.users;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UsersRepository implements PanacheRepository<Users> {

}
