import com.titsuite.models.AuthCredentials;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class UserCreationTest {

    public static final String TEST_EMAIL = "jamai.mohamedamine2017@gmail.com";
    public static final String TEST_PASSWORD = "amine";
    public static final String TEST_CITY = "Casablanca";
    public static final String TEST_ADDRESS = "Sidi Maarouf Lot El Haddioui";
    public static final String TEST_PHONE_NUMBER = "212679223766";
    public static final String CUSTOMER_ROLE = "customer";
    public static final String FREELANCER_ROLE = "freelancer";
    public static final String STAFF_ROLE = "staff";

    private WebTarget webTarget;

    @ArquillianResource
    private URI baseURL;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
            .importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class)
            .addPackages(true, "com.titsuite")
            .addAsResource("config.properties")
            .addAsLibraries(files);
    }

    @Before
    public void initWebTarget() {
        Client client = ClientBuilder.newClient();
        webTarget = client.target(baseURL).path("api/users");
    }

    @Test
    public void shouldCreateCustomer() {
        AuthCredentials credentials = new AuthCredentials();
        credentials.setEmail(TEST_EMAIL);
        credentials.setPassword(TEST_PASSWORD);
        credentials.setRole(CUSTOMER_ROLE);
        credentials.setAddress(TEST_ADDRESS);
        credentials.setCity(TEST_CITY);
        credentials.setPhoneNumber(TEST_PHONE_NUMBER);

        Response response = webTarget.path("/register").request(MediaType.APPLICATION_JSON).post(
            Entity.entity(credentials, MediaType.APPLICATION_JSON)
        );

        assertEquals(201, response.getStatus());
    }

    @Test
    public void shouldCreateFreelancer() {
        AuthCredentials credentials = new AuthCredentials();
        credentials.setEmail(TEST_EMAIL);
        credentials.setPassword(TEST_PASSWORD);
        credentials.setRole(FREELANCER_ROLE);
        credentials.setAddress(TEST_ADDRESS);
        credentials.setCity(TEST_CITY);
        credentials.setPhoneNumber(TEST_PHONE_NUMBER);

        Response response = webTarget.path("/register").request(MediaType.APPLICATION_JSON).post(
            Entity.entity(credentials, MediaType.APPLICATION_JSON)
        );

        assertEquals(201, response.getStatus());
    }

    @Test
    public void shouldCreateStaff() {
        AuthCredentials credentials = new AuthCredentials();
        credentials.setEmail(TEST_EMAIL);
        credentials.setPassword(TEST_PASSWORD);
        credentials.setRole(STAFF_ROLE);
        credentials.setAddress(TEST_ADDRESS);
        credentials.setCity(TEST_CITY);
        credentials.setPhoneNumber(TEST_PHONE_NUMBER);

        Response response = webTarget.path("/register").request(MediaType.APPLICATION_JSON).post(
            Entity.entity(credentials, MediaType.APPLICATION_JSON)
        );

        assertEquals(201, response.getStatus());
    }

}
