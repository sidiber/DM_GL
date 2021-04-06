package fr.polytech.info4.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SystemePaiement.
 */
@Entity
@Table(name = "systeme_paiement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SystemePaiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 16, max = 16)
    @Pattern(regexp = "^[0-9_ ]$")
    @Column(name = "num_carte", length = 16)
    private String numCarte;

    @NotNull
    @Column(name = "date_expiration", nullable = false)
    private Instant dateExpiration;

    @Size(max = 10)
    @Pattern(regexp = "^[a-zA-Z_ ]$")
    @Column(name = "type_carte", length = 10)
    private String typeCarte;

    @Column(name = "montant")
    private String montant;

    @NotNull
    @Column(name = "date_facture", nullable = false)
    private Instant dateFacture;

    @OneToMany(mappedBy = "estValidePar")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "constitue", "estValidePar", "plats" }, allowSetters = true)
    private Set<Panier> paniers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SystemePaiement id(Long id) {
        this.id = id;
        return this;
    }

    public String getNumCarte() {
        return this.numCarte;
    }

    public SystemePaiement numCarte(String numCarte) {
        this.numCarte = numCarte;
        return this;
    }

    public void setNumCarte(String numCarte) {
        this.numCarte = numCarte;
    }

    public Instant getDateExpiration() {
        return this.dateExpiration;
    }

    public SystemePaiement dateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
        return this;
    }

    public void setDateExpiration(Instant dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getTypeCarte() {
        return this.typeCarte;
    }

    public SystemePaiement typeCarte(String typeCarte) {
        this.typeCarte = typeCarte;
        return this;
    }

    public void setTypeCarte(String typeCarte) {
        this.typeCarte = typeCarte;
    }

    public String getMontant() {
        return this.montant;
    }

    public SystemePaiement montant(String montant) {
        this.montant = montant;
        return this;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public Instant getDateFacture() {
        return this.dateFacture;
    }

    public SystemePaiement dateFacture(Instant dateFacture) {
        this.dateFacture = dateFacture;
        return this;
    }

    public void setDateFacture(Instant dateFacture) {
        this.dateFacture = dateFacture;
    }

    public Set<Panier> getPaniers() {
        return this.paniers;
    }

    public SystemePaiement paniers(Set<Panier> paniers) {
        this.setPaniers(paniers);
        return this;
    }

    public SystemePaiement addPanier(Panier panier) {
        this.paniers.add(panier);
        panier.setEstValidePar(this);
        return this;
    }

    public SystemePaiement removePanier(Panier panier) {
        this.paniers.remove(panier);
        panier.setEstValidePar(null);
        return this;
    }

    public void setPaniers(Set<Panier> paniers) {
        if (this.paniers != null) {
            this.paniers.forEach(i -> i.setEstValidePar(null));
        }
        if (paniers != null) {
            paniers.forEach(i -> i.setEstValidePar(this));
        }
        this.paniers = paniers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SystemePaiement)) {
            return false;
        }
        return id != null && id.equals(((SystemePaiement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SystemePaiement{" +
            "id=" + getId() +
            ", numCarte='" + getNumCarte() + "'" +
            ", dateExpiration='" + getDateExpiration() + "'" +
            ", typeCarte='" + getTypeCarte() + "'" +
            ", montant='" + getMontant() + "'" +
            ", dateFacture='" + getDateFacture() + "'" +
            "}";
    }
}
