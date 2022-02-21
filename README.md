# Quarkus

Neste repositório estarão disponíveis nosso *Workshop* de implementação fazendo uso da tecnologia [Quarkus](https://quarkus.io/)

## Pré Requisitos

- [JDK/Open JDK 11](https://openjdk.java.net/install/)
- [Apache Maven 3.8.1+](https://maven.apache.org/download.cgi)
- [GraalVM](https://www.graalvm.org/docs/release-notes/19_3/)
- [HTTPie](https://httpie.org/)
- [Prometheus](https://prometheus.io/)
- [Grafana](https://grafana.com/)

## Workshop

0. [Criação de um Projeto Quarkus](#workshop-quarkus-create)
1. [Execução do Projeto](#workshop-quarkus-execution)
2. [Geração do Pacote](#workshop-quarkus-package)
3. [Compilações Quarkus](#workshop-quarkus-compile)
4. [Restfull WebServices](#workshop-quarkus-restfull)
5. [Adicionar suporte a JSON-B](#workshop-quarkus-create)
6. [Criação da Camada de Service - Dependency Injection](#workshop-quarkus-di)
7. [Adicionar suporte a OpenAPI/Swagger](#workshop-quarkus-openapi)
8. [Inclusão Persistência - Hibernate Panache](#workshop-quarkus-panache)
9. [Inclusão BuscaCEP - MicroProfile Rest Client](#workshop-quarkus-restclient)
10. [Inclusão Monitoramento - Micrometer Metrics](#workshop-quarkus-metrics)
11. [Implementar Tolerância Falha - MicroProfile Fault Tolerance](#workshop-quarkus-fault)
12. [Implementar APM - OpenTracing](#workshop-quarkus-opentracing)
13. [Segurança - Keycloak/OAuth/OIDC/JWT](#workshop-quarkus-security)

## Implementação

### 0 - Criação de um Projeto Quarkus <a name="workshop-quarkus-create">

  * Criação possível através do [Quarkus Initializer](https://code.quarkus.io/)

  ```
  Group: br.com.impacta.quarkus
  Artifact code-with-quarkus
  Build Tool: maven
  ```

### 1 - Execução do Projeto <a name="workshop-quarkus-execution">

  Sintaxe: `mvn quarkus:dev`
  ```
  [INFO] Using 'UTF-8' encoding to copy filtered resources.
  [INFO] Copying 2 resources
  [INFO] Changes detected - recompiling the module!
  [INFO] Compiling 1 source file to /Users/vinny/Documents/IMPACTA/2021/FullStack/MicroservicesServeless/FsQuarkus/source/helloworld-v1/code-with-quarkus/target/classes
  OpenJDK 64-Bit Server VM warning: forcing TieredStopAtLevel to full optimization because JVMCI is enabled
  Listening for transport dt_socket at address: 5005
  __  ____  __  _____   ___  __ ____  ______
   --/ __ \/ / / / _ | / _ \/ //_/ / / / __/
   -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
  --\___\_\____/_/ |_/_/|_/_/|_|\____/___/
  2021-02-04 09:23:58,643 INFO  [io.quarkus] (Quarkus Main Thread) code-with-quarkus 1.0.0-SNAPSHOT on JVM (powered by Quarkus 1.11.1.Final) started in 1.846s. Listening on: http://localhost:8080
  2021-02-04 09:23:58,647 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
  2021-02-04 09:23:58,648 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, resteasy]

  http :8080/hello
  HTTP/1.1 200 OK
  Content-Type: text/plain;charset=UTF-8
  content-length: 14

  Hello RESTEasy
  ```

### 2 - Geração do Pacote  <a name="workshop-quarkus-package">

  * Geração de Pacote:

  ```
  ./mvnw package -DskipTests
  ```

  * Certifique-se que a aplicação foi compilada com sucesso, e após esse processo, inicialize a mesma:

  ```
  classes                              generated-test-sources               quarkus                              test-classes
  code-with-quarkus-1.0.0-SNAPSHOT.jar maven-archiver                       quarkus-app
  generated-sources                    maven-status                         quarkus-artifact.properties
  ```

  * Verifique o tamanho do arquivo jar gerado, quantidade de memória utilizada e tempo de bootstrap:

  ```
  du -sh ./target/quarkus-app
  ```
    * arquivo *quarkus-run.jar* consiste no *jar* executável. Necessária a presença do diretório */lib*. Maiores detalhes nesse [LINK](https://quarkus.io/guides/getting-started#packaging-and-run-the-application)
    * caso necessite criar um *uber-jar*, basta adicionar as seguintes properties no arquivo *application.properties*. Maiores detalhes nesses links: [MAVEN](https://quarkus.io/guides/maven-tooling#uber-jar-maven) [GRADLE](https://quarkus.io/guides/gradle-tooling#building-uber-jars)


### 3 - Compilações Quarkus <a name="workshop-quarkus-compile">

  * Maiores detalhes sobre esses processos estão disponíveis nesses links: [Native Image](https://quarkus.io/guides/building-native-image) [Container Image](https://quarkus.io/guides/container-image)

  * Geração de uma imagem Docker com binário tradicional:

  ```
  docker build -f src/main/docker/Dockerfile.jvm -t getting-started-jvm .
  docker images | grep getting-started
  ```

  * Geração de uma imagem Docker com binário compilado nativamente (macOS):

  ```
  ./mvnw package -Pnative -DskipTests
  file target/code-with-quarkus-1.0.0-SNAPSHOT-runner
  ./target/code-with-quarkus-1.0.0-SNAPSHOT-runner
  docker build -f src/main/docker/Dockerfile.native -t getting-started-native .
  docker images | grep getting-started
  ```

  * Geração de uma imagem Docker com binário compilado nativamente (Linux):

  ```
  ./mvnw package -Pnative -Dquarkus.native.container-build=true -DskipTests
  file target/code-with-quarkus-1.0.0-SNAPSHOT-runner
  ./target/code-with-quarkus-1.0.0-SNAPSHOT-runner
  docker build -f src/main/docker/Dockerfile.native -t getting-started-native-linux .
  docker images | grep getting-started
  ```

  * Execução das imagens:

  ```
  docker run -it -p 8080:8080 getting-started-jvm
  docker run -it -p 8180:8080 getting-started-native
  docker run -it -p 8280:8080 getting-started-native-linux
  ```

  * Monitoramento dos Containers:

  ```
  docker stats
  ```

* Execução Benchmarks de Performance:

  ```
  ab -n 50000 -c 10 http://localhost:8080/hello
  ab -n 50000 -c 10 http://localhost:8280/hello
  ```

* Pare todos os containers:

  ```
  docker kill $(docker ps -q)
  ```

### 4 - Restfull WebServices <a name="workshop-quarkus-restfull">

  * Criação possível através do [Quarkus Initializer](https://code.quarkus.io/)

  ```
  Group: br.com.impacta.quarkus
  Artifact customer
  Build Tool: maven
  Extensions:
    RESTEasy JAX-RS [quarkus-resteasy]
  ```

  * Inicialização da aplicação em *dev mode*:

    * maiores detalhem em [Development Mode](https://quarkus.io/guides/getting-started#development-mode)

  ```
  ./mvnw quarkus:dev
  ```

  * Executar o teste no *Endpoint*:

  ```
  http :8080/hello
  HTTP/1.1 200 OK
  Content-Length: 14
  Content-Type: text/plain;charset=UTF-8

  Hello RESTEasy
  ```

  * Alterar a classe **GreetingResource**:

  ```
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
      return "Hello Impacta";
  }
  ```

  * Executar o teste no *Endpoint*:

  ```
  http :8080/hello-resteasy
  HTTP/1.1 200 OK
  Content-Length: 14
  Content-Type: text/plain;charset=UTF-8

  Hello Impacta
  ```

  * Criação do Domínio Customer:

  ```
  package br.com.impacta.quarkus;

  public class Customer {

    public String primeiroNome;
    public Integer rg;
    public String sobreNome;

	public String getPrimeiroNome() {
		return primeiroNome;
	}
	public void setPrimeiroNome(String primeiroNome) {
		this.primeiroNome = primeiroNome;
	}
	public Integer getRg() {
		return rg;
	}
	public void setRg(Integer rg) {
		this.rg = rg;
	}
	public String getSobreNome() {
		return sobreNome;
	}
	public void setSobreNome(String sobreNome) {
		this.sobreNome = sobreNome;
	}

  @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rg == null) ? 0 : rg.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (rg == null) {
			if (other.rg != null)
				return false;
		} else if (!rg.equals(other.rg))
			return false;
		return true;
	}
   }
  ```

### 5 - Adicionar suporte a JSON-B <a name="workshop-quarkus-jsonb">

  * Maiores detalhes em na documentação [JSON-B](https://quarkus.io/guides/rest-json#configuring-json-support)

  * Criar a classe **CustomerResource** com o seguinte conteúdo:

  ```
  package br.com.impacta.quarkus;

  import javax.ws.rs.GET;
  import javax.ws.rs.Path;
  import javax.ws.rs.Produces;
  import javax.ws.rs.core.MediaType;

  @Path("/customers")
  public class CustomerResource {

      @GET
      @Produces(MediaType.APPLICATION_JSON)
      public Customer hello() {
          Customer customer = new Customer();
          customer.setPrimeiroNome("nome1");
          customer.setSobreNome("sobreNome1");
          customer.setRg(101010);
          return customer;
      }

  }
  ```

  * Executar o teste no *Endpoint /customers*:

  ```
  http :8080/customers
  HTTP/1.1 500 Internal Server Error
  Content-Length: 125
  Content-Type: text/html;charset=UTF-8

  Could not find MessageBodyWriter for response object of type: br.com.impacta.quarkus.Customer of media type: application/json
  ```

  * Adicionar a *extension RESTEasy JSON* no projeto através da edição do arquivo **pom.xml**:

  ```
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-jackson</artifactId>
  </dependency>
  ```

  * Executar o teste no *Endpoint /customers*:

  ```
  http :8080/customers
  HTTP/1.1 200 OK
  Content-Length: 61
  Content-Type: application/json
  {
    "primeiroNome": "nome1",
    "rg": 101010,
    "sobreNome": "sobreNome1"
  }
  ```

### 6 - Criação da Camada de Service - Dependency Injection <a name="workshop-quarkus-di">

  * Referência do suporte a [CDI](https://quarkus.io/guides/cdi-reference) pelo **Quarkus**

  * Criar a classe **CustomerService**:

  ```
  package br.com.impacta.quarkus;

  import javax.enterprise.context.ApplicationScoped;
  import java.util.HashSet;
  import java.util.Set;

  @ApplicationScoped
  public class CustomerService {
      private Set<Customer> customerSet = new HashSet<Customer>(0);

      public Customer addCustomer(Customer customer){
          if (customerSet.contains(customer)){
              for (Customer customerEntity : customerSet) {
                  if (customerEntity.equals(customer)){
                      return customerEntity;
                  }
              }
          }
          customerSet.add(customer);
          return customer;
      }

      public Customer getCustomer(Customer customer){
          if (customerSet.contains(customer)){
              for (Customer customerEntity : customerSet) {
                  if (customerEntity.equals(customer)){
                      return customerEntity;
                  }
              }
          }
          return null;
      }

      public Set<Customer> listCustomer(){
          return customerSet;
      }

      public Customer deleteCustomer(Customer customer){
          if (customerSet.contains(customer)){
              customerSet.remove(customer);
              return customer;
          }
          return null;
      }

      public Customer updateCustomer(Customer customer){
          if (customerSet.contains(customer)){
              for (Customer customerEntity : customerSet) {
                  if (customerEntity.equals(customer)){
                      customerEntity.setRg(customerEntity.getRg());
                      customerEntity.setPrimeiroNome(customer.getPrimeiroNome());
                      customerEntity.setSobreNome(customer.getSobreNome());
                      return customerEntity;
                  }
              }
          }
          return null;
      }
  }
  ```

* Customizar a classe **CustomerResource**:

  ```
  package br.com.impacta.quarkus;

  import javax.inject.Inject;

  import javax.ws.rs.*;
  import javax.ws.rs.core.MediaType;
  import java.util.Set;

  @Path("/customers")
  public class CustomerResource {

      @Inject
      CustomerService customerService;

      @GET
      @Produces(MediaType.APPLICATION_JSON)
      public Set<Customer> listCustomer(){
          return customerService.listCustomer();
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer getCustomer(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomer(customerEntity);
          return customerEntity;
      }

      @POST
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer addCustomer(Customer customer){
          Customer customerEntity = customerService.addCustomer(customer);
          return customerEntity;
      }

      @PUT
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer updatCustomer(Customer customer){
          Customer customerEntity = customerService.updateCustomer(customer);
          return customerEntity;
      }

      @DELETE
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer deleteCustomer(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.deleteCustomer(customerEntity);
          return customerEntity;
      }
  }
  ```

  * Execução de Testes da *RestFull API* **CustomerResource**:

  ```
  http :8080/customers
  http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1
  http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2
  http :8080/customers/rg/11111
  http :8080/customers/rg/22222
  http PUT :8080/customers rg=11111 primeiroNome=nome1Editado sobreNome=sobrenome1Editado
  http :8080/customers/rg/11111
  http :8080/customers
  http DELETE :8080/customers/rg/11111
  http :8080/customers
  ```

### 7 - Adicionar suporte a OpenAPI/Swagger <a name="workshop-quarkus-openapi">

  * Maiores detalhes em na documentação [OpenAPI](https://quarkus.io/guides/openapi-swaggerui)

  * Adicionar a *extension SmallRye OpenAPI* no projeto através da edição do arquivo **pom.xml**:

  ```
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
  </dependency>
  ```

  * Habilitar *Swagger UI* em ambiente *produtivo* editando o arquivo **application.properties**:

  ```
  quarkus.swagger-ui.always-include = true
  ```

  * Acessar *Swagger UI* ou *OpenAPI Endpoint*:

  ```
  http://localhost:8080
  http://localhost:8080/q/swagger-ui/
  http :8080/openapi
  ```

  * Criação da classe **CustomerAPIApplication**

  ```
  package br.com.impacta.quarkus;

  import javax.ws.rs.core.Application;
  import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
  import org.eclipse.microprofile.openapi.annotations.info.Contact;
  import org.eclipse.microprofile.openapi.annotations.info.Info;
  import org.eclipse.microprofile.openapi.annotations.info.License;
  import org.eclipse.microprofile.openapi.annotations.tags.Tag;

  @OpenAPIDefinition(
          tags = {
                  @Tag(name="customers", description="API de Gerenciamento de Customers"),
          },
          info = @Info(
          title="Customers API",
          version = "1.0.0",
          contact = @Contact(
                  name = "Customers API Support",
                  url = "http://customersapi.com/contact",
                  email = "customersapi@customersapi.com"),
          license = @License(
                  name = "Apache 2.0",
                  url = "http://www.apache.org/licenses/LICENSE-2.0.html"))
  )
  public class CustomerAPIApplication extends Application {}
  ```

  * Acessar *Swagger UI* ou *OpenAPI Endpoint*:

  ```
  http://localhost:8080/q/swagger-ui/
  ```

### 8 - Inclusão Persistência - Hibernate Panache <a name="workshop-quarkus-panache">

  * Maiores detalhes em na documentação [Hibernate Panache](https://quarkus.io/guides/hibernate-orm-panache)

  * Adicionar a *extension Hibernate ORM with Panache*  e *JDBC Driver - PostgreSQL* no projeto através da edição do arquivo **pom.xml**:

  ```
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
  </dependency>
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-postgresql</artifactId>
  </dependency>
  ```

  * Inicializar Banco de Dados:

  ```
  docker run -e "POSTGRES_PASSWORD=postgres" -p 5432:5432 -d postgres:9.6.18-alpine
  ```
    * posterior a essa etapa crie o banco de dados **customers**


  * Modificar classe **Customer** adicionando referências ao **Hibernate Panache**:

  ```
  package br.com.impacta.quarkus;

  import io.quarkus.hibernate.orm.panache.PanacheEntity;
  import io.quarkus.panache.common.Parameters;

  import javax.persistence.Entity;
  import java.util.List;

  @Entity
  public class Customer extends PanacheEntity {

      public String primeiroNome;
      public Integer rg;
      public String sobreNome;

      public static List<Customer> findByPrimeiroNome(Customer customer){
          List<Customer> customerList = list("primeiroNome",  customer.getPrimeiroNome());
          return customerList;
      }

      public static List<Customer> findByPrimeiroOrSobreNome(Customer customer){
          List<Customer> customerList = list("primeiroNome = :firstName or sobreNome = :lastName",
                  Parameters.with("firstName", customer.getPrimeiroNome())
                          .and("lastName", customer.getSobreNome()));
          return customerList;
      }

      public static Customer findByRg(Customer customer){
          customer = Customer.find("rg", customer.getRg()).firstResult();
          return customer;
      }

      public String getPrimeiroNome() {
          return primeiroNome;
      }

      public void setPrimeiroNome(String primeiroNome) {
          this.primeiroNome = primeiroNome.toUpperCase();
      }

      public Integer getRg() {
          return rg;
      }

      public void setRg(Integer rg) {
          this.rg = rg;
      }

      public String getSobreNome() {
          return sobreNome;
      }

      public void setSobreNome(String sobreNome) {
          this.sobreNome = sobreNome.toUpperCase();
      }
  }
  ```

* Modificar classe **CustomerService** adicionando referências aos métodos recém criados:

  ```
  package br.com.impacta.quarkus;

  import javax.enterprise.context.ApplicationScoped;
  import javax.transaction.Transactional;
  import java.util.List;

  @ApplicationScoped
  public class CustomerService {

      @Transactional
      public Customer addCustomer(Customer customer){
          Customer.persist(customer);
          return customer;
      }

      @Transactional
      public Customer getCustomerById(Customer customer){
          customer = Customer.findById(customer.id);
          return customer;
      }

      @Transactional
      public List<Customer> getByPrimeiroNome(Customer customer){
          List<Customer> cList = Customer.findByPrimeiroNome(customer);
          return cList;
      }

      @Transactional
      public List<Customer> getByPrimeiroNomeOrSobreNome(Customer customer){
          List<Customer> cList = Customer.findByPrimeiroOrSobreNome(customer);
          return cList;
      }

      @Transactional
      public Customer getCustomerByRg(Customer customer){
          customer = Customer.findByRg(customer);
          return customer;
      }

      @Transactional
      public List<Customer> listCustomer(){
          List<Customer> cList = Customer.listAll();
          return cList;
      }

      @Transactional
      public Customer deleteCustomer(Customer customer){
          customer = Customer.findById(customer.id);
          Customer.deleteById(customer.id);
          return customer;
      }

      @Transactional
      public Customer updateCustomer(Customer customer){
          Customer customerEntity = Customer.findById(customer.id);
          if (customerEntity != null){
              customerEntity.setPrimeiroNome(customer.getPrimeiroNome());
              customerEntity.setSobreNome(customer.getSobreNome());
              customerEntity.setRg(customer.getRg());
          }
          return customerEntity;
      }
  }
  ```

* Modificar classe **CustomerResource** adicionando referências aos métodos recém criados:

  ```
  package br.com.impacta.quarkus;

  import javax.inject.Inject;
  import javax.ws.rs.*;
  import javax.ws.rs.core.MediaType;
  import java.util.List;

  @Path("/customers")
  public class CustomerResource {

      @Inject
      CustomerService customerService;

      @GET
      @Produces(MediaType.APPLICATION_JSON)
      public List<Customer> listCustomer(){
          return customerService.listCustomer();
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/{id}")
      public Customer getCustomerById(@PathParam("id") Long id){
          Customer customerEntity = new Customer();
          customerEntity.id = id;
          customerEntity = customerService.getCustomerById(customerEntity);
          return customerEntity;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer getCustomer(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomerByRg(customerEntity);
          return customerEntity;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/primeiroNome/{primeiroNome}")
      public List<Customer> getCustomerByName(@PathParam("primeiroNome") String name){
          Customer customerEntity = new Customer();
          customerEntity.setPrimeiroNome(name);
          List<Customer> customers = customerService.getByPrimeiroNome(customerEntity);
          return customers;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/primeiroNome/{primeiroNome}/sobreNome/{sobreNome}")
      public List<Customer> getCustomerByNameOrLastName(@PathParam("primeiroNome") String primeiroNome,
                                                        @PathParam("sobreNome") String sobreNome){
          Customer customerEntity = new Customer();
          customerEntity.setPrimeiroNome(primeiroNome);
          customerEntity.setSobreNome(sobreNome);
          List<Customer> customers = customerService.getByPrimeiroNomeOrSobreNome(customerEntity);
          return customers;
      }

      @POST
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer addCustomer(Customer customer){
          Customer customerEntity = customerService.addCustomer(customer);
          return customerEntity;
      }

      @PUT
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer updatCustomer(Customer customer){
          Customer customerEntity = customerService.updateCustomer(customer);
          return customerEntity;
      }

      @DELETE
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer deleteCustomerByRg(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomerByRg(customerEntity);
          customerEntity = customerService.deleteCustomer(customerEntity);
          return customerEntity;
      }

  }
  ```

  * Ajustar *application.properties*:

  ```
  quarkus.swagger-ui.always-include = true

  quarkus.datasource.db-kind = postgresql
  %dev.quarkus.datasource.username = postgres
  %dev.quarkus.datasource.password = postgres
  %dev.quarkus.datasource.jdbc.url = jdbc:postgresql://localhost:5432/customers
  %dev.quarkus.hibernate-orm.database.generation = drop-and-create
  ```

  * Iniciar aplicação e executar testes:

  ```
  http :8080/customers
  http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1
  http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2
  http :8080/customers/rg/11111
  http :8080/customers/rg/22222
  http PUT :8080/customers id=1 rg=11111 primeiroNome=nome1Editado sobreNome=sobrenome1Editado
  http :8080/customers/rg/11111
  http :8080/customers
  http DELETE :8080/customers/rg/11111
  http :8080/customers

  ** Testes com os novos métodos
  http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3
  http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4
  http :8080/customers/3
  http :8080/customers/4
  http POST :8080/customers rg=55555 primeiroNome=nomeigual sobreNome=sobrenome5
  http POST :8080/customers rg=66666 primeiroNome=nomeigual sobreNome=sobrenome6
  http :8080/customers/primeiroNome/NOMEIGUAL

  http POST :8080/customers rg=77777 primeiroNome=nomeigualA sobreNome=sobrenomeA
  http POST :8080/customers rg=88888 primeiroNome=nomeigualA sobreNome=sobrenomeB
  http POST :8080/customers rg=99999 primeiroNome=nome99999 sobreNome=sobrenomeB
  http :8080/customers/primeiroNome/nomeigualA/sobreNome/ahushuhahus
  http :8080/customers/primeiroNome/ahuahuhs/sobreNome/sobrenomeB
  ```

### 9 - Inclusão BuscaCEP - MicroProfile Rest Client <a name="workshop-quarkus-restclient">

  * Criação possível através do [Quarkus Initializer](https://code.quarkus.io/)

  ```
  Group: br.com.impacta.quarkus
  Artifact cep
  Build Tool: maven
  Selected Extensions:
    RestEasy JAX-RS RESTEasy JSON-B
  ```

  * Criação da classe **CEP** com o seguinte conteúdo:

  ```
  package br.com.impacta.quarkus;

  public class CEP {

      private Integer numeroCep;

      public Integer getNumeroCep() {
          return numeroCep;
      }

      public void setNumeroCep(Integer numeroCep) {
          this.numeroCep = numeroCep;
      }
  }
  ```

  * Criação da classe **CEPResource** com o seguinte conteúdo:

  ```
  package br.com.impacta.quarkus;

  import javax.ws.rs.Consumes;
  import javax.ws.rs.GET;
  import javax.ws.rs.Path;
  import javax.ws.rs.Produces;
  import javax.ws.rs.core.MediaType;
  import javax.ws.rs.core.Response;
  import java.util.Random;

  @Path("/cep")
  public class CEPResource {

      @GET
      @Produces(MediaType.APPLICATION_JSON)
      @Consumes(MediaType.APPLICATION_JSON)
      public Response getNumeroCEP() {
          CEP cep = new CEP();
          cep.setNumeroCep(new Random().ints(1, 99999).findFirst().getAsInt());
          return Response.ok(cep).build();
      }
  }
  ```

  * Ajustar *application.properties*: `%dev.quarkus.http.port = 8180`

  * Iniciar o serviço no *profile dev* e executar um teste em seu respectivo *Endpoint /cep*:

  ```
  mvn quarkus:dev

  http :8180/cep
  HTTP/1.1 200 OK
  Content-Length: 19
  Content-Type: application/json
  {
      "numeroCep": 54068
  }
  ```

  * Copie/Cole a classe **CEP** no projeto *Customer*;

  * Adicione as *extensions REST Client* no projeto *Customer*:

  ```
  <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-rest-client</artifactId>
  </dependency>
  ```

  * Crie uma *interface* **BuscaCEPRestClient** no projeto *Customer* com o seguinte conteúdo:

    ```
    package br.com.impacta.quarkus;

    import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

    import javax.enterprise.context.ApplicationScoped;
    import javax.ws.rs.Consumes;
    import javax.ws.rs.GET;
    import javax.ws.rs.Produces;
    import javax.ws.rs.core.MediaType;

    @ApplicationScoped
    @RegisterRestClient
    public interface BuscaCEPRestClient {

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public CEP getNumeroCEP();
    }
    ```

  * Adicione um atributo  do tipo *Integer numeroCep* na classe de domínio **Customer** e gere seus respectivos *get/set*:

    ```
    package br.com.impacta.quarkus;

    import io.quarkus.hibernate.orm.panache.PanacheEntity;
    import io.quarkus.panache.common.Parameters;

    import javax.persistence.Entity;
    import java.util.List;

    @Entity
    public class Customer extends PanacheEntity {

      public String primeiroNome;
      public Integer rg;
      public String sobreNome;
      public Integer numeroCep;

      public static List<Customer> findByPrimeiroNome(Customer customer){
          List<Customer> customerList = list("primeiroNome",  customer.getPrimeiroNome());
          return customerList;
      }

      public static List<Customer> findByPrimeiroOrSobreNome(Customer customer){
          List<Customer> customerList = list("primeiroNome = :firstName or sobreNome = :lastName",
                  Parameters.with("firstName", customer.getPrimeiroNome())
                          .and("lastName", customer.getSobreNome()));
          return customerList;
      }

      public static Customer findByRg(Customer customer){
          customer = Customer.find("rg", customer.getRg()).firstResult();
          return customer;
      }

      public String getPrimeiroNome() {
          return primeiroNome;
      }

      public void setPrimeiroNome(String primeiroNome) {
          this.primeiroNome = primeiroNome.toUpperCase();
      }

      public Integer getRg() {
          return rg;
      }

      public void setRg(Integer rg) {
          this.rg = rg;
      }

      public String getSobreNome() {
          return sobreNome;
      }

      public void setSobreNome(String sobreNome) {
          this.sobreNome = sobreNome.toUpperCase();
      }

      public Integer getNumeroCep() {
          return numeroCep;
      }

      public void setNumeroCep(Integer numeroCep) {
          this.numeroCep = numeroCep;
      }

    }
    ```

  * Injete a referência da *interface* **BuscaCEPRestClient** na classe *CustomerResource*:

    ```
    package br.com.impacta.quarkus;

    import org.eclipse.microprofile.rest.client.inject.RestClient;

    import javax.inject.Inject;
    import javax.ws.rs.*;
    import javax.ws.rs.core.MediaType;
    import java.util.List;

    @Path("/customers")
    public class CustomerResource {

        @Inject
        CustomerService customerService;

        @Inject
        @RestClient
        BuscaCEPRestClient buscaCepRestClient;

        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public List<Customer> listCustomer(){
            return customerService.listCustomer();
        }

        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/{id}")
        public Customer getCustomerById(@PathParam("id") Long id){
            Customer customerEntity = new Customer();
            customerEntity.id = id;
            customerEntity = customerService.getCustomerById(customerEntity);
            return customerEntity;
        }

        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/rg/{rg}")
        public Customer getCustomer(@PathParam("rg") Integer rg){
            Customer customerEntity = new Customer();
            customerEntity.setRg(rg);
            customerEntity = customerService.getCustomerByRg(customerEntity);
            return customerEntity;
        }

        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/primeiroNome/{primeiroNome}")
        public List<Customer> getCustomerByName(@PathParam("primeiroNome") String name){
            Customer customerEntity = new Customer();
            customerEntity.setPrimeiroNome(name);
            List<Customer> customers = customerService.getByPrimeiroNome(customerEntity);
            return customers;
        }

        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/primeiroNome/{primeiroNome}/sobreNome/{sobreNome}")
        public List<Customer> getCustomerByNameOrLastName(@PathParam("primeiroNome") String primeiroNome,
                                                          @PathParam("sobreNome") String sobreNome){
            Customer customerEntity = new Customer();
            customerEntity.setPrimeiroNome(primeiroNome);
            customerEntity.setSobreNome(sobreNome);
            List<Customer> customers = customerService.getByPrimeiroNomeOrSobreNome(customerEntity);
            return customers;
        }

        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Customer addCustomer(Customer customer){
            Customer customerEntity = customerService.addCustomer(customer);
            return customerEntity;
        }

        @PUT
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        public Customer updatCustomer(Customer customer){
            Customer customerEntity = customerService.updateCustomer(customer);
            return customerEntity;
        }

        @DELETE
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @Path("/rg/{rg}")
        public Customer deleteCustomerByRg(@PathParam("rg") Integer rg){
            Customer customerEntity = new Customer();
            customerEntity.setRg(rg);
            customerEntity = customerService.getCustomerByRg(customerEntity);
            customerEntity = customerService.deleteCustomer(customerEntity);
            return customerEntity;
        }

    }
    ```

  * Modifique o método **addCustomer** da classe **CustomerResource** atribuindo ao atributo **numeroCEP** um valor gerado através de uma chamada remota ao *webservice restfull* **BuscaCEPRestClient**:

    ```
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer addCustomer(Customer customer){
      customer.setNumeroCep(buscaCepRestClient.getNumeroCEP().getNumeroCep());
      Customer customerEntity = customerService.addCustomer(customer);
      return customerEntity;
    }
    ```

  * Modifique o arquivo **application.properties** do projeto *Customer*:

    ```
    quarkus.swagger-ui.always-include = true

    quarkus.datasource.db-kind = postgresql
    %dev.quarkus.datasource.username = postgres
    %dev.quarkus.datasource.password = postgres
    %dev.quarkus.datasource.jdbc.url = jdbc:postgresql://localhost:5432/customers
    %dev.quarkus.hibernate-orm.database.generation = drop-and-create

    br.com.impacta.quarkus.BuscaCEPRestClient/mp-rest/url=http://localhost:8180/cep
    br.com.impacta.quarkus.BuscaCEPRestClient/mp-rest/scope=javax.inject.Singleton
    ```

  * Adicione um novo **Customer** através de sua *API*:

    ```
    http POST :8080/customers rg=12345 primeiroNome=nome12345 sobreNome=sobrenome12345
    http :8080/customers/rg/12345

    http :8080/customers
    ```

### 10 - Inclusão Monitoramento - Micrometer Metrics <a name="workshop-quarkus-metrics">

  * Maiores detalhes em na documentação [Quarkus Micrometer Metrics](https://quarkus.io/guides/micrometer)

  ```
  <dependency>
    <groupId>org.eclipse.microprofile.metrics</groupId>
    <artifactId>microprofile-metrics-api</artifactId>
  </dependency>
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
  </dependency>
  ```

  * Modificar a classe **CustomerResource** adicionando os seguintes métodos:

  ```
  package br.com.impacta.quarkus;

  import org.eclipse.microprofile.metrics.MetricUnits;
  import org.eclipse.microprofile.metrics.annotation.Gauge;
  import org.eclipse.microprofile.rest.client.inject.RestClient;

  import javax.inject.Inject;
  import javax.ws.rs.*;
  import javax.ws.rs.core.MediaType;
  import java.util.List;

  @Path("/customers")
  public class CustomerResource {

      @Inject
      CustomerService customerService;

      @Inject
      @RestClient
      BuscaCEPRestClient buscaCepRestClient;

      @GET
      @Produces(MediaType.APPLICATION_JSON)
      public List<Customer> listCustomer(){
          return customerService.listCustomer();
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/{id}")
      public Customer getCustomerById(@PathParam("id") Long id){
          Customer customerEntity = new Customer();
          customerEntity.id = id;
          customerEntity = customerService.getCustomerById(customerEntity);
          return customerEntity;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer getCustomer(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomerByRg(customerEntity);
          return customerEntity;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/primeiroNome/{primeiroNome}")
      public List<Customer> getCustomerByName(@PathParam("primeiroNome") String name){
          Customer customerEntity = new Customer();
          customerEntity.setPrimeiroNome(name);
          List<Customer> customers = customerService.getByPrimeiroNome(customerEntity);
          return customers;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/primeiroNome/{primeiroNome}/sobreNome/{sobreNome}")
      public List<Customer> getCustomerByNameOrLastName(@PathParam("primeiroNome") String primeiroNome,
                                                        @PathParam("sobreNome") String sobreNome){
          Customer customerEntity = new Customer();
          customerEntity.setPrimeiroNome(primeiroNome);
          customerEntity.setSobreNome(sobreNome);
          List<Customer> customers = customerService.getByPrimeiroNomeOrSobreNome(customerEntity);
          return customers;
      }

      @POST
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer addCustomer(Customer customer){
          customer.setNumeroCep(buscaCepRestClient.getNumeroCEP().getNumeroCep());
          Customer customerEntity = customerService.addCustomer(customer);
          return customerEntity;
      }

      @PUT
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer updatCustomer(Customer customer){
          Customer customerEntity = customerService.updateCustomer(customer);
          return customerEntity;
      }

      @DELETE
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer deleteCustomerByRg(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomerByRg(customerEntity);
          customerEntity = customerService.deleteCustomer(customerEntity);
          return customerEntity;
      }

      @Gauge(name = "QUARKUS_QUANTIDADE_CLIENTES", unit = MetricUnits.NONE, description = "QUANTIDADE DE CLIENTES")
      public long checkCustomerAmmout(){
          return customerService.listCustomer().size();
      }

  }
  ```

* Adicionar alguns *customers*:

  ```
  http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1;
  http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2;
  http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3;
  http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4
  ```

* Verificar nos *Endpoints* de monitoramento a inclusão da métrica **QUARKUS_QUANTIDADE_CLIENTES**

  ```
  application_br_com_redhat_quarkus_CustomerResource_QUARKUS_QUANTIDADE_CLIENTES
  http://localhost:8080/q/metrics
  ```

### 11 - Implementar Tolerância Falha - MicroProfile Fault Tolerance <a name="workshop-quarkus-fault">

  * Criação possível através do [Quarkus Fault Tolerance](https://quarkus.io/guides/microprofile-fault-tolerance)

  * Incluir *extension SmallRye Fault Tolerance*:

  ```
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-fault-tolerance</artifactId>
  </dependency>
  ```

  * Incluir as seguintes propriedades na classe **CustomerResource**:

    ```
    package br.com.impacta.quarkus;

    import org.eclipse.microprofile.config.inject.ConfigProperty;
    import org.eclipse.microprofile.metrics.MetricUnits;
    import org.eclipse.microprofile.metrics.annotation.Gauge;
    import org.eclipse.microprofile.rest.client.inject.RestClient;

    import javax.inject.Inject;
    import javax.ws.rs.*;
    import javax.ws.rs.core.MediaType;
    import java.util.List;
    import java.util.logging.Logger;

    @Path("/customers")
    public class CustomerResource {

      private static final Logger LOGGER = Logger.getLogger(CustomerResource.class.toString());

      @ConfigProperty(name = "isTestingFault")
      boolean isTestingFault;

      @ConfigProperty(name = "isRetry")
      boolean isRetry;
      final int quantidadeRetry = 5;
      int exceptionCount = quantidadeRetry - 1;
      int count = 0;

      @ConfigProperty(name = "isFallBack")
      boolean isFallBack;

      @Inject
      CustomerService customerService;

      @Inject
      @RestClient
      BuscaCEPRestClient buscaCepRestClient;

      @GET
      @Produces(MediaType.APPLICATION_JSON)
      //@Retry(maxRetries = quantidadeRetry, delay = 1, delayUnit = ChronoUnit.SECONDS)
      //@Fallback(fallbackMethod = "fallbackCustomers")
      public List<Customer> listCustomer(){
          if (isTestingFault){
              executeFault();
          }
          return customerService.listCustomer();
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/{id}")
      public Customer getCustomerById(@PathParam("id") Long id){
          Customer customerEntity = new Customer();
          customerEntity.id = id;
          customerEntity = customerService.getCustomerById(customerEntity);
          return customerEntity;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer getCustomer(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomerByRg(customerEntity);
          return customerEntity;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/primeiroNome/{primeiroNome}")
      public List<Customer> getCustomerByName(@PathParam("primeiroNome") String name){
          Customer customerEntity = new Customer();
          customerEntity.setPrimeiroNome(name);
          List<Customer> customers = customerService.getByPrimeiroNome(customerEntity);
          return customers;
      }

      @GET
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/primeiroNome/{primeiroNome}/sobreNome/{sobreNome}")
      public List<Customer> getCustomerByNameOrLastName(@PathParam("primeiroNome") String primeiroNome,
                                                        @PathParam("sobreNome") String sobreNome){
          Customer customerEntity = new Customer();
          customerEntity.setPrimeiroNome(primeiroNome);
          customerEntity.setSobreNome(sobreNome);
          List<Customer> customers = customerService.getByPrimeiroNomeOrSobreNome(customerEntity);
          return customers;
      }

      @POST
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer addCustomer(Customer customer){
          customer.setNumeroCep(buscaCepRestClient.getNumeroCEP().getNumeroCep());
          Customer customerEntity = customerService.addCustomer(customer);
          return customerEntity;
      }

      @PUT
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Customer updatCustomer(Customer customer){
          Customer customerEntity = customerService.updateCustomer(customer);
          return customerEntity;
      }

      @DELETE
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      @Path("/rg/{rg}")
      public Customer deleteCustomerByRg(@PathParam("rg") Integer rg){
          Customer customerEntity = new Customer();
          customerEntity.setRg(rg);
          customerEntity = customerService.getCustomerByRg(customerEntity);
          customerEntity = customerService.deleteCustomer(customerEntity);
          return customerEntity;
      }

      @Gauge(name = "QUARKUS_QUANTIDADE_CLIENTES", unit = MetricUnits.NONE, description = "QUANTIDADE DE CLIENTES")
      public long checkCustomerAmmout(){
          return customerService.listCustomer().size();
      }

    }
    ```

  * Modificar a classe **CustomerResource** adicionando os seguintes métodos:

    ```

    private void executeFault(){
       if (isRetry){
           while ( count < exceptionCount){
               count++;
               String message = "Simulando Erro: " + count;
               LOGGER.log(Level.ERROR, message);
               throw new RuntimeException(message);
           }
           count = 0;
       }
       if (isFallBack){
           throw new RuntimeException("Simulando Erro: ");
       }
    }

    private List<Customer> fallbackCustomers(){
        return new ArrayList<Customer>(0);
    }
    ```

  * Teste de *Retry*:

    * Inclua as seguintes propriedades no arquivo **application.properties**:

      ```
      # Teste Fault Tolerance
      %dev.isTestingFault = true
      # Retry
      %dev.isRetry = true
      # Timeout
      %dev.isFallBack = false
      ```

    * Descomente a *Annotation @Retry* do método **listCustomer**


    * Inclua alguns *Customers*:

      ```
      http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1;
      http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2;
      http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3;
      http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4
      ```

    * Faça uma chamada ao método **listCustomer()**

      ```
      http :8080/customers
      ```

    * Verifique que algumas exceções são lançadas porém é obtido um retorno após execução bem sucedida:

      ```
      2021-02-04 15:59:16,335 ERROR [cla.com.imp.qua.CustomerResource] (executor-thread-199) Simulando Erro: 1
      2021-02-04 15:59:17,457 ERROR [cla.com.imp.qua.CustomerResource] (executor-thread-199) Simulando Erro: 2
      2021-02-04 15:59:18,464 ERROR [cla.com.imp.qua.CustomerResource] (executor-thread-199) Simulando Erro: 3
      2021-02-04 15:59:19,632 ERROR [cla.com.imp.qua.CustomerResource] (executor-thread-199) Simulando Erro: 4

      HTTP/1.1 200 OK
      Content-Length: 345
      Content-Type: application/json

      [
          {
              "id": 1,
              "numeroCep": 38646,
              "primeiroNome": "NOME1",
              "rg": 11111,
              "sobreNome": "SOBRENOME1"
          },
          {
              "id": 2,
              "numeroCep": 93457,
              "primeiroNome": "NOME2",
              "rg": 22222,
              "sobreNome": "SOBRENOME2"
          },
          {
              "id": 3,
              "numeroCep": 82306,
              "primeiroNome": "NOME3",
              "rg": 33333,
              "sobreNome": "SOBRENOME3"
          },
          {
              "id": 4,
              "numeroCep": 69182,
              "primeiroNome": "NOME4",
              "rg": 44444,
              "sobreNome": "SOBRENOME4"
          }
      ]
      ```

      * Teste de *Fallback*:

        * Altere as seguintes propriedades no arquivo **application.properties**:

          ```
          # Teste Fault Tolerance
          %dev.isTestingFault = true
          # Retry
          %dev.isRetry = false
          # Timeout
          %dev.isFallBack = true
          ```

        * Descomente a *Annotation @Fallback* e comente *@Retry* do método **listCustomer**

        * Inclua alguns *Customers*:

          ```
          http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1;
          http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2;
          http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3;
          http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4
          ```

        * Faça uma chamada ao método **listCustomer()**

          ```
          http :8080/customers  
          ```

        * Verifique o retorno da invocação do *Endpoint RestFull* não apresenta erro porém sem dados:

          ```
          HTTP/1.1 200 OK
          Content-Length: 2
          Content-Type: application/json

          []
          ```

        * Altere as seguintes propriedades no arquivo **application.properties**:

          ```
          # Teste Fault Tolerance
          %dev.isTestingFault = false
          # Retry
          %dev.isRetry = false
          # Timeout
          %dev.isFallBack = false
          ```

### 12 - Inclusão Monitoramento - MicroProfile Metrics <a name="workshop-quarkus-opentracing">

  * Maiores detalhes em na documentação [OpenTracing](https://quarkus.io/guides/opentracing)

  * Inicializar serviço do *Jaeger*:

    ```
    docker run -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 jaegertracing/all-in-one:latest
    ```

    * Incluir *extension SmallRye OpenTracing*:

    ```
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-opentracing</artifactId>
    </dependency>
    ```

  * Inclua alguns *Customers*:

    ```
    http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1;
    http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2;
    http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3;
    http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4
    ```

  * Acesse a interface do *Jaeger* e verifique as rotas:

    ```
    http://localhost:16686/
    ```

### 13 - Segurança - Keycloak/OAuth/OIDC/JWT <a name="execute-step-13">

  * Maiores detalhes em na documentação [Quarkus OpenID Connect](https://quarkus.io/guides/security-openid-connect)

  * Inicie o serviço do **Keycloak**

    ```
    docker run -p 10080:8080 viniciusmartinez/quarkus-rhsso:1.0
    ```

    * Incluir *extension OpenID Connect*:

    ```
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-oidc</artifactId>
    </dependency>
    ```

  * Adicione no arquivo **application.properties** os seguintes parâmetros:

    ```  
    %dev.quarkus.oidc.auth-server-url = http://localhost:10080/auth/realms/Quarkus
    %dev.quarkus.oidc.client-id = customer-app
    %dev.quarkus.oidc.credentials.secret =5ffb3490-4d7b-42ed-8cac-e6774550bc92
    %dev.quarkus.http.auth.policy.role-policy1.roles-allowed = user,admin                      
    %dev.quarkus.http.auth.permission.roles1.paths = /*
    %dev.quarkus.http.auth.permission.roles1.policy = role-policy1
    ```

  * Tente executar qualquer endpoint e um *HTTP 403* é esperado:

    ```
    http :8080/customers

    HTTP/1.1 401 Unauthorized
    content-length: 0
    ```

  * Obtenha um *Token* com as credenciais do usuário **user1**:

    ```
    export access_token=$(\
      curl -X POST http://localhost:10080/auth/realms/Quarkus/protocol/openid-connect/token \
      --user customer-app:5ffb3490-4d7b-42ed-8cac-e6774550bc92 \
      -H 'content-type: application/x-www-form-urlencoded' \
      -d 'username=user1&password=user1&grant_type=password' | jq --raw-output '.access_token' \
    )

    echo $access_token
    ```

  * Inclua alguns *Customers*:

    ```
    http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1 "Authorization: Bearer "$access_token;
    http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2 "Authorization: Bearer "$access_token;
    http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3 "Authorization: Bearer "$access_token;
    http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4 "Authorization: Bearer "$access_token

    http :8080/customers "Authorization: Bearer "$access_token
    ```

  * Modifique o método de *DELETE* adicionando a necessidade de uma *role admin*:

    ```
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/rg/{rg}")
    @RolesAllowed("admin")
    public Customer deleteCustomerByRg(@PathParam("rg") Integer rg){
      Customer customerEntity = new Customer();
      customerEntity.setRg(rg);
      customerEntity = customerService.getCustomerByRg(customerEntity);
      customerEntity = customerService.deleteCustomer(customerEntity);
      return customerEntity;
    }
    ```

  * Tente remover qualquer *customer* recém criado. Um erro *403* é esperado:

    ```
    http POST :8080/customers rg=11111 primeiroNome=nome1 sobreNome=sobrenome1 "Authorization: Bearer "$access_token;
    http POST :8080/customers rg=22222 primeiroNome=nome2 sobreNome=sobrenome2 "Authorization: Bearer "$access_token;
    http POST :8080/customers rg=33333 primeiroNome=nome3 sobreNome=sobrenome3 "Authorization: Bearer "$access_token;
    http POST :8080/customers rg=44444 primeiroNome=nome4 sobreNome=sobrenome4 "Authorization: Bearer "$access_token

    http DELETE :8080/customers/rg/33333 "Authorization: Bearer "$access_token

    HTTP/1.1 403 Forbidden
    Content-Length: 9
    Content-Type: application/json

    Forbidden
    ```

  * Obtenha um *Token* com as credenciais do usuário **admin**:

    ```
    export access_token=$(\
      curl -X POST http://localhost:10080/auth/realms/Quarkus/protocol/openid-connect/token \
      --user customer-app:5ffb3490-4d7b-42ed-8cac-e6774550bc92 \
      -H 'content-type: application/x-www-form-urlencoded' \
      -d 'username=admin&password=admin&grant_type=password' | jq --raw-output '.access_token' \
    )

    echo $access_token
    ```

  * Tente remover qualquer *customer* recém criado:

    ```
    http DELETE :8080/customers/rg/33333 "Authorization: Bearer "$access_token
    ```

## Referências Adicionais <a name="additional-references">

- [Tutoriais Hands On - developers.redhat.com](https://developers.redhat.com/courses/quarkus/)
- [Tutoriais Hands On - learn.openshift.com](https://learn.openshift.com/middleware/courses/middleware-quarkus/)
- [Exemplos & Guias](https://quarkus.io/guides/)
- [Quarkus Blog](https://quarkus.io/blog/)
- [Quarkus @ Youtube](https://www.youtube.com/channel/UCaW8QG_QoIk_FnjLgr5eOqg)
- [Red Hat Developers](https://developers.redhat.com/)
- [Red Hat Developers @ Youtube](https://www.youtube.com/channel/UC7noUdfWp-ukXUlAsJnSm-Q)
