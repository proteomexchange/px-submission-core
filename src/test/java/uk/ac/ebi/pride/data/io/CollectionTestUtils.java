package uk.ac.ebi.pride.data.io;

import uk.ac.ebi.pride.data.model.CvParam;
import uk.ac.ebi.pride.data.model.Param;

import java.util.Collection;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class CollectionTestUtils {

    public static boolean findCvParamAccession(Collection<CvParam> params, String accession) {
        for (CvParam param : params) {
            if (param.getAccession().equalsIgnoreCase(accession)) {
                return true;
            }
        }
        return false;
    }

    public static boolean findParamValue(Collection<Param> params, String accession) {
        for (Param param : params) {
            if (param.getValue().equalsIgnoreCase(accession)) {
                return true;
            }
        }
        return false;
    }

    public static boolean findCvParamValue(Collection<CvParam> params, String value) {
        for (CvParam param : params) {
            if (param.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
