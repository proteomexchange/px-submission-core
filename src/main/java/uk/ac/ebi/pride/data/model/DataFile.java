package uk.ac.ebi.pride.data.model;

import uk.ac.ebi.pride.data.util.FileURLUtil;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileType;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@code DataFile} represents each data file to be submitted
 *
 * @author Rui Wang
 * @version $Id$
 */
public class DataFile implements Serializable {

    /**
     * Unique id to identify this data file, required
     */
    private int fileId = -1;
    /**
     * File object represents the actual file, note: this could also be an URL, required if url is null
     */
    private File file;
    /**
     * URL represents the location of the data file, required if file is null
     */
    private URL url;
    /**
     * The type of the file, required
     * Note: this may not be the same as the file type defined in fileFormat, user may manually assign a new file type
     */
    private ProjectFileType fileType;
    /**
     * The format of the file, optional
     */
    private MassSpecFileFormat fileFormat;
    /**
     * All related data files, optional
     */
    private final List<DataFile> fileMappings = Collections.synchronizedList(new ArrayList<DataFile>());
    /**
     * additional metadata, optional, should only be assigned to result file
     */
    private SampleMetaData sampleMetaData;
    /**
     * Assigned PRIDE accession, optional, should only be assigned to result file
     */
    private String assayAccession;

    public DataFile() {
    }

    public DataFile(File file, MassSpecFileFormat fileFormat) {
        this(-1, file, null, fileFormat, new ArrayList<DataFile>(), null);
    }

    public DataFile(URL url, MassSpecFileFormat fileFormat) {
        this(-1, null, url, fileFormat, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    File file,
                    MassSpecFileFormat fileFormat) {
        this(id, file, null, fileFormat, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    URL url,
                    MassSpecFileFormat fileFormat) {
        this(id, null, url, fileFormat, new ArrayList<DataFile>(), null);
    }

    public DataFile(File file, ProjectFileType fileType) {
        this(-1, file, null, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(URL url, ProjectFileType fileType) {
        this(-1, null, url, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    File file,
                    ProjectFileType fileType) {
        this(id, file, null, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    URL url,
                    ProjectFileType fileType) {
        this(id, null, url, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    File file,
                    URL url,
                    ProjectFileType fileType,
                    List<DataFile> mappings,
                    String assayAccession) {
        this.fileId = id;
        this.file = file;
        this.url = url;
        this.fileType = fileType;
        addFileMappings(mappings);
        this.assayAccession = assayAccession;

        if (this.fileType != null && this.fileType.equals(ProjectFileType.RESULT)) {
            this.sampleMetaData = new SampleMetaData();
        }
    }

    public DataFile(int id,
                    File file,
                    URL url,
                    MassSpecFileFormat fileFormat,
                    List<DataFile> mappings,
                    String assayAccession) {
        this.fileId = id;
        this.file = file;
        this.url = url;
        this.fileFormat = fileFormat;
        this.fileType = fileFormat.getFileType();
        addFileMappings(mappings);
        this.assayAccession = assayAccession;

        if (this.fileType != null && this.fileType.equals(ProjectFileType.RESULT)) {
            this.sampleMetaData = new SampleMetaData();
        }
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public boolean isFile() {
        return file != null;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isUrl() {
        return url != null && !url.toString().isEmpty();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public ProjectFileType getFileType() {
        return fileType;
    }

    public void setFileType(ProjectFileType fileType) {
        this.fileType = fileType;
    }

    public MassSpecFileFormat getFileFormat() {
        if (fileFormat == null && ((file != null && file.exists()) || url != null)) {
            try {
                if (isFile()) {
                    fileFormat = MassSpecFileFormat.checkFormat(file);
                } else if (isUrl()) {
                    fileFormat = MassSpecFileFormat.checkFormat(url);
                }
            } catch (IOException e) {
                // do nothing here
            } catch (URISyntaxException e) {
                // do nothing here
            }
        }
        return fileFormat;
    }

    public void setFileFormat(MassSpecFileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public boolean hasMappings() {
        return fileMappings.size() > 0;
    }

    public boolean hasRawMappings() {
        for (DataFile fileMapping : fileMappings) {
            if (fileMapping.getFileType().equals(ProjectFileType.RAW)) {
                return true;
            }
        }

        return false;
    }

    public List<DataFile> getFileMappings() {
        return new ArrayList<DataFile>(fileMappings);
    }

    public boolean containsFileMapping(DataFile mapping) {
        return fileMappings.contains(mapping);
    }

    public void removeAllFileMappings() {
        fileMappings.clear();
    }

    public void removeFileMapping(DataFile fileMapping) {
        if (fileMapping != null) {
            fileMappings.remove(fileMapping);
        }
    }

    public void addFileMappings(Collection<DataFile> mappings) {
        if (mappings != null) {
            fileMappings.addAll(mappings);
        }
    }

    public void addFileMapping(DataFile fileMapping) {
        fileMappings.add(fileMapping);
    }

    public SampleMetaData getSampleMetaData() {
        return sampleMetaData;
    }

    public void setSampleMetaData(SampleMetaData sampleMetaData) {
        this.sampleMetaData = sampleMetaData;
    }

    public String getAssayAccession() {
        return assayAccession;
    }

    public void setAssayAccession(String assayAccession) {
        this.assayAccession = assayAccession;
    }

    public String getFileName() {
        String fileName = null;

        if (isFile()) {
            fileName = file.getName();
        } else if (isUrl()) {
            fileName = FileURLUtil.getFileName(url);
        }

        return fileName;
    }

    public String getFilePath() {
        String path = null;

        if (isFile()) {
            path = file.getAbsolutePath();
        } else if (isUrl()) {
            path = url.getFile();
        }
        return path;
    }

    public long getFileSize() {
        long fileSizeInBytes = 0;

        if (isFile()) {
            fileSizeInBytes = file.length();
        } else if (isUrl()) {
            fileSizeInBytes = FileURLUtil.getFileSize(url);
        }

        return fileSizeInBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataFile)) return false;

        DataFile dataFile = (DataFile) o;

        if (fileId != dataFile.fileId) return false;
        if (assayAccession != null ? !assayAccession.equals(dataFile.assayAccession) : dataFile.assayAccession != null)
            return false;
        if (file != null ? !file.equals(dataFile.file) : dataFile.file != null) return false;
        if (fileFormat != dataFile.fileFormat) return false;
        if (!fileMappings.equals(dataFile.fileMappings)) return false;
        if (fileType != dataFile.fileType) return false;
        if (sampleMetaData != null ? !sampleMetaData.equals(dataFile.sampleMetaData) : dataFile.sampleMetaData != null)
            return false;
        if (url != null ? !url.equals(dataFile.url) : dataFile.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileId;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
        result = 31 * result + (fileFormat != null ? fileFormat.hashCode() : 0);
        result = 31 * result + fileMappings.hashCode();
        result = 31 * result + (sampleMetaData != null ? sampleMetaData.hashCode() : 0);
        result = 31 * result + (assayAccession != null ? assayAccession.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataFile{" +
                "fileId=" + fileId +
                ", file=" + file +
                ", url=" + url +
                ", fileType=" + fileType +
                ", fileFormat=" + fileFormat +
                ", fileMappings=" + fileMappings +
                ", sampleMetaData=" + sampleMetaData +
                ", assayAccession='" + assayAccession + '\'' +
                '}';
    }
}
