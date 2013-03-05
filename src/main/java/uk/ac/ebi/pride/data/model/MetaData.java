package uk.ac.ebi.pride.data.model;

import uk.ac.ebi.pride.data.util.SubmissionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Experiment meta data information
 *
 * @author Rui Wang
 * @version $Id$
 */
public class MetaData implements Serializable {
    /**
     * experiment title, required
     */
    private String title;
    /**
     * a short description of the experiment, required
     */
    private String description;
    /**
     * a list of keywords to describe the experiment, required
     */
    private String keywords;
    /**
     * submission type
     */
    private SubmissionType submissionType;
    /**
     * Comments on why include unsupported result file formats, required when unsupported result files present
     */
    private String comment;
    /**
     * a list of species in controlled vocabulary, required when unsupported result files present
     */
    private List<CvParam> species;
    /**
     * a list of instruments, each instrument is represented using a list of controlled vocabularies,
     * required when unsupported result files present
     */
    private List<List<CvParam>> instruments;
    /**
     * a list of modifications in controlled vocabulary, required when unsupported result files present
     */
    private List<CvParam> modifications;
    /**
     * a list of additional controlled vocabulary describe the experiment, optional when unsupported result files present
     */
    private List<CvParam> additional;
    /**
     * a list of pubmed ids, optional
     */
    private List<String> pubmedIds;
    /**
     * resubmission, reference previously submitted px accession, optional
     */
    private String resubmissionPxAccession;
    /**
     * reanalysis, reference previously submitted px accessions, optional
     */
    private List<String> reanalysisAccessions;

    public MetaData() {
    }

    /**
     * Constructor for creating metadata with supported result files
     */
    public MetaData(String title,
                    String description,
                    String keywords) {
        this(title, description, keywords, SubmissionType.SUPPORTED, null, null, null, null, null, null, null, null);
    }

    public MetaData(String title,
                    String description,
                    String keywords,
                    SubmissionType submissionType,
                    String comment,
                    List<CvParam> species,
                    List<List<CvParam>> instruments,
                    List<CvParam> modifications,
                    List<CvParam> additional,
                    List<String> pubmedIds,
                    String resubmissionPxAccession,
                    List<String> reanalysisAccessions) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.submissionType = submissionType;
        this.comment = comment;
        this.species = species;
        this.instruments = instruments;
        this.modifications = modifications;
        this.additional = additional;
        this.pubmedIds = pubmedIds;
        this.resubmissionPxAccession = resubmissionPxAccession;
        this.reanalysisAccessions = reanalysisAccessions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public boolean isSupported() {
        return SubmissionType.SUPPORTED.equals(submissionType);
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<CvParam> getSpecies() {
        return species;
    }

    public int getNumberOfSpecies() {
        return species == null ? 0 : species.size();
    }

    public boolean hasSpecies() {
        return species != null && !species.isEmpty();
    }

    public boolean hasSpecies(CvParam cvParam) {
        if (cvParam== null || species == null) {
            return false;
        } else {
            return species.contains(cvParam);
        }
    }

    public void addSpecies(CvParam cvParam) {
        if (species == null) {
            species = new ArrayList<CvParam>();
        }
        species.add(cvParam);
    }

    public void removeSpecies(CvParam cvParam) {
        if (species != null) {
            species.remove(cvParam);
        }
    }

    public void setSpecies(List<CvParam> species) {
        this.species = species;
    }

    public List<List<CvParam>> getInstruments() {
        return instruments;
    }

    public int getNumberOfInstruments() {
        return instruments == null ? 0 : instruments.size();
    }

    public boolean hasInstruments() {
        return instruments != null && !instruments.isEmpty();
    }

    public boolean hasInstrument(List<CvParam> instrument) {
        if (instrument == null || instruments == null) {
            return false;
        } else {
            return instruments.contains(instrument);
        }
    }

    public void addInstrument(List<CvParam> instrument) {
        if (instruments == null) {
            instruments = new ArrayList<List<CvParam>>();
        }
        instruments.add(instrument);
    }

    public void removeInstrument(List<CvParam> instrument) {
        if (instruments != null) {
            instruments.remove(instrument);
        }
    }

    public void setInstruments(List<List<CvParam>> instruments) {
        this.instruments = instruments;
    }

    public List<CvParam> getModifications() {
        return modifications;
    }

    public int getNumberOfModifications() {
        return modifications == null ? 0 : modifications.size();
    }

    public boolean hasModifications() {
        return modifications != null && !modifications.isEmpty();
    }

    public boolean hasModification(CvParam modification) {
        if (modification == null || modifications == null) {
            return false;
        } else {
            return modifications.contains(modification);
        }
    }

    public void addModification(CvParam modification) {
        if (modifications == null) {
            modifications = new ArrayList<CvParam>();
        }
        modifications.add(modification);
    }

    public void removeModification(CvParam modification) {
        if (modifications != null) {
            modifications.remove(modification);
        }
    }

    public void setModifications(List<CvParam> modifications) {
        this.modifications = modifications;
    }

    public List<CvParam> getAdditional() {
        return additional;
    }

    public int getNumberOfAdditional() {
        return additional == null ? 0 : additional.size();
    }

    public boolean hasAdditional() {
        return additional != null && !additional.isEmpty();
    }

    public boolean hasAdditional(CvParam al) {
        if (al == null || additional == null) {
            return false;
        } else {
            return additional.contains(al);
        }
    }

    public void addAdditional(CvParam al) {
        if (additional == null) {
            additional = new ArrayList<CvParam>();
        }
        additional.add(al);
    }

    public void removeAdditional(CvParam al) {
        if (additional != null) {
            additional.remove(al);
        }
    }

    public void setAdditional(List<CvParam> additional) {
        this.additional = additional;
    }

    public boolean hasPubmedIds() {
        return pubmedIds != null && !pubmedIds.isEmpty();
    }

    public List<String> getPubmedIds() {
        return pubmedIds;
    }

    public int getNumberOfPubmedIds() {
        return pubmedIds == null ? 0 : pubmedIds.size();
    }

    public boolean hasPubmedId(String pubmedId) {
        if (pubmedId == null || pubmedIds == null) {
            return false;
        } else {
            return pubmedIds.contains(pubmedId);
        }
    }

    public void addPubmedId(String pubmedId) {
        if (pubmedIds == null) {
            pubmedIds = new ArrayList<String>();
        }
        pubmedIds.add(pubmedId);
    }

    public void removePubmedId(String pubmedId) {
        if (pubmedIds != null) {
            pubmedIds.remove(pubmedId);
        }
    }

    public void setPubmedIds(List<String> pubmedIds) {
        this.pubmedIds = pubmedIds;
    }

    public boolean isResubmission() {
        return resubmissionPxAccession != null;
    }

    public String getResubmissionPxAccession() {
        return resubmissionPxAccession;
    }

    public void setResubmissionPxAccession(String resubmissionPxAccession) {
        this.resubmissionPxAccession = resubmissionPxAccession;
    }

    public boolean isReanalysis() {
        return reanalysisAccessions != null && reanalysisAccessions.size() > 0;
    }

    public boolean hasReanalysisPxAccessions() {
        return reanalysisAccessions != null && !reanalysisAccessions.isEmpty();
    }

    public int getNumberOfReanalysisPxAccessions() {
        return reanalysisAccessions == null ? 0 : reanalysisAccessions.size();
    }

    public boolean hasReanalysisPxAccession(String px) {
        if (px == null || reanalysisAccessions == null) {
            return false;
        } else {
            return reanalysisAccessions.contains(px);
        }
    }

    public List<String> getReanalysisAccessions() {
        return reanalysisAccessions;
    }

    public void addReanalysisPxAccession(String pxAccession) {
        if (reanalysisAccessions == null) {
            reanalysisAccessions = new ArrayList<String>();
        }
        reanalysisAccessions.add(pxAccession);
    }

    public void removeReanalysisPxAccession(String pxAccession) {
        if (reanalysisAccessions != null) {
            reanalysisAccessions.remove(pxAccession);
        }
    }

    public void setReanalysisAccessions(List<String> reanalysisAccessions) {
        this.reanalysisAccessions = reanalysisAccessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaData)) return false;

        MetaData metaData = (MetaData) o;

        if (additional != null ? !additional.equals(metaData.additional) : metaData.additional != null) return false;
        if (comment != null ? !comment.equals(metaData.comment) : metaData.comment != null) return false;
        if (description != null ? !description.equals(metaData.description) : metaData.description != null)
            return false;
        if (instruments != null ? !instruments.equals(metaData.instruments) : metaData.instruments != null)
            return false;
        if (keywords != null ? !keywords.equals(metaData.keywords) : metaData.keywords != null) return false;
        if (modifications != null ? !modifications.equals(metaData.modifications) : metaData.modifications != null)
            return false;
        if (pubmedIds != null ? !pubmedIds.equals(metaData.pubmedIds) : metaData.pubmedIds != null) return false;
        if (reanalysisAccessions != null ? !reanalysisAccessions.equals(metaData.reanalysisAccessions) : metaData.reanalysisAccessions != null)
            return false;
        if (resubmissionPxAccession != null ? !resubmissionPxAccession.equals(metaData.resubmissionPxAccession) : metaData.resubmissionPxAccession != null)
            return false;
        if (species != null ? !species.equals(metaData.species) : metaData.species != null) return false;
        if (submissionType != metaData.submissionType) return false;
        if (title != null ? !title.equals(metaData.title) : metaData.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        result = 31 * result + (submissionType != null ? submissionType.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (species != null ? species.hashCode() : 0);
        result = 31 * result + (instruments != null ? instruments.hashCode() : 0);
        result = 31 * result + (modifications != null ? modifications.hashCode() : 0);
        result = 31 * result + (additional != null ? additional.hashCode() : 0);
        result = 31 * result + (pubmedIds != null ? pubmedIds.hashCode() : 0);
        result = 31 * result + (resubmissionPxAccession != null ? resubmissionPxAccession.hashCode() : 0);
        result = 31 * result + (reanalysisAccessions != null ? reanalysisAccessions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", submissionType=" + submissionType +
                ", comment='" + comment + '\'' +
                ", species=" + species +
                ", instruments=" + instruments +
                ", modifications=" + modifications +
                ", additional=" + additional +
                ", pubmedIds=" + pubmedIds +
                ", resubmissionPxAccession='" + resubmissionPxAccession + '\'' +
                ", reanalysisAccessions=" + reanalysisAccessions +
                '}';
    }
}
