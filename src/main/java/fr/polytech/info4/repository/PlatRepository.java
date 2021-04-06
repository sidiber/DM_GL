package fr.polytech.info4.repository;

import fr.polytech.info4.domain.Plat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Plat entity.
 */
@Repository
public interface PlatRepository extends JpaRepository<Plat, Long>, JpaSpecificationExecutor<Plat> {
    @Query(
        value = "select distinct plat from Plat plat left join fetch plat.restaurants left join fetch plat.paniers",
        countQuery = "select count(distinct plat) from Plat plat"
    )
    Page<Plat> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct plat from Plat plat left join fetch plat.restaurants left join fetch plat.paniers")
    List<Plat> findAllWithEagerRelationships();

    @Query("select plat from Plat plat left join fetch plat.restaurants left join fetch plat.paniers where plat.id =:id")
    Optional<Plat> findOneWithEagerRelationships(@Param("id") Long id);
}
