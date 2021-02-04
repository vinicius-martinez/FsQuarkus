package br.com.impacta.quarkus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello-resteasy")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Customer hello() {
        Customer customer = new Customer();
        customer.setPrimeiroNome("nome1");
        customer.setSobreNome("sobreNome1");
        customer.setRg(101010);
        return customer;
    }
}