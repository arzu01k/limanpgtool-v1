package tr.com.havelsan.limanpgtool.sselogs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import tr.com.havelsan.limanpgtool.sselogs.config.AsyncSyncConfiguration;
import tr.com.havelsan.limanpgtool.sselogs.config.JacksonConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { SselogsApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
public @interface IntegrationTest {
}
