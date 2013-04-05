package de.wehner.mediamagpie.common.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Registration extends CreationDateBase {

    @Column(nullable = false)
    private Date _validUntil;

    /**
     * This is equal to User.name (login name or id called)
     */
    @Column(nullable = false)
    private String _user;

    /**
     * The hashed password
     */
    @Column(nullable = false)
    private String _password;

    /**
     * The user's fore name (optional)
     */
    private String _forename;

    /**
     * The user's last name (optional)
     */
    private String _surname;

    @Column(nullable = false)
    private String _email;

    private String _activationLink;

    private Date _userCreationDate = null;

    public Date getValidUntil() {
        return _validUntil;
    }

    public void setValidUntil(Date validUntil) {
        _validUntil = validUntil;
    }

    @NotEmpty
    public String getUser() {
        return _user;
    }

    public void setUser(String user) {
        _user = user;
    }

    @NotEmpty
    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public String getForename() {
        return _forename;
    }

    public void setForename(String forename) {
        _forename = forename;
    }

    public String getSurname() {
        return _surname;
    }

    public void setSurname(String surname) {
        _surname = surname;
    }

    @NotEmpty
    @Email
    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    public String getActivationLink() {
        return _activationLink;
    }

    public void setActivationLink(String activationLink) {
        _activationLink = activationLink;
    }

    public Date getUserCreationDate() {
        return _userCreationDate;
    }

    public void setUserCreationDate(Date userCreationDate) {
        _userCreationDate = userCreationDate;
    }

}
