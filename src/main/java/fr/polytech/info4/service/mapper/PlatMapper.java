package fr.polytech.info4.service.mapper;

import fr.polytech.info4.domain.*;
import fr.polytech.info4.service.dto.PlatDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Plat} and its DTO {@link PlatDTO}.
 */
@Mapper(componentModel = "spring", uses = { RestaurantMapper.class, PanierMapper.class })
public interface PlatMapper extends EntityMapper<PlatDTO, Plat> {
    @Mapping(target = "restaurants", source = "restaurants", qualifiedByName = "idSet")
    @Mapping(target = "paniers", source = "paniers", qualifiedByName = "idSet")
    PlatDTO toDto(Plat s);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<PlatDTO> toDtoIdSet(Set<Plat> plat);

    @Mapping(target = "removeRestaurant", ignore = true)
    @Mapping(target = "removePanier", ignore = true)
    Plat toEntity(PlatDTO platDTO);
}
