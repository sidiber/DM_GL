package fr.polytech.info4.service;

import fr.polytech.info4.domain.*; // for static metamodels
import fr.polytech.info4.domain.Compte;
import fr.polytech.info4.repository.CompteRepository;
import fr.polytech.info4.service.criteria.CompteCriteria;
import fr.polytech.info4.service.dto.CompteDTO;
import fr.polytech.info4.service.mapper.CompteMapper;
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
 * Service for executing complex queries for {@link Compte} entities in the database.
 * The main input is a {@link CompteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CompteDTO} or a {@link Page} of {@link CompteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompteQueryService extends QueryService<Compte> {

    private final Logger log = LoggerFactory.getLogger(CompteQueryService.class);

    private final CompteRepository compteRepository;

    private final CompteMapper compteMapper;

    public CompteQueryService(CompteRepository compteRepository, CompteMapper compteMapper) {
        this.compteRepository = compteRepository;
        this.compteMapper = compteMapper;
    }

    /**
     * Return a {@link List} of {@link CompteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CompteDTO> findByCriteria(CompteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Compte> specification = createSpecification(criteria);
        return compteMapper.toDto(compteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CompteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CompteDTO> findByCriteria(CompteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Compte> specification = createSpecification(criteria);
        return compteRepository.findAll(specification, page).map(compteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Compte> specification = createSpecification(criteria);
        return compteRepository.count(specification);
    }

    /**
     * Function to convert {@link CompteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Compte> createSpecification(CompteCriteria criteria) {
        Specification<Compte> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Compte_.id));
            }
            if (criteria.getNom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNom(), Compte_.nom));
            }
            if (criteria.getPrenom() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrenom(), Compte_.prenom));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Compte_.email));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), Compte_.phoneNumber));
            }
            if (criteria.getAddressCompte() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddressCompte(), Compte_.addressCompte));
            }
            if (criteria.getCodePCompte() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCodePCompte(), Compte_.codePCompte));
            }
            if (criteria.getVilleCompte() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVilleCompte(), Compte_.villeCompte));
            }
            if (criteria.getPanierId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPanierId(), root -> root.join(Compte_.paniers, JoinType.LEFT).get(Panier_.id))
                    );
            }
            if (criteria.getCourseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCourseId(), root -> root.join(Compte_.courses, JoinType.LEFT).get(Course_.id))
                    );
            }
            if (criteria.getMembredeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getMembredeId(),
                            root -> root.join(Compte_.membrede, JoinType.LEFT).get(Cooperative_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
