package uk.ac.ebi.pride.data.model;

import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.data.util.MassSpecFileType;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This file represents each data file to be submitted
 *
 * @author Rui Wang
 * @version $Id$
 */
public class DataFile implements Serializable {

    /**
     * Unique id to identify this data file, required
     */
    private int id = -1;
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
    private MassSpecFileType fileType;
    /**
     * The format of the file, optional
     */
    private MassSpecFileFormat fileFormat;
    /**
     * All related data files, optional
     */
    private List<DataFile> fileMappings;
    /**
     * Assigned PRIDE accession, optional
     */
    private String prideAccession;

    public DataFile() {}

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

    public DataFile(File file, MassSpecFileType fileType) {
        this(-1, file, null, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(URL url, MassSpecFileType fileType) {
        this(-1, null, url, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    File file,
                    MassSpecFileType fileType) {
        this(id, file, null, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    URL url,
                    MassSpecFileType fileType) {
        this(id, null, url, fileType, new ArrayList<DataFile>(), null);
    }

    public DataFile(int id,
                    File file,
                    URL url,
                    MassSpecFileType fileType,
                    List<DataFile> mappings,
                    String prideAccession) {
        this.id = id;
        this.file = file;
        this.url = url;
        this.fileType = fileType;
        this.fileMappings = mappings;
        this.prideAccession = prideAccession;
    }

    public DataFile(int id,
                    File file,
                    URL url,
                    MassSpecFileFormat fileFormat,
                    List<DataFile> mappings,
                    String prideAccession) {
        this.id = id;
        this.file = file;
        this.url = url;
        this.fileFormat = fileFormat;
        this.fileType = fileFormat.getFileType();
        this.fileMappings = mappings;
        this.prideAccession = prideAccession;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return url != null;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public MassSpecFileType getFileType() {
        return fileType;
    }

    public void setFileType(MassSpecFileType fileType) {
        this.fileType = fileType;
    }

    public MassSpecFileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(MassSpecFileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public boolean hasMappings() {
        return fileMappings != null && fileMappings.size() > 0;
    }

    public boolean hasRawMappings() {
        if (fileMappings != null) {
            for (DataFile fileMapping : fileMappings) {
                if (fileMapping.getFileType().equals(MassSpecFileType.RAW)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<DataFile> getFileMappings() {
        return fileMappings;
    }

    public void setFileMappings(List<DataFile> fileMappings) {
        this.fileMappings = fileMappings;
    }

    public void addFileMapping(DataFile fileMapping) {
        if (this.fileMappings == null) {
            this.fileMappings = new ArrayList<DataFile>();
        }
        fileMappings.add(fileMapping);
    }

    public String getPrideAccession() {
        return prideAccession;
    }

    public void setPrideAccession(String prideAccession) {
        this.prideAccession = prideAccession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataFile)) return false;

        DataFile dataFile = (DataFile) o;

        if (id != dataFile.id) return false;
        if (file != null ? !file.equals(dataFile.file) : dataFile.file != null) return false;
        if (fileFormat != dataFile.fileFormat) return false;
        if (fileMappings != null ? !fileMappings.equals(dataFile.fileMappings) : dataFile.fileMappings != null)
            return false;
        if (fileType != dataFile.fileType) return false;
        if (prideAccession != null ? !prideAccession.equals(dataFile.prideAccession) : dataFile.prideAccession != null)
            return false;
        if (url != null ? !url.equals(dataFile.url) : dataFile.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (fileType != null ? fileType.hashCode() : 0);
        result = 31 * result + (fileFormat != null ? fileFormat.hashCode() : 0);
        result = 31 * result + (fileMappings != null ? fileMappings.hashCode() : 0);
        result = 31 * result + (prideAccession != null ? prideAccession.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataFile{" +
                "id=" + id +
                ", file=" + file +
                ", url=" + url +
                ", fileType=" + fileType +
                ", fileFormat=" + fileFormat +
                ", fileMappings=" + fileMappings +
                ", prideAccession='" + prideAccession + '\'' +
                '}';
    }
}
