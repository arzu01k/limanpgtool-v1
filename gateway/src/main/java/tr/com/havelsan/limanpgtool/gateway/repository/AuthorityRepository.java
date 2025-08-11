package tr.com.havelsan.limanpgtool.gateway.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import tr.com.havelsan.limanpgtool.gateway.domain.Authority;

/**
 * Spring Data R2DBC repository for the Authority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends R2dbcRepository<Authority, String> {}
