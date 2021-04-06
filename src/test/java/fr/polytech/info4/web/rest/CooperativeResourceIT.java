package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Compte;
import fr.polytech.info4.domain.Cooperative;
import fr.polytech.info4.repository.CooperativeRepository;
import fr.polytech.info4.service.criteria.CooperativeCriteria;
import fr.polytech.info4.service.dto.CooperativeDTO;
import fr.polytech.info4.service.mapper.CooperativeMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CooperativeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CooperativeResourceIT {

    private static final String DEFAULT_NOM_COOP = "w";
    private static final String UPDATED_NOM_COOP = "e";

    private static final String DEFAULT_VILLE_COOP = "W";
    private static final String UPDATED_VILLE_COOP = "h";

    private static final String ENTITY_API_URL = "/api/cooperatives";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CooperativeRepository cooperativeRepository;

    @Autowired
    private CooperativeMapper cooperativeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCooperativeMockMvc;

    private Cooperative cooperative;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cooperative createEntity(EntityManager em) {
        Cooperative cooperative = new Cooperative().nomCoop(DEFAULT_NOM_COOP).villeCoop(DEFAULT_VILLE_COOP);
        return cooperative;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cooperative createUpdatedEntity(EntityManager em) {
        Cooperative cooperative = new Cooperative().nomCoop(UPDATED_NOM_COOP).villeCoop(UPDATED_VILLE_COOP);
        return cooperative;
    }

    @BeforeEach
    public void initTest() {
        cooperative = createEntity(em);
    }

    @Test
    @Transactional
    void createCooperative() throws Exception {
        int databaseSizeBeforeCreate = cooperativeRepository.findAll().size();
        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);
        restCooperativeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeCreate + 1);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getNomCoop()).isEqualTo(DEFAULT_NOM_COOP);
        assertThat(testCooperative.getVilleCoop()).isEqualTo(DEFAULT_VILLE_COOP);
    }

    @Test
    @Transactional
    void createCooperativeWithExistingId() throws Exception {
        // Create the Cooperative with an existing ID
        cooperative.setId(1L);
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        int databaseSizeBeforeCreate = cooperativeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCooperativeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCooperatives() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList
        restCooperativeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cooperative.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomCoop").value(hasItem(DEFAULT_NOM_COOP)))
            .andExpect(jsonPath("$.[*].villeCoop").value(hasItem(DEFAULT_VILLE_COOP)));
    }

    @Test
    @Transactional
    void getCooperative() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get the cooperative
        restCooperativeMockMvc
            .perform(get(ENTITY_API_URL_ID, cooperative.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cooperative.getId().intValue()))
            .andExpect(jsonPath("$.nomCoop").value(DEFAULT_NOM_COOP))
            .andExpect(jsonPath("$.villeCoop").value(DEFAULT_VILLE_COOP));
    }

    @Test
    @Transactional
    void getCooperativesByIdFiltering() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        Long id = cooperative.getId();

        defaultCooperativeShouldBeFound("id.equals=" + id);
        defaultCooperativeShouldNotBeFound("id.notEquals=" + id);

        defaultCooperativeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCooperativeShouldNotBeFound("id.greaterThan=" + id);

        defaultCooperativeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCooperativeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCooperativesByNomCoopIsEqualToSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where nomCoop equals to DEFAULT_NOM_COOP
        defaultCooperativeShouldBeFound("nomCoop.equals=" + DEFAULT_NOM_COOP);

        // Get all the cooperativeList where nomCoop equals to UPDATED_NOM_COOP
        defaultCooperativeShouldNotBeFound("nomCoop.equals=" + UPDATED_NOM_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByNomCoopIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where nomCoop not equals to DEFAULT_NOM_COOP
        defaultCooperativeShouldNotBeFound("nomCoop.notEquals=" + DEFAULT_NOM_COOP);

        // Get all the cooperativeList where nomCoop not equals to UPDATED_NOM_COOP
        defaultCooperativeShouldBeFound("nomCoop.notEquals=" + UPDATED_NOM_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByNomCoopIsInShouldWork() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where nomCoop in DEFAULT_NOM_COOP or UPDATED_NOM_COOP
        defaultCooperativeShouldBeFound("nomCoop.in=" + DEFAULT_NOM_COOP + "," + UPDATED_NOM_COOP);

        // Get all the cooperativeList where nomCoop equals to UPDATED_NOM_COOP
        defaultCooperativeShouldNotBeFound("nomCoop.in=" + UPDATED_NOM_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByNomCoopIsNullOrNotNull() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where nomCoop is not null
        defaultCooperativeShouldBeFound("nomCoop.specified=true");

        // Get all the cooperativeList where nomCoop is null
        defaultCooperativeShouldNotBeFound("nomCoop.specified=false");
    }

    @Test
    @Transactional
    void getAllCooperativesByNomCoopContainsSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where nomCoop contains DEFAULT_NOM_COOP
        defaultCooperativeShouldBeFound("nomCoop.contains=" + DEFAULT_NOM_COOP);

        // Get all the cooperativeList where nomCoop contains UPDATED_NOM_COOP
        defaultCooperativeShouldNotBeFound("nomCoop.contains=" + UPDATED_NOM_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByNomCoopNotContainsSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where nomCoop does not contain DEFAULT_NOM_COOP
        defaultCooperativeShouldNotBeFound("nomCoop.doesNotContain=" + DEFAULT_NOM_COOP);

        // Get all the cooperativeList where nomCoop does not contain UPDATED_NOM_COOP
        defaultCooperativeShouldBeFound("nomCoop.doesNotContain=" + UPDATED_NOM_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByVilleCoopIsEqualToSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where villeCoop equals to DEFAULT_VILLE_COOP
        defaultCooperativeShouldBeFound("villeCoop.equals=" + DEFAULT_VILLE_COOP);

        // Get all the cooperativeList where villeCoop equals to UPDATED_VILLE_COOP
        defaultCooperativeShouldNotBeFound("villeCoop.equals=" + UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByVilleCoopIsNotEqualToSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where villeCoop not equals to DEFAULT_VILLE_COOP
        defaultCooperativeShouldNotBeFound("villeCoop.notEquals=" + DEFAULT_VILLE_COOP);

        // Get all the cooperativeList where villeCoop not equals to UPDATED_VILLE_COOP
        defaultCooperativeShouldBeFound("villeCoop.notEquals=" + UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByVilleCoopIsInShouldWork() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where villeCoop in DEFAULT_VILLE_COOP or UPDATED_VILLE_COOP
        defaultCooperativeShouldBeFound("villeCoop.in=" + DEFAULT_VILLE_COOP + "," + UPDATED_VILLE_COOP);

        // Get all the cooperativeList where villeCoop equals to UPDATED_VILLE_COOP
        defaultCooperativeShouldNotBeFound("villeCoop.in=" + UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByVilleCoopIsNullOrNotNull() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where villeCoop is not null
        defaultCooperativeShouldBeFound("villeCoop.specified=true");

        // Get all the cooperativeList where villeCoop is null
        defaultCooperativeShouldNotBeFound("villeCoop.specified=false");
    }

    @Test
    @Transactional
    void getAllCooperativesByVilleCoopContainsSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where villeCoop contains DEFAULT_VILLE_COOP
        defaultCooperativeShouldBeFound("villeCoop.contains=" + DEFAULT_VILLE_COOP);

        // Get all the cooperativeList where villeCoop contains UPDATED_VILLE_COOP
        defaultCooperativeShouldNotBeFound("villeCoop.contains=" + UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByVilleCoopNotContainsSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        // Get all the cooperativeList where villeCoop does not contain DEFAULT_VILLE_COOP
        defaultCooperativeShouldNotBeFound("villeCoop.doesNotContain=" + DEFAULT_VILLE_COOP);

        // Get all the cooperativeList where villeCoop does not contain UPDATED_VILLE_COOP
        defaultCooperativeShouldBeFound("villeCoop.doesNotContain=" + UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void getAllCooperativesByCompteIsEqualToSomething() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);
        Compte compte = CompteResourceIT.createEntity(em);
        em.persist(compte);
        em.flush();
        cooperative.addCompte(compte);
        cooperativeRepository.saveAndFlush(cooperative);
        Long compteId = compte.getId();

        // Get all the cooperativeList where compte equals to compteId
        defaultCooperativeShouldBeFound("compteId.equals=" + compteId);

        // Get all the cooperativeList where compte equals to (compteId + 1)
        defaultCooperativeShouldNotBeFound("compteId.equals=" + (compteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCooperativeShouldBeFound(String filter) throws Exception {
        restCooperativeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cooperative.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomCoop").value(hasItem(DEFAULT_NOM_COOP)))
            .andExpect(jsonPath("$.[*].villeCoop").value(hasItem(DEFAULT_VILLE_COOP)));

        // Check, that the count call also returns 1
        restCooperativeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCooperativeShouldNotBeFound(String filter) throws Exception {
        restCooperativeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCooperativeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCooperative() throws Exception {
        // Get the cooperative
        restCooperativeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCooperative() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();

        // Update the cooperative
        Cooperative updatedCooperative = cooperativeRepository.findById(cooperative.getId()).get();
        // Disconnect from session so that the updates on updatedCooperative are not directly saved in db
        em.detach(updatedCooperative);
        updatedCooperative.nomCoop(UPDATED_NOM_COOP).villeCoop(UPDATED_VILLE_COOP);
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(updatedCooperative);

        restCooperativeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cooperativeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getNomCoop()).isEqualTo(UPDATED_NOM_COOP);
        assertThat(testCooperative.getVilleCoop()).isEqualTo(UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void putNonExistingCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCooperativeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cooperativeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCooperativeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCooperativeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cooperativeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCooperativeWithPatch() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();

        // Update the cooperative using partial update
        Cooperative partialUpdatedCooperative = new Cooperative();
        partialUpdatedCooperative.setId(cooperative.getId());

        restCooperativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCooperative.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCooperative))
            )
            .andExpect(status().isOk());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getNomCoop()).isEqualTo(DEFAULT_NOM_COOP);
        assertThat(testCooperative.getVilleCoop()).isEqualTo(DEFAULT_VILLE_COOP);
    }

    @Test
    @Transactional
    void fullUpdateCooperativeWithPatch() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();

        // Update the cooperative using partial update
        Cooperative partialUpdatedCooperative = new Cooperative();
        partialUpdatedCooperative.setId(cooperative.getId());

        partialUpdatedCooperative.nomCoop(UPDATED_NOM_COOP).villeCoop(UPDATED_VILLE_COOP);

        restCooperativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCooperative.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCooperative))
            )
            .andExpect(status().isOk());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getNomCoop()).isEqualTo(UPDATED_NOM_COOP);
        assertThat(testCooperative.getVilleCoop()).isEqualTo(UPDATED_VILLE_COOP);
    }

    @Test
    @Transactional
    void patchNonExistingCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCooperativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cooperativeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCooperativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCooperativeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCooperative() throws Exception {
        // Initialize the database
        cooperativeRepository.saveAndFlush(cooperative);

        int databaseSizeBeforeDelete = cooperativeRepository.findAll().size();

        // Delete the cooperative
        restCooperativeMockMvc
            .perform(delete(ENTITY_API_URL_ID, cooperative.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cooperative> cooperativeList = cooperativeRepository.findAll();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
