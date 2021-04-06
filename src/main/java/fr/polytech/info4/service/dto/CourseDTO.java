package fr.polytech.info4.service.dto;

import fr.polytech.info4.domain.enumeration.EtatCourse;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.polytech.info4.domain.Course} entity.
 */
public class CourseDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    private EtatCourse etat;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    private PanierDTO montant;

    private Set<PlatDTO> plats = new HashSet<>();

    private CompteDTO estlivre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public EtatCourse getEtat() {
        return etat;
    }

    public void setEtat(EtatCourse etat) {
        this.etat = etat;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public PanierDTO getMontant() {
        return montant;
    }

    public void setMontant(PanierDTO montant) {
        this.montant = montant;
    }

    public Set<PlatDTO> getPlats() {
        return plats;
    }

    public void setPlats(Set<PlatDTO> plats) {
        this.plats = plats;
    }

    public CompteDTO getEstlivre() {
        return estlivre;
    }

    public void setEstlivre(CompteDTO estlivre) {
        this.estlivre = estlivre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseDTO)) {
            return false;
        }

        CourseDTO courseDTO = (CourseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", etat='" + getEtat() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", montant=" + getMontant() +
            ", plats=" + getPlats() +
            ", estlivre=" + getEstlivre() +
            "}";
    }
}
