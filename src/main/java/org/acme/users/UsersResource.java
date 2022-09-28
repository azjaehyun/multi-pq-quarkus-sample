package org.acme.users;

import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;
import org.acme.users.Users;

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

    @GET
    @Path("{id}")
    public Uni<Users> getSingle(@RestPath Long name) {
        return usersRepository.findById(name);
    }

    @POST
    public void create(Users fruit) {
        if (fruit == null || fruit.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        usersRepository.persist(fruit);


//        return Panache.withTransaction(fruit::persist)
//            .replaceWith(Response.ok(fruit).status(CREATED)::build);
    }


//    @GET
//    @Path("{id}")
//    public Uni<Fruit> getSingle(@RestPath Long id) {
//        return Fruit.findById(id);
//    }
//
//    @POST
//    public Uni<Response> create(Fruit fruit) {
//        if (fruit == null || fruit.id != null) {
//            throw new WebApplicationException("Id was invalidly set on request.", 422);
//        }
//
//        return Panache.withTransaction(fruit::persist)
//            .replaceWith(Response.ok(fruit).status(CREATED)::build);
//    }
//
//    @PUT
//    @Path("{id}")
//    public Uni<Response> update(@RestPath Long id, Fruit fruit) {
//        if (fruit == null || fruit.name == null) {
//            throw new WebApplicationException("Fruit name was not set on request.", 422);
//        }
//
//        return Panache
//            .withTransaction(() -> Fruit.<Fruit> findById(id)
//                .onItem().ifNotNull().invoke(entity -> entity.name = fruit.name)
//            )
//            .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
//            .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
//    }
//
//    @DELETE
//    @Path("{id}")
//    public Uni<Response> delete(@RestPath Long id) {
//        return Panache.withTransaction(() -> Fruit.deleteById(id))
//            .map(deleted -> deleted
//                ? Response.ok().status(NO_CONTENT).build()
//                : Response.ok().status(NOT_FOUND).build());
//    }
//
//    /**
//     * Create a HTTP response from an exception.
//     *
//     * Response Example:
//     *
//     * <pre>
//     * HTTP/1.1 422 Unprocessable Entity
//     * Content-Length: 111
//     * Content-Type: application/json
//     *
//     * {
//     *     "code": 422,
//     *     "error": "Fruit name was not set on request.",
//     *     "exceptionType": "javax.ws.rs.WebApplicationException"
//     * }
//     * </pre>
//     */
//    @Provider
//    public static class ErrorMapper implements ExceptionMapper<Exception> {
//
//        @Inject
//        ObjectMapper objectMapper;
//
//        @Override
//        public Response toResponse(Exception exception) {
//            LOGGER.error("Failed to handle request", exception);
//
//            Throwable throwable = exception;
//
//            int code = 500;
//            if (throwable instanceof WebApplicationException) {
//                code = ((WebApplicationException) exception).getResponse().getStatus();
//            }
//
//            // This is a Mutiny exception and it happens, for example, when we try to insert a new
//            // fruit but the name is already in the database
//            if (throwable instanceof CompositeException) {
//                throwable = ((CompositeException) throwable).getCause();
//            }
//
//            ObjectNode exceptionJson = objectMapper.createObjectNode();
//            exceptionJson.put("exceptionType", throwable.getClass().getName());
//            exceptionJson.put("code", code);
//
//            if (exception.getMessage() != null) {
//                exceptionJson.put("error", throwable.getMessage());
//            }
//
//            return Response.status(code)
//                .entity(exceptionJson)
//                .build();
//        }
//
//    }
}
