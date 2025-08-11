package tr.com.havelsan.limanpgtool.gateway.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;
import tr.com.havelsan.limanpgtool.gateway.repository.UserroleRepository;
import tr.com.havelsan.limanpgtool.gateway.service.UserroleService;
import tr.com.havelsan.limanpgtool.gateway.service.dto.UserroleDTO;
import tr.com.havelsan.limanpgtool.gateway.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link tr.com.havelsan.limanpgtool.gateway.domain.Userrole}.
 */
@RestController
@RequestMapping("/api/userroles")
public class UserroleResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserroleResource.class);

    private static final String ENTITY_NAME = "userrole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserroleService userroleService;

    private final UserroleRepository userroleRepository;

    public UserroleResource(UserroleService userroleService, UserroleRepository userroleRepository) {
        this.userroleService = userroleService;
        this.userroleRepository = userroleRepository;
    }

    /**
     * {@code POST  /userroles} : Create a new userrole.
     *
     * @param userroleDTO the userroleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userroleDTO, or with status {@code 400 (Bad Request)} if the userrole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UserroleDTO>> createUserrole(@Valid @RequestBody UserroleDTO userroleDTO) throws URISyntaxException {
        LOG.debug("REST request to save Userrole : {}", userroleDTO);
        if (userroleDTO.getId() != null) {
            throw new BadRequestAlertException("A new userrole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userroleService
            .save(userroleDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/userroles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /userroles/:id} : Updates an existing userrole.
     *
     * @param id the id of the userroleDTO to save.
     * @param userroleDTO the userroleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userroleDTO,
     * or with status {@code 400 (Bad Request)} if the userroleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userroleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserroleDTO>> updateUserrole(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserroleDTO userroleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Userrole : {}, {}", id, userroleDTO);
        if (userroleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userroleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userroleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userroleService
                    .update(userroleDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /userroles/:id} : Partial updates given fields of an existing userrole, field will ignore if it is null
     *
     * @param id the id of the userroleDTO to save.
     * @param userroleDTO the userroleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userroleDTO,
     * or with status {@code 400 (Bad Request)} if the userroleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userroleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userroleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserroleDTO>> partialUpdateUserrole(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserroleDTO userroleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Userrole partially : {}, {}", id, userroleDTO);
        if (userroleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userroleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userroleRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserroleDTO> result = userroleService.partialUpdate(userroleDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /userroles} : get all the userroles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userroles in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<UserroleDTO>> getAllUserroles(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Userroles");
        return userroleService.findAll().collectList();
    }

    /**
     * {@code GET  /userroles} : get all the userroles as a stream.
     * @return the {@link Flux} of userroles.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserroleDTO> getAllUserrolesAsStream() {
        LOG.debug("REST request to get all Userroles as a stream");
        return userroleService.findAll();
    }

    /**
     * {@code GET  /userroles/:id} : get the "id" userrole.
     *
     * @param id the id of the userroleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userroleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserroleDTO>> getUserrole(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Userrole : {}", id);
        Mono<UserroleDTO> userroleDTO = userroleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userroleDTO);
    }

    /**
     * {@code DELETE  /userroles/:id} : delete the "id" userrole.
     *
     * @param id the id of the userroleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUserrole(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Userrole : {}", id);
        return userroleService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
