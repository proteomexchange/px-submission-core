package uk.ac.ebi.pride.data.model;

import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents all the data collected for one submission
 * It is used for generating a submission file
 *
 * @author Rui Wang
 * @version $Id$
 */
public class Submission implements Serializable {
    /**
     * Experiment meta data
     */
    private ProjectMetaData projectMetaData;
    /**
     * A list of dataFiles to be submitted
     */
    private final List<DataFile> dataFiles;

    public Submission() {
        this.projectMetaData = new ProjectMetaData();
        this.dataFiles = Collections.synchronizedList(new ArrayList<DataFile>());
    }

    public Submission(ProjectMetaData projectMetaData,
                      List<DataFile> dataFiles) {
        this.projectMetaData = projectMetaData;
        this.dataFiles = dataFiles;
    }

    public ProjectMetaData getProjectMetaData() {
        return projectMetaData;
    }

    public void setProjectMetaData(ProjectMetaData projectMetaData) {
        this.projectMetaData = projectMetaData;
    }

    public synchronized List<DataFile> getDataFiles() {
        return new ArrayList<DataFile>(dataFiles);
    }

    public synchronized boolean containsDataFile(DataFile dataFile) {
        return dataFiles.contains(dataFile);
    }

    public synchronized void removeAllDataFiles() {
        dataFiles.clear();
    }

    public synchronized void addDataFile(DataFile dataFile) {
        dataFiles.add(dataFile);
    }

    public synchronized void addDataFiles(Collection<DataFile> newDataFiles) {
        dataFiles.addAll(newDataFiles);
    }

    public synchronized void removeDataFile(DataFile dataFile) {
        if (dataFile != null) {
            dataFiles.remove(dataFile);
        }
    }

    public synchronized DataFile getDataFileById(int fileId) {
        for (DataFile file : dataFiles) {
            if (file.getFileId() == fileId) {
                return file;
            }
        }

        return null;
    }

    public synchronized List<DataFile> getDataFileByType(ProjectFileType fileType) {
        List<DataFile> typedDataFiles = new ArrayList<DataFile>();

        for (DataFile dataFile : dataFiles) {
            if (dataFile.getFileType().equals(fileType)) {
                typedDataFiles.add(dataFile);
            }
        }

        return typedDataFiles;
    }

    public synchronized List<DataFile> getDataFilesByFormat(MassSpecFileFormat format) {
        List<DataFile> formattedDataFiles = new ArrayList<DataFile>();

        for (DataFile dataFile : dataFiles) {
            if (format.equals(dataFile.getFileFormat())) {
                formattedDataFiles.add(dataFile);
            }
        }

        return formattedDataFiles;
    }

    public synchronized int countDataFilesByType(ProjectFileType fileType) {
        int cnt = 0;

        for (DataFile dataFile : dataFiles) {
            if (dataFile.getFileType().equals(fileType)) {
                cnt++;
            }
        }

        return cnt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Submission)) return false;

        Submission that = (Submission) o;

        if (!dataFiles.equals(that.dataFiles)) return false;
        if (projectMetaData != null ? !projectMetaData.equals(that.projectMetaData) : that.projectMetaData != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = projectMetaData != null ? projectMetaData.hashCode() : 0;
        result = 31 * result + dataFiles.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "projectMetaData=" + projectMetaData +
                ", dataFiles=" + dataFiles +
                '}';
    }
}
