package fr.polytech.info4.service.mapper;

import fr.polytech.info4.domain.*;
import fr.polytech.info4.service.dto.CourseDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring", uses = { PanierMapper.class, PlatMapper.class, CompteMapper.class })
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "montant", source = "montant", qualifiedByName = "prixTotal")
    @Mapping(target = "plats", source = "plats", qualifiedByName = "idSet")
    @Mapping(target = "livre", source = "livre", qualifiedByName = "nom")
    CourseDTO toDto(Course s);

    @Mapping(target = "removePlat", ignore = true)
    Course toEntity(CourseDTO courseDTO);
}
