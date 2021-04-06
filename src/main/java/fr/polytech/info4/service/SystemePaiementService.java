package fr.polytech.info4.service;

import fr.polytech.info4.service.dto.SystemePaiementDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link fr.polytech.info4.domain.SystemePaiement}.
 */
public interface SystemePaiementService {
    /**
     * Save a systemePaiement.
     *
     * @param systemePaiementDTO the entity to save.
     * @return the persisted entity.
     */
    SystemePaiementDTO save(SystemePaiementDTO systemePaiementDTO);

    /**
     * Partially updates a systemePaiement.
     *
     * @param systemePaiementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SystemePaiementDTO> partialUpdate(SystemePaiementDTO systemePaiementDTO);

    /**
     * Get all the systemePaiements.
     *
     * @return the list of entities.
     */
    List<SystemePaiementDTO> findAll();

    /**
     * Get the "id" systemePaiement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SystemePaiementDTO> findOne(Long id);

    /**
     * Delete the "id" systemePaiement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
