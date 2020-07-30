package uk.ac.ebi.pride.data.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Suresh Hewapathirana
 */
public class ValidateAnnotationFiles {

    public static Set<String> getValidProjectTags() throws IOException {

        Set<String> validProjectTags = new HashSet<>();
        URL url;
        try {
            url = new URL(Constant.PROJECT_TAG_FILE);
            File fileTemp = File.createTempFile("project-citations", ".csv");
            FileUtils.copyURLToFile(url, fileTemp);
            String line;
            BufferedReader br = new BufferedReader(new FileReader(fileTemp));
            br.readLine(); // read the header
            while ((line = br.readLine()) != null) {
                validProjectTags.add(line.trim());
            }
            fileTemp.deleteOnExit();

        } catch (Exception e) {
            throw new IOException("Error while reading project-citations file from - " + e.getMessage());
        }
        return validProjectTags;
    }
}
