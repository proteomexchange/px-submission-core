package uk.ac.ebi.pride.data.model;


import uk.ac.ebi.pride.archive.dataprovider.project.SubmissionType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Experiment meta data information
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ProjectMetaData implements Serializable {

    /**
     * experiment projectTitle, required
     */
    private String projectTitle;
    /**
     * a short projectDescription of the experiment, required
     */
    private String projectDescription;

    /**
     * A short description of the sample processing protocol being followed, required
     */
    private String sampleProcessingProtocol;

    /**
     * A short description of the data processing protocol being followed, required
     */
    private String dataProcessingProtocol;

    /**
     * A link to other omics datasets, optional
     */
    private String otherOmicsLink;

    /**
     * a list of keywords to describe the experiment, required
     */
    private String keywords;

    /**
     * Project submitter details, required
     */
    private Contact submitterContact = new Contact();

    /**
     * Project lab head contact details, required
     */
    private Contact labHeadContact = new Contact();

    /**
     * a list of project tags, indicates it as part of a larger project
     */
    private final Set<String> projectTags = new HashSet<String>();

    /**
     * submission type
     */
    private SubmissionType submissionType;

    /**
     * a list of mass spec experiment methods used, required
     */
    private final Set<CvParam> massSpecExperimentMethods = new HashSet<CvParam>();

    /**
     * Comments on why include unsupported result file formats, required when unsupported result files present
     */
    private String reasonForPartialSubmission;

    /**
     * a list of species in controlled vocabulary, required when unsupported result files present
     */
    private final Set<CvParam> species = new HashSet<CvParam>();

    /**
     * a list of tissue in controlled vocabulary, required when unsupported result files present
     */
    private final Set<CvParam> tissues = new HashSet<CvParam>();

    /**
     * a list of cell types in controlled vocabulary, required when unsupported result files present
     */
    private final Set<CvParam> cellTypes = new HashSet<CvParam>();

    /**
     * a list of diseases in controlled vocabulary, required when unsupported result files present
     */
    private final Set<CvParam> diseases = new HashSet<CvParam>();

    /**
     * a list of instruments in controlled vocabulary,
     */
    private final Set<CvParam> instruments = new HashSet<CvParam>();

    /**
     * a list of modifications in controlled vocabulary, required when unsupported result files present
     */
    private final Set<CvParam> modifications = new HashSet<CvParam>();

    /**
     * a list of quantifications in controlled vocabulary, required when unsupported result files present
     */
    private final Set<CvParam> quantifications = new HashSet<CvParam>();

    /**
     * a list of additional controlled vocabulary describe the experiment, optional when unsupported result files present
     */
    private final Set<Param> additional = new HashSet<Param>();

    /**
     * a list of pubmed ids, optional
     */
    private final Set<String> pubmedIds = new HashSet<String>();

    /**
     * a list of DOIs, optional
     */
    private final Set<String> dois = new HashSet<String>();

    /**
     * resubmission, reference previously submitted px accession, optional
     */
    private String resubmissionPxAccession;

    /**
     * reanalysis, reference previously submitted px accessions, optional
     */
    private final Set<String> reanalysisAccessions = new HashSet<String>();

    public ProjectMetaData() {
    }

    /**
     * Constructor for creating metadata with supported result files
     */
    public ProjectMetaData(String projectTitle,
                           String projectDescription,
                           String sampleProcessingProtocol,
                           String dataProcessingProtocol,
                           String otherOmicsLink,
                           String keywords) {
        this(projectTitle,
                projectDescription,
                sampleProcessingProtocol,
                dataProcessingProtocol,
                otherOmicsLink,
                keywords,
                new Contact(),
                new Contact(),
                null,
                null,
                SubmissionType.COMPLETE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }


    public ProjectMetaData(String projectTitle,
                           String projectDescription,
                           String sampleProcessingProtocol,
                           String dataProcessingProtocol,
                           String otherOmicsLink,
                           String keywords,
                           Contact submitterContact,
                           Contact labHeadContact,
                           Set<String> projectTags,
                           Set<CvParam> massSpecExperimentMethods,
                           SubmissionType submissionType,
                           String reasonForPartialSubmission,
                           Set<CvParam> species,
                           Set<CvParam> tissues,
                           Set<CvParam> cellTypes,
                           Set<CvParam> diseases,
                           Set<CvParam> instruments,
                           Set<CvParam> modifications,
                           Set<CvParam> quantifications,
                           Set<Param> additional,
                           Set<String> pubmedIds,
                           Set<String> dois,
                           String resubmissionPxAccession,
                           Set<String> reanalysisAccessions) {
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
        this.sampleProcessingProtocol = sampleProcessingProtocol;
        this.dataProcessingProtocol = dataProcessingProtocol;
        this.otherOmicsLink = otherOmicsLink;
        this.keywords = keywords;
        this.submitterContact = submitterContact;
        this.labHeadContact = labHeadContact;

        if (projectTags != null) {
            this.projectTags.addAll(projectTags);
        }

        if (massSpecExperimentMethods != null) {
            this.massSpecExperimentMethods.addAll(massSpecExperimentMethods);
        }

        this.submissionType = submissionType;
        this.reasonForPartialSubmission = reasonForPartialSubmission;

        if (species != null) {
            this.species.addAll(species);
        }

        if (tissues != null) {
            this.tissues.addAll(tissues);
        }

        if (cellTypes != null) {
            this.cellTypes.addAll(cellTypes);
        }

        if (diseases != null) {
            this.diseases.addAll(diseases);
        }

        if (instruments != null) {
            this.instruments.addAll(instruments);
        }

        if (modifications != null) {
            this.modifications.addAll(modifications);
        }

        if (quantifications != null) {
            this.quantifications.addAll(quantifications);
        }

        if (additional != null) {
            this.additional.addAll(additional);
        }

        if (pubmedIds != null) {
            this.pubmedIds.addAll(pubmedIds);
        }

        if (dois != null) {
            this.dois.addAll(dois);
        }

        this.resubmissionPxAccession = resubmissionPxAccession;

        if (reanalysisAccessions != null) {
            this.reanalysisAccessions.addAll(reanalysisAccessions);
        }
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public boolean isCompleteSubmission() {
        return SubmissionType.COMPLETE.equals(submissionType);
    }

    public boolean isPrideSubmission() {
        return SubmissionType.PRIDE.equals(submissionType);
    }

    public boolean isPartialSubmission() {
        return SubmissionType.PARTIAL.equals(submissionType);
    }

    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
    }

    public String getReasonForPartialSubmission() {
        return reasonForPartialSubmission;
    }

    public void setReasonForPartialSubmission(String reasonForPartialSubmission) {
        this.reasonForPartialSubmission = reasonForPartialSubmission;
    }

    public Set<CvParam> getSpecies() {
        return species;
    }

    public int getNumberOfSpecies() {
        return species.size();
    }

    public boolean hasSpecies() {
        return !species.isEmpty();
    }

    public boolean hasSpecies(CvParam species) {
        return this.species.contains(species);
    }

    public void addSpecies(CvParam ... species) {
        this.species.addAll(Arrays.asList(species));
    }

    public void removeSpecies(CvParam ... species) {
        if (species != null) {
            this.species.removeAll(Arrays.asList(species));
        }
    }

    public void clearSpecies() {
        this.species.clear();
    }

    public Set<CvParam> getTissues() {
        return tissues;
    }

    public void addTissues(CvParam... tissue) {
        tissues.addAll(Arrays.asList(tissue));
    }

    public void removeTissues(CvParam ... tissue) {
        if (tissue != null) {
            tissues.removeAll(Arrays.asList(tissue));
        }
    }

    public void clearTissues() {
        this.tissues.clear();
    }

    public Set<CvParam> getCellTypes() {
        return cellTypes;
    }

    public void addCellTypes(CvParam... cellType) {
        cellTypes.addAll(Arrays.asList(cellType));
    }

    public void removeCellTypes(CvParam ... cellType) {
        if (cellType != null) {
            cellTypes.removeAll(Arrays.asList(cellType));
        }
    }

    public void clearCellTypes() {
        this.cellTypes.clear();
    }

    public Set<CvParam> getDiseases() {
        return diseases;
    }

    public void addDiseases(CvParam... disease) {
        diseases.addAll(Arrays.asList(disease));
    }

    public void removeDiseases(CvParam ... disease) {
        if (disease != null) {
            diseases.removeAll(Arrays.asList(disease));
        }
    }

    public void clearDiseases() {
        this.diseases.clear();
    }

    public Set<CvParam> getInstruments() {
        return instruments;
    }

    public int getNumberOfInstruments() {
        return instruments.size();
    }

    public boolean hasInstruments() {
        return !instruments.isEmpty();
    }

    public boolean hasInstrument(CvParam instrument) {
        return instruments.contains(instrument);
    }

    public void addInstruments(CvParam... instrument) {
        instruments.addAll(Arrays.asList(instrument));
    }

    public void removeInstruments(CvParam ... instrument) {
        if (instrument != null) {
            instruments.removeAll(Arrays.asList(instrument));
        }
    }

    public void clearInstruments() {
        this.instruments.clear();
    }

    public Set<CvParam> getModifications() {
        return modifications;
    }

    public int getNumberOfModifications() {
        return modifications.size();
    }

    public boolean hasModifications() {
        return !modifications.isEmpty();
    }

    public boolean hasModification(CvParam modification) {
        return modifications.contains(modification);
    }

    public void addModifications(CvParam... modification) {
        modifications.addAll(Arrays.asList(modification));
    }

    public void removeModifications(CvParam ... modification) {
        if (modification != null) {
            modifications.removeAll(Arrays.asList(modification));
        }
    }

    public void clearModifications() {
        modifications.clear();
    }

    public Set<CvParam> getQuantifications() {
        return quantifications;
    }

    public int getNumberOfQuantifications() {
        return quantifications.size();
    }

    public boolean hasQuantifications() {
        return !quantifications.isEmpty();
    }

    public boolean hasQuantification(CvParam quantification) {
        return quantifications.contains(quantification);
    }

    public void addQuantifications(CvParam... quantification) {
        quantifications.addAll(Arrays.asList(quantification));
    }

    public void removeQuantifications(CvParam ... quantification) {
        if (quantification != null) {
            quantifications.removeAll(Arrays.asList(quantification));
        }
    }

    public void clearQuantifications() {
        quantifications.clear();
    }

    public Set<Param> getAdditional() {
        return additional;
    }

    public int getNumberOfAdditional() {
        return additional.size();
    }

    public boolean hasAdditional() {
        return !additional.isEmpty();
    }

    public boolean hasAdditional(CvParam al) {
        return additional.contains(al);
    }

    public void addAdditional(Param ... al) {
        additional.addAll(Arrays.asList(al));
    }

    public void removeAdditional(CvParam ... al) {
        if (al != null){
            additional.removeAll(Arrays.asList(al));
        }
    }

    public void clearAdditional() {
        this.additional.clear();
    }

    public boolean hasPubmedIds() {
        return !pubmedIds.isEmpty();
    }

    public Set<String> getPubmedIds() {
        return pubmedIds;
    }

    public int getNumberOfPubmedIds() {
        return pubmedIds.size();
    }

    public boolean hasPubmedId(String pubmedId) {
        return pubmedIds.contains(pubmedId);
    }

    public void addPubmedIds(String... pubmedId) {
        pubmedIds.addAll(Arrays.asList(pubmedId));
    }

    public void removePubmedIds(String ... pubmedId) {
        if (pubmedId != null) {
            pubmedIds.removeAll(Arrays.asList(pubmedId));
        }
    }

    public void clearPubmedIds() {
        this.pubmedIds.clear();
    }

    public boolean hasDois() {
        return !dois.isEmpty();
    }

    public Set<String> getDois() {
        return dois;
    }

    public int getNumberOfDois() {
        return dois.size();
    }

    public boolean hasDoi(String doi) {
        return dois.contains(doi);
    }

    public void addDois(String... doi) {
        dois.addAll(Arrays.asList(doi));
    }

    public void removeDois(String ... doi) {
        if (doi!= null) {
            dois.removeAll(Arrays.asList(doi));
        }
    }

    public void clearDois() {
        this.dois.clear();
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
        return reanalysisAccessions.size() > 0;
    }

    public boolean hasReanalysisPxAccessions() {
        return !reanalysisAccessions.isEmpty();
    }

    public int getNumberOfReanalysisPxAccessions() {
        return reanalysisAccessions.size();
    }

    public boolean hasReanalysisPxAccession(String px) {
        return reanalysisAccessions.contains(px);
    }

    public Set<String> getReanalysisAccessions() {
        return reanalysisAccessions;
    }

    public void addReanalysisPxAccessions(String... pxAccession) {
        reanalysisAccessions.addAll(Arrays.asList(pxAccession));
    }

    public void removeReanalysisPxAccessions(String ... pxAccession) {
        if (pxAccession != null) {
            reanalysisAccessions.removeAll(Arrays.asList(pxAccession));
        }
    }

    public void clearReanalysisPxAccessions() {
        this.reanalysisAccessions.clear();
    }

    public Contact getSubmitterContact() {
        return submitterContact;
    }

    public void setSubmitterContact(Contact submitterContact) {
        this.submitterContact = submitterContact;
    }

    public Contact getLabHeadContact() {
        return labHeadContact;
    }

    public void setLabHeadContact(Contact labHeadContact) {
        this.labHeadContact = labHeadContact;
    }

    public Set<String> getProjectTags() {
        return projectTags;
    }

    public void addProjectTags(String... projectTag) {
        this.projectTags.addAll(Arrays.asList(projectTag));
    }

    public void removeProjectTags(String ... projectTag) {
        if (projectTag != null) {
            this.projectTags.removeAll(Arrays.asList(projectTag));
        }
    }

    public void clearProjectTags() {
        this.projectTags.clear();
    }

    public String getSampleProcessingProtocol() {
        return sampleProcessingProtocol;
    }

    public void setSampleProcessingProtocol(String sampleProcessingProtocol) {
        this.sampleProcessingProtocol = sampleProcessingProtocol;
    }

    public String getDataProcessingProtocol() {
        return dataProcessingProtocol;
    }

    public void setDataProcessingProtocol(String dataProcessingProtocol) {
        this.dataProcessingProtocol = dataProcessingProtocol;
    }

    public boolean hasOtherOmicsLink() {
        return this.otherOmicsLink != null && !this.otherOmicsLink.isEmpty();
    }

    public String getOtherOmicsLink() {
        return otherOmicsLink;
    }

    public void setOtherOmicsLink(String otherOmicsLink) {
        this.otherOmicsLink = otherOmicsLink;
    }

    public Set<CvParam> getMassSpecExperimentMethods() {
        return massSpecExperimentMethods;
    }

    public int getNumberOfMassSpecExperimentMethods() {
        return this.massSpecExperimentMethods.size();
    }

    public boolean hasMassSpecExperimentMethods() {
        return !this.massSpecExperimentMethods.isEmpty();
    }

    public boolean hasMassSpecExperimentMethod(CvParam massSpecExperimentMethod) {
        return this.massSpecExperimentMethods.contains(massSpecExperimentMethod);
    }

    public void addMassSpecExperimentMethods(CvParam... massSpecExperimentMethod) {
        this.massSpecExperimentMethods.addAll(Arrays.asList(massSpecExperimentMethod));
    }

    public void removeMassSpecExperimentMethods(CvParam... massSpecExperimentMethod) {
        if (massSpecExperimentMethod != null) {
            this.massSpecExperimentMethods.removeAll(Arrays.asList(massSpecExperimentMethod));
        }
    }

    public void clearMassSpecExperimentMethods() {
        this.massSpecExperimentMethods.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMetaData)) return false;

        ProjectMetaData that = (ProjectMetaData) o;

        if (!additional.equals(that.additional)) return false;
        if (!cellTypes.equals(that.cellTypes)) return false;
        if (dataProcessingProtocol != null ? !dataProcessingProtocol.equals(that.dataProcessingProtocol) : that.dataProcessingProtocol != null)
            return false;
        if (!diseases.equals(that.diseases)) return false;
        if (!instruments.equals(that.instruments)) return false;
        if (keywords != null ? !keywords.equals(that.keywords) : that.keywords != null) return false;
        if (labHeadContact != null ? !labHeadContact.equals(that.labHeadContact) : that.labHeadContact != null)
            return false;
        if (!massSpecExperimentMethods.equals(that.massSpecExperimentMethods)) return false;
        if (!modifications.equals(that.modifications)) return false;
        if (otherOmicsLink != null ? !otherOmicsLink.equals(that.otherOmicsLink) : that.otherOmicsLink != null)
            return false;
        if (projectDescription != null ? !projectDescription.equals(that.projectDescription) : that.projectDescription != null)
            return false;
        if (!projectTags.equals(that.projectTags)) return false;
        if (projectTitle != null ? !projectTitle.equals(that.projectTitle) : that.projectTitle != null) return false;
        if (!pubmedIds.equals(that.pubmedIds)) return false;
        if (!dois.equals(that.dois)) return false;
        if (!quantifications.equals(that.quantifications)) return false;
        if (!reanalysisAccessions.equals(that.reanalysisAccessions)) return false;
        if (reasonForPartialSubmission != null ? !reasonForPartialSubmission.equals(that.reasonForPartialSubmission) : that.reasonForPartialSubmission != null)
            return false;
        if (resubmissionPxAccession != null ? !resubmissionPxAccession.equals(that.resubmissionPxAccession) : that.resubmissionPxAccession != null)
            return false;
        if (sampleProcessingProtocol != null ? !sampleProcessingProtocol.equals(that.sampleProcessingProtocol) : that.sampleProcessingProtocol != null)
            return false;
        if (!species.equals(that.species)) return false;
        if (submissionType != that.submissionType) return false;
        if (submitterContact != null ? !submitterContact.equals(that.submitterContact) : that.submitterContact != null)
            return false;
        if (!tissues.equals(that.tissues)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = projectTitle != null ? projectTitle.hashCode() : 0;
        result = 31 * result + (projectDescription != null ? projectDescription.hashCode() : 0);
        result = 31 * result + (sampleProcessingProtocol != null ? sampleProcessingProtocol.hashCode() : 0);
        result = 31 * result + (dataProcessingProtocol != null ? dataProcessingProtocol.hashCode() : 0);
        result = 31 * result + (otherOmicsLink != null ? otherOmicsLink.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        result = 31 * result + (submitterContact != null ? submitterContact.hashCode() : 0);
        result = 31 * result + (labHeadContact != null ? labHeadContact.hashCode() : 0);
        result = 31 * result + projectTags.hashCode();
        result = 31 * result + (submissionType != null ? submissionType.hashCode() : 0);
        result = 31 * result + massSpecExperimentMethods.hashCode();
        result = 31 * result + (reasonForPartialSubmission != null ? reasonForPartialSubmission.hashCode() : 0);
        result = 31 * result + species.hashCode();
        result = 31 * result + tissues.hashCode();
        result = 31 * result + cellTypes.hashCode();
        result = 31 * result + diseases.hashCode();
        result = 31 * result + instruments.hashCode();
        result = 31 * result + modifications.hashCode();
        result = 31 * result + quantifications.hashCode();
        result = 31 * result + additional.hashCode();
        result = 31 * result + pubmedIds.hashCode();
        result = 31 * result + dois.hashCode();
        result = 31 * result + (resubmissionPxAccession != null ? resubmissionPxAccession.hashCode() : 0);
        result = 31 * result + reanalysisAccessions.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProjectMetaData{" +
                "projectTitle='" + projectTitle + '\'' +
                '}';
    }
}
