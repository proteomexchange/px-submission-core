package uk.ac.ebi.pride.data.mztab.model;

import uk.ac.ebi.pride.data.mztab.exceptions.InvalidCvParameterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Project: px-submission-core
 * Package: uk.ac.ebi.pride.data.mztab.model
 * Timestamp: 2016-08-24 9:55
 * ---
 * © 2016 Manuel Bernal Llinares <mbdebian@gmail.com>
 * All rights reserved.
 */
public class Instrument {

    // Name
    public class Name extends CvParameter {
        public Name(String label, String accession, String name, String value) throws InvalidCvParameterException {
            super(label, accession, name, value);
        }

        public Name(CvParameter cv) {
            super(cv);
        }
    }

    // Source
    public class Source extends CvParameter {
        public Source(String label, String accession, String name, String value) throws InvalidCvParameterException {
            super(label, accession, name, value);
        }

        public Source(CvParameter cv) {
            super(cv);
        }
    }

    // Analyzer
    public class Analyzer extends CvParameter {
        public Analyzer(String label, String accession, String name, String value) throws InvalidCvParameterException {
            super(label, accession, name, value);
        }

        public Analyzer(CvParameter cv) {
            super(cv);
        }
    }

    // Detector
    public class Detector extends CvParameter {
        public Detector(String label, String accession, String name, String value) throws InvalidCvParameterException {
            super(label, accession, name, value);
        }

        public Detector(CvParameter cv) {
            super(cv);
        }
    }

    // Attributes
    private Name name = null;
    private Source source = null;
    private Detector detector = null;
    private Map<Integer, Analyzer> analyzers = new HashMap<>();

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Detector getDetector() {
        return detector;
    }

    public void setDetector(Detector detector) {
        this.detector = detector;
    }

    // Analyzers management
    public Analyzer updateAnalyzer(Analyzer analyzer, int index) {
        return analyzers.put(index, analyzer);
    }

    public Analyzer getAnalyzerEntry(int index) {
        return analyzers.get(index);
    }

    public Set<Integer> getAvailableAnalyzerIndexes() {
        return analyzers.keySet();
    }

    // Validation criteria
    public boolean validate() throws ValidationException {
        // There should be, at least, a name, otherwise, having an instance of this has no purpose
        return (name != null) && name.validate();
    }
}
