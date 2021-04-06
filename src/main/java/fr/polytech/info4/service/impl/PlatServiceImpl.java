package fr.polytech.info4.service.impl;

import fr.polytech.info4.domain.Plat;
import fr.polytech.info4.repository.PlatRepository;
import fr.polytech.info4.service.PlatService;
import fr.polytech.info4.service.dto.PlatDTO;
import fr.polytech.info4.service.mapper.PlatMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Plat}.
 */
@Service
@Transactional
public class PlatServiceImpl implements PlatService {

    private final Logger log = LoggerFactory.getLogger(PlatServiceImpl.class);

    private final PlatRepository platRepository;

    private final PlatMapper platMapper;

    public PlatServiceImpl(PlatRepository platRepository, PlatMapper platMapper) {
        this.platRepository = platRepository;
        this.platMapper = platMapper;
    }

    @Override
    public PlatDTO save(PlatDTO platDTO) {
        log.debug("Request to save Plat : {}", platDTO);
        Plat plat = platMapper.toEntity(platDTO);
        plat = platRepository.save(plat);
        return platMapper.toDto(plat);
    }

    @Override
    public Optional<PlatDTO> partialUpdate(PlatDTO platDTO) {
        log.debug("Request to partially update Plat : {}", platDTO);

        return platRepository
            .findById(platDTO.getId())
            .map(
                existingPlat -> {
                    platMapper.partialUpdate(existingPlat, platDTO);
                    return existingPlat;
                }
            )
            .map(platRepository::save)
            .map(platMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlatDTO> findAll() {
        log.debug("Request to get all Plats");
        return platRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(platMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<PlatDTO> findAllWithEagerRelationships(Pageable pageable) {
        return platRepository.findAllWithEagerRelationships(pageable).map(platMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlatDTO> findOne(Long id) {
        log.debug("Request to get Plat : {}", id);
        return platRepository.findOneWithEagerRelationships(id).map(platMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Plat : {}", id);
        platRepository.deleteById(id);
    }
}
