package fr.polytech.info4.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.polytech.info4.domain.Plat} entity.
 */
public class PlatDTO implements Serializable {

    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_ ]$")
    private String nomPlat;

    @Lob
    private String description;

    @NotNull
    private String prix;

    @Lob
    private byte[] photo;

    private String photoContentType;
    private Set<RestaurantDTO> restaurants = new HashSet<>();

    private Set<PanierDTO> paniers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPlat() {
        return nomPlat;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Set<RestaurantDTO> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<RestaurantDTO> restaurants) {
        this.restaurants = restaurants;
    }

    public Set<PanierDTO> getPaniers() {
        return paniers;
    }

    public void setPaniers(Set<PanierDTO> paniers) {
        this.paniers = paniers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatDTO)) {
            return false;
        }

        PlatDTO platDTO = (PlatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, platDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlatDTO{" +
            "id=" + getId() +
            ", nomPlat='" + getNomPlat() + "'" +
            ", description='" + getDescription() + "'" +
            ", prix='" + getPrix() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", restaurants=" + getRestaurants() +
            ", paniers=" + getPaniers() +
            "}";
    }
}
