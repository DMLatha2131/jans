package io.jans.ca.server;

import com.google.inject.Inject;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.testing.ResourceHelpers;
import io.jans.ca.client.ClientInterface;
import io.jans.ca.client.RsProtectParams2;
import io.jans.ca.common.Jackson2;
import io.jans.ca.common.params.RsCheckAccessParams;
import io.jans.ca.common.response.RegisterSiteResponse;
import io.jans.ca.common.response.RsCheckAccessResponse;
import io.jans.ca.common.response.RsProtectResponse;
import io.jans.ca.rs.protect.RsResource;
import io.jans.ca.server.guice.GuiceModule;
import io.jans.ca.server.op.RsProtectOperation;
import io.jans.ca.server.persistence.service.PersistenceService;
import io.jans.ca.server.service.ConfigurationService;
import io.jans.ca.server.service.Rp;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import jakarta.ws.rs.BadRequestException;
import java.io.IOException;
import java.util.List;

import static org.testng.AssertJUnit.*;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 10/06/2016
 */
@Guice(modules = GuiceModule.class)
public class RsProtectTest {

    @Inject
    ConfigurationService configurationService;
    @Inject
    PersistenceService persistenceService;

    @BeforeClass
    public void setUp() throws IOException, ConfigurationException {
        configurationService.setConfiguration(TestUtils.parseConfiguration(ResourceHelpers.resourceFilePath("client-api-server-jenkins.yml")));
        persistenceService.create();
    }


    @Parameters({"host", "redirectUrls", "opHost", "rsProtect"})
    @Test
    public void protect(String host, String redirectUrls, String opHost, String rsProtect) throws IOException {

        ClientInterface client = Tester.newClient(host);

        final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrls);

        protectResources(client, site, UmaFullTest.resourceList(rsProtect).getResources());
        RsCheckAccessTest.checkAccess(client, site, null);
    }

    @Parameters({"host", "redirectUrls", "opHost", "rsProtectWithCreationExpiration"})
    @Test
    public void protect_withResourceCreationExpiration(String host, String redirectUrls, String opHost, String rsProtectWithCreationExpiration) throws IOException {

        ClientInterface client = Tester.newClient(host);

        final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrls);

        protectResources(client, site, UmaFullTest.resourceList(rsProtectWithCreationExpiration).getResources());

        Rp rp = persistenceService.getRp(site.getRpId());
        rp.getUmaProtectedResources().forEach(ele -> {
            assertEquals(1582890956L, ele.getIat().longValue());
            assertEquals(2079299799L, ele.getExp().longValue());
        });
    }

    @Parameters({"host", "redirectUrls", "opHost", "rsProtect"})
    @Test
    public void overwriteFalse(String host, String redirectUrls, String opHost, String rsProtect) throws IOException {
        ClientInterface client = Tester.newClient(host);

        final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrls);

        List<RsResource> resources = UmaFullTest.resourceList(rsProtect).getResources();
        protectResources(client, site, resources);

        final RsProtectParams2 params = new RsProtectParams2();
        params.setRpId(site.getRpId());
        params.setResources(Jackson2.createJsonMapper().readTree(Jackson2.asJsonSilently(resources)));

        try {
            client.umaRsProtect(Tester.getAuthorization(site), null, params);
        } catch (BadRequestException e) {
            assertEquals("uma_protection_exists", TestUtils.asError(e).getError());
            return;
        }

        throw new AssertionError("Expected 400 (bad request) but got successful result.");
    }

    @Parameters({"host", "redirectUrls", "opHost", "rsProtect"})
    @Test
    public void overwriteTrue(String host, String redirectUrls, String opHost, String rsProtect) throws IOException {
        ClientInterface client = Tester.newClient(host);

        final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrls);

        List<RsResource> resources = UmaFullTest.resourceList(rsProtect).getResources();
        protectResources(client, site, resources);

        final RsProtectParams2 params = new RsProtectParams2();
        params.setRpId(site.getRpId());
        params.setResources(Jackson2.createJsonMapper().readTree(Jackson2.asJsonSilently(resources)));
        params.setOverwrite(true); // force overwrite

        RsProtectResponse response = client.umaRsProtect(Tester.getAuthorization(site), null, params);
        assertNotNull(response);
    }

    @Parameters({"host", "redirectUrls", "opHost", "rsProtectScopeExpression"})
    @Test
    public void protectWithScopeExpression(String host, String redirectUrls, String opHost, String rsProtectScopeExpression) throws IOException {
        ClientInterface client = Tester.newClient(host);

        final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrls);

        protectResources(client, site, UmaFullTest.resourceList(rsProtectScopeExpression).getResources());
        RsCheckAccessTest.checkAccess(client, site, null);
    }

    @Parameters({"host", "redirectUrls", "opHost", "rsProtectScopeExpressionSecond"})
    @Test
    public void protectWithScopeExpressionSeconds(String host, String redirectUrls, String opHost, String rsProtectScopeExpressionSecond) throws IOException {
        ClientInterface client = Tester.newClient(host);

        final RegisterSiteResponse site = RegisterSiteTest.registerSite(client, opHost, redirectUrls);

        protectResources(client, site, UmaFullTest.resourceList(rsProtectScopeExpressionSecond).getResources());

        final RsCheckAccessParams params = new RsCheckAccessParams();
        params.setRpId(site.getRpId());
        params.setHttpMethod("GET");
        params.setPath("/GetAll");
        params.setRpt("");

        final RsCheckAccessResponse response = client.umaRsCheckAccess(Tester.getAuthorization(site), null, params);

        assertNotNull(response);
        assertTrue(StringUtils.isNotBlank(response.getAccess()));
    }

    public static RsProtectResponse protectResources(ClientInterface client, RegisterSiteResponse site, List<RsResource> resources) {
        final RsProtectParams2 params = new RsProtectParams2();
        params.setRpId(site.getRpId());
        try {
            params.setResources(Jackson2.createJsonMapper().readTree(Jackson2.asJsonSilently(resources)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final RsProtectResponse resp = client.umaRsProtect(Tester.getAuthorization(site), null, params);
        assertNotNull(resp);
        return resp;
    }

    @Parameters({"correctScopeExpression"})
    @Test
    public void testCorrectScopeExpression(String correctScopeExpression) {
        RsProtectOperation.validateScopeExpression(correctScopeExpression.replaceAll("'", "\""));
    }

    @Parameters({"incorrectScopeExpression"})
    @Test(expectedExceptions = HttpException.class)
    public void testIncorrectScopeExpression(String incorrectScopeExpression) {
        RsProtectOperation.validateScopeExpression(incorrectScopeExpression.replaceAll("'", "\""));
    }

}
