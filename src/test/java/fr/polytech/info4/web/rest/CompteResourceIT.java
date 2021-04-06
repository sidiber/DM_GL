package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Compte;
import fr.polytech.info4.domain.Cooperative;
import fr.polytech.info4.domain.Course;
import fr.polytech.info4.domain.Panier;
import fr.polytech.info4.repository.CompteRepository;
import fr.polytech.info4.service.criteria.CompteCriteria;
import fr.polytech.info4.service.dto.CompteDTO;
import fr.polytech.info4.service.mapper.CompteMapper;
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
 * Integration tests for the {@link CompteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CompteResourceIT {

    private static final String DEFAULT_NOM = "y";
    private static final String UPDATED_NOM = "U";

    private static final String DEFAULT_PRENOM = "U";
    private static final String UPDATED_PRENOM = "E";

    private static final String DEFAULT_EMAIL = "JgXVe@imag.fr";
    private static final String UPDATED_EMAIL = "in3zAT@gmail.fr";

    private static final String DEFAULT_PHONE_NUMBER = "7";
    private static final String UPDATED_PHONE_NUMBER = "1";

    private static final String DEFAULT_ADDRESS_COMPTE = "8";
    private static final String UPDATED_ADDRESS_COMPTE = "_";

    private static final String DEFAULT_CODE_P_COMPTE = "6";
    private static final String UPDATED_CODE_P_COMPTE = "1";

    private static final String DEFAULT_VILLE_COMPTE = "L";
    private static final String UPDATED_VILLE_COMPTE = "s";

    private static final String ENTITY_API_URL = "/api/comptes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private CompteMapper compteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCompteMockMvc;

    private Compte compte;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Compte createEntity(EntityManager em) {
        Compte compte = new Compte()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .addressCompte(DEFAULT_ADDRESS_COMPTE)
            .codePCompte(DEFAULT_CODE_P_COMPTE)
            .villeCompte(DEFAULT_VILLE_COMPTE);
        return compte;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Compte createUpdatedEntity(EntityManager em) {
        Compte compte = new Compte()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .addressCompte(UPDATED_ADDRESS_COMPTE)
            .codePCompte(UPDATED_CODE_P_COMPTE)
            .villeCompte(UPDATED_VILLE_COMPTE);
        return compte;
    }

    @BeforeEach
    public void initTest() {
        compte = createEntity(em);
    }

    @Test
    @Transactional
    void createCompte() throws Exception {
        int databaseSizeBeforeCreate = compteRepository.findAll().size();
        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);
        restCompteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compteDTO)))
            .andExpect(status().isCreated());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeCreate + 1);
        Compte testCompte = compteList.get(compteList.size() - 1);
        assertThat(testCompte.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testCompte.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testCompte.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCompte.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testCompte.getAddressCompte()).isEqualTo(DEFAULT_ADDRESS_COMPTE);
        assertThat(testCompte.getCodePCompte()).isEqualTo(DEFAULT_CODE_P_COMPTE);
        assertThat(testCompte.getVilleCompte()).isEqualTo(DEFAULT_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void createCompteWithExistingId() throws Exception {
        // Create the Compte with an existing ID
        compte.setId(1L);
        CompteDTO compteDTO = compteMapper.toDto(compte);

        int databaseSizeBeforeCreate = compteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = compteRepository.findAll().size();
        // set the field null
        compte.setNom(null);

        // Create the Compte, which fails.
        CompteDTO compteDTO = compteMapper.toDto(compte);

        restCompteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compteDTO)))
            .andExpect(status().isBadRequest());

        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        int databaseSizeBeforeTest = compteRepository.findAll().size();
        // set the field null
        compte.setPrenom(null);

        // Create the Compte, which fails.
        CompteDTO compteDTO = compteMapper.toDto(compte);

        restCompteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compteDTO)))
            .andExpect(status().isBadRequest());

        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = compteRepository.findAll().size();
        // set the field null
        compte.setEmail(null);

        // Create the Compte, which fails.
        CompteDTO compteDTO = compteMapper.toDto(compte);

        restCompteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compteDTO)))
            .andExpect(status().isBadRequest());

        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllComptes() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList
        restCompteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compte.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].addressCompte").value(hasItem(DEFAULT_ADDRESS_COMPTE)))
            .andExpect(jsonPath("$.[*].codePCompte").value(hasItem(DEFAULT_CODE_P_COMPTE)))
            .andExpect(jsonPath("$.[*].villeCompte").value(hasItem(DEFAULT_VILLE_COMPTE)));
    }

    @Test
    @Transactional
    void getCompte() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get the compte
        restCompteMockMvc
            .perform(get(ENTITY_API_URL_ID, compte.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(compte.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.addressCompte").value(DEFAULT_ADDRESS_COMPTE))
            .andExpect(jsonPath("$.codePCompte").value(DEFAULT_CODE_P_COMPTE))
            .andExpect(jsonPath("$.villeCompte").value(DEFAULT_VILLE_COMPTE));
    }

    @Test
    @Transactional
    void getComptesByIdFiltering() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        Long id = compte.getId();

        defaultCompteShouldBeFound("id.equals=" + id);
        defaultCompteShouldNotBeFound("id.notEquals=" + id);

        defaultCompteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCompteShouldNotBeFound("id.greaterThan=" + id);

        defaultCompteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCompteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllComptesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where nom equals to DEFAULT_NOM
        defaultCompteShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the compteList where nom equals to UPDATED_NOM
        defaultCompteShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllComptesByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where nom not equals to DEFAULT_NOM
        defaultCompteShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the compteList where nom not equals to UPDATED_NOM
        defaultCompteShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllComptesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultCompteShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the compteList where nom equals to UPDATED_NOM
        defaultCompteShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllComptesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where nom is not null
        defaultCompteShouldBeFound("nom.specified=true");

        // Get all the compteList where nom is null
        defaultCompteShouldNotBeFound("nom.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByNomContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where nom contains DEFAULT_NOM
        defaultCompteShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the compteList where nom contains UPDATED_NOM
        defaultCompteShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllComptesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where nom does not contain DEFAULT_NOM
        defaultCompteShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the compteList where nom does not contain UPDATED_NOM
        defaultCompteShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllComptesByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where prenom equals to DEFAULT_PRENOM
        defaultCompteShouldBeFound("prenom.equals=" + DEFAULT_PRENOM);

        // Get all the compteList where prenom equals to UPDATED_PRENOM
        defaultCompteShouldNotBeFound("prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllComptesByPrenomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where prenom not equals to DEFAULT_PRENOM
        defaultCompteShouldNotBeFound("prenom.notEquals=" + DEFAULT_PRENOM);

        // Get all the compteList where prenom not equals to UPDATED_PRENOM
        defaultCompteShouldBeFound("prenom.notEquals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllComptesByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where prenom in DEFAULT_PRENOM or UPDATED_PRENOM
        defaultCompteShouldBeFound("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM);

        // Get all the compteList where prenom equals to UPDATED_PRENOM
        defaultCompteShouldNotBeFound("prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllComptesByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where prenom is not null
        defaultCompteShouldBeFound("prenom.specified=true");

        // Get all the compteList where prenom is null
        defaultCompteShouldNotBeFound("prenom.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByPrenomContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where prenom contains DEFAULT_PRENOM
        defaultCompteShouldBeFound("prenom.contains=" + DEFAULT_PRENOM);

        // Get all the compteList where prenom contains UPDATED_PRENOM
        defaultCompteShouldNotBeFound("prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllComptesByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where prenom does not contain DEFAULT_PRENOM
        defaultCompteShouldNotBeFound("prenom.doesNotContain=" + DEFAULT_PRENOM);

        // Get all the compteList where prenom does not contain UPDATED_PRENOM
        defaultCompteShouldBeFound("prenom.doesNotContain=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllComptesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where email equals to DEFAULT_EMAIL
        defaultCompteShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the compteList where email equals to UPDATED_EMAIL
        defaultCompteShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllComptesByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where email not equals to DEFAULT_EMAIL
        defaultCompteShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the compteList where email not equals to UPDATED_EMAIL
        defaultCompteShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllComptesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultCompteShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the compteList where email equals to UPDATED_EMAIL
        defaultCompteShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllComptesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where email is not null
        defaultCompteShouldBeFound("email.specified=true");

        // Get all the compteList where email is null
        defaultCompteShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByEmailContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where email contains DEFAULT_EMAIL
        defaultCompteShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the compteList where email contains UPDATED_EMAIL
        defaultCompteShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllComptesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where email does not contain DEFAULT_EMAIL
        defaultCompteShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the compteList where email does not contain UPDATED_EMAIL
        defaultCompteShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllComptesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultCompteShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the compteList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultCompteShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllComptesByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultCompteShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the compteList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultCompteShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllComptesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultCompteShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the compteList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultCompteShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllComptesByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where phoneNumber is not null
        defaultCompteShouldBeFound("phoneNumber.specified=true");

        // Get all the compteList where phoneNumber is null
        defaultCompteShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultCompteShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the compteList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultCompteShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllComptesByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultCompteShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the compteList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultCompteShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllComptesByAddressCompteIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where addressCompte equals to DEFAULT_ADDRESS_COMPTE
        defaultCompteShouldBeFound("addressCompte.equals=" + DEFAULT_ADDRESS_COMPTE);

        // Get all the compteList where addressCompte equals to UPDATED_ADDRESS_COMPTE
        defaultCompteShouldNotBeFound("addressCompte.equals=" + UPDATED_ADDRESS_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByAddressCompteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where addressCompte not equals to DEFAULT_ADDRESS_COMPTE
        defaultCompteShouldNotBeFound("addressCompte.notEquals=" + DEFAULT_ADDRESS_COMPTE);

        // Get all the compteList where addressCompte not equals to UPDATED_ADDRESS_COMPTE
        defaultCompteShouldBeFound("addressCompte.notEquals=" + UPDATED_ADDRESS_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByAddressCompteIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where addressCompte in DEFAULT_ADDRESS_COMPTE or UPDATED_ADDRESS_COMPTE
        defaultCompteShouldBeFound("addressCompte.in=" + DEFAULT_ADDRESS_COMPTE + "," + UPDATED_ADDRESS_COMPTE);

        // Get all the compteList where addressCompte equals to UPDATED_ADDRESS_COMPTE
        defaultCompteShouldNotBeFound("addressCompte.in=" + UPDATED_ADDRESS_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByAddressCompteIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where addressCompte is not null
        defaultCompteShouldBeFound("addressCompte.specified=true");

        // Get all the compteList where addressCompte is null
        defaultCompteShouldNotBeFound("addressCompte.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByAddressCompteContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where addressCompte contains DEFAULT_ADDRESS_COMPTE
        defaultCompteShouldBeFound("addressCompte.contains=" + DEFAULT_ADDRESS_COMPTE);

        // Get all the compteList where addressCompte contains UPDATED_ADDRESS_COMPTE
        defaultCompteShouldNotBeFound("addressCompte.contains=" + UPDATED_ADDRESS_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByAddressCompteNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where addressCompte does not contain DEFAULT_ADDRESS_COMPTE
        defaultCompteShouldNotBeFound("addressCompte.doesNotContain=" + DEFAULT_ADDRESS_COMPTE);

        // Get all the compteList where addressCompte does not contain UPDATED_ADDRESS_COMPTE
        defaultCompteShouldBeFound("addressCompte.doesNotContain=" + UPDATED_ADDRESS_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByCodePCompteIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where codePCompte equals to DEFAULT_CODE_P_COMPTE
        defaultCompteShouldBeFound("codePCompte.equals=" + DEFAULT_CODE_P_COMPTE);

        // Get all the compteList where codePCompte equals to UPDATED_CODE_P_COMPTE
        defaultCompteShouldNotBeFound("codePCompte.equals=" + UPDATED_CODE_P_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByCodePCompteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where codePCompte not equals to DEFAULT_CODE_P_COMPTE
        defaultCompteShouldNotBeFound("codePCompte.notEquals=" + DEFAULT_CODE_P_COMPTE);

        // Get all the compteList where codePCompte not equals to UPDATED_CODE_P_COMPTE
        defaultCompteShouldBeFound("codePCompte.notEquals=" + UPDATED_CODE_P_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByCodePCompteIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where codePCompte in DEFAULT_CODE_P_COMPTE or UPDATED_CODE_P_COMPTE
        defaultCompteShouldBeFound("codePCompte.in=" + DEFAULT_CODE_P_COMPTE + "," + UPDATED_CODE_P_COMPTE);

        // Get all the compteList where codePCompte equals to UPDATED_CODE_P_COMPTE
        defaultCompteShouldNotBeFound("codePCompte.in=" + UPDATED_CODE_P_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByCodePCompteIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where codePCompte is not null
        defaultCompteShouldBeFound("codePCompte.specified=true");

        // Get all the compteList where codePCompte is null
        defaultCompteShouldNotBeFound("codePCompte.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByCodePCompteContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where codePCompte contains DEFAULT_CODE_P_COMPTE
        defaultCompteShouldBeFound("codePCompte.contains=" + DEFAULT_CODE_P_COMPTE);

        // Get all the compteList where codePCompte contains UPDATED_CODE_P_COMPTE
        defaultCompteShouldNotBeFound("codePCompte.contains=" + UPDATED_CODE_P_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByCodePCompteNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where codePCompte does not contain DEFAULT_CODE_P_COMPTE
        defaultCompteShouldNotBeFound("codePCompte.doesNotContain=" + DEFAULT_CODE_P_COMPTE);

        // Get all the compteList where codePCompte does not contain UPDATED_CODE_P_COMPTE
        defaultCompteShouldBeFound("codePCompte.doesNotContain=" + UPDATED_CODE_P_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByVilleCompteIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where villeCompte equals to DEFAULT_VILLE_COMPTE
        defaultCompteShouldBeFound("villeCompte.equals=" + DEFAULT_VILLE_COMPTE);

        // Get all the compteList where villeCompte equals to UPDATED_VILLE_COMPTE
        defaultCompteShouldNotBeFound("villeCompte.equals=" + UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByVilleCompteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where villeCompte not equals to DEFAULT_VILLE_COMPTE
        defaultCompteShouldNotBeFound("villeCompte.notEquals=" + DEFAULT_VILLE_COMPTE);

        // Get all the compteList where villeCompte not equals to UPDATED_VILLE_COMPTE
        defaultCompteShouldBeFound("villeCompte.notEquals=" + UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByVilleCompteIsInShouldWork() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where villeCompte in DEFAULT_VILLE_COMPTE or UPDATED_VILLE_COMPTE
        defaultCompteShouldBeFound("villeCompte.in=" + DEFAULT_VILLE_COMPTE + "," + UPDATED_VILLE_COMPTE);

        // Get all the compteList where villeCompte equals to UPDATED_VILLE_COMPTE
        defaultCompteShouldNotBeFound("villeCompte.in=" + UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByVilleCompteIsNullOrNotNull() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where villeCompte is not null
        defaultCompteShouldBeFound("villeCompte.specified=true");

        // Get all the compteList where villeCompte is null
        defaultCompteShouldNotBeFound("villeCompte.specified=false");
    }

    @Test
    @Transactional
    void getAllComptesByVilleCompteContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where villeCompte contains DEFAULT_VILLE_COMPTE
        defaultCompteShouldBeFound("villeCompte.contains=" + DEFAULT_VILLE_COMPTE);

        // Get all the compteList where villeCompte contains UPDATED_VILLE_COMPTE
        defaultCompteShouldNotBeFound("villeCompte.contains=" + UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByVilleCompteNotContainsSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        // Get all the compteList where villeCompte does not contain DEFAULT_VILLE_COMPTE
        defaultCompteShouldNotBeFound("villeCompte.doesNotContain=" + DEFAULT_VILLE_COMPTE);

        // Get all the compteList where villeCompte does not contain UPDATED_VILLE_COMPTE
        defaultCompteShouldBeFound("villeCompte.doesNotContain=" + UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void getAllComptesByPanierIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);
        Panier panier = PanierResourceIT.createEntity(em);
        em.persist(panier);
        em.flush();
        compte.addPanier(panier);
        compteRepository.saveAndFlush(compte);
        Long panierId = panier.getId();

        // Get all the compteList where panier equals to panierId
        defaultCompteShouldBeFound("panierId.equals=" + panierId);

        // Get all the compteList where panier equals to (panierId + 1)
        defaultCompteShouldNotBeFound("panierId.equals=" + (panierId + 1));
    }

    @Test
    @Transactional
    void getAllComptesByCourseIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);
        Course course = CourseResourceIT.createEntity(em);
        em.persist(course);
        em.flush();
        compte.addCourse(course);
        compteRepository.saveAndFlush(compte);
        Long courseId = course.getId();

        // Get all the compteList where course equals to courseId
        defaultCompteShouldBeFound("courseId.equals=" + courseId);

        // Get all the compteList where course equals to (courseId + 1)
        defaultCompteShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    @Test
    @Transactional
    void getAllComptesByMembreIsEqualToSomething() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);
        Cooperative membre = CooperativeResourceIT.createEntity(em);
        em.persist(membre);
        em.flush();
        compte.setMembre(membre);
        compteRepository.saveAndFlush(compte);
        Long membreId = membre.getId();

        // Get all the compteList where membre equals to membreId
        defaultCompteShouldBeFound("membreId.equals=" + membreId);

        // Get all the compteList where membre equals to (membreId + 1)
        defaultCompteShouldNotBeFound("membreId.equals=" + (membreId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCompteShouldBeFound(String filter) throws Exception {
        restCompteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(compte.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].addressCompte").value(hasItem(DEFAULT_ADDRESS_COMPTE)))
            .andExpect(jsonPath("$.[*].codePCompte").value(hasItem(DEFAULT_CODE_P_COMPTE)))
            .andExpect(jsonPath("$.[*].villeCompte").value(hasItem(DEFAULT_VILLE_COMPTE)));

        // Check, that the count call also returns 1
        restCompteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCompteShouldNotBeFound(String filter) throws Exception {
        restCompteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCompteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCompte() throws Exception {
        // Get the compte
        restCompteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCompte() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        int databaseSizeBeforeUpdate = compteRepository.findAll().size();

        // Update the compte
        Compte updatedCompte = compteRepository.findById(compte.getId()).get();
        // Disconnect from session so that the updates on updatedCompte are not directly saved in db
        em.detach(updatedCompte);
        updatedCompte
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .addressCompte(UPDATED_ADDRESS_COMPTE)
            .codePCompte(UPDATED_CODE_P_COMPTE)
            .villeCompte(UPDATED_VILLE_COMPTE);
        CompteDTO compteDTO = compteMapper.toDto(updatedCompte);

        restCompteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
        Compte testCompte = compteList.get(compteList.size() - 1);
        assertThat(testCompte.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testCompte.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testCompte.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCompte.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCompte.getAddressCompte()).isEqualTo(UPDATED_ADDRESS_COMPTE);
        assertThat(testCompte.getCodePCompte()).isEqualTo(UPDATED_CODE_P_COMPTE);
        assertThat(testCompte.getVilleCompte()).isEqualTo(UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void putNonExistingCompte() throws Exception {
        int databaseSizeBeforeUpdate = compteRepository.findAll().size();
        compte.setId(count.incrementAndGet());

        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, compteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCompte() throws Exception {
        int databaseSizeBeforeUpdate = compteRepository.findAll().size();
        compte.setId(count.incrementAndGet());

        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(compteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCompte() throws Exception {
        int databaseSizeBeforeUpdate = compteRepository.findAll().size();
        compte.setId(count.incrementAndGet());

        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(compteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCompteWithPatch() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        int databaseSizeBeforeUpdate = compteRepository.findAll().size();

        // Update the compte using partial update
        Compte partialUpdatedCompte = new Compte();
        partialUpdatedCompte.setId(compte.getId());

        partialUpdatedCompte.prenom(UPDATED_PRENOM).codePCompte(UPDATED_CODE_P_COMPTE).villeCompte(UPDATED_VILLE_COMPTE);

        restCompteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompte.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompte))
            )
            .andExpect(status().isOk());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
        Compte testCompte = compteList.get(compteList.size() - 1);
        assertThat(testCompte.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testCompte.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testCompte.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCompte.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testCompte.getAddressCompte()).isEqualTo(DEFAULT_ADDRESS_COMPTE);
        assertThat(testCompte.getCodePCompte()).isEqualTo(UPDATED_CODE_P_COMPTE);
        assertThat(testCompte.getVilleCompte()).isEqualTo(UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void fullUpdateCompteWithPatch() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        int databaseSizeBeforeUpdate = compteRepository.findAll().size();

        // Update the compte using partial update
        Compte partialUpdatedCompte = new Compte();
        partialUpdatedCompte.setId(compte.getId());

        partialUpdatedCompte
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .addressCompte(UPDATED_ADDRESS_COMPTE)
            .codePCompte(UPDATED_CODE_P_COMPTE)
            .villeCompte(UPDATED_VILLE_COMPTE);

        restCompteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCompte.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCompte))
            )
            .andExpect(status().isOk());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
        Compte testCompte = compteList.get(compteList.size() - 1);
        assertThat(testCompte.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testCompte.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testCompte.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCompte.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testCompte.getAddressCompte()).isEqualTo(UPDATED_ADDRESS_COMPTE);
        assertThat(testCompte.getCodePCompte()).isEqualTo(UPDATED_CODE_P_COMPTE);
        assertThat(testCompte.getVilleCompte()).isEqualTo(UPDATED_VILLE_COMPTE);
    }

    @Test
    @Transactional
    void patchNonExistingCompte() throws Exception {
        int databaseSizeBeforeUpdate = compteRepository.findAll().size();
        compte.setId(count.incrementAndGet());

        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, compteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(compteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCompte() throws Exception {
        int databaseSizeBeforeUpdate = compteRepository.findAll().size();
        compte.setId(count.incrementAndGet());

        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(compteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCompte() throws Exception {
        int databaseSizeBeforeUpdate = compteRepository.findAll().size();
        compte.setId(count.incrementAndGet());

        // Create the Compte
        CompteDTO compteDTO = compteMapper.toDto(compte);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCompteMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(compteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Compte in the database
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCompte() throws Exception {
        // Initialize the database
        compteRepository.saveAndFlush(compte);

        int databaseSizeBeforeDelete = compteRepository.findAll().size();

        // Delete the compte
        restCompteMockMvc
            .perform(delete(ENTITY_API_URL_ID, compte.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Compte> compteList = compteRepository.findAll();
        assertThat(compteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
