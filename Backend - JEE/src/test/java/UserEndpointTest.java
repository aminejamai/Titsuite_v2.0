import com.titsuite.diplomas.Diploma;
import com.titsuite.filters.AuthenticationFilter;
import com.titsuite.models.AuthCredentials;
import com.titsuite.utils.RandomStringGenerator;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
@RunAsClient
public class UserEndpointTest {

    private WebTarget userTarget;
    private WebTarget diplomaTarget;
    private static NewCookie tokenCookie;
    private static Long newDiplomaId;

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
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        Client client = ClientBuilder.newClient(clientConfig);
        userTarget = client.target(baseURL).path("api/users");
        diplomaTarget = client.target(baseURL).path("api/diplomas");
    }

    @Test
    @InSequence(1)
    public void shouldFailLogin() {
        AuthCredentials credentials = new AuthCredentials();
        credentials.setEmail("dummyEmail@dummyDomain.com");
        credentials.setPassword("dummyPassword");
        credentials.setRole(UserCreationTest.CUSTOMER_ROLE);
        credentials.setAddress(UserCreationTest.TEST_ADDRESS);
        credentials.setCity(UserCreationTest.TEST_CITY);
        credentials.setPhoneNumber(UserCreationTest.TEST_PHONE_NUMBER);

        Response response = userTarget.path("/login").request(MediaType.APPLICATION_JSON).post(
            Entity.entity(credentials, MediaType.APPLICATION_JSON)
        );

        assertEquals(401, response.getStatus());
    }

    @Test
    @InSequence(2)
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
    @InSequence(3)
    public void shouldCreateDiploma() {
        RandomStringGenerator rsg = new RandomStringGenerator(10);
        Diploma newDiploma = new Diploma();
        newDiploma.setName(rsg.nextString());
        newDiploma.setAcquisitionDate(new Date());
        newDiploma.setField(rsg.nextString());

        Response response = diplomaTarget.path("/create").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).post(Entity.entity(newDiploma, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(201, response.getStatus());
        newDiplomaId = Long.parseLong(response.readEntity(String.class).split(":|}")[1]);
    }

    @Test
    @InSequence(4)
    public void shouldGetProfile() {
        Response response = userTarget.path("/profile").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).get();

        assertEquals(200, response.getStatus());
        String userProfile = response.readEntity(String.class);
        System.out.println(userProfile);
    }

    @Test
    @InSequence(5)
    public void shouldUpdateProfile() {
        AuthCredentials updatedCredentials = new AuthCredentials();
        RandomStringGenerator rsg = new RandomStringGenerator(15);
        updatedCredentials.setFirstName(rsg.nextString());
        updatedCredentials.setLastName(rsg.nextString());
        updatedCredentials.setBirthDate(new Date(new GregorianCalendar(1999, Calendar.AUGUST, 2)
            .getTimeInMillis()));
        updatedCredentials.setPhoneNumber(rsg.nextString());
        updatedCredentials.setActivity(rsg.nextString());
        updatedCredentials.setMinimumWage(150f);

        Response response = userTarget.path("/profile/update").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).post(Entity.entity(updatedCredentials, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
    }

    @Test
    @InSequence(6)
    public void shouldUpdateDiploma() {
        RandomStringGenerator rsg = new RandomStringGenerator(10);
        Diploma newDiploma = new Diploma();
        newDiploma.setId(newDiplomaId);
        newDiploma.setName(rsg.nextString());
        newDiploma.setAcquisitionDate(new Date());
        newDiploma.setField(rsg.nextString());

        Response response = diplomaTarget.path("/update").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).post(Entity.entity(newDiploma, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(200, response.getStatus());
    }

    @Test
    @InSequence(7)
    public void shouldGetUpdatedProfile() {
        Response response = userTarget.path("/profile").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).get();

        assertEquals(200, response.getStatus());
        String userProfile = response.readEntity(String.class);
        System.out.println(userProfile);
    }

    @Test
    @InSequence(8)
    public void shouldDeleteDiploma() {
        Diploma newDiploma = new Diploma();
        newDiploma.setId(newDiplomaId);

        Response response = diplomaTarget.path("/delete").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).build("DELETE", Entity.entity(newDiploma, MediaType.APPLICATION_JSON_TYPE))
            .invoke();

        assertEquals(200, response.getStatus());
    }

    @Test
    @InSequence(9)
    public void shouldGetDeletedDiplomaProfile() {
        Response response = userTarget.path("/profile").request(MediaType.APPLICATION_JSON)
                .cookie(tokenCookie).get();

        assertEquals(200, response.getStatus());
        String userProfile = response.readEntity(String.class);
        System.out.println(userProfile);
    }

    @Test
    @InSequence(10)
    public void shouldLogout() {
        Response response = userTarget.path("/logout").request(MediaType.APPLICATION_JSON)
            .cookie(tokenCookie).post(null);

        assertEquals(200, response.getStatus());
        tokenCookie = response.getCookies().get(AuthenticationFilter.AUTHORIZATION_PROPERTY);
    }

}
