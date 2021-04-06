package fr.polytech.info4.service;

import fr.polytech.info4.domain.*; // for static metamodels
import fr.polytech.info4.domain.Restaurant;
import fr.polytech.info4.repository.RestaurantRepository;
import fr.polytech.info4.service.criteria.RestaurantCriteria;
import fr.polytech.info4.service.dto.RestaurantDTO;
import fr.polytech.info4.service.mapper.RestaurantMapper;
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
 * Service for executing complex queries for {@link Restaurant} entities in the database.
 * The main input is a {@link RestaurantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RestaurantDTO} or a {@link Page} of {@link RestaurantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RestaurantQueryService extends QueryService<Restaurant> {

    private final Logger log = LoggerFactory.getLogger(RestaurantQueryService.class);

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    public RestaurantQueryService(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    /**
     * Return a {@link List} of {@link RestaurantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> findByCriteria(RestaurantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Restaurant> specification = createSpecification(criteria);
        return restaurantMapper.toDto(restaurantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RestaurantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RestaurantDTO> findByCriteria(RestaurantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Restaurant> specification = createSpecification(criteria);
        return restaurantRepository.findAll(specification, page).map(restaurantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RestaurantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Restaurant> specification = createSpecification(criteria);
        return restaurantRepository.count(specification);
    }

    /**
     * Function to convert {@link RestaurantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Restaurant> createSpecification(RestaurantCriteria criteria) {
        Specification<Restaurant> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Restaurant_.id));
            }
            if (criteria.getNomResto() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomResto(), Restaurant_.nomResto));
            }
            if (criteria.getFraisLivraison() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFraisLivraison(), Restaurant_.fraisLivraison));
            }
            if (criteria.getAdresseResto() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdresseResto(), Restaurant_.adresseResto));
            }
            if (criteria.getCodePResto() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCodePResto(), Restaurant_.codePResto));
            }
            if (criteria.getVilleResto() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVilleResto(), Restaurant_.villeResto));
            }
            if (criteria.getPlatId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPlatId(), root -> root.join(Restaurant_.plats, JoinType.LEFT).get(Plat_.id))
                    );
            }
        }
        return specification;
    }
}
