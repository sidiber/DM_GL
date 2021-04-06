package fr.polytech.info4.service.mapper;

import fr.polytech.info4.domain.*;
import fr.polytech.info4.service.dto.CompteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Compte} and its DTO {@link CompteDTO}.
 */
@Mapper(componentModel = "spring", uses = { CooperativeMapper.class })
public interface CompteMapper extends EntityMapper<CompteDTO, Compte> {
    @Mapping(target = "membrede", source = "membrede", qualifiedByName = "nomCoop")
    CompteDTO toDto(Compte s);

    @Named("nom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    CompteDTO toDtoNom(Compte compte);
}
