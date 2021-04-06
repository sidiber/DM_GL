package fr.polytech.info4.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.polytech.info4.IntegrationTest;
import fr.polytech.info4.domain.Compte;
import fr.polytech.info4.domain.Course;
import fr.polytech.info4.domain.Panier;
import fr.polytech.info4.domain.Plat;
import fr.polytech.info4.domain.enumeration.EtatCourse;
import fr.polytech.info4.repository.CourseRepository;
import fr.polytech.info4.service.CourseService;
import fr.polytech.info4.service.criteria.CourseCriteria;
import fr.polytech.info4.service.dto.CourseDTO;
import fr.polytech.info4.service.mapper.CourseMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

/**
 * Integration tests for the {@link CourseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CourseResourceIT {

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EtatCourse DEFAULT_ETAT = EtatCourse.CREE;
    private static final EtatCourse UPDATED_ETAT = EtatCourse.TRAITEMENT;

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/courses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CourseRepository courseRepository;

    @Mock
    private CourseRepository courseRepositoryMock;

    @Autowired
    private CourseMapper courseMapper;

    @Mock
    private CourseService courseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCourseMockMvc;

    private Course course;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createEntity(EntityManager em) {
        Course course = new Course()
            .createdAt(DEFAULT_CREATED_AT)
            .etat(DEFAULT_ETAT)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME);
        return course;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Course createUpdatedEntity(EntityManager em) {
        Course course = new Course()
            .createdAt(UPDATED_CREATED_AT)
            .etat(UPDATED_ETAT)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        return course;
    }

    @BeforeEach
    public void initTest() {
        course = createEntity(em);
    }

    @Test
    @Transactional
    void createCourse() throws Exception {
        int databaseSizeBeforeCreate = courseRepository.findAll().size();
        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isCreated());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate + 1);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCourse.getEtat()).isEqualTo(DEFAULT_ETAT);
        assertThat(testCourse.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testCourse.getEndTime()).isEqualTo(DEFAULT_END_TIME);
    }

    @Test
    @Transactional
    void createCourseWithExistingId() throws Exception {
        // Create the Course with an existing ID
        course.setId(1L);
        CourseDTO courseDTO = courseMapper.toDto(course);

        int databaseSizeBeforeCreate = courseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = courseRepository.findAll().size();
        // set the field null
        course.setCreatedAt(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = courseRepository.findAll().size();
        // set the field null
        course.setStartTime(null);

        // Create the Course, which fails.
        CourseDTO courseDTO = courseMapper.toDto(course);

        restCourseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isBadRequest());

        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCourses() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCoursesWithEagerRelationshipsIsEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCoursesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(courseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCourseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(courseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get the course
        restCourseMockMvc
            .perform(get(ENTITY_API_URL_ID, course.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(course.getId().intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.etat").value(DEFAULT_ETAT.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }

    @Test
    @Transactional
    void getCoursesByIdFiltering() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        Long id = course.getId();

        defaultCourseShouldBeFound("id.equals=" + id);
        defaultCourseShouldNotBeFound("id.notEquals=" + id);

        defaultCourseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCourseShouldNotBeFound("id.greaterThan=" + id);

        defaultCourseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCourseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt equals to DEFAULT_CREATED_AT
        defaultCourseShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the courseList where createdAt equals to UPDATED_CREATED_AT
        defaultCourseShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt not equals to DEFAULT_CREATED_AT
        defaultCourseShouldNotBeFound("createdAt.notEquals=" + DEFAULT_CREATED_AT);

        // Get all the courseList where createdAt not equals to UPDATED_CREATED_AT
        defaultCourseShouldBeFound("createdAt.notEquals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultCourseShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the courseList where createdAt equals to UPDATED_CREATED_AT
        defaultCourseShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllCoursesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where createdAt is not null
        defaultCourseShouldBeFound("createdAt.specified=true");

        // Get all the courseList where createdAt is null
        defaultCourseShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByEtatIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where etat equals to DEFAULT_ETAT
        defaultCourseShouldBeFound("etat.equals=" + DEFAULT_ETAT);

        // Get all the courseList where etat equals to UPDATED_ETAT
        defaultCourseShouldNotBeFound("etat.equals=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCoursesByEtatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where etat not equals to DEFAULT_ETAT
        defaultCourseShouldNotBeFound("etat.notEquals=" + DEFAULT_ETAT);

        // Get all the courseList where etat not equals to UPDATED_ETAT
        defaultCourseShouldBeFound("etat.notEquals=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCoursesByEtatIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where etat in DEFAULT_ETAT or UPDATED_ETAT
        defaultCourseShouldBeFound("etat.in=" + DEFAULT_ETAT + "," + UPDATED_ETAT);

        // Get all the courseList where etat equals to UPDATED_ETAT
        defaultCourseShouldNotBeFound("etat.in=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCoursesByEtatIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where etat is not null
        defaultCourseShouldBeFound("etat.specified=true");

        // Get all the courseList where etat is null
        defaultCourseShouldNotBeFound("etat.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startTime equals to DEFAULT_START_TIME
        defaultCourseShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the courseList where startTime equals to UPDATED_START_TIME
        defaultCourseShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCoursesByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startTime not equals to DEFAULT_START_TIME
        defaultCourseShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the courseList where startTime not equals to UPDATED_START_TIME
        defaultCourseShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCoursesByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultCourseShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the courseList where startTime equals to UPDATED_START_TIME
        defaultCourseShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCoursesByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where startTime is not null
        defaultCourseShouldBeFound("startTime.specified=true");

        // Get all the courseList where startTime is null
        defaultCourseShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endTime equals to DEFAULT_END_TIME
        defaultCourseShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the courseList where endTime equals to UPDATED_END_TIME
        defaultCourseShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCoursesByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endTime not equals to DEFAULT_END_TIME
        defaultCourseShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the courseList where endTime not equals to UPDATED_END_TIME
        defaultCourseShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCoursesByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultCourseShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the courseList where endTime equals to UPDATED_END_TIME
        defaultCourseShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCoursesByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        // Get all the courseList where endTime is not null
        defaultCourseShouldBeFound("endTime.specified=true");

        // Get all the courseList where endTime is null
        defaultCourseShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCoursesByMontantIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);
        Panier montant = PanierResourceIT.createEntity(em);
        em.persist(montant);
        em.flush();
        course.setMontant(montant);
        courseRepository.saveAndFlush(course);
        Long montantId = montant.getId();

        // Get all the courseList where montant equals to montantId
        defaultCourseShouldBeFound("montantId.equals=" + montantId);

        // Get all the courseList where montant equals to (montantId + 1)
        defaultCourseShouldNotBeFound("montantId.equals=" + (montantId + 1));
    }

    @Test
    @Transactional
    void getAllCoursesByPlatIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);
        Plat plat = PlatResourceIT.createEntity(em);
        em.persist(plat);
        em.flush();
        course.addPlat(plat);
        courseRepository.saveAndFlush(course);
        Long platId = plat.getId();

        // Get all the courseList where plat equals to platId
        defaultCourseShouldBeFound("platId.equals=" + platId);

        // Get all the courseList where plat equals to (platId + 1)
        defaultCourseShouldNotBeFound("platId.equals=" + (platId + 1));
    }

    @Test
    @Transactional
    void getAllCoursesByEstlivreIsEqualToSomething() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);
        Compte estlivre = CompteResourceIT.createEntity(em);
        em.persist(estlivre);
        em.flush();
        course.setEstlivre(estlivre);
        courseRepository.saveAndFlush(course);
        Long estlivreId = estlivre.getId();

        // Get all the courseList where estlivre equals to estlivreId
        defaultCourseShouldBeFound("estlivreId.equals=" + estlivreId);

        // Get all the courseList where estlivre equals to (estlivreId + 1)
        defaultCourseShouldNotBeFound("estlivreId.equals=" + (estlivreId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCourseShouldBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(course.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));

        // Check, that the count call also returns 1
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCourseShouldNotBeFound(String filter) throws Exception {
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCourseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCourse() throws Exception {
        // Get the course
        restCourseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course
        Course updatedCourse = courseRepository.findById(course.getId()).get();
        // Disconnect from session so that the updates on updatedCourse are not directly saved in db
        em.detach(updatedCourse);
        updatedCourse.createdAt(UPDATED_CREATED_AT).etat(UPDATED_ETAT).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        CourseDTO courseDTO = courseMapper.toDto(updatedCourse);

        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCourse.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCourse.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCourse.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void putNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(courseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse.createdAt(UPDATED_CREATED_AT).etat(UPDATED_ETAT).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCourse.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCourse.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCourse.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void fullUpdateCourseWithPatch() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeUpdate = courseRepository.findAll().size();

        // Update the course using partial update
        Course partialUpdatedCourse = new Course();
        partialUpdatedCourse.setId(course.getId());

        partialUpdatedCourse.createdAt(UPDATED_CREATED_AT).etat(UPDATED_ETAT).startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCourse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCourse))
            )
            .andExpect(status().isOk());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
        Course testCourse = courseList.get(courseList.size() - 1);
        assertThat(testCourse.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCourse.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCourse.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testCourse.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, courseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCourse() throws Exception {
        int databaseSizeBeforeUpdate = courseRepository.findAll().size();
        course.setId(count.incrementAndGet());

        // Create the Course
        CourseDTO courseDTO = courseMapper.toDto(course);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCourseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(courseDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Course in the database
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCourse() throws Exception {
        // Initialize the database
        courseRepository.saveAndFlush(course);

        int databaseSizeBeforeDelete = courseRepository.findAll().size();

        // Delete the course
        restCourseMockMvc
            .perform(delete(ENTITY_API_URL_ID, course.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Course> courseList = courseRepository.findAll();
        assertThat(courseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
