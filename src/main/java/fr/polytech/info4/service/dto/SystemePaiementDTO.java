package fr.polytech.info4.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.polytech.info4.domain.SystemePaiement} entity.
 */
public class SystemePaiementDTO implements Serializable {

    private Long id;

    @Size(min = 16, max = 16)
    @Pattern(regexp = "[0-9]*")
    private String numCarte;

    @NotNull
    private Instant dateExpiration;

    @Size(max = 10)
    private String typeCarte;

    private String montant;

    @NotNull
    private Instant dateFacture;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumCarte() {
        return numCarte;
    }

    public void setNumCarte(String numCarte) {
        this.numCarte = numCarte;
    }

    public Instant getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getTypeCarte() {
        return typeCarte;
    }

    public void setTypeCarte(String typeCarte) {
        this.typeCarte = typeCarte;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public Instant getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(Instant dateFacture) {
        this.dateFacture = dateFacture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemePaiementDTO)) {
            return false;
        }

        SystemePaiementDTO systemePaiementDTO = (SystemePaiementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, systemePaiementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemePaiementDTO{" +
            "id=" + getId() +
            ", numCarte='" + getNumCarte() + "'" +
            ", dateExpiration='" + getDateExpiration() + "'" +
            ", typeCarte='" + getTypeCarte() + "'" +
            ", montant='" + getMontant() + "'" +
            ", dateFacture='" + getDateFacture() + "'" +
            "}";
    }
}
