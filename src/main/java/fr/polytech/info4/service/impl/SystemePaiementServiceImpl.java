package fr.polytech.info4.service.impl;

import fr.polytech.info4.domain.SystemePaiement;
import fr.polytech.info4.repository.SystemePaiementRepository;
import fr.polytech.info4.service.SystemePaiementService;
import fr.polytech.info4.service.dto.SystemePaiementDTO;
import fr.polytech.info4.service.mapper.SystemePaiementMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SystemePaiement}.
 */
@Service
@Transactional
public class SystemePaiementServiceImpl implements SystemePaiementService {

    private final Logger log = LoggerFactory.getLogger(SystemePaiementServiceImpl.class);

    private final SystemePaiementRepository systemePaiementRepository;

    private final SystemePaiementMapper systemePaiementMapper;

    public SystemePaiementServiceImpl(SystemePaiementRepository systemePaiementRepository, SystemePaiementMapper systemePaiementMapper) {
        this.systemePaiementRepository = systemePaiementRepository;
        this.systemePaiementMapper = systemePaiementMapper;
    }

    @Override
    public SystemePaiementDTO save(SystemePaiementDTO systemePaiementDTO) {
        log.debug("Request to save SystemePaiement : {}", systemePaiementDTO);
        SystemePaiement systemePaiement = systemePaiementMapper.toEntity(systemePaiementDTO);
        systemePaiement = systemePaiementRepository.save(systemePaiement);
        return systemePaiementMapper.toDto(systemePaiement);
    }

    @Override
    public Optional<SystemePaiementDTO> partialUpdate(SystemePaiementDTO systemePaiementDTO) {
        log.debug("Request to partially update SystemePaiement : {}", systemePaiementDTO);

        return systemePaiementRepository
            .findById(systemePaiementDTO.getId())
            .map(
                existingSystemePaiement -> {
                    systemePaiementMapper.partialUpdate(existingSystemePaiement, systemePaiementDTO);
                    return existingSystemePaiement;
                }
            )
            .map(systemePaiementRepository::save)
            .map(systemePaiementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemePaiementDTO> findAll() {
        log.debug("Request to get all SystemePaiements");
        return systemePaiementRepository
            .findAll()
            .stream()
            .map(systemePaiementMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemePaiementDTO> findOne(Long id) {
        log.debug("Request to get SystemePaiement : {}", id);
        return systemePaiementRepository.findById(id).map(systemePaiementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete SystemePaiement : {}", id);
        systemePaiementRepository.deleteById(id);
    }
}
