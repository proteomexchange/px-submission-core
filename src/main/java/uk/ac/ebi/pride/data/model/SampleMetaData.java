package uk.ac.ebi.pride.data.model;

import java.io.Serializable;
import java.util.*;

/**
 * {@code SampleMetaData} captures sample related metadata for each result file
 * such as: species, tissues, cell types and etc
 * <p/>
 * NOTE: some of them are mandatory, see documentation below
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SampleMetaData implements Serializable {

    public enum Type {
        SPECIES ("Species"),
        TISSUE ("Tissue"),
        CELL_TYPE ("Cell type"),
        DISEASE ("Disease"),
        INSTRUMENT ("Instrument"),
        MODIFICATION ("Modification"),
        QUANTIFICATION_METHOD ("Quantification method"),
        EXPERIMENTAL_FACTOR ("Experimental factor");

        private String name;

        private Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Map of meta data
     */
    private final Map<Type, Set<CvParam>> metaData;

    public SampleMetaData() {
        this.metaData = new HashMap<Type, Set<CvParam>>();
    }

    public Map<Type, Set<CvParam>> getMetaData() {
        return this.metaData;
    }

    public Set<CvParam> getMetaData(Type type) {
        return this.metaData.get(type);
    }

    public void setMetaData(Type type, Collection<CvParam> values) {
        LinkedHashSet<CvParam> cvParams = new LinkedHashSet<CvParam>(values);
        this.metaData.put(type, cvParams);
    }

    public void addMetaData(Type type, CvParam value) {
        Set<CvParam> params = getMetaData(type);
        if (params == null) {
            params = new LinkedHashSet<CvParam>();
            metaData.put(type, params);
        }

        params.add(value);
    }

    public void removeMetaData(Type type, CvParam value) {
        Set<CvParam> params = getMetaData(type);
        if (params != null) {
            getMetaData(type).remove(value);
        }
    }

    public boolean hasMetaData(Type type) {
        Set<CvParam> params = getMetaData(type);
        return params != null && !params.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SampleMetaData)) return false;

        SampleMetaData that = (SampleMetaData) o;

        if (!metaData.equals(that.metaData)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return metaData.hashCode();
    }
}
