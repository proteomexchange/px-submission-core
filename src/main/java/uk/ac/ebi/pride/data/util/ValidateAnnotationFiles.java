package uk.ac.ebi.pride.data.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Suresh Hewapathirana
 */
public class ValidateAnnotationFiles {
    private static final Logger logger = LoggerFactory.getLogger(ValidateAnnotationFiles.class);

    public static Set<String> getValidProjectTags(){

        Set<String> validProjectTags = new HashSet<>();
        final String stringURL = "https://raw.githubusercontent.com/PRIDE-Utilities/pride-ontology/master/pride-annotations/project-tags.csv";
        URL url;
        try {
            url = new URL(stringURL);
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
            logger.error("Error reading project-citations file from --" + stringURL + e.getMessage());
        }
        return validProjectTags;
    }
}
