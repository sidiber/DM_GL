package fr.polytech.info4.service.mapper;

import fr.polytech.info4.domain.*;
import fr.polytech.info4.service.dto.SystemePaiementDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SystemePaiement} and its DTO {@link SystemePaiementDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SystemePaiementMapper extends EntityMapper<SystemePaiementDTO, SystemePaiement> {
    @Named("numCarte")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numCarte", source = "numCarte")
    SystemePaiementDTO toDtoNumCarte(SystemePaiement systemePaiement);
}
