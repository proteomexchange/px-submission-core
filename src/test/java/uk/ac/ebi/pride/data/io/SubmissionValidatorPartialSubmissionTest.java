package uk.ac.ebi.pride.data.io;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.archive.dataprovider.project.SubmissionType;
import uk.ac.ebi.pride.data.model.CvParam;
import uk.ac.ebi.pride.data.model.Submission;
import uk.ac.ebi.pride.data.validation.SubmissionValidator;
import uk.ac.ebi.pride.data.validation.ValidationReport;

import java.io.File;
import java.net.URL;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Test submission validator
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionValidatorPartialSubmissionTest {
    private File inputFile;

    @Before
    public void setUp() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("partialSubmissionFile.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        inputFile = new File(url.toURI());

    }

    @Test
    public void metaDataIsValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        ValidationReport report = SubmissionValidator.validateProjectMetaData(submission.getProjectMetaData());
        assertEquals(true, report.hasSuccess());
        assertEquals(false, report.hasError());
    }

    @Test
    public void speciesMustBeNEWT() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> species = submission.getProjectMetaData().getSpecies();
        species.iterator().next().setCvLabel("TEST");
        assertEquals(true, SubmissionValidator.validateSpecies(species).hasError());
        assertEquals(false, SubmissionValidator.validateSpecies(species).hasSuccess());
    }

    @Test
    public void tissuesMustBeBTO() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> tissues = submission.getProjectMetaData().getTissues();
        tissues.iterator().next().setCvLabel("TEST");
        assertEquals(true, SubmissionValidator.validateTissues(tissues).hasError());
        assertEquals(false, SubmissionValidator.validateTissues(tissues).hasSuccess());
    }

    @Test
    public void cellTypeMustBeCL() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> cellTypes = submission.getProjectMetaData().getCellTypes();
        cellTypes.iterator().next().setCvLabel("TEST");
        assertEquals(true, SubmissionValidator.validateCellTypes(cellTypes).hasError());
        assertEquals(false, SubmissionValidator.validateCellTypes(cellTypes).hasSuccess());
    }

    @Test
    public void diseaseMustBeDOID() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> diseases = submission.getProjectMetaData().getDiseases();
        diseases.iterator().next().setCvLabel("TEST");
        assertEquals(true, SubmissionValidator.validateDiseases(diseases).hasError());
        assertEquals(false, SubmissionValidator.validateDiseases(diseases).hasSuccess());
    }

    @Test
    public void modificationMustBeEitherPSIMODorUniMod() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> modifications = submission.getProjectMetaData().getModifications();
        modifications.iterator().next().setCvLabel("TEST");
        SubmissionType submissionType = submission.getProjectMetaData().getSubmissionType();
        assertEquals(true, SubmissionValidator.validateModifications(modifications, submissionType).hasError());
        assertEquals(false, SubmissionValidator.validateModifications(modifications, submissionType).hasSuccess());
    }

    @Test
    public void quantificationMustBePRIDE() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> quantifications = submission.getProjectMetaData().getQuantifications();
        quantifications.iterator().next().setCvLabel("TEST");
        assertEquals(true, SubmissionValidator.validateQuantifications(quantifications).hasError());
        assertEquals(false, SubmissionValidator.validateQuantifications(quantifications).hasSuccess());
    }

    @Test
    public void fileMappingsMustBeValid() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        assertEquals(true, SubmissionValidator.validateFileMappings(submission).hasError());
        assertEquals(false, SubmissionValidator.validateFileMappings(submission).hasSuccess());
    }

    @Test
    public void fileMappingsAreInvalid() throws Exception {
        URL url = SubmissionFileParser.class.getClassLoader().getResource("badPartialSubmissionFile.px");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        Submission submission = SubmissionFileParser.parse(new File(url.toURI()));
        assertEquals(true, SubmissionValidator.validateFileMappings(submission).hasError());
    }

    @Test
    public void modificationIsMandatory() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> modifications = submission.getProjectMetaData().getModifications();
        modifications.clear();
        SubmissionType submissionType = submission.getProjectMetaData().getSubmissionType();
        assertEquals(true, SubmissionValidator.validateModifications(modifications, submissionType).hasError());
        assertEquals(false, SubmissionValidator.validateModifications(modifications, submissionType).hasSuccess());
    }

    @Test
    public void speciesIsMandatory() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> species = submission.getProjectMetaData().getSpecies();
        species.clear();
        assertEquals(true, SubmissionValidator.validateSpecies(species).hasError());
        assertEquals(false, SubmissionValidator.validateSpecies(species).hasSuccess());
    }

    @Test
    public void tissueIsMandatory() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> tissues = submission.getProjectMetaData().getTissues();
        tissues.clear();
        assertEquals(true, SubmissionValidator.validateTissues(tissues).hasError());
        assertEquals(false, SubmissionValidator.validateTissues(tissues).hasSuccess());
    }

    @Test
    public void cellTypeIsOptional() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> cellTypes = submission.getProjectMetaData().getCellTypes();
        cellTypes.clear();
        assertEquals(true, SubmissionValidator.validateCellTypes(cellTypes).hasWarning());
        assertEquals(false, SubmissionValidator.validateCellTypes(cellTypes).hasError());
        assertEquals(false, SubmissionValidator.validateCellTypes(cellTypes).hasSuccess());
    }

    @Test
    public void diseaseIsOptional() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> disease = submission.getProjectMetaData().getDiseases();
        disease.clear();
        assertEquals(true, SubmissionValidator.validateDiseases(disease).hasWarning());
        assertEquals(false, SubmissionValidator.validateDiseases(disease).hasError());
        assertEquals(false, SubmissionValidator.validateDiseases(disease).hasSuccess());
    }

    @Test
    public void quantificationIsOptional() throws Exception {
        Submission submission = SubmissionFileParser.parse(inputFile);
        Set<CvParam> quantifications = submission.getProjectMetaData().getQuantifications();
        quantifications.clear();
        assertEquals(true, SubmissionValidator.validateQuantifications(quantifications).hasWarning());
        assertEquals(false, SubmissionValidator.validateQuantifications(quantifications).hasError());
        assertEquals(false, SubmissionValidator.validateQuantifications(quantifications).hasSuccess());
    }


}
