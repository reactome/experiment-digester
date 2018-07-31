package org.reactome.server.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.reactome.server.tools.Importer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(Importer.class.getName());

    public static boolean validFile(File file){
        boolean rtn = true;
        String fileName = file.toString();

        if(file.isDirectory()){
            String msg = fileName + " is a folder. Please specify a valid file name.";
            logger.error(msg);
            rtn = false;
        }

        if(file.getParent()==null){
            file = new File("./" + fileName);
        }
        Path parent = Paths.get(file.getParent());
        if(!Files.exists(parent)){
            String msg = parent + " folder does not exist.";
            logger.error(msg);
            rtn = false;
        }

        if(!file.getParentFile().canWrite()){
            String msg = "No write access in " + file.getParentFile();
            logger.error(msg);
            rtn = false;
        }

        return rtn;
    }
}
