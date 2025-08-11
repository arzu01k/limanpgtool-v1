package tr.com.havelsan.limanpgtool.gateway.service.mapper;

import static tr.com.havelsan.limanpgtool.gateway.domain.UserroleAsserts.*;
import static tr.com.havelsan.limanpgtool.gateway.domain.UserroleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserroleMapperTest {

    private UserroleMapper userroleMapper;

    @BeforeEach
    void setUp() {
        userroleMapper = new UserroleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserroleSample1();
        var actual = userroleMapper.toEntity(userroleMapper.toDto(expected));
        assertUserroleAllPropertiesEquals(expected, actual);
    }
}
