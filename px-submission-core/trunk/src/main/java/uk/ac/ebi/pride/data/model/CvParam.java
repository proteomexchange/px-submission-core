package uk.ac.ebi.pride.data.model;

/**
 * Controlled vocabulary
 *
 * @author Rui Wang
 * @version $Id$
 */
public class CvParam extends Param {
    private String cvLabel;
    private String accession;

    public CvParam(String cvLabel, String accession, String name, String value) {
        super(name, value);
        this.cvLabel = cvLabel;
        this.accession = accession;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CvParam)) return false;
        if (!super.equals(o)) return false;

        CvParam cvParam = (CvParam) o;

        if (!accession.equals(cvParam.accession)) return false;
        if (!cvLabel.equals(cvParam.cvLabel)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + cvLabel.hashCode();
        result = 31 * result + accession.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" + cvLabel + ", " + accession +  ", " + getName() + ", " + (getValue() == null ? "" : getValue()) + "]";
    }
}
