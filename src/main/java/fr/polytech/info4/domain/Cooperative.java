package fr.polytech.info4.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Cooperative.
 */
@Entity
@Table(name = "cooperative")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Cooperative implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "[a-zA-Z_ ]+")
    @Column(name = "nom_coop")
    private String nomCoop;

    @Pattern(regexp = "[a-zA-Z_ ]+")
    @Column(name = "ville_coop")
    private String villeCoop;

    @OneToMany(mappedBy = "membre")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "paniers", "courses", "membre" }, allowSetters = true)
    private Set<Compte> comptes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cooperative id(Long id) {
        this.id = id;
        return this;
    }

    public String getNomCoop() {
        return this.nomCoop;
    }

    public Cooperative nomCoop(String nomCoop) {
        this.nomCoop = nomCoop;
        return this;
    }

    public void setNomCoop(String nomCoop) {
        this.nomCoop = nomCoop;
    }

    public String getVilleCoop() {
        return this.villeCoop;
    }

    public Cooperative villeCoop(String villeCoop) {
        this.villeCoop = villeCoop;
        return this;
    }

    public void setVilleCoop(String villeCoop) {
        this.villeCoop = villeCoop;
    }

    public Set<Compte> getComptes() {
        return this.comptes;
    }

    public Cooperative comptes(Set<Compte> comptes) {
        this.setComptes(comptes);
        return this;
    }

    public Cooperative addCompte(Compte compte) {
        this.comptes.add(compte);
        compte.setMembre(this);
        return this;
    }

    public Cooperative removeCompte(Compte compte) {
        this.comptes.remove(compte);
        compte.setMembre(null);
        return this;
    }

    public void setComptes(Set<Compte> comptes) {
        if (this.comptes != null) {
            this.comptes.forEach(i -> i.setMembre(null));
        }
        if (comptes != null) {
            comptes.forEach(i -> i.setMembre(this));
        }
        this.comptes = comptes;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cooperative)) {
            return false;
        }
        return id != null && id.equals(((Cooperative) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cooperative{" +
            "id=" + getId() +
            ", nomCoop='" + getNomCoop() + "'" +
            ", villeCoop='" + getVilleCoop() + "'" +
            "}";
    }
}
