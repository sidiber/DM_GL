package fr.polytech.info4.service;

import fr.polytech.info4.domain.*; // for static metamodels
import fr.polytech.info4.domain.Plat;
import fr.polytech.info4.repository.PlatRepository;
import fr.polytech.info4.service.criteria.PlatCriteria;
import fr.polytech.info4.service.dto.PlatDTO;
import fr.polytech.info4.service.mapper.PlatMapper;
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
 * Service for executing complex queries for {@link Plat} entities in the database.
 * The main input is a {@link PlatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PlatDTO} or a {@link Page} of {@link PlatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PlatQueryService extends QueryService<Plat> {

    private final Logger log = LoggerFactory.getLogger(PlatQueryService.class);

    private final PlatRepository platRepository;

    private final PlatMapper platMapper;

    public PlatQueryService(PlatRepository platRepository, PlatMapper platMapper) {
        this.platRepository = platRepository;
        this.platMapper = platMapper;
    }

    /**
     * Return a {@link List} of {@link PlatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PlatDTO> findByCriteria(PlatCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Plat> specification = createSpecification(criteria);
        return platMapper.toDto(platRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PlatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PlatDTO> findByCriteria(PlatCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Plat> specification = createSpecification(criteria);
        return platRepository.findAll(specification, page).map(platMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PlatCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Plat> specification = createSpecification(criteria);
        return platRepository.count(specification);
    }

    /**
     * Function to convert {@link PlatCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Plat> createSpecification(PlatCriteria criteria) {
        Specification<Plat> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Plat_.id));
            }
            if (criteria.getNomPlat() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomPlat(), Plat_.nomPlat));
            }
            if (criteria.getPrix() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPrix(), Plat_.prix));
            }
            if (criteria.getRestaurantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRestaurantId(),
                            root -> root.join(Plat_.restaurants, JoinType.LEFT).get(Restaurant_.id)
                        )
                    );
            }
            if (criteria.getPanierId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPanierId(), root -> root.join(Plat_.paniers, JoinType.LEFT).get(Panier_.id))
                    );
            }
            if (criteria.getCourseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getCourseId(), root -> root.join(Plat_.courses, JoinType.LEFT).get(Course_.id))
                    );
            }
        }
        return specification;
    }
}
