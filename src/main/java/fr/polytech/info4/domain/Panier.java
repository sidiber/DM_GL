package fr.polytech.info4.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Panier.
 */
@Entity
@Table(name = "panier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Panier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prix_total")
    private String prixTotal;

    @ManyToOne
    @JsonIgnoreProperties(value = { "paniers", "courses", "membre" }, allowSetters = true)
    private Compte constitue;

    @ManyToOne
    @JsonIgnoreProperties(value = { "paniers" }, allowSetters = true)
    private SystemePaiement estValidePar;

    @ManyToMany(mappedBy = "paniers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "restaurants", "paniers", "courses" }, allowSetters = true)
    private Set<Plat> plats = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Panier id(Long id) {
        this.id = id;
        return this;
    }

    public String getPrixTotal() {
        return this.prixTotal;
    }

    public Panier prixTotal(String prixTotal) {
        this.prixTotal = prixTotal;
        return this;
    }

    public void setPrixTotal(String prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Compte getConstitue() {
        return this.constitue;
    }

    public Panier constitue(Compte compte) {
        this.setConstitue(compte);
        return this;
    }

    public void setConstitue(Compte compte) {
        this.constitue = compte;
    }

    public SystemePaiement getEstValidePar() {
        return this.estValidePar;
    }

    public Panier estValidePar(SystemePaiement systemePaiement) {
        this.setEstValidePar(systemePaiement);
        return this;
    }

    public void setEstValidePar(SystemePaiement systemePaiement) {
        this.estValidePar = systemePaiement;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public Panier plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public Panier addPlat(Plat plat) {
        this.plats.add(plat);
        plat.getPaniers().add(this);
        return this;
    }

    public Panier removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.getPaniers().remove(this);
        return this;
    }

    public void setPlats(Set<Plat> plats) {
        if (this.plats != null) {
            this.plats.forEach(i -> i.removePanier(this));
        }
        if (plats != null) {
            plats.forEach(i -> i.addPanier(this));
        }
        this.plats = plats;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Panier)) {
            return false;
        }
        return id != null && id.equals(((Panier) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Panier{" +
            "id=" + getId() +
            ", prixTotal='" + getPrixTotal() + "'" +
            "}";
    }
}
