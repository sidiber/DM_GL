package fr.polytech.info4.service;

import fr.polytech.info4.service.dto.PlatDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link fr.polytech.info4.domain.Plat}.
 */
public interface PlatService {
    /**
     * Save a plat.
     *
     * @param platDTO the entity to save.
     * @return the persisted entity.
     */
    PlatDTO save(PlatDTO platDTO);

    /**
     * Partially updates a plat.
     *
     * @param platDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PlatDTO> partialUpdate(PlatDTO platDTO);

    /**
     * Get all the plats.
     *
     * @return the list of entities.
     */
    List<PlatDTO> findAll();

    /**
     * Get all the plats with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PlatDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" plat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PlatDTO> findOne(Long id);

    /**
     * Delete the "id" plat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
