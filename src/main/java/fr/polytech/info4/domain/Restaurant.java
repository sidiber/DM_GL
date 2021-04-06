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
 * A Restaurant.
 */
@Entity
@Table(name = "restaurant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_resto")
    private String nomResto;

    @Column(name = "frais_livraison")
    private String fraisLivraison;

    @Column(name = "adresse_resto")
    private String adresseResto;

    @Size(min = 5, max = 5)
    @Column(name = "code_p_resto", length = 5)
    private String codePResto;

    @Column(name = "ville_resto")
    private String villeResto;

    @ManyToMany(mappedBy = "restaurants")
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

    public Restaurant id(Long id) {
        this.id = id;
        return this;
    }

    public String getNomResto() {
        return this.nomResto;
    }

    public Restaurant nomResto(String nomResto) {
        this.nomResto = nomResto;
        return this;
    }

    public void setNomResto(String nomResto) {
        this.nomResto = nomResto;
    }

    public String getFraisLivraison() {
        return this.fraisLivraison;
    }

    public Restaurant fraisLivraison(String fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
        return this;
    }

    public void setFraisLivraison(String fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public String getAdresseResto() {
        return this.adresseResto;
    }

    public Restaurant adresseResto(String adresseResto) {
        this.adresseResto = adresseResto;
        return this;
    }

    public void setAdresseResto(String adresseResto) {
        this.adresseResto = adresseResto;
    }

    public String getCodePResto() {
        return this.codePResto;
    }

    public Restaurant codePResto(String codePResto) {
        this.codePResto = codePResto;
        return this;
    }

    public void setCodePResto(String codePResto) {
        this.codePResto = codePResto;
    }

    public String getVilleResto() {
        return this.villeResto;
    }

    public Restaurant villeResto(String villeResto) {
        this.villeResto = villeResto;
        return this;
    }

    public void setVilleResto(String villeResto) {
        this.villeResto = villeResto;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public Restaurant plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public Restaurant addPlat(Plat plat) {
        this.plats.add(plat);
        plat.getRestaurants().add(this);
        return this;
    }

    public Restaurant removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.getRestaurants().remove(this);
        return this;
    }

    public void setPlats(Set<Plat> plats) {
        if (this.plats != null) {
            this.plats.forEach(i -> i.removeRestaurant(this));
        }
        if (plats != null) {
            plats.forEach(i -> i.addRestaurant(this));
        }
        this.plats = plats;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Restaurant)) {
            return false;
        }
        return id != null && id.equals(((Restaurant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Restaurant{" +
            "id=" + getId() +
            ", nomResto='" + getNomResto() + "'" +
            ", fraisLivraison='" + getFraisLivraison() + "'" +
            ", adresseResto='" + getAdresseResto() + "'" +
            ", codePResto='" + getCodePResto() + "'" +
            ", villeResto='" + getVilleResto() + "'" +
            "}";
    }
}
