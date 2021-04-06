package fr.polytech.info4.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link fr.polytech.info4.domain.Compte} entity. This class is used
 * in {@link fr.polytech.info4.web.rest.CompteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /comptes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CompteCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private StringFilter prenom;

    private StringFilter email;

    private StringFilter phoneNumber;

    private StringFilter addressCompte;

    private StringFilter codePCompte;

    private StringFilter villeCompte;

    private LongFilter panierId;

    private LongFilter courseId;

    private LongFilter membredeId;

    public CompteCriteria() {}

    public CompteCriteria(CompteCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nom = other.nom == null ? null : other.nom.copy();
        this.prenom = other.prenom == null ? null : other.prenom.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.addressCompte = other.addressCompte == null ? null : other.addressCompte.copy();
        this.codePCompte = other.codePCompte == null ? null : other.codePCompte.copy();
        this.villeCompte = other.villeCompte == null ? null : other.villeCompte.copy();
        this.panierId = other.panierId == null ? null : other.panierId.copy();
        this.courseId = other.courseId == null ? null : other.courseId.copy();
        this.membredeId = other.membredeId == null ? null : other.membredeId.copy();
    }

    @Override
    public CompteCriteria copy() {
        return new CompteCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNom() {
        return nom;
    }

    public StringFilter nom() {
        if (nom == null) {
            nom = new StringFilter();
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getPrenom() {
        return prenom;
    }

    public StringFilter prenom() {
        if (prenom == null) {
            prenom = new StringFilter();
        }
        return prenom;
    }

    public void setPrenom(StringFilter prenom) {
        this.prenom = prenom;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new StringFilter();
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getAddressCompte() {
        return addressCompte;
    }

    public StringFilter addressCompte() {
        if (addressCompte == null) {
            addressCompte = new StringFilter();
        }
        return addressCompte;
    }

    public void setAddressCompte(StringFilter addressCompte) {
        this.addressCompte = addressCompte;
    }

    public StringFilter getCodePCompte() {
        return codePCompte;
    }

    public StringFilter codePCompte() {
        if (codePCompte == null) {
            codePCompte = new StringFilter();
        }
        return codePCompte;
    }

    public void setCodePCompte(StringFilter codePCompte) {
        this.codePCompte = codePCompte;
    }

    public StringFilter getVilleCompte() {
        return villeCompte;
    }

    public StringFilter villeCompte() {
        if (villeCompte == null) {
            villeCompte = new StringFilter();
        }
        return villeCompte;
    }

    public void setVilleCompte(StringFilter villeCompte) {
        this.villeCompte = villeCompte;
    }

    public LongFilter getPanierId() {
        return panierId;
    }

    public LongFilter panierId() {
        if (panierId == null) {
            panierId = new LongFilter();
        }
        return panierId;
    }

    public void setPanierId(LongFilter panierId) {
        this.panierId = panierId;
    }

    public LongFilter getCourseId() {
        return courseId;
    }

    public LongFilter courseId() {
        if (courseId == null) {
            courseId = new LongFilter();
        }
        return courseId;
    }

    public void setCourseId(LongFilter courseId) {
        this.courseId = courseId;
    }

    public LongFilter getMembredeId() {
        return membredeId;
    }

    public LongFilter membredeId() {
        if (membredeId == null) {
            membredeId = new LongFilter();
        }
        return membredeId;
    }

    public void setMembredeId(LongFilter membredeId) {
        this.membredeId = membredeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CompteCriteria that = (CompteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(prenom, that.prenom) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(addressCompte, that.addressCompte) &&
            Objects.equals(codePCompte, that.codePCompte) &&
            Objects.equals(villeCompte, that.villeCompte) &&
            Objects.equals(panierId, that.panierId) &&
            Objects.equals(courseId, that.courseId) &&
            Objects.equals(membredeId, that.membredeId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prenom, email, phoneNumber, addressCompte, codePCompte, villeCompte, panierId, courseId, membredeId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompteCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nom != null ? "nom=" + nom + ", " : "") +
            (prenom != null ? "prenom=" + prenom + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (addressCompte != null ? "addressCompte=" + addressCompte + ", " : "") +
            (codePCompte != null ? "codePCompte=" + codePCompte + ", " : "") +
            (villeCompte != null ? "villeCompte=" + villeCompte + ", " : "") +
            (panierId != null ? "panierId=" + panierId + ", " : "") +
            (courseId != null ? "courseId=" + courseId + ", " : "") +
            (membredeId != null ? "membredeId=" + membredeId + ", " : "") +
            "}";
    }
}
