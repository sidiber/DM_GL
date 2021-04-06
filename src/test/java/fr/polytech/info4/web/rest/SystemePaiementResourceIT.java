package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Panier;
import fr.polytech.info4.domain.SystemePaiement;
import fr.polytech.info4.repository.SystemePaiementRepository;
import fr.polytech.info4.service.criteria.SystemePaiementCriteria;
import fr.polytech.info4.service.dto.SystemePaiementDTO;
import fr.polytech.info4.service.mapper.SystemePaiementMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link SystemePaiementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SystemePaiementResourceIT {

    private static final String DEFAULT_NUM_CARTE = " 8_7";
    private static final String UPDATED_NUM_CARTE = "0";

    private static final Instant DEFAULT_DATE_EXPIRATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_EXPIRATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TYPE_CARTE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_CARTE = "BBBBBBBBBB";

    private static final String DEFAULT_MONTANT = "AAAAAAAAAA";
    private static final String UPDATED_MONTANT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_FACTURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FACTURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/systeme-paiements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SystemePaiementRepository systemePaiementRepository;

    @Autowired
    private SystemePaiementMapper systemePaiementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSystemePaiementMockMvc;

    private SystemePaiement systemePaiement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemePaiement createEntity(EntityManager em) {
        SystemePaiement systemePaiement = new SystemePaiement()
            .numCarte(DEFAULT_NUM_CARTE)
            .dateExpiration(DEFAULT_DATE_EXPIRATION)
            .typeCarte(DEFAULT_TYPE_CARTE)
            .montant(DEFAULT_MONTANT)
            .dateFacture(DEFAULT_DATE_FACTURE);
        return systemePaiement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemePaiement createUpdatedEntity(EntityManager em) {
        SystemePaiement systemePaiement = new SystemePaiement()
            .numCarte(UPDATED_NUM_CARTE)
            .dateExpiration(UPDATED_DATE_EXPIRATION)
            .typeCarte(UPDATED_TYPE_CARTE)
            .montant(UPDATED_MONTANT)
            .dateFacture(UPDATED_DATE_FACTURE);
        return systemePaiement;
    }

    @BeforeEach
    public void initTest() {
        systemePaiement = createEntity(em);
    }

    @Test
    @Transactional
    void createSystemePaiement() throws Exception {
        int databaseSizeBeforeCreate = systemePaiementRepository.findAll().size();
        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);
        restSystemePaiementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeCreate + 1);
        SystemePaiement testSystemePaiement = systemePaiementList.get(systemePaiementList.size() - 1);
        assertThat(testSystemePaiement.getNumCarte()).isEqualTo(DEFAULT_NUM_CARTE);
        assertThat(testSystemePaiement.getDateExpiration()).isEqualTo(DEFAULT_DATE_EXPIRATION);
        assertThat(testSystemePaiement.getTypeCarte()).isEqualTo(DEFAULT_TYPE_CARTE);
        assertThat(testSystemePaiement.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testSystemePaiement.getDateFacture()).isEqualTo(DEFAULT_DATE_FACTURE);
    }

    @Test
    @Transactional
    void createSystemePaiementWithExistingId() throws Exception {
        // Create the SystemePaiement with an existing ID
        systemePaiement.setId(1L);
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        int databaseSizeBeforeCreate = systemePaiementRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemePaiementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateExpirationIsRequired() throws Exception {
        int databaseSizeBeforeTest = systemePaiementRepository.findAll().size();
        // set the field null
        systemePaiement.setDateExpiration(null);

        // Create the SystemePaiement, which fails.
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        restSystemePaiementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateFactureIsRequired() throws Exception {
        int databaseSizeBeforeTest = systemePaiementRepository.findAll().size();
        // set the field null
        systemePaiement.setDateFacture(null);

        // Create the SystemePaiement, which fails.
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        restSystemePaiementMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSystemePaiements() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList
        restSystemePaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemePaiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].numCarte").value(hasItem(DEFAULT_NUM_CARTE)))
            .andExpect(jsonPath("$.[*].dateExpiration").value(hasItem(DEFAULT_DATE_EXPIRATION.toString())))
            .andExpect(jsonPath("$.[*].typeCarte").value(hasItem(DEFAULT_TYPE_CARTE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.[*].dateFacture").value(hasItem(DEFAULT_DATE_FACTURE.toString())));
    }

    @Test
    @Transactional
    void getSystemePaiement() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get the systemePaiement
        restSystemePaiementMockMvc
            .perform(get(ENTITY_API_URL_ID, systemePaiement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(systemePaiement.getId().intValue()))
            .andExpect(jsonPath("$.numCarte").value(DEFAULT_NUM_CARTE))
            .andExpect(jsonPath("$.dateExpiration").value(DEFAULT_DATE_EXPIRATION.toString()))
            .andExpect(jsonPath("$.typeCarte").value(DEFAULT_TYPE_CARTE))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT))
            .andExpect(jsonPath("$.dateFacture").value(DEFAULT_DATE_FACTURE.toString()));
    }

    @Test
    @Transactional
    void getSystemePaiementsByIdFiltering() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        Long id = systemePaiement.getId();

        defaultSystemePaiementShouldBeFound("id.equals=" + id);
        defaultSystemePaiementShouldNotBeFound("id.notEquals=" + id);

        defaultSystemePaiementShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSystemePaiementShouldNotBeFound("id.greaterThan=" + id);

        defaultSystemePaiementShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSystemePaiementShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByNumCarteIsEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where numCarte equals to DEFAULT_NUM_CARTE
        defaultSystemePaiementShouldBeFound("numCarte.equals=" + DEFAULT_NUM_CARTE);

        // Get all the systemePaiementList where numCarte equals to UPDATED_NUM_CARTE
        defaultSystemePaiementShouldNotBeFound("numCarte.equals=" + UPDATED_NUM_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByNumCarteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where numCarte not equals to DEFAULT_NUM_CARTE
        defaultSystemePaiementShouldNotBeFound("numCarte.notEquals=" + DEFAULT_NUM_CARTE);

        // Get all the systemePaiementList where numCarte not equals to UPDATED_NUM_CARTE
        defaultSystemePaiementShouldBeFound("numCarte.notEquals=" + UPDATED_NUM_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByNumCarteIsInShouldWork() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where numCarte in DEFAULT_NUM_CARTE or UPDATED_NUM_CARTE
        defaultSystemePaiementShouldBeFound("numCarte.in=" + DEFAULT_NUM_CARTE + "," + UPDATED_NUM_CARTE);

        // Get all the systemePaiementList where numCarte equals to UPDATED_NUM_CARTE
        defaultSystemePaiementShouldNotBeFound("numCarte.in=" + UPDATED_NUM_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByNumCarteIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where numCarte is not null
        defaultSystemePaiementShouldBeFound("numCarte.specified=true");

        // Get all the systemePaiementList where numCarte is null
        defaultSystemePaiementShouldNotBeFound("numCarte.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByNumCarteContainsSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where numCarte contains DEFAULT_NUM_CARTE
        defaultSystemePaiementShouldBeFound("numCarte.contains=" + DEFAULT_NUM_CARTE);

        // Get all the systemePaiementList where numCarte contains UPDATED_NUM_CARTE
        defaultSystemePaiementShouldNotBeFound("numCarte.contains=" + UPDATED_NUM_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByNumCarteNotContainsSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where numCarte does not contain DEFAULT_NUM_CARTE
        defaultSystemePaiementShouldNotBeFound("numCarte.doesNotContain=" + DEFAULT_NUM_CARTE);

        // Get all the systemePaiementList where numCarte does not contain UPDATED_NUM_CARTE
        defaultSystemePaiementShouldBeFound("numCarte.doesNotContain=" + UPDATED_NUM_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateExpirationIsEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateExpiration equals to DEFAULT_DATE_EXPIRATION
        defaultSystemePaiementShouldBeFound("dateExpiration.equals=" + DEFAULT_DATE_EXPIRATION);

        // Get all the systemePaiementList where dateExpiration equals to UPDATED_DATE_EXPIRATION
        defaultSystemePaiementShouldNotBeFound("dateExpiration.equals=" + UPDATED_DATE_EXPIRATION);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateExpirationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateExpiration not equals to DEFAULT_DATE_EXPIRATION
        defaultSystemePaiementShouldNotBeFound("dateExpiration.notEquals=" + DEFAULT_DATE_EXPIRATION);

        // Get all the systemePaiementList where dateExpiration not equals to UPDATED_DATE_EXPIRATION
        defaultSystemePaiementShouldBeFound("dateExpiration.notEquals=" + UPDATED_DATE_EXPIRATION);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateExpirationIsInShouldWork() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateExpiration in DEFAULT_DATE_EXPIRATION or UPDATED_DATE_EXPIRATION
        defaultSystemePaiementShouldBeFound("dateExpiration.in=" + DEFAULT_DATE_EXPIRATION + "," + UPDATED_DATE_EXPIRATION);

        // Get all the systemePaiementList where dateExpiration equals to UPDATED_DATE_EXPIRATION
        defaultSystemePaiementShouldNotBeFound("dateExpiration.in=" + UPDATED_DATE_EXPIRATION);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateExpirationIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateExpiration is not null
        defaultSystemePaiementShouldBeFound("dateExpiration.specified=true");

        // Get all the systemePaiementList where dateExpiration is null
        defaultSystemePaiementShouldNotBeFound("dateExpiration.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByTypeCarteIsEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where typeCarte equals to DEFAULT_TYPE_CARTE
        defaultSystemePaiementShouldBeFound("typeCarte.equals=" + DEFAULT_TYPE_CARTE);

        // Get all the systemePaiementList where typeCarte equals to UPDATED_TYPE_CARTE
        defaultSystemePaiementShouldNotBeFound("typeCarte.equals=" + UPDATED_TYPE_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByTypeCarteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where typeCarte not equals to DEFAULT_TYPE_CARTE
        defaultSystemePaiementShouldNotBeFound("typeCarte.notEquals=" + DEFAULT_TYPE_CARTE);

        // Get all the systemePaiementList where typeCarte not equals to UPDATED_TYPE_CARTE
        defaultSystemePaiementShouldBeFound("typeCarte.notEquals=" + UPDATED_TYPE_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByTypeCarteIsInShouldWork() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where typeCarte in DEFAULT_TYPE_CARTE or UPDATED_TYPE_CARTE
        defaultSystemePaiementShouldBeFound("typeCarte.in=" + DEFAULT_TYPE_CARTE + "," + UPDATED_TYPE_CARTE);

        // Get all the systemePaiementList where typeCarte equals to UPDATED_TYPE_CARTE
        defaultSystemePaiementShouldNotBeFound("typeCarte.in=" + UPDATED_TYPE_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByTypeCarteIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where typeCarte is not null
        defaultSystemePaiementShouldBeFound("typeCarte.specified=true");

        // Get all the systemePaiementList where typeCarte is null
        defaultSystemePaiementShouldNotBeFound("typeCarte.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByTypeCarteContainsSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where typeCarte contains DEFAULT_TYPE_CARTE
        defaultSystemePaiementShouldBeFound("typeCarte.contains=" + DEFAULT_TYPE_CARTE);

        // Get all the systemePaiementList where typeCarte contains UPDATED_TYPE_CARTE
        defaultSystemePaiementShouldNotBeFound("typeCarte.contains=" + UPDATED_TYPE_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByTypeCarteNotContainsSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where typeCarte does not contain DEFAULT_TYPE_CARTE
        defaultSystemePaiementShouldNotBeFound("typeCarte.doesNotContain=" + DEFAULT_TYPE_CARTE);

        // Get all the systemePaiementList where typeCarte does not contain UPDATED_TYPE_CARTE
        defaultSystemePaiementShouldBeFound("typeCarte.doesNotContain=" + UPDATED_TYPE_CARTE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByMontantIsEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where montant equals to DEFAULT_MONTANT
        defaultSystemePaiementShouldBeFound("montant.equals=" + DEFAULT_MONTANT);

        // Get all the systemePaiementList where montant equals to UPDATED_MONTANT
        defaultSystemePaiementShouldNotBeFound("montant.equals=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByMontantIsNotEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where montant not equals to DEFAULT_MONTANT
        defaultSystemePaiementShouldNotBeFound("montant.notEquals=" + DEFAULT_MONTANT);

        // Get all the systemePaiementList where montant not equals to UPDATED_MONTANT
        defaultSystemePaiementShouldBeFound("montant.notEquals=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByMontantIsInShouldWork() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where montant in DEFAULT_MONTANT or UPDATED_MONTANT
        defaultSystemePaiementShouldBeFound("montant.in=" + DEFAULT_MONTANT + "," + UPDATED_MONTANT);

        // Get all the systemePaiementList where montant equals to UPDATED_MONTANT
        defaultSystemePaiementShouldNotBeFound("montant.in=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByMontantIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where montant is not null
        defaultSystemePaiementShouldBeFound("montant.specified=true");

        // Get all the systemePaiementList where montant is null
        defaultSystemePaiementShouldNotBeFound("montant.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByMontantContainsSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where montant contains DEFAULT_MONTANT
        defaultSystemePaiementShouldBeFound("montant.contains=" + DEFAULT_MONTANT);

        // Get all the systemePaiementList where montant contains UPDATED_MONTANT
        defaultSystemePaiementShouldNotBeFound("montant.contains=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByMontantNotContainsSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where montant does not contain DEFAULT_MONTANT
        defaultSystemePaiementShouldNotBeFound("montant.doesNotContain=" + DEFAULT_MONTANT);

        // Get all the systemePaiementList where montant does not contain UPDATED_MONTANT
        defaultSystemePaiementShouldBeFound("montant.doesNotContain=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateFactureIsEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateFacture equals to DEFAULT_DATE_FACTURE
        defaultSystemePaiementShouldBeFound("dateFacture.equals=" + DEFAULT_DATE_FACTURE);

        // Get all the systemePaiementList where dateFacture equals to UPDATED_DATE_FACTURE
        defaultSystemePaiementShouldNotBeFound("dateFacture.equals=" + UPDATED_DATE_FACTURE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateFactureIsNotEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateFacture not equals to DEFAULT_DATE_FACTURE
        defaultSystemePaiementShouldNotBeFound("dateFacture.notEquals=" + DEFAULT_DATE_FACTURE);

        // Get all the systemePaiementList where dateFacture not equals to UPDATED_DATE_FACTURE
        defaultSystemePaiementShouldBeFound("dateFacture.notEquals=" + UPDATED_DATE_FACTURE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateFactureIsInShouldWork() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateFacture in DEFAULT_DATE_FACTURE or UPDATED_DATE_FACTURE
        defaultSystemePaiementShouldBeFound("dateFacture.in=" + DEFAULT_DATE_FACTURE + "," + UPDATED_DATE_FACTURE);

        // Get all the systemePaiementList where dateFacture equals to UPDATED_DATE_FACTURE
        defaultSystemePaiementShouldNotBeFound("dateFacture.in=" + UPDATED_DATE_FACTURE);
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByDateFactureIsNullOrNotNull() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        // Get all the systemePaiementList where dateFacture is not null
        defaultSystemePaiementShouldBeFound("dateFacture.specified=true");

        // Get all the systemePaiementList where dateFacture is null
        defaultSystemePaiementShouldNotBeFound("dateFacture.specified=false");
    }

    @Test
    @Transactional
    void getAllSystemePaiementsByPanierIsEqualToSomething() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);
        Panier panier = PanierResourceIT.createEntity(em);
        em.persist(panier);
        em.flush();
        systemePaiement.addPanier(panier);
        systemePaiementRepository.saveAndFlush(systemePaiement);
        Long panierId = panier.getId();

        // Get all the systemePaiementList where panier equals to panierId
        defaultSystemePaiementShouldBeFound("panierId.equals=" + panierId);

        // Get all the systemePaiementList where panier equals to (panierId + 1)
        defaultSystemePaiementShouldNotBeFound("panierId.equals=" + (panierId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSystemePaiementShouldBeFound(String filter) throws Exception {
        restSystemePaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemePaiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].numCarte").value(hasItem(DEFAULT_NUM_CARTE)))
            .andExpect(jsonPath("$.[*].dateExpiration").value(hasItem(DEFAULT_DATE_EXPIRATION.toString())))
            .andExpect(jsonPath("$.[*].typeCarte").value(hasItem(DEFAULT_TYPE_CARTE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.[*].dateFacture").value(hasItem(DEFAULT_DATE_FACTURE.toString())));

        // Check, that the count call also returns 1
        restSystemePaiementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSystemePaiementShouldNotBeFound(String filter) throws Exception {
        restSystemePaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSystemePaiementMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSystemePaiement() throws Exception {
        // Get the systemePaiement
        restSystemePaiementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSystemePaiement() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();

        // Update the systemePaiement
        SystemePaiement updatedSystemePaiement = systemePaiementRepository.findById(systemePaiement.getId()).get();
        // Disconnect from session so that the updates on updatedSystemePaiement are not directly saved in db
        em.detach(updatedSystemePaiement);
        updatedSystemePaiement
            .numCarte(UPDATED_NUM_CARTE)
            .dateExpiration(UPDATED_DATE_EXPIRATION)
            .typeCarte(UPDATED_TYPE_CARTE)
            .montant(UPDATED_MONTANT)
            .dateFacture(UPDATED_DATE_FACTURE);
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(updatedSystemePaiement);

        restSystemePaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemePaiementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isOk());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
        SystemePaiement testSystemePaiement = systemePaiementList.get(systemePaiementList.size() - 1);
        assertThat(testSystemePaiement.getNumCarte()).isEqualTo(UPDATED_NUM_CARTE);
        assertThat(testSystemePaiement.getDateExpiration()).isEqualTo(UPDATED_DATE_EXPIRATION);
        assertThat(testSystemePaiement.getTypeCarte()).isEqualTo(UPDATED_TYPE_CARTE);
        assertThat(testSystemePaiement.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testSystemePaiement.getDateFacture()).isEqualTo(UPDATED_DATE_FACTURE);
    }

    @Test
    @Transactional
    void putNonExistingSystemePaiement() throws Exception {
        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();
        systemePaiement.setId(count.incrementAndGet());

        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemePaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, systemePaiementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSystemePaiement() throws Exception {
        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();
        systemePaiement.setId(count.incrementAndGet());

        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemePaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSystemePaiement() throws Exception {
        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();
        systemePaiement.setId(count.incrementAndGet());

        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemePaiementMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSystemePaiementWithPatch() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();

        // Update the systemePaiement using partial update
        SystemePaiement partialUpdatedSystemePaiement = new SystemePaiement();
        partialUpdatedSystemePaiement.setId(systemePaiement.getId());

        partialUpdatedSystemePaiement.numCarte(UPDATED_NUM_CARTE).dateExpiration(UPDATED_DATE_EXPIRATION);

        restSystemePaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystemePaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSystemePaiement))
            )
            .andExpect(status().isOk());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
        SystemePaiement testSystemePaiement = systemePaiementList.get(systemePaiementList.size() - 1);
        assertThat(testSystemePaiement.getNumCarte()).isEqualTo(UPDATED_NUM_CARTE);
        assertThat(testSystemePaiement.getDateExpiration()).isEqualTo(UPDATED_DATE_EXPIRATION);
        assertThat(testSystemePaiement.getTypeCarte()).isEqualTo(DEFAULT_TYPE_CARTE);
        assertThat(testSystemePaiement.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testSystemePaiement.getDateFacture()).isEqualTo(DEFAULT_DATE_FACTURE);
    }

    @Test
    @Transactional
    void fullUpdateSystemePaiementWithPatch() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();

        // Update the systemePaiement using partial update
        SystemePaiement partialUpdatedSystemePaiement = new SystemePaiement();
        partialUpdatedSystemePaiement.setId(systemePaiement.getId());

        partialUpdatedSystemePaiement
            .numCarte(UPDATED_NUM_CARTE)
            .dateExpiration(UPDATED_DATE_EXPIRATION)
            .typeCarte(UPDATED_TYPE_CARTE)
            .montant(UPDATED_MONTANT)
            .dateFacture(UPDATED_DATE_FACTURE);

        restSystemePaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSystemePaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSystemePaiement))
            )
            .andExpect(status().isOk());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
        SystemePaiement testSystemePaiement = systemePaiementList.get(systemePaiementList.size() - 1);
        assertThat(testSystemePaiement.getNumCarte()).isEqualTo(UPDATED_NUM_CARTE);
        assertThat(testSystemePaiement.getDateExpiration()).isEqualTo(UPDATED_DATE_EXPIRATION);
        assertThat(testSystemePaiement.getTypeCarte()).isEqualTo(UPDATED_TYPE_CARTE);
        assertThat(testSystemePaiement.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testSystemePaiement.getDateFacture()).isEqualTo(UPDATED_DATE_FACTURE);
    }

    @Test
    @Transactional
    void patchNonExistingSystemePaiement() throws Exception {
        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();
        systemePaiement.setId(count.incrementAndGet());

        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemePaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, systemePaiementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSystemePaiement() throws Exception {
        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();
        systemePaiement.setId(count.incrementAndGet());

        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemePaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSystemePaiement() throws Exception {
        int databaseSizeBeforeUpdate = systemePaiementRepository.findAll().size();
        systemePaiement.setId(count.incrementAndGet());

        // Create the SystemePaiement
        SystemePaiementDTO systemePaiementDTO = systemePaiementMapper.toDto(systemePaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSystemePaiementMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(systemePaiementDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SystemePaiement in the database
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSystemePaiement() throws Exception {
        // Initialize the database
        systemePaiementRepository.saveAndFlush(systemePaiement);

        int databaseSizeBeforeDelete = systemePaiementRepository.findAll().size();

        // Delete the systemePaiement
        restSystemePaiementMockMvc
            .perform(delete(ENTITY_API_URL_ID, systemePaiement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SystemePaiement> systemePaiementList = systemePaiementRepository.findAll();
        assertThat(systemePaiementList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
