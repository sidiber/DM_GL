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
 * A Compte.
 */
@Entity
@Table(name = "compte")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Compte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_ ]+$")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_ ]+$")
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @NotNull
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@(|hotmail|yahoo|imag|gmail|etu.univ-grenoble-alpes|univ-grenoble-alpes+)\\.(fr|com)$")
    @Column(name = "email", nullable = false)
    private String email;

    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[0-9]+$")
    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$")
    @Column(name = "address_compte")
    private String addressCompte;

    @Size(min = 5, max = 5)
    @Pattern(regexp = "^[0-9]+$")
    @Column(name = "code_p_compte", length = 5)
    private String codePCompte;

    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$")
    @Column(name = "ville_compte")
    private String villeCompte;

    @OneToMany(mappedBy = "constitue")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "constitue", "estValidePar", "plats" }, allowSetters = true)
    private Set<Panier> paniers = new HashSet<>();

    @OneToMany(mappedBy = "livre")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "montant", "plats", "livre" }, allowSetters = true)
    private Set<Course> courses = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "comptes" }, allowSetters = true)
    private Cooperative membre;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compte id(Long id) {
        this.id = id;
        return this;
    }

    public String getNom() {
        return this.nom;
    }

    public Compte nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Compte prenom(String prenom) {
        this.prenom = prenom;
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return this.email;
    }

    public Compte email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Compte phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressCompte() {
        return this.addressCompte;
    }

    public Compte addressCompte(String addressCompte) {
        this.addressCompte = addressCompte;
        return this;
    }

    public void setAddressCompte(String addressCompte) {
        this.addressCompte = addressCompte;
    }

    public String getCodePCompte() {
        return this.codePCompte;
    }

    public Compte codePCompte(String codePCompte) {
        this.codePCompte = codePCompte;
        return this;
    }

    public void setCodePCompte(String codePCompte) {
        this.codePCompte = codePCompte;
    }

    public String getVilleCompte() {
        return this.villeCompte;
    }

    public Compte villeCompte(String villeCompte) {
        this.villeCompte = villeCompte;
        return this;
    }

    public void setVilleCompte(String villeCompte) {
        this.villeCompte = villeCompte;
    }

    public Set<Panier> getPaniers() {
        return this.paniers;
    }

    public Compte paniers(Set<Panier> paniers) {
        this.setPaniers(paniers);
        return this;
    }

    public Compte addPanier(Panier panier) {
        this.paniers.add(panier);
        panier.setConstitue(this);
        return this;
    }

    public Compte removePanier(Panier panier) {
        this.paniers.remove(panier);
        panier.setConstitue(null);
        return this;
    }

    public void setPaniers(Set<Panier> paniers) {
        if (this.paniers != null) {
            this.paniers.forEach(i -> i.setConstitue(null));
        }
        if (paniers != null) {
            paniers.forEach(i -> i.setConstitue(this));
        }
        this.paniers = paniers;
    }

    public Set<Course> getCourses() {
        return this.courses;
    }

    public Compte courses(Set<Course> courses) {
        this.setCourses(courses);
        return this;
    }

    public Compte addCourse(Course course) {
        this.courses.add(course);
        course.setLivre(this);
        return this;
    }

    public Compte removeCourse(Course course) {
        this.courses.remove(course);
        course.setLivre(null);
        return this;
    }

    public void setCourses(Set<Course> courses) {
        if (this.courses != null) {
            this.courses.forEach(i -> i.setLivre(null));
        }
        if (courses != null) {
            courses.forEach(i -> i.setLivre(this));
        }
        this.courses = courses;
    }

    public Cooperative getMembre() {
        return this.membre;
    }

    public Compte membre(Cooperative cooperative) {
        this.setMembre(cooperative);
        return this;
    }

    public void setMembre(Cooperative cooperative) {
        this.membre = cooperative;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Compte)) {
            return false;
        }
        return id != null && id.equals(((Compte) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Compte{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", addressCompte='" + getAddressCompte() + "'" +
            ", codePCompte='" + getCodePCompte() + "'" +
            ", villeCompte='" + getVilleCompte() + "'" +
            "}";
    }
}
