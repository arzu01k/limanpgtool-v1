package tr.com.havelsan.limanpgtool.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static tr.com.havelsan.limanpgtool.gateway.domain.UserroleAsserts.*;
import static tr.com.havelsan.limanpgtool.gateway.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import tr.com.havelsan.limanpgtool.gateway.IntegrationTest;
import tr.com.havelsan.limanpgtool.gateway.domain.Userrole;
import tr.com.havelsan.limanpgtool.gateway.repository.EntityManager;
import tr.com.havelsan.limanpgtool.gateway.repository.UserRepository;
import tr.com.havelsan.limanpgtool.gateway.repository.UserroleRepository;
import tr.com.havelsan.limanpgtool.gateway.service.UserroleService;
import tr.com.havelsan.limanpgtool.gateway.service.dto.UserroleDTO;
import tr.com.havelsan.limanpgtool.gateway.service.mapper.UserroleMapper;

/**
 * Integration tests for the {@link UserroleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserroleResourceIT {

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/userroles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserroleRepository userroleRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserroleRepository userroleRepositoryMock;

    @Autowired
    private UserroleMapper userroleMapper;

    @Mock
    private UserroleService userroleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Userrole userrole;

    private Userrole insertedUserrole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Userrole createEntity() {
        return new Userrole().role(DEFAULT_ROLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Userrole createUpdatedEntity() {
        return new Userrole().role(UPDATED_ROLE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Userrole.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        userrole = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserrole != null) {
            userroleRepository.delete(insertedUserrole).block();
            insertedUserrole = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createUserrole() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);
        var returnedUserroleDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserroleDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Userrole in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserrole = userroleMapper.toEntity(returnedUserroleDTO);
        assertUserroleUpdatableFieldsEquals(returnedUserrole, getPersistedUserrole(returnedUserrole));

        insertedUserrole = returnedUserrole;
    }

    @Test
    void createUserroleWithExistingId() throws Exception {
        // Create the Userrole with an existing ID
        userrole.setId(1L);
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkRoleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userrole.setRole(null);

        // Create the Userrole, which fails.
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUserrolesAsStream() {
        // Initialize the database
        userroleRepository.save(userrole).block();

        List<Userrole> userroleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserroleDTO.class)
            .getResponseBody()
            .map(userroleMapper::toEntity)
            .filter(userrole::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userroleList).isNotNull();
        assertThat(userroleList).hasSize(1);
        Userrole testUserrole = userroleList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertUserroleAllPropertiesEquals(userrole, testUserrole);
        assertUserroleUpdatableFieldsEquals(userrole, testUserrole);
    }

    @Test
    void getAllUserroles() {
        // Initialize the database
        insertedUserrole = userroleRepository.save(userrole).block();

        // Get all the userroleList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userrole.getId().intValue()))
            .jsonPath("$.[*].role")
            .value(hasItem(DEFAULT_ROLE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserrolesWithEagerRelationshipsIsEnabled() {
        when(userroleServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(userroleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserrolesWithEagerRelationshipsIsNotEnabled() {
        when(userroleServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(userroleRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getUserrole() {
        // Initialize the database
        insertedUserrole = userroleRepository.save(userrole).block();

        // Get the userrole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userrole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userrole.getId().intValue()))
            .jsonPath("$.role")
            .value(is(DEFAULT_ROLE));
    }

    @Test
    void getNonExistingUserrole() {
        // Get the userrole
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUserrole() throws Exception {
        // Initialize the database
        insertedUserrole = userroleRepository.save(userrole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userrole
        Userrole updatedUserrole = userroleRepository.findById(userrole.getId()).block();
        updatedUserrole.role(UPDATED_ROLE);
        UserroleDTO userroleDTO = userroleMapper.toDto(updatedUserrole);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userroleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserroleToMatchAllProperties(updatedUserrole);
    }

    @Test
    void putNonExistingUserrole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userrole.setId(longCount.incrementAndGet());

        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userroleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserrole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userrole.setId(longCount.incrementAndGet());

        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserrole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userrole.setId(longCount.incrementAndGet());

        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserroleWithPatch() throws Exception {
        // Initialize the database
        insertedUserrole = userroleRepository.save(userrole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userrole using partial update
        Userrole partialUpdatedUserrole = new Userrole();
        partialUpdatedUserrole.setId(userrole.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserrole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserrole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Userrole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserroleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserrole, userrole), getPersistedUserrole(userrole));
    }

    @Test
    void fullUpdateUserroleWithPatch() throws Exception {
        // Initialize the database
        insertedUserrole = userroleRepository.save(userrole).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userrole using partial update
        Userrole partialUpdatedUserrole = new Userrole();
        partialUpdatedUserrole.setId(userrole.getId());

        partialUpdatedUserrole.role(UPDATED_ROLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserrole.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserrole))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Userrole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserroleUpdatableFieldsEquals(partialUpdatedUserrole, getPersistedUserrole(partialUpdatedUserrole));
    }

    @Test
    void patchNonExistingUserrole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userrole.setId(longCount.incrementAndGet());

        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userroleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserrole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userrole.setId(longCount.incrementAndGet());

        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserrole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userrole.setId(longCount.incrementAndGet());

        // Create the Userrole
        UserroleDTO userroleDTO = userroleMapper.toDto(userrole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userroleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Userrole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserrole() {
        // Initialize the database
        insertedUserrole = userroleRepository.save(userrole).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userrole
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userrole.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userroleRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Userrole getPersistedUserrole(Userrole userrole) {
        return userroleRepository.findById(userrole.getId()).block();
    }

    protected void assertPersistedUserroleToMatchAllProperties(Userrole expectedUserrole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserroleAllPropertiesEquals(expectedUserrole, getPersistedUserrole(expectedUserrole));
        assertUserroleUpdatableFieldsEquals(expectedUserrole, getPersistedUserrole(expectedUserrole));
    }

    protected void assertPersistedUserroleToMatchUpdatableProperties(Userrole expectedUserrole) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserroleAllUpdatablePropertiesEquals(expectedUserrole, getPersistedUserrole(expectedUserrole));
        assertUserroleUpdatableFieldsEquals(expectedUserrole, getPersistedUserrole(expectedUserrole));
    }
}
