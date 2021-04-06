package fr.polytech.info4.service;

import fr.polytech.info4.service.dto.PanierDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link fr.polytech.info4.domain.Panier}.
 */
public interface PanierService {
    /**
     * Save a panier.
     *
     * @param panierDTO the entity to save.
     * @return the persisted entity.
     */
    PanierDTO save(PanierDTO panierDTO);

    /**
     * Partially updates a panier.
     *
     * @param panierDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PanierDTO> partialUpdate(PanierDTO panierDTO);

    /**
     * Get all the paniers.
     *
     * @return the list of entities.
     */
    List<PanierDTO> findAll();

    /**
     * Get the "id" panier.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PanierDTO> findOne(Long id);

    /**
     * Delete the "id" panier.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
