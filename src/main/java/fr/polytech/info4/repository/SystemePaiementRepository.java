package fr.polytech.info4.repository;

import fr.polytech.info4.domain.SystemePaiement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SystemePaiement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemePaiementRepository extends JpaRepository<SystemePaiement, Long>, JpaSpecificationExecutor<SystemePaiement> {}
