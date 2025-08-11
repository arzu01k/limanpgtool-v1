package tr.com.havelsan.limanpgtool.gateway.service.mapper;

import org.mapstruct.*;
import tr.com.havelsan.limanpgtool.gateway.domain.User;
import tr.com.havelsan.limanpgtool.gateway.domain.Userrole;
import tr.com.havelsan.limanpgtool.gateway.service.dto.UserDTO;
import tr.com.havelsan.limanpgtool.gateway.service.dto.UserroleDTO;

/**
 * Mapper for the entity {@link Userrole} and its DTO {@link UserroleDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserroleMapper extends EntityMapper<UserroleDTO, Userrole> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    UserroleDTO toDto(Userrole s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
