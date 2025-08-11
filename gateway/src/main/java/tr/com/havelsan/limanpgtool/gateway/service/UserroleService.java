package tr.com.havelsan.limanpgtool.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tr.com.havelsan.limanpgtool.gateway.repository.UserroleRepository;
import tr.com.havelsan.limanpgtool.gateway.service.dto.UserroleDTO;
import tr.com.havelsan.limanpgtool.gateway.service.mapper.UserroleMapper;

/**
 * Service Implementation for managing {@link tr.com.havelsan.limanpgtool.gateway.domain.Userrole}.
 */
@Service
@Transactional
public class UserroleService {

    private static final Logger LOG = LoggerFactory.getLogger(UserroleService.class);

    private final UserroleRepository userroleRepository;

    private final UserroleMapper userroleMapper;

    public UserroleService(UserroleRepository userroleRepository, UserroleMapper userroleMapper) {
        this.userroleRepository = userroleRepository;
        this.userroleMapper = userroleMapper;
    }

    /**
     * Save a userrole.
     *
     * @param userroleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserroleDTO> save(UserroleDTO userroleDTO) {
        LOG.debug("Request to save Userrole : {}", userroleDTO);
        return userroleRepository.save(userroleMapper.toEntity(userroleDTO)).map(userroleMapper::toDto);
    }

    /**
     * Update a userrole.
     *
     * @param userroleDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserroleDTO> update(UserroleDTO userroleDTO) {
        LOG.debug("Request to update Userrole : {}", userroleDTO);
        return userroleRepository.save(userroleMapper.toEntity(userroleDTO)).map(userroleMapper::toDto);
    }

    /**
     * Partially update a userrole.
     *
     * @param userroleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserroleDTO> partialUpdate(UserroleDTO userroleDTO) {
        LOG.debug("Request to partially update Userrole : {}", userroleDTO);

        return userroleRepository
            .findById(userroleDTO.getId())
            .map(existingUserrole -> {
                userroleMapper.partialUpdate(existingUserrole, userroleDTO);

                return existingUserrole;
            })
            .flatMap(userroleRepository::save)
            .map(userroleMapper::toDto);
    }

    /**
     * Get all the userroles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserroleDTO> findAll() {
        LOG.debug("Request to get all Userroles");
        return userroleRepository.findAll().map(userroleMapper::toDto);
    }

    /**
     * Get all the userroles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<UserroleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userroleRepository.findAllWithEagerRelationships(pageable).map(userroleMapper::toDto);
    }

    /**
     * Returns the number of userroles available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userroleRepository.count();
    }

    /**
     * Get one userrole by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserroleDTO> findOne(Long id) {
        LOG.debug("Request to get Userrole : {}", id);
        return userroleRepository.findOneWithEagerRelationships(id).map(userroleMapper::toDto);
    }

    /**
     * Delete the userrole by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Userrole : {}", id);
        return userroleRepository.deleteById(id);
    }
}
