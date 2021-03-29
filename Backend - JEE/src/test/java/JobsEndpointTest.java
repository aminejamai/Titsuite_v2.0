import com.titsuite.filters.AuthenticationFilter;
import com.titsuite.models.AuthCredentials;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@RunAsClient
public class JobsEndpointTest {


    private WebTarget jobTarget;
    private static NewCookie tokenCookie;
    @ArquillianResource
    private URI baseURL;
    private WebTarget userTarget;

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
        jobTarget = client.target(baseURL).path("api/myjobs");
        userTarget = client.target(baseURL).path("api/users");
    }

    @Test
    @InSequence(1)
    public void shouldLogin() {
        AuthCredentials credentials = new AuthCredentials();
        credentials.setEmail(UserCreationTest.TEST_EMAIL);
        credentials.setPassword(UserCreationTest.TEST_PASSWORD);
        credentials.setRole(UserCreationTest.FREELANCER_ROLE);

        Response response = userTarget.path("/login").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(credentials, MediaType.APPLICATION_JSON));

        assertEquals(202, response.getStatus());
        tokenCookie = response.getCookies().get(AuthenticationFilter.AUTHORIZATION_PROPERTY);
        assertNotNull(tokenCookie);
    }

    @Test
    @InSequence(2)
    public void mustReturnFreelancerJobs(){
        Response response = jobTarget.path("/all").request(MediaType.APPLICATION_JSON)
                .cookie(tokenCookie).get();

        assertEquals(200, response.getStatus());
        System.out.println(response.readEntity(String.class));
    }


}
