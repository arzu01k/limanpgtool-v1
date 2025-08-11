package tr.com.havelsan.limanpgtool.gateway.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tr.com.havelsan.limanpgtool.gateway.web.rest.TestUtil;

class UserroleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserroleDTO.class);
        UserroleDTO userroleDTO1 = new UserroleDTO();
        userroleDTO1.setId(1L);
        UserroleDTO userroleDTO2 = new UserroleDTO();
        assertThat(userroleDTO1).isNotEqualTo(userroleDTO2);
        userroleDTO2.setId(userroleDTO1.getId());
        assertThat(userroleDTO1).isEqualTo(userroleDTO2);
        userroleDTO2.setId(2L);
        assertThat(userroleDTO1).isNotEqualTo(userroleDTO2);
        userroleDTO1.setId(null);
        assertThat(userroleDTO1).isNotEqualTo(userroleDTO2);
    }
}
