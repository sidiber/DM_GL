package fr.polytech.info4.service;

import fr.polytech.info4.domain.*; // for static metamodels
import fr.polytech.info4.domain.SystemePaiement;
import fr.polytech.info4.repository.SystemePaiementRepository;
import fr.polytech.info4.service.criteria.SystemePaiementCriteria;
import fr.polytech.info4.service.dto.SystemePaiementDTO;
import fr.polytech.info4.service.mapper.SystemePaiementMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SystemePaiement} entities in the database.
 * The main input is a {@link SystemePaiementCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SystemePaiementDTO} or a {@link Page} of {@link SystemePaiementDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SystemePaiementQueryService extends QueryService<SystemePaiement> {

    private final Logger log = LoggerFactory.getLogger(SystemePaiementQueryService.class);

    private final SystemePaiementRepository systemePaiementRepository;

    private final SystemePaiementMapper systemePaiementMapper;

    public SystemePaiementQueryService(SystemePaiementRepository systemePaiementRepository, SystemePaiementMapper systemePaiementMapper) {
        this.systemePaiementRepository = systemePaiementRepository;
        this.systemePaiementMapper = systemePaiementMapper;
    }

    /**
     * Return a {@link List} of {@link SystemePaiementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SystemePaiementDTO> findByCriteria(SystemePaiementCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SystemePaiement> specification = createSpecification(criteria);
        return systemePaiementMapper.toDto(systemePaiementRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SystemePaiementDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SystemePaiementDTO> findByCriteria(SystemePaiementCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SystemePaiement> specification = createSpecification(criteria);
        return systemePaiementRepository.findAll(specification, page).map(systemePaiementMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SystemePaiementCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SystemePaiement> specification = createSpecification(criteria);
        return systemePaiementRepository.count(specification);
    }

    /**
     * Function to convert {@link SystemePaiementCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SystemePaiement> createSpecification(SystemePaiementCriteria criteria) {
        Specification<SystemePaiement> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SystemePaiement_.id));
            }
            if (criteria.getNumCarte() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNumCarte(), SystemePaiement_.numCarte));
            }
            if (criteria.getDateExpiration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateExpiration(), SystemePaiement_.dateExpiration));
            }
            if (criteria.getTypeCarte() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTypeCarte(), SystemePaiement_.typeCarte));
            }
            if (criteria.getMontant() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMontant(), SystemePaiement_.montant));
            }
            if (criteria.getDateFacture() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateFacture(), SystemePaiement_.dateFacture));
            }
            if (criteria.getPanierId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPanierId(),
                            root -> root.join(SystemePaiement_.paniers, JoinType.LEFT).get(Panier_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
