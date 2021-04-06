package fr.polytech.info4.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.polytech.info4.domain.Cooperative} entity.
 */
public class CooperativeDTO implements Serializable {

    private Long id;

    @Pattern(regexp = "[a-zA-Z_ ]+")
    private String nomCoop;

    @Pattern(regexp = "[a-zA-Z_ ]+")
    private String villeCoop;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomCoop() {
        return nomCoop;
    }

    public void setNomCoop(String nomCoop) {
        this.nomCoop = nomCoop;
    }

    public String getVilleCoop() {
        return villeCoop;
    }

    public void setVilleCoop(String villeCoop) {
        this.villeCoop = villeCoop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CooperativeDTO)) {
            return false;
        }

        CooperativeDTO cooperativeDTO = (CooperativeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cooperativeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CooperativeDTO{" +
            "id=" + getId() +
            ", nomCoop='" + getNomCoop() + "'" +
            ", villeCoop='" + getVilleCoop() + "'" +
            "}";
    }
}
