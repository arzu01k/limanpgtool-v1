package tr.com.havelsan.limanpgtool.gateway.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link tr.com.havelsan.limanpgtool.gateway.domain.Userrole} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserroleDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String role;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserroleDTO)) {
            return false;
        }

        UserroleDTO userroleDTO = (UserroleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userroleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserroleDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
