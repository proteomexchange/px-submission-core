package uk.ac.ebi.pride.data.util;

import uk.ac.ebi.pride.data.model.CvParam;

/**
 * {@code MassSpecQuantificationMethod} defined a list of mass spec related quantification methods
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum MassSpecQuantificationMethod {

    ITRAQ("ITRAQ", null);

    private String name;
    private CvParam cvParam;

    private MassSpecQuantificationMethod(String name, CvParam cvParam) {
        this.name = name;
        this.cvParam = cvParam;
    }

    public String getName() {
        return name;
    }

    public CvParam getCvParam() {
        return cvParam;
    }
}
