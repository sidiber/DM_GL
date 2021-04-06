package fr.polytech.info4.service.impl;

import fr.polytech.info4.domain.Course;
import fr.polytech.info4.repository.CourseRepository;
import fr.polytech.info4.service.CourseService;
import fr.polytech.info4.service.dto.CourseDTO;
import fr.polytech.info4.service.mapper.CourseMapper;
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
 * Service Implementation for managing {@link Course}.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Override
    public CourseDTO save(CourseDTO courseDTO) {
        log.debug("Request to save Course : {}", courseDTO);
        Course course = courseMapper.toEntity(courseDTO);
        course = courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    @Override
    public Optional<CourseDTO> partialUpdate(CourseDTO courseDTO) {
        log.debug("Request to partially update Course : {}", courseDTO);

        return courseRepository
            .findById(courseDTO.getId())
            .map(
                existingCourse -> {
                    courseMapper.partialUpdate(existingCourse, courseDTO);
                    return existingCourse;
                }
            )
            .map(courseRepository::save)
            .map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findAll() {
        log.debug("Request to get all Courses");
        return courseRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(courseMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<CourseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseRepository.findAllWithEagerRelationships(pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseDTO> findOne(Long id) {
        log.debug("Request to get Course : {}", id);
        return courseRepository.findOneWithEagerRelationships(id).map(courseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Course : {}", id);
        courseRepository.deleteById(id);
    }
}
