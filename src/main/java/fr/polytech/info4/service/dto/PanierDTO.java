package fr.polytech.info4.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link fr.polytech.info4.domain.Panier} entity.
 */
public class PanierDTO implements Serializable {

    private Long id;

    private String prixTotal;

    private CompteDTO constitue;

    private SystemePaiementDTO estValidePar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(String prixTotal) {
        this.prixTotal = prixTotal;
    }

    public CompteDTO getConstitue() {
        return constitue;
    }

    public void setConstitue(CompteDTO constitue) {
        this.constitue = constitue;
    }

    public SystemePaiementDTO getEstValidePar() {
        return estValidePar;
    }

    public void setEstValidePar(SystemePaiementDTO estValidePar) {
        this.estValidePar = estValidePar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PanierDTO)) {
            return false;
        }

        PanierDTO panierDTO = (PanierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, panierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PanierDTO{" +
            "id=" + getId() +
            ", prixTotal='" + getPrixTotal() + "'" +
            ", constitue=" + getConstitue() +
            ", estValidePar=" + getEstValidePar() +
            "}";
    }
}
