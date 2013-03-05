package uk.ac.ebi.pride.data.model;

import java.io.Serializable;

/**
 * Controlled vocabulary
 *
 * @author Rui Wang
 * @version $Id$
 */
public class CvParam implements Serializable {
    private String cvLabel;
    private String accession;
    private String name;
    private String value;

    public CvParam(String cvLabel, String accession, String name, String value) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.value = value;
    }

    public String getCvLabel() {
        return cvLabel;
    }

    public void setCvLabel(String cvLabel) {
        this.cvLabel = cvLabel;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CvParam)) return false;

        CvParam cvParam = (CvParam) o;

        if (accession != null ? !accession.equals(cvParam.accession) : cvParam.accession != null) return false;
        if (cvLabel != null ? !cvLabel.equals(cvParam.cvLabel) : cvParam.cvLabel != null) return false;
        if (name != null ? !name.equals(cvParam.name) : cvParam.name != null) return false;
        if (value != null ? !value.equals(cvParam.value) : cvParam.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cvLabel != null ? cvLabel.hashCode() : 0;
        result = 31 * result + (accession != null ? accession.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CvParam{" +
                "cvLabel='" + cvLabel + '\'' +
                ", accession='" + accession + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
