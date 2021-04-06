package fr.polytech.info4.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemePaiementMapperTest {

    private SystemePaiementMapper systemePaiementMapper;

    @BeforeEach
    public void setUp() {
        systemePaiementMapper = new SystemePaiementMapperImpl();
    }
}
