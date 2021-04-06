package fr.polytech.info4.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.polytech.info4.domain.Compte} entity.
 */
public class CompteDTO implements Serializable {

    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_ ]$")
    private String nom;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_ ]$")
    private String prenom;

    @NotNull
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@(|hotmail|yahoo|imag|gmail|etu.univ-grenoble-alpes|univ-grenoble-alpes+)\\.(fr|com)$")
    private String email;

    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[0-9]$")
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z0-9_ ]$")
    private String addressCompte;

    @Size(min = 5, max = 5)
    @Pattern(regexp = "^[0-9]$")
    private String codePCompte;

    @Pattern(regexp = "^[a-zA-Z0-9_ ]$")
    private String villeCompte;

    private CooperativeDTO membre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressCompte() {
        return addressCompte;
    }

    public void setAddressCompte(String addressCompte) {
        this.addressCompte = addressCompte;
    }

    public String getCodePCompte() {
        return codePCompte;
    }

    public void setCodePCompte(String codePCompte) {
        this.codePCompte = codePCompte;
    }

    public String getVilleCompte() {
        return villeCompte;
    }

    public void setVilleCompte(String villeCompte) {
        this.villeCompte = villeCompte;
    }

    public CooperativeDTO getMembre() {
        return membre;
    }

    public void setMembre(CooperativeDTO membre) {
        this.membre = membre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompteDTO)) {
            return false;
        }

        CompteDTO compteDTO = (CompteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, compteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompteDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", addressCompte='" + getAddressCompte() + "'" +
            ", codePCompte='" + getCodePCompte() + "'" +
            ", villeCompte='" + getVilleCompte() + "'" +
            ", membre=" + getMembre() +
            "}";
    }
}
