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
 * A Plat.
 */
@Entity
@Table(name = "plat")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Plat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z_ ]$")
    @Column(name = "nom_plat", nullable = false)
    private String nomPlat;

    @Lob
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "prix", nullable = false)
    private String prix;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_plat__restaurant",
        joinColumns = @JoinColumn(name = "plat_id"),
        inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    @JsonIgnoreProperties(value = { "plats" }, allowSetters = true)
    private Set<Restaurant> restaurants = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "rel_plat__panier", joinColumns = @JoinColumn(name = "plat_id"), inverseJoinColumns = @JoinColumn(name = "panier_id"))
    @JsonIgnoreProperties(value = { "constitue", "estValidePar", "plats" }, allowSetters = true)
    private Set<Panier> paniers = new HashSet<>();

    @ManyToMany(mappedBy = "plats")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "montant", "plats", "livre" }, allowSetters = true)
    private Set<Course> courses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Plat id(Long id) {
        this.id = id;
        return this;
    }

    public String getNomPlat() {
        return this.nomPlat;
    }

    public Plat nomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
        return this;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public String getDescription() {
        return this.description;
    }

    public Plat description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrix() {
        return this.prix;
    }

    public Plat prix(String prix) {
        this.prix = prix;
        return this;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Plat photo(byte[] photo) {
        this.photo = photo;
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Plat photoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Set<Restaurant> getRestaurants() {
        return this.restaurants;
    }

    public Plat restaurants(Set<Restaurant> restaurants) {
        this.setRestaurants(restaurants);
        return this;
    }

    public Plat addRestaurant(Restaurant restaurant) {
        this.restaurants.add(restaurant);
        restaurant.getPlats().add(this);
        return this;
    }

    public Plat removeRestaurant(Restaurant restaurant) {
        this.restaurants.remove(restaurant);
        restaurant.getPlats().remove(this);
        return this;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public Set<Panier> getPaniers() {
        return this.paniers;
    }

    public Plat paniers(Set<Panier> paniers) {
        this.setPaniers(paniers);
        return this;
    }

    public Plat addPanier(Panier panier) {
        this.paniers.add(panier);
        panier.getPlats().add(this);
        return this;
    }

    public Plat removePanier(Panier panier) {
        this.paniers.remove(panier);
        panier.getPlats().remove(this);
        return this;
    }

    public void setPaniers(Set<Panier> paniers) {
        this.paniers = paniers;
    }

    public Set<Course> getCourses() {
        return this.courses;
    }

    public Plat courses(Set<Course> courses) {
        this.setCourses(courses);
        return this;
    }

    public Plat addCourse(Course course) {
        this.courses.add(course);
        course.getPlats().add(this);
        return this;
    }

    public Plat removeCourse(Course course) {
        this.courses.remove(course);
        course.getPlats().remove(this);
        return this;
    }

    public void setCourses(Set<Course> courses) {
        if (this.courses != null) {
            this.courses.forEach(i -> i.removePlat(this));
        }
        if (courses != null) {
            courses.forEach(i -> i.addPlat(this));
        }
        this.courses = courses;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plat)) {
            return false;
        }
        return id != null && id.equals(((Plat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plat{" +
            "id=" + getId() +
            ", nomPlat='" + getNomPlat() + "'" +
            ", description='" + getDescription() + "'" +
            ", prix='" + getPrix() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", photoContentType='" + getPhotoContentType() + "'" +
            "}";
    }
}
