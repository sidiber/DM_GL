package fr.polytech.info4.service.mapper;

import fr.polytech.info4.domain.*;
import fr.polytech.info4.service.dto.PanierDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Panier} and its DTO {@link PanierDTO}.
 */
@Mapper(componentModel = "spring", uses = { CompteMapper.class, SystemePaiementMapper.class })
public interface PanierMapper extends EntityMapper<PanierDTO, Panier> {
    @Mapping(target = "constitue", source = "constitue", qualifiedByName = "nom")
    @Mapping(target = "estValidePar", source = "estValidePar", qualifiedByName = "numCarte")
    PanierDTO toDto(Panier s);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<PanierDTO> toDtoIdSet(Set<Panier> panier);

    @Named("prixTotal")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "prixTotal", source = "prixTotal")
    PanierDTO toDtoPrixTotal(Panier panier);
}
