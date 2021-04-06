package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Compte;
import fr.polytech.info4.domain.Panier;
import fr.polytech.info4.domain.Plat;
import fr.polytech.info4.domain.SystemePaiement;
import fr.polytech.info4.repository.PanierRepository;
import fr.polytech.info4.service.criteria.PanierCriteria;
import fr.polytech.info4.service.dto.PanierDTO;
import fr.polytech.info4.service.mapper.PanierMapper;
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
 * Integration tests for the {@link PanierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PanierResourceIT {

    private static final String DEFAULT_PRIX_TOTAL = "AAAAAAAAAA";
    private static final String UPDATED_PRIX_TOTAL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/paniers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private PanierMapper panierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPanierMockMvc;

    private Panier panier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Panier createEntity(EntityManager em) {
        Panier panier = new Panier().prixTotal(DEFAULT_PRIX_TOTAL);
        return panier;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Panier createUpdatedEntity(EntityManager em) {
        Panier panier = new Panier().prixTotal(UPDATED_PRIX_TOTAL);
        return panier;
    }

    @BeforeEach
    public void initTest() {
        panier = createEntity(em);
    }

    @Test
    @Transactional
    void createPanier() throws Exception {
        int databaseSizeBeforeCreate = panierRepository.findAll().size();
        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);
        restPanierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(panierDTO)))
            .andExpect(status().isCreated());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeCreate + 1);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getPrixTotal()).isEqualTo(DEFAULT_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void createPanierWithExistingId() throws Exception {
        // Create the Panier with an existing ID
        panier.setId(1L);
        PanierDTO panierDTO = panierMapper.toDto(panier);

        int databaseSizeBeforeCreate = panierRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPanierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(panierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPaniers() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList
        restPanierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(panier.getId().intValue())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(DEFAULT_PRIX_TOTAL)));
    }

    @Test
    @Transactional
    void getPanier() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get the panier
        restPanierMockMvc
            .perform(get(ENTITY_API_URL_ID, panier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(panier.getId().intValue()))
            .andExpect(jsonPath("$.prixTotal").value(DEFAULT_PRIX_TOTAL));
    }

    @Test
    @Transactional
    void getPaniersByIdFiltering() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        Long id = panier.getId();

        defaultPanierShouldBeFound("id.equals=" + id);
        defaultPanierShouldNotBeFound("id.notEquals=" + id);

        defaultPanierShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPanierShouldNotBeFound("id.greaterThan=" + id);

        defaultPanierShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPanierShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaniersByPrixTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList where prixTotal equals to DEFAULT_PRIX_TOTAL
        defaultPanierShouldBeFound("prixTotal.equals=" + DEFAULT_PRIX_TOTAL);

        // Get all the panierList where prixTotal equals to UPDATED_PRIX_TOTAL
        defaultPanierShouldNotBeFound("prixTotal.equals=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaniersByPrixTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList where prixTotal not equals to DEFAULT_PRIX_TOTAL
        defaultPanierShouldNotBeFound("prixTotal.notEquals=" + DEFAULT_PRIX_TOTAL);

        // Get all the panierList where prixTotal not equals to UPDATED_PRIX_TOTAL
        defaultPanierShouldBeFound("prixTotal.notEquals=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaniersByPrixTotalIsInShouldWork() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList where prixTotal in DEFAULT_PRIX_TOTAL or UPDATED_PRIX_TOTAL
        defaultPanierShouldBeFound("prixTotal.in=" + DEFAULT_PRIX_TOTAL + "," + UPDATED_PRIX_TOTAL);

        // Get all the panierList where prixTotal equals to UPDATED_PRIX_TOTAL
        defaultPanierShouldNotBeFound("prixTotal.in=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaniersByPrixTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList where prixTotal is not null
        defaultPanierShouldBeFound("prixTotal.specified=true");

        // Get all the panierList where prixTotal is null
        defaultPanierShouldNotBeFound("prixTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllPaniersByPrixTotalContainsSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList where prixTotal contains DEFAULT_PRIX_TOTAL
        defaultPanierShouldBeFound("prixTotal.contains=" + DEFAULT_PRIX_TOTAL);

        // Get all the panierList where prixTotal contains UPDATED_PRIX_TOTAL
        defaultPanierShouldNotBeFound("prixTotal.contains=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaniersByPrixTotalNotContainsSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        // Get all the panierList where prixTotal does not contain DEFAULT_PRIX_TOTAL
        defaultPanierShouldNotBeFound("prixTotal.doesNotContain=" + DEFAULT_PRIX_TOTAL);

        // Get all the panierList where prixTotal does not contain UPDATED_PRIX_TOTAL
        defaultPanierShouldBeFound("prixTotal.doesNotContain=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllPaniersByConstitueIsEqualToSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);
        Compte constitue = CompteResourceIT.createEntity(em);
        em.persist(constitue);
        em.flush();
        panier.setConstitue(constitue);
        panierRepository.saveAndFlush(panier);
        Long constitueId = constitue.getId();

        // Get all the panierList where constitue equals to constitueId
        defaultPanierShouldBeFound("constitueId.equals=" + constitueId);

        // Get all the panierList where constitue equals to (constitueId + 1)
        defaultPanierShouldNotBeFound("constitueId.equals=" + (constitueId + 1));
    }

    @Test
    @Transactional
    void getAllPaniersByEstValideParIsEqualToSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);
        SystemePaiement estValidePar = SystemePaiementResourceIT.createEntity(em);
        em.persist(estValidePar);
        em.flush();
        panier.setEstValidePar(estValidePar);
        panierRepository.saveAndFlush(panier);
        Long estValideParId = estValidePar.getId();

        // Get all the panierList where estValidePar equals to estValideParId
        defaultPanierShouldBeFound("estValideParId.equals=" + estValideParId);

        // Get all the panierList where estValidePar equals to (estValideParId + 1)
        defaultPanierShouldNotBeFound("estValideParId.equals=" + (estValideParId + 1));
    }

    @Test
    @Transactional
    void getAllPaniersByPlatIsEqualToSomething() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);
        Plat plat = PlatResourceIT.createEntity(em);
        em.persist(plat);
        em.flush();
        panier.addPlat(plat);
        panierRepository.saveAndFlush(panier);
        Long platId = plat.getId();

        // Get all the panierList where plat equals to platId
        defaultPanierShouldBeFound("platId.equals=" + platId);

        // Get all the panierList where plat equals to (platId + 1)
        defaultPanierShouldNotBeFound("platId.equals=" + (platId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPanierShouldBeFound(String filter) throws Exception {
        restPanierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(panier.getId().intValue())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(DEFAULT_PRIX_TOTAL)));

        // Check, that the count call also returns 1
        restPanierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPanierShouldNotBeFound(String filter) throws Exception {
        restPanierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPanierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPanier() throws Exception {
        // Get the panier
        restPanierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPanier() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        int databaseSizeBeforeUpdate = panierRepository.findAll().size();

        // Update the panier
        Panier updatedPanier = panierRepository.findById(panier.getId()).get();
        // Disconnect from session so that the updates on updatedPanier are not directly saved in db
        em.detach(updatedPanier);
        updatedPanier.prixTotal(UPDATED_PRIX_TOTAL);
        PanierDTO panierDTO = panierMapper.toDto(updatedPanier);

        restPanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, panierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(panierDTO))
            )
            .andExpect(status().isOk());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void putNonExistingPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, panierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(panierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePanierWithPatch() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        int databaseSizeBeforeUpdate = panierRepository.findAll().size();

        // Update the panier using partial update
        Panier partialUpdatedPanier = new Panier();
        partialUpdatedPanier.setId(panier.getId());

        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPanier.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPanier))
            )
            .andExpect(status().isOk());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getPrixTotal()).isEqualTo(DEFAULT_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdatePanierWithPatch() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        int databaseSizeBeforeUpdate = panierRepository.findAll().size();

        // Update the panier using partial update
        Panier partialUpdatedPanier = new Panier();
        partialUpdatedPanier.setId(panier.getId());

        partialUpdatedPanier.prixTotal(UPDATED_PRIX_TOTAL);

        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPanier.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPanier))
            )
            .andExpect(status().isOk());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
        Panier testPanier = panierList.get(panierList.size() - 1);
        assertThat(testPanier.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, panierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(panierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPanier() throws Exception {
        int databaseSizeBeforeUpdate = panierRepository.findAll().size();
        panier.setId(count.incrementAndGet());

        // Create the Panier
        PanierDTO panierDTO = panierMapper.toDto(panier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPanierMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(panierDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Panier in the database
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePanier() throws Exception {
        // Initialize the database
        panierRepository.saveAndFlush(panier);

        int databaseSizeBeforeDelete = panierRepository.findAll().size();

        // Delete the panier
        restPanierMockMvc
            .perform(delete(ENTITY_API_URL_ID, panier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Panier> panierList = panierRepository.findAll();
        assertThat(panierList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
