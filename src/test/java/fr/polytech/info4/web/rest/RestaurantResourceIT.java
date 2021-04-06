package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Plat;
import fr.polytech.info4.domain.Restaurant;
import fr.polytech.info4.repository.RestaurantRepository;
import fr.polytech.info4.service.criteria.RestaurantCriteria;
import fr.polytech.info4.service.dto.RestaurantDTO;
import fr.polytech.info4.service.mapper.RestaurantMapper;
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
 * Integration tests for the {@link RestaurantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RestaurantResourceIT {

    private static final String DEFAULT_NOM_RESTO = "AAAAAAAAAA";
    private static final String UPDATED_NOM_RESTO = "BBBBBBBBBB";

    private static final String DEFAULT_FRAIS_LIVRAISON = "AAAAAAAAAA";
    private static final String UPDATED_FRAIS_LIVRAISON = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE_RESTO = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_RESTO = "BBBBBBBBBB";

    private static final String DEFAULT_CODE_P_RESTO = "AAAAA";
    private static final String UPDATED_CODE_P_RESTO = "BBBBB";

    private static final String DEFAULT_VILLE_RESTO = "AAAAAAAAAA";
    private static final String UPDATED_VILLE_RESTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/restaurants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRestaurantMockMvc;

    private Restaurant restaurant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant()
            .nomResto(DEFAULT_NOM_RESTO)
            .fraisLivraison(DEFAULT_FRAIS_LIVRAISON)
            .adresseResto(DEFAULT_ADRESSE_RESTO)
            .codePResto(DEFAULT_CODE_P_RESTO)
            .villeResto(DEFAULT_VILLE_RESTO);
        return restaurant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createUpdatedEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant()
            .nomResto(UPDATED_NOM_RESTO)
            .fraisLivraison(UPDATED_FRAIS_LIVRAISON)
            .adresseResto(UPDATED_ADRESSE_RESTO)
            .codePResto(UPDATED_CODE_P_RESTO)
            .villeResto(UPDATED_VILLE_RESTO);
        return restaurant;
    }

    @BeforeEach
    public void initTest() {
        restaurant = createEntity(em);
    }

    @Test
    @Transactional
    void createRestaurant() throws Exception {
        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();
        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);
        restRestaurantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(restaurantDTO)))
            .andExpect(status().isCreated());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate + 1);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomResto()).isEqualTo(DEFAULT_NOM_RESTO);
        assertThat(testRestaurant.getFraisLivraison()).isEqualTo(DEFAULT_FRAIS_LIVRAISON);
        assertThat(testRestaurant.getAdresseResto()).isEqualTo(DEFAULT_ADRESSE_RESTO);
        assertThat(testRestaurant.getCodePResto()).isEqualTo(DEFAULT_CODE_P_RESTO);
        assertThat(testRestaurant.getVilleResto()).isEqualTo(DEFAULT_VILLE_RESTO);
    }

    @Test
    @Transactional
    void createRestaurantWithExistingId() throws Exception {
        // Create the Restaurant with an existing ID
        restaurant.setId(1L);
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(restaurantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRestaurants() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomResto").value(hasItem(DEFAULT_NOM_RESTO)))
            .andExpect(jsonPath("$.[*].fraisLivraison").value(hasItem(DEFAULT_FRAIS_LIVRAISON)))
            .andExpect(jsonPath("$.[*].adresseResto").value(hasItem(DEFAULT_ADRESSE_RESTO)))
            .andExpect(jsonPath("$.[*].codePResto").value(hasItem(DEFAULT_CODE_P_RESTO)))
            .andExpect(jsonPath("$.[*].villeResto").value(hasItem(DEFAULT_VILLE_RESTO)));
    }

    @Test
    @Transactional
    void getRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get the restaurant
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL_ID, restaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(restaurant.getId().intValue()))
            .andExpect(jsonPath("$.nomResto").value(DEFAULT_NOM_RESTO))
            .andExpect(jsonPath("$.fraisLivraison").value(DEFAULT_FRAIS_LIVRAISON))
            .andExpect(jsonPath("$.adresseResto").value(DEFAULT_ADRESSE_RESTO))
            .andExpect(jsonPath("$.codePResto").value(DEFAULT_CODE_P_RESTO))
            .andExpect(jsonPath("$.villeResto").value(DEFAULT_VILLE_RESTO));
    }

    @Test
    @Transactional
    void getRestaurantsByIdFiltering() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        Long id = restaurant.getId();

        defaultRestaurantShouldBeFound("id.equals=" + id);
        defaultRestaurantShouldNotBeFound("id.notEquals=" + id);

        defaultRestaurantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRestaurantShouldNotBeFound("id.greaterThan=" + id);

        defaultRestaurantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRestaurantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestoIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomResto equals to DEFAULT_NOM_RESTO
        defaultRestaurantShouldBeFound("nomResto.equals=" + DEFAULT_NOM_RESTO);

        // Get all the restaurantList where nomResto equals to UPDATED_NOM_RESTO
        defaultRestaurantShouldNotBeFound("nomResto.equals=" + UPDATED_NOM_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomResto not equals to DEFAULT_NOM_RESTO
        defaultRestaurantShouldNotBeFound("nomResto.notEquals=" + DEFAULT_NOM_RESTO);

        // Get all the restaurantList where nomResto not equals to UPDATED_NOM_RESTO
        defaultRestaurantShouldBeFound("nomResto.notEquals=" + UPDATED_NOM_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestoIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomResto in DEFAULT_NOM_RESTO or UPDATED_NOM_RESTO
        defaultRestaurantShouldBeFound("nomResto.in=" + DEFAULT_NOM_RESTO + "," + UPDATED_NOM_RESTO);

        // Get all the restaurantList where nomResto equals to UPDATED_NOM_RESTO
        defaultRestaurantShouldNotBeFound("nomResto.in=" + UPDATED_NOM_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestoIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomResto is not null
        defaultRestaurantShouldBeFound("nomResto.specified=true");

        // Get all the restaurantList where nomResto is null
        defaultRestaurantShouldNotBeFound("nomResto.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestoContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomResto contains DEFAULT_NOM_RESTO
        defaultRestaurantShouldBeFound("nomResto.contains=" + DEFAULT_NOM_RESTO);

        // Get all the restaurantList where nomResto contains UPDATED_NOM_RESTO
        defaultRestaurantShouldNotBeFound("nomResto.contains=" + UPDATED_NOM_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestoNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomResto does not contain DEFAULT_NOM_RESTO
        defaultRestaurantShouldNotBeFound("nomResto.doesNotContain=" + DEFAULT_NOM_RESTO);

        // Get all the restaurantList where nomResto does not contain UPDATED_NOM_RESTO
        defaultRestaurantShouldBeFound("nomResto.doesNotContain=" + UPDATED_NOM_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByFraisLivraisonIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where fraisLivraison equals to DEFAULT_FRAIS_LIVRAISON
        defaultRestaurantShouldBeFound("fraisLivraison.equals=" + DEFAULT_FRAIS_LIVRAISON);

        // Get all the restaurantList where fraisLivraison equals to UPDATED_FRAIS_LIVRAISON
        defaultRestaurantShouldNotBeFound("fraisLivraison.equals=" + UPDATED_FRAIS_LIVRAISON);
    }

    @Test
    @Transactional
    void getAllRestaurantsByFraisLivraisonIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where fraisLivraison not equals to DEFAULT_FRAIS_LIVRAISON
        defaultRestaurantShouldNotBeFound("fraisLivraison.notEquals=" + DEFAULT_FRAIS_LIVRAISON);

        // Get all the restaurantList where fraisLivraison not equals to UPDATED_FRAIS_LIVRAISON
        defaultRestaurantShouldBeFound("fraisLivraison.notEquals=" + UPDATED_FRAIS_LIVRAISON);
    }

    @Test
    @Transactional
    void getAllRestaurantsByFraisLivraisonIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where fraisLivraison in DEFAULT_FRAIS_LIVRAISON or UPDATED_FRAIS_LIVRAISON
        defaultRestaurantShouldBeFound("fraisLivraison.in=" + DEFAULT_FRAIS_LIVRAISON + "," + UPDATED_FRAIS_LIVRAISON);

        // Get all the restaurantList where fraisLivraison equals to UPDATED_FRAIS_LIVRAISON
        defaultRestaurantShouldNotBeFound("fraisLivraison.in=" + UPDATED_FRAIS_LIVRAISON);
    }

    @Test
    @Transactional
    void getAllRestaurantsByFraisLivraisonIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where fraisLivraison is not null
        defaultRestaurantShouldBeFound("fraisLivraison.specified=true");

        // Get all the restaurantList where fraisLivraison is null
        defaultRestaurantShouldNotBeFound("fraisLivraison.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByFraisLivraisonContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where fraisLivraison contains DEFAULT_FRAIS_LIVRAISON
        defaultRestaurantShouldBeFound("fraisLivraison.contains=" + DEFAULT_FRAIS_LIVRAISON);

        // Get all the restaurantList where fraisLivraison contains UPDATED_FRAIS_LIVRAISON
        defaultRestaurantShouldNotBeFound("fraisLivraison.contains=" + UPDATED_FRAIS_LIVRAISON);
    }

    @Test
    @Transactional
    void getAllRestaurantsByFraisLivraisonNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where fraisLivraison does not contain DEFAULT_FRAIS_LIVRAISON
        defaultRestaurantShouldNotBeFound("fraisLivraison.doesNotContain=" + DEFAULT_FRAIS_LIVRAISON);

        // Get all the restaurantList where fraisLivraison does not contain UPDATED_FRAIS_LIVRAISON
        defaultRestaurantShouldBeFound("fraisLivraison.doesNotContain=" + UPDATED_FRAIS_LIVRAISON);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestoIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseResto equals to DEFAULT_ADRESSE_RESTO
        defaultRestaurantShouldBeFound("adresseResto.equals=" + DEFAULT_ADRESSE_RESTO);

        // Get all the restaurantList where adresseResto equals to UPDATED_ADRESSE_RESTO
        defaultRestaurantShouldNotBeFound("adresseResto.equals=" + UPDATED_ADRESSE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseResto not equals to DEFAULT_ADRESSE_RESTO
        defaultRestaurantShouldNotBeFound("adresseResto.notEquals=" + DEFAULT_ADRESSE_RESTO);

        // Get all the restaurantList where adresseResto not equals to UPDATED_ADRESSE_RESTO
        defaultRestaurantShouldBeFound("adresseResto.notEquals=" + UPDATED_ADRESSE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestoIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseResto in DEFAULT_ADRESSE_RESTO or UPDATED_ADRESSE_RESTO
        defaultRestaurantShouldBeFound("adresseResto.in=" + DEFAULT_ADRESSE_RESTO + "," + UPDATED_ADRESSE_RESTO);

        // Get all the restaurantList where adresseResto equals to UPDATED_ADRESSE_RESTO
        defaultRestaurantShouldNotBeFound("adresseResto.in=" + UPDATED_ADRESSE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestoIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseResto is not null
        defaultRestaurantShouldBeFound("adresseResto.specified=true");

        // Get all the restaurantList where adresseResto is null
        defaultRestaurantShouldNotBeFound("adresseResto.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestoContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseResto contains DEFAULT_ADRESSE_RESTO
        defaultRestaurantShouldBeFound("adresseResto.contains=" + DEFAULT_ADRESSE_RESTO);

        // Get all the restaurantList where adresseResto contains UPDATED_ADRESSE_RESTO
        defaultRestaurantShouldNotBeFound("adresseResto.contains=" + UPDATED_ADRESSE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestoNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseResto does not contain DEFAULT_ADRESSE_RESTO
        defaultRestaurantShouldNotBeFound("adresseResto.doesNotContain=" + DEFAULT_ADRESSE_RESTO);

        // Get all the restaurantList where adresseResto does not contain UPDATED_ADRESSE_RESTO
        defaultRestaurantShouldBeFound("adresseResto.doesNotContain=" + UPDATED_ADRESSE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByCodePRestoIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where codePResto equals to DEFAULT_CODE_P_RESTO
        defaultRestaurantShouldBeFound("codePResto.equals=" + DEFAULT_CODE_P_RESTO);

        // Get all the restaurantList where codePResto equals to UPDATED_CODE_P_RESTO
        defaultRestaurantShouldNotBeFound("codePResto.equals=" + UPDATED_CODE_P_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByCodePRestoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where codePResto not equals to DEFAULT_CODE_P_RESTO
        defaultRestaurantShouldNotBeFound("codePResto.notEquals=" + DEFAULT_CODE_P_RESTO);

        // Get all the restaurantList where codePResto not equals to UPDATED_CODE_P_RESTO
        defaultRestaurantShouldBeFound("codePResto.notEquals=" + UPDATED_CODE_P_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByCodePRestoIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where codePResto in DEFAULT_CODE_P_RESTO or UPDATED_CODE_P_RESTO
        defaultRestaurantShouldBeFound("codePResto.in=" + DEFAULT_CODE_P_RESTO + "," + UPDATED_CODE_P_RESTO);

        // Get all the restaurantList where codePResto equals to UPDATED_CODE_P_RESTO
        defaultRestaurantShouldNotBeFound("codePResto.in=" + UPDATED_CODE_P_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByCodePRestoIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where codePResto is not null
        defaultRestaurantShouldBeFound("codePResto.specified=true");

        // Get all the restaurantList where codePResto is null
        defaultRestaurantShouldNotBeFound("codePResto.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByCodePRestoContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where codePResto contains DEFAULT_CODE_P_RESTO
        defaultRestaurantShouldBeFound("codePResto.contains=" + DEFAULT_CODE_P_RESTO);

        // Get all the restaurantList where codePResto contains UPDATED_CODE_P_RESTO
        defaultRestaurantShouldNotBeFound("codePResto.contains=" + UPDATED_CODE_P_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByCodePRestoNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where codePResto does not contain DEFAULT_CODE_P_RESTO
        defaultRestaurantShouldNotBeFound("codePResto.doesNotContain=" + DEFAULT_CODE_P_RESTO);

        // Get all the restaurantList where codePResto does not contain UPDATED_CODE_P_RESTO
        defaultRestaurantShouldBeFound("codePResto.doesNotContain=" + UPDATED_CODE_P_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByVilleRestoIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where villeResto equals to DEFAULT_VILLE_RESTO
        defaultRestaurantShouldBeFound("villeResto.equals=" + DEFAULT_VILLE_RESTO);

        // Get all the restaurantList where villeResto equals to UPDATED_VILLE_RESTO
        defaultRestaurantShouldNotBeFound("villeResto.equals=" + UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByVilleRestoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where villeResto not equals to DEFAULT_VILLE_RESTO
        defaultRestaurantShouldNotBeFound("villeResto.notEquals=" + DEFAULT_VILLE_RESTO);

        // Get all the restaurantList where villeResto not equals to UPDATED_VILLE_RESTO
        defaultRestaurantShouldBeFound("villeResto.notEquals=" + UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByVilleRestoIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where villeResto in DEFAULT_VILLE_RESTO or UPDATED_VILLE_RESTO
        defaultRestaurantShouldBeFound("villeResto.in=" + DEFAULT_VILLE_RESTO + "," + UPDATED_VILLE_RESTO);

        // Get all the restaurantList where villeResto equals to UPDATED_VILLE_RESTO
        defaultRestaurantShouldNotBeFound("villeResto.in=" + UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByVilleRestoIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where villeResto is not null
        defaultRestaurantShouldBeFound("villeResto.specified=true");

        // Get all the restaurantList where villeResto is null
        defaultRestaurantShouldNotBeFound("villeResto.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByVilleRestoContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where villeResto contains DEFAULT_VILLE_RESTO
        defaultRestaurantShouldBeFound("villeResto.contains=" + DEFAULT_VILLE_RESTO);

        // Get all the restaurantList where villeResto contains UPDATED_VILLE_RESTO
        defaultRestaurantShouldNotBeFound("villeResto.contains=" + UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByVilleRestoNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where villeResto does not contain DEFAULT_VILLE_RESTO
        defaultRestaurantShouldNotBeFound("villeResto.doesNotContain=" + DEFAULT_VILLE_RESTO);

        // Get all the restaurantList where villeResto does not contain UPDATED_VILLE_RESTO
        defaultRestaurantShouldBeFound("villeResto.doesNotContain=" + UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void getAllRestaurantsByPlatIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);
        Plat plat = PlatResourceIT.createEntity(em);
        em.persist(plat);
        em.flush();
        restaurant.addPlat(plat);
        restaurantRepository.saveAndFlush(restaurant);
        Long platId = plat.getId();

        // Get all the restaurantList where plat equals to platId
        defaultRestaurantShouldBeFound("platId.equals=" + platId);

        // Get all the restaurantList where plat equals to (platId + 1)
        defaultRestaurantShouldNotBeFound("platId.equals=" + (platId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRestaurantShouldBeFound(String filter) throws Exception {
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomResto").value(hasItem(DEFAULT_NOM_RESTO)))
            .andExpect(jsonPath("$.[*].fraisLivraison").value(hasItem(DEFAULT_FRAIS_LIVRAISON)))
            .andExpect(jsonPath("$.[*].adresseResto").value(hasItem(DEFAULT_ADRESSE_RESTO)))
            .andExpect(jsonPath("$.[*].codePResto").value(hasItem(DEFAULT_CODE_P_RESTO)))
            .andExpect(jsonPath("$.[*].villeResto").value(hasItem(DEFAULT_VILLE_RESTO)));

        // Check, that the count call also returns 1
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRestaurantShouldNotBeFound(String filter) throws Exception {
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRestaurant() throws Exception {
        // Get the restaurant
        restRestaurantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant
        Restaurant updatedRestaurant = restaurantRepository.findById(restaurant.getId()).get();
        // Disconnect from session so that the updates on updatedRestaurant are not directly saved in db
        em.detach(updatedRestaurant);
        updatedRestaurant
            .nomResto(UPDATED_NOM_RESTO)
            .fraisLivraison(UPDATED_FRAIS_LIVRAISON)
            .adresseResto(UPDATED_ADRESSE_RESTO)
            .codePResto(UPDATED_CODE_P_RESTO)
            .villeResto(UPDATED_VILLE_RESTO);
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(updatedRestaurant);

        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomResto()).isEqualTo(UPDATED_NOM_RESTO);
        assertThat(testRestaurant.getFraisLivraison()).isEqualTo(UPDATED_FRAIS_LIVRAISON);
        assertThat(testRestaurant.getAdresseResto()).isEqualTo(UPDATED_ADRESSE_RESTO);
        assertThat(testRestaurant.getCodePResto()).isEqualTo(UPDATED_CODE_P_RESTO);
        assertThat(testRestaurant.getVilleResto()).isEqualTo(UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void putNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(restaurantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        partialUpdatedRestaurant.villeResto(UPDATED_VILLE_RESTO);

        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurant))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomResto()).isEqualTo(DEFAULT_NOM_RESTO);
        assertThat(testRestaurant.getFraisLivraison()).isEqualTo(DEFAULT_FRAIS_LIVRAISON);
        assertThat(testRestaurant.getAdresseResto()).isEqualTo(DEFAULT_ADRESSE_RESTO);
        assertThat(testRestaurant.getCodePResto()).isEqualTo(DEFAULT_CODE_P_RESTO);
        assertThat(testRestaurant.getVilleResto()).isEqualTo(UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void fullUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        partialUpdatedRestaurant
            .nomResto(UPDATED_NOM_RESTO)
            .fraisLivraison(UPDATED_FRAIS_LIVRAISON)
            .adresseResto(UPDATED_ADRESSE_RESTO)
            .codePResto(UPDATED_CODE_P_RESTO)
            .villeResto(UPDATED_VILLE_RESTO);

        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurant))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomResto()).isEqualTo(UPDATED_NOM_RESTO);
        assertThat(testRestaurant.getFraisLivraison()).isEqualTo(UPDATED_FRAIS_LIVRAISON);
        assertThat(testRestaurant.getAdresseResto()).isEqualTo(UPDATED_ADRESSE_RESTO);
        assertThat(testRestaurant.getCodePResto()).isEqualTo(UPDATED_CODE_P_RESTO);
        assertThat(testRestaurant.getVilleResto()).isEqualTo(UPDATED_VILLE_RESTO);
    }

    @Test
    @Transactional
    void patchNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, restaurantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeDelete = restaurantRepository.findAll().size();

        // Delete the restaurant
        restRestaurantMockMvc
            .perform(delete(ENTITY_API_URL_ID, restaurant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
