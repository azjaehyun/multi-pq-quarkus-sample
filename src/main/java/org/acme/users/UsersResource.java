package org.acme.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Path("users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UsersResource {

    @Inject
    UsersRepository usersRepository;


    private static final Logger LOGGER = Logger.getLogger(UsersResource.class.getName());

    @GET
    public Uni<List<Users>> get() {
        return Users.listAll(Sort.by("name"));
    }

//    @GET
//    @Path("{id}")
//    public Uni<Users> getSingle(@RestPath Long name) {
//        return usersRepository.findById(name);
//    }
    @GET
    @Path("{id}")
    public Uni<Users> getSingle(@RestPath Long id) {
        return Users.findById(id);
    }
    @POST
    public Uni<Response> create(Users Users) {
        if (Users == null || Users.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        //usersRepository.persist(Users);


        return Panache.withTransaction(Users::persist)
            .replaceWith(Response.ok(Users).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@RestPath Long id, Users Users) {
        if (Users == null || Users.name == null) {
            throw new WebApplicationException("Users name was not set on request.", 422);
        }

        return Panache
            .withTransaction(() -> Users.<Users> findById(id)
                .onItem().ifNotNull().invoke(entity -> entity.name = Users.name)
            )
            .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
            .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@RestPath Long id) {
        return Panache.withTransaction(() -> Users.deleteById(id))
            .map(deleted -> deleted
                ? Response.ok().status(NO_CONTENT).build()
                : Response.ok().status(NOT_FOUND).build());
    }

    /**
     * Create a HTTP response from an exception.
     *
     * Response Example:
     *
     * <pre>
     * HTTP/1.1 422 Unprocessable Entity
     * Content-Length: 111
     * Content-Type: application.yaml/json
     *
     * {
     *     "code": 422,
     *     "error": "Users name was not set on request.",
     *     "exceptionType": "javax.ws.rs.WebApplicationException"
     * }
     * </pre>
     */
    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            Throwable throwable = exception;

            int code = 500;
            if (throwable instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            // This is a Mutiny exception and it happens, for example, when we try to insert a new
            // Users but the name is already in the database
            if (throwable instanceof CompositeException) {
                throwable = ((CompositeException) throwable).getCause();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", throwable.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", throwable.getMessage());
            }

            return Response.status(code)
                .entity(exceptionJson)
                .build();
        }

    }
}
