package fr.polytech.info4.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.polytech.info4.domain.Restaurant} entity.
 */
public class RestaurantDTO implements Serializable {

    private Long id;

    @Pattern(regexp = "^[a-zA-Z_ ]+$")
    private String nomResto;

    private String fraisLivraison;

    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$")
    private String adresseResto;

    @Size(min = 5, max = 5)
    @Pattern(regexp = "^[0-9]+$")
    private String codePResto;

    @Pattern(regexp = "^[a-zA-Z_ ]+$")
    private String villeResto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomResto() {
        return nomResto;
    }

    public void setNomResto(String nomResto) {
        this.nomResto = nomResto;
    }

    public String getFraisLivraison() {
        return fraisLivraison;
    }

    public void setFraisLivraison(String fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }

    public String getAdresseResto() {
        return adresseResto;
    }

    public void setAdresseResto(String adresseResto) {
        this.adresseResto = adresseResto;
    }

    public String getCodePResto() {
        return codePResto;
    }

    public void setCodePResto(String codePResto) {
        this.codePResto = codePResto;
    }

    public String getVilleResto() {
        return villeResto;
    }

    public void setVilleResto(String villeResto) {
        this.villeResto = villeResto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantDTO)) {
            return false;
        }

        RestaurantDTO restaurantDTO = (RestaurantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantDTO{" +
            "id=" + getId() +
            ", nomResto='" + getNomResto() + "'" +
            ", fraisLivraison='" + getFraisLivraison() + "'" +
            ", adresseResto='" + getAdresseResto() + "'" +
            ", codePResto='" + getCodePResto() + "'" +
            ", villeResto='" + getVilleResto() + "'" +
            "}";
    }
}
