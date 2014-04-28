package uk.ac.ebi.pride.data.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class represent a submitter contact details
 *
 * @author Rui Wang
 * @version $Id$
 */
public class Contact implements Serializable {
    /**
     * required
     */
    private String name;
    /**
     * required
     */
    private String email;
    /**
     * required
     */
    private String affiliation;

    /**
     * pride username, required
     */
    private String userName;

    /**
     * pride password, required
     */
    private char[] password;

    public Contact() {}

    public Contact(String name,
                   String email,
                   String affiliation,
                   String userName,
                   char[] password) {
        this.name = name;
        this.email = email;
        this.affiliation = affiliation;
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;

        Contact contact = (Contact) o;

        if (affiliation != null ? !affiliation.equals(contact.affiliation) : contact.affiliation != null) return false;
        if (email != null ? !email.equals(contact.email) : contact.email != null) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        if (!Arrays.equals(password, contact.password)) return false;
        if (userName != null ? !userName.equals(contact.userName) : contact.userName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? Arrays.hashCode(password) : 0);
        return result;
    }
}
