package tr.com.havelsan.limanpgtool.sselogs.security.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import tech.jhipster.config.JHipsterProperties;
import tr.com.havelsan.limanpgtool.sselogs.config.SecurityConfiguration;
import tr.com.havelsan.limanpgtool.sselogs.config.SecurityJwtConfiguration;
import tr.com.havelsan.limanpgtool.sselogs.config.WebConfigurer;
import tr.com.havelsan.limanpgtool.sselogs.management.SecurityMetersService;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        JHipsterProperties.class,
        WebConfigurer.class,
        SecurityConfiguration.class,
        SecurityJwtConfiguration.class,
        SecurityMetersService.class,
        JwtAuthenticationTestUtils.class,
    }
)
public @interface AuthenticationIntegrationTest {
}
