package uk.ac.ebi.pride.data.util;

import uk.ac.ebi.pride.data.model.CvParam;

/**
 * @author Rui Wang
 * @version $Id$
 */
public final class ExperimentalFactorUtil {
    private ExperimentalFactorUtil() {
    }

    public static CvParam getExperimentalFactorCvParam(String experimentalFactor) {
        return new CvParam(Constant.EFO, Constant.EXPERIMENTAL_FACTOR_ACCESSION, Constant.EXPERIMENTAL_FACTOR_NAME, experimentalFactor);
    }
}
