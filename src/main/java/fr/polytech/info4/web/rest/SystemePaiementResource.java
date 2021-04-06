package fr.polytech.info4.web.rest;

import fr.polytech.info4.repository.SystemePaiementRepository;
import fr.polytech.info4.service.SystemePaiementQueryService;
import fr.polytech.info4.service.SystemePaiementService;
import fr.polytech.info4.service.criteria.SystemePaiementCriteria;
import fr.polytech.info4.service.dto.SystemePaiementDTO;
import fr.polytech.info4.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fr.polytech.info4.domain.SystemePaiement}.
 */
@RestController
@RequestMapping("/api")
public class SystemePaiementResource {

    private final Logger log = LoggerFactory.getLogger(SystemePaiementResource.class);

    private static final String ENTITY_NAME = "systemePaiement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SystemePaiementService systemePaiementService;

    private final SystemePaiementRepository systemePaiementRepository;

    private final SystemePaiementQueryService systemePaiementQueryService;

    public SystemePaiementResource(
        SystemePaiementService systemePaiementService,
        SystemePaiementRepository systemePaiementRepository,
        SystemePaiementQueryService systemePaiementQueryService
    ) {
        this.systemePaiementService = systemePaiementService;
        this.systemePaiementRepository = systemePaiementRepository;
        this.systemePaiementQueryService = systemePaiementQueryService;
    }

    /**
     * {@code POST  /systeme-paiements} : Create a new systemePaiement.
     *
     * @param systemePaiementDTO the systemePaiementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new systemePaiementDTO, or with status {@code 400 (Bad Request)} if the systemePaiement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/systeme-paiements")
    public ResponseEntity<SystemePaiementDTO> createSystemePaiement(@Valid @RequestBody SystemePaiementDTO systemePaiementDTO)
        throws URISyntaxException {
        log.debug("REST request to save SystemePaiement : {}", systemePaiementDTO);
        if (systemePaiementDTO.getId() != null) {
            throw new BadRequestAlertException("A new systemePaiement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SystemePaiementDTO result = systemePaiementService.save(systemePaiementDTO);
        return ResponseEntity
            .created(new URI("/api/systeme-paiements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /systeme-paiements/:id} : Updates an existing systemePaiement.
     *
     * @param id the id of the systemePaiementDTO to save.
     * @param systemePaiementDTO the systemePaiementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemePaiementDTO,
     * or with status {@code 400 (Bad Request)} if the systemePaiementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemePaiementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/systeme-paiements/{id}")
    public ResponseEntity<SystemePaiementDTO> updateSystemePaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SystemePaiementDTO systemePaiementDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SystemePaiement : {}, {}", id, systemePaiementDTO);
        if (systemePaiementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemePaiementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemePaiementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SystemePaiementDTO result = systemePaiementService.save(systemePaiementDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemePaiementDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /systeme-paiements/:id} : Partial updates given fields of an existing systemePaiement, field will ignore if it is null
     *
     * @param id the id of the systemePaiementDTO to save.
     * @param systemePaiementDTO the systemePaiementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemePaiementDTO,
     * or with status {@code 400 (Bad Request)} if the systemePaiementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the systemePaiementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the systemePaiementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/systeme-paiements/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SystemePaiementDTO> partialUpdateSystemePaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SystemePaiementDTO systemePaiementDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SystemePaiement partially : {}, {}", id, systemePaiementDTO);
        if (systemePaiementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, systemePaiementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!systemePaiementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SystemePaiementDTO> result = systemePaiementService.partialUpdate(systemePaiementDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemePaiementDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /systeme-paiements} : get all the systemePaiements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemePaiements in body.
     */
    @GetMapping("/systeme-paiements")
    public ResponseEntity<List<SystemePaiementDTO>> getAllSystemePaiements(SystemePaiementCriteria criteria) {
        log.debug("REST request to get SystemePaiements by criteria: {}", criteria);
        List<SystemePaiementDTO> entityList = systemePaiementQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /systeme-paiements/count} : count all the systemePaiements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/systeme-paiements/count")
    public ResponseEntity<Long> countSystemePaiements(SystemePaiementCriteria criteria) {
        log.debug("REST request to count SystemePaiements by criteria: {}", criteria);
        return ResponseEntity.ok().body(systemePaiementQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /systeme-paiements/:id} : get the "id" systemePaiement.
     *
     * @param id the id of the systemePaiementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemePaiementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/systeme-paiements/{id}")
    public ResponseEntity<SystemePaiementDTO> getSystemePaiement(@PathVariable Long id) {
        log.debug("REST request to get SystemePaiement : {}", id);
        Optional<SystemePaiementDTO> systemePaiementDTO = systemePaiementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(systemePaiementDTO);
    }

    /**
     * {@code DELETE  /systeme-paiements/:id} : delete the "id" systemePaiement.
     *
     * @param id the id of the systemePaiementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/systeme-paiements/{id}")
    public ResponseEntity<Void> deleteSystemePaiement(@PathVariable Long id) {
        log.debug("REST request to delete SystemePaiement : {}", id);
        systemePaiementService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
