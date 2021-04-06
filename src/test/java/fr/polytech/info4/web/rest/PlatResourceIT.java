package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Course;
import fr.polytech.info4.domain.Panier;
import fr.polytech.info4.domain.Plat;
import fr.polytech.info4.domain.Restaurant;
import fr.polytech.info4.repository.PlatRepository;
import fr.polytech.info4.service.PlatService;
import fr.polytech.info4.service.criteria.PlatCriteria;
import fr.polytech.info4.service.dto.PlatDTO;
import fr.polytech.info4.service.mapper.PlatMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PlatResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PlatResourceIT {

    private static final String DEFAULT_NOM_PLAT = "AAAAAAAAAA";
    private static final String UPDATED_NOM_PLAT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_PRIX = "AAAAAAAAAA";
    private static final String UPDATED_PRIX = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/plats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlatRepository platRepository;

    @Mock
    private PlatRepository platRepositoryMock;

    @Autowired
    private PlatMapper platMapper;

    @Mock
    private PlatService platServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlatMockMvc;

    private Plat plat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plat createEntity(EntityManager em) {
        Plat plat = new Plat()
            .nomPlat(DEFAULT_NOM_PLAT)
            .description(DEFAULT_DESCRIPTION)
            .prix(DEFAULT_PRIX)
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE);
        return plat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plat createUpdatedEntity(EntityManager em) {
        Plat plat = new Plat()
            .nomPlat(UPDATED_NOM_PLAT)
            .description(UPDATED_DESCRIPTION)
            .prix(UPDATED_PRIX)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);
        return plat;
    }

    @BeforeEach
    public void initTest() {
        plat = createEntity(em);
    }

    @Test
    @Transactional
    void createPlat() throws Exception {
        int databaseSizeBeforeCreate = platRepository.findAll().size();
        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);
        restPlatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(platDTO)))
            .andExpect(status().isCreated());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeCreate + 1);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(DEFAULT_NOM_PLAT);
        assertThat(testPlat.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPlat.getPrix()).isEqualTo(DEFAULT_PRIX);
        assertThat(testPlat.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testPlat.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createPlatWithExistingId() throws Exception {
        // Create the Plat with an existing ID
        plat.setId(1L);
        PlatDTO platDTO = platMapper.toDto(plat);

        int databaseSizeBeforeCreate = platRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(platDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPrixIsRequired() throws Exception {
        int databaseSizeBeforeTest = platRepository.findAll().size();
        // set the field null
        plat.setPrix(null);

        // Create the Plat, which fails.
        PlatDTO platDTO = platMapper.toDto(plat);

        restPlatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(platDTO)))
            .andExpect(status().isBadRequest());

        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPlats() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomPlat").value(hasItem(DEFAULT_NOM_PLAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlatsWithEagerRelationshipsIsEnabled() throws Exception {
        when(platServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(platServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPlatsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(platServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPlatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(platServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getPlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get the plat
        restPlatMockMvc
            .perform(get(ENTITY_API_URL_ID, plat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plat.getId().intValue()))
            .andExpect(jsonPath("$.nomPlat").value(DEFAULT_NOM_PLAT))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.prix").value(DEFAULT_PRIX))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)));
    }

    @Test
    @Transactional
    void getPlatsByIdFiltering() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        Long id = plat.getId();

        defaultPlatShouldBeFound("id.equals=" + id);
        defaultPlatShouldNotBeFound("id.notEquals=" + id);

        defaultPlatShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPlatShouldNotBeFound("id.greaterThan=" + id);

        defaultPlatShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPlatShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat equals to DEFAULT_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.equals=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat equals to UPDATED_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.equals=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat not equals to DEFAULT_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.notEquals=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat not equals to UPDATED_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.notEquals=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsInShouldWork() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat in DEFAULT_NOM_PLAT or UPDATED_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.in=" + DEFAULT_NOM_PLAT + "," + UPDATED_NOM_PLAT);

        // Get all the platList where nomPlat equals to UPDATED_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.in=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsNullOrNotNull() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat is not null
        defaultPlatShouldBeFound("nomPlat.specified=true");

        // Get all the platList where nomPlat is null
        defaultPlatShouldNotBeFound("nomPlat.specified=false");
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatContainsSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat contains DEFAULT_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.contains=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat contains UPDATED_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.contains=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatNotContainsSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat does not contain DEFAULT_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.doesNotContain=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat does not contain UPDATED_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.doesNotContain=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix equals to DEFAULT_PRIX
        defaultPlatShouldBeFound("prix.equals=" + DEFAULT_PRIX);

        // Get all the platList where prix equals to UPDATED_PRIX
        defaultPlatShouldNotBeFound("prix.equals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsNotEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix not equals to DEFAULT_PRIX
        defaultPlatShouldNotBeFound("prix.notEquals=" + DEFAULT_PRIX);

        // Get all the platList where prix not equals to UPDATED_PRIX
        defaultPlatShouldBeFound("prix.notEquals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsInShouldWork() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix in DEFAULT_PRIX or UPDATED_PRIX
        defaultPlatShouldBeFound("prix.in=" + DEFAULT_PRIX + "," + UPDATED_PRIX);

        // Get all the platList where prix equals to UPDATED_PRIX
        defaultPlatShouldNotBeFound("prix.in=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix is not null
        defaultPlatShouldBeFound("prix.specified=true");

        // Get all the platList where prix is null
        defaultPlatShouldNotBeFound("prix.specified=false");
    }

    @Test
    @Transactional
    void getAllPlatsByPrixContainsSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix contains DEFAULT_PRIX
        defaultPlatShouldBeFound("prix.contains=" + DEFAULT_PRIX);

        // Get all the platList where prix contains UPDATED_PRIX
        defaultPlatShouldNotBeFound("prix.contains=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixNotContainsSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix does not contain DEFAULT_PRIX
        defaultPlatShouldNotBeFound("prix.doesNotContain=" + DEFAULT_PRIX);

        // Get all the platList where prix does not contain UPDATED_PRIX
        defaultPlatShouldBeFound("prix.doesNotContain=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByRestaurantIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);
        Restaurant restaurant = RestaurantResourceIT.createEntity(em);
        em.persist(restaurant);
        em.flush();
        plat.addRestaurant(restaurant);
        platRepository.saveAndFlush(plat);
        Long restaurantId = restaurant.getId();

        // Get all the platList where restaurant equals to restaurantId
        defaultPlatShouldBeFound("restaurantId.equals=" + restaurantId);

        // Get all the platList where restaurant equals to (restaurantId + 1)
        defaultPlatShouldNotBeFound("restaurantId.equals=" + (restaurantId + 1));
    }

    @Test
    @Transactional
    void getAllPlatsByPanierIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);
        Panier panier = PanierResourceIT.createEntity(em);
        em.persist(panier);
        em.flush();
        plat.addPanier(panier);
        platRepository.saveAndFlush(plat);
        Long panierId = panier.getId();

        // Get all the platList where panier equals to panierId
        defaultPlatShouldBeFound("panierId.equals=" + panierId);

        // Get all the platList where panier equals to (panierId + 1)
        defaultPlatShouldNotBeFound("panierId.equals=" + (panierId + 1));
    }

    @Test
    @Transactional
    void getAllPlatsByCourseIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);
        Course course = CourseResourceIT.createEntity(em);
        em.persist(course);
        em.flush();
        plat.addCourse(course);
        platRepository.saveAndFlush(plat);
        Long courseId = course.getId();

        // Get all the platList where course equals to courseId
        defaultPlatShouldBeFound("courseId.equals=" + courseId);

        // Get all the platList where course equals to (courseId + 1)
        defaultPlatShouldNotBeFound("courseId.equals=" + (courseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPlatShouldBeFound(String filter) throws Exception {
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomPlat").value(hasItem(DEFAULT_NOM_PLAT)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX)))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))));

        // Check, that the count call also returns 1
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPlatShouldNotBeFound(String filter) throws Exception {
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPlat() throws Exception {
        // Get the plat
        restPlatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat
        Plat updatedPlat = platRepository.findById(plat.getId()).get();
        // Disconnect from session so that the updates on updatedPlat are not directly saved in db
        em.detach(updatedPlat);
        updatedPlat
            .nomPlat(UPDATED_NOM_PLAT)
            .description(UPDATED_DESCRIPTION)
            .prix(UPDATED_PRIX)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);
        PlatDTO platDTO = platMapper.toDto(updatedPlat);

        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, platDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(UPDATED_NOM_PLAT);
        assertThat(testPlat.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPlat.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testPlat.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testPlat.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, platDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(platDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlatWithPatch() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat using partial update
        Plat partialUpdatedPlat = new Plat();
        partialUpdatedPlat.setId(plat.getId());

        partialUpdatedPlat.prix(UPDATED_PRIX).photo(UPDATED_PHOTO).photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(DEFAULT_NOM_PLAT);
        assertThat(testPlat.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPlat.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testPlat.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testPlat.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdatePlatWithPatch() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat using partial update
        Plat partialUpdatedPlat = new Plat();
        partialUpdatedPlat.setId(plat.getId());

        partialUpdatedPlat
            .nomPlat(UPDATED_NOM_PLAT)
            .description(UPDATED_DESCRIPTION)
            .prix(UPDATED_PRIX)
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE);

        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(UPDATED_NOM_PLAT);
        assertThat(testPlat.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPlat.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testPlat.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testPlat.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, platDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(platDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeDelete = platRepository.findAll().size();

        // Delete the plat
        restPlatMockMvc
            .perform(delete(ENTITY_API_URL_ID, plat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
