package uk.ac.ebi.pride.data.model;

import java.io.Serializable;
import java.util.ArrayList;
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
     * Submitter contact details
     */
    private Contact contact;
    /**
     * Experiment meta data
     */
    private MetaData metaData;
    /**
     * A list of dataFiles to be submitted
     */
    private List<DataFile> dataFiles;

    public Submission() {
        this.contact = new Contact();
        this.metaData = new MetaData();
        this.dataFiles = Collections.synchronizedList(new ArrayList<DataFile>());
    }

    public Submission(Contact contact,
                      MetaData metaData,
                      List<DataFile> dataFiles) {
        this.contact = contact;
        this.metaData = metaData;
        this.dataFiles = dataFiles;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public List<DataFile> getDataFiles() {
        return dataFiles;
    }

    public void removeAllDataFiles() {
        if (this.dataFiles != null) {
            dataFiles.clear();
        }
    }

    public void setDataFiles(List<DataFile> dataFiles) {
        this.dataFiles = dataFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Submission that = (Submission) o;

        if (contact != null ? !contact.equals(that.contact) : that.contact != null) return false;
        if (dataFiles != null ? !dataFiles.equals(that.dataFiles) : that.dataFiles != null) return false;
        if (metaData != null ? !metaData.equals(that.metaData) : that.metaData != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contact != null ? contact.hashCode() : 0;
        result = 31 * result + (metaData != null ? metaData.hashCode() : 0);
        result = 31 * result + (dataFiles != null ? dataFiles.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "contact=" + contact +
                ", metaData=" + metaData +
                ", dataFiles=" + dataFiles +
                '}';
    }
}
