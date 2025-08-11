package tr.com.havelsan.limanpgtool.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static tr.com.havelsan.limanpgtool.gateway.domain.UserroleTestSamples.*;

import org.junit.jupiter.api.Test;
import tr.com.havelsan.limanpgtool.gateway.web.rest.TestUtil;

class UserroleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Userrole.class);
        Userrole userrole1 = getUserroleSample1();
        Userrole userrole2 = new Userrole();
        assertThat(userrole1).isNotEqualTo(userrole2);

        userrole2.setId(userrole1.getId());
        assertThat(userrole1).isEqualTo(userrole2);

        userrole2 = getUserroleSample2();
        assertThat(userrole1).isNotEqualTo(userrole2);
    }
}
