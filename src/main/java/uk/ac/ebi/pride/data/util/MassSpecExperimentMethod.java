package uk.ac.ebi.pride.data.util;

import uk.ac.ebi.pride.data.model.CvParam;

/**
 * {@code MassSpecExperimentType} is an enumeration of predefined mass spectrometry experiment types
 *
 * @author Rui Wang
 * @version $Id$
 *
 * todo: add a complete list of mass spec experiment types
 */
public enum MassSpecExperimentMethod {

    MS1("MS1", null),
    MS2("MS2", null),
    PMF("PMF", null),
    SRM("SRM", null),
    TOP_DOWN("Top Down", null);

    private String name;
    private CvParam cvParam;

    private MassSpecExperimentMethod(String name, CvParam cvParam) {
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
