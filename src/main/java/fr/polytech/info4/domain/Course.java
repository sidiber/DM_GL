package fr.polytech.info4.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.polytech.info4.domain.enumeration.EtatCourse;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat")
    private EtatCourse etat;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @JsonIgnoreProperties(value = { "constitue", "estValidePar", "plats" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Panier montant;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "rel_course__plat", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "plat_id"))
    @JsonIgnoreProperties(value = { "restaurants", "paniers", "courses" }, allowSetters = true)
    private Set<Plat> plats = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "paniers", "courses", "membre" }, allowSetters = true)
    private Compte livre;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course id(Long id) {
        this.id = id;
        return this;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Course createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public EtatCourse getEtat() {
        return this.etat;
    }

    public Course etat(EtatCourse etat) {
        this.etat = etat;
        return this;
    }

    public void setEtat(EtatCourse etat) {
        this.etat = etat;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Course startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Course endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Panier getMontant() {
        return this.montant;
    }

    public Course montant(Panier panier) {
        this.setMontant(panier);
        return this;
    }

    public void setMontant(Panier panier) {
        this.montant = panier;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public Course plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public Course addPlat(Plat plat) {
        this.plats.add(plat);
        plat.getCourses().add(this);
        return this;
    }

    public Course removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.getCourses().remove(this);
        return this;
    }

    public void setPlats(Set<Plat> plats) {
        this.plats = plats;
    }

    public Compte getLivre() {
        return this.livre;
    }

    public Course livre(Compte compte) {
        this.setLivre(compte);
        return this;
    }

    public void setLivre(Compte compte) {
        this.livre = compte;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return id != null && id.equals(((Course) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", etat='" + getEtat() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
