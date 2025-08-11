package tr.com.havelsan.limanpgtool.gateway.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tr.com.havelsan.limanpgtool.gateway.domain.Userrole;

/**
 * Spring Data R2DBC repository for the Userrole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserroleRepository extends ReactiveCrudRepository<Userrole, Long>, UserroleRepositoryInternal {
    @Override
    Mono<Userrole> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Userrole> findAllWithEagerRelationships();

    @Override
    Flux<Userrole> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM userrole entity WHERE entity.user_id = :id")
    Flux<Userrole> findByUser(Long id);

    @Query("SELECT * FROM userrole entity WHERE entity.user_id IS NULL")
    Flux<Userrole> findAllWhereUserIsNull();

    @Override
    <S extends Userrole> Mono<S> save(S entity);

    @Override
    Flux<Userrole> findAll();

    @Override
    Mono<Userrole> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserroleRepositoryInternal {
    <S extends Userrole> Mono<S> save(S entity);

    Flux<Userrole> findAllBy(Pageable pageable);

    Flux<Userrole> findAll();

    Mono<Userrole> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Userrole> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Userrole> findOneWithEagerRelationships(Long id);

    Flux<Userrole> findAllWithEagerRelationships();

    Flux<Userrole> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
