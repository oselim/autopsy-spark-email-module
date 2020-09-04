package org.sleuthkit.autopsy.thunderbirdparser;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.openide.util.Exceptions;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModuleProgress;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SparkDataSourceModule implements DataSourceIngestModule, Serializable {

    Case currentCase;
    SleuthkitCase sleuthkitCase;
    SparkSession sparkSession;
    JavaSparkContext javaSparkContext;
    List<AbstractFile> abstractFileList;
    List<FileMaterials> fileMaterialsList;
    long runTime = 0;

    public SparkDataSourceModule() {
        sparkSession = SparkSession
                .builder()
                .appName("Autopsy Spark Module")
                .master("local[*]")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .getOrCreate();

        javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());

        System.out.println("SPARK HAS BEEN STARTED");
        System.out.println(Arrays.toString(javaSparkContext.getConf().getAll()));
    }

    @Override
    public void startUp(IngestJobContext ingestJobContext) {
        long startTime = System.nanoTime();
        System.out.println("STARTUP Started!!");
        try {
            currentCase = Case.getCurrentCaseThrows();
            sleuthkitCase = currentCase.getSleuthkitCase();
        } catch (NoCurrentCaseException e) {
            e.printStackTrace();
        }

        try {
            abstractFileList = sleuthkitCase.findAllFilesWhere("1=1");
            fileMaterialsList = abstractFileList.parallelStream()
                    .filter(abstractFile -> !abstractFile.getType().getName().equals("Slack"))
                    .filter(abstractFile -> !abstractFile.getType().getName().equals("Unallocated Blocks"))
                    .filter(abstractFile -> !(abstractFile.getKnown().getName().equals("known")))
                    .filter(AbstractFile::isFile)
                    .map(abstractFile -> {
                        byte[] t = new byte[64];
                        long fileSize = abstractFile.getSize();
                        if (fileSize > 64) {
                            int byteRead = 0;

                            try {
                                byteRead = abstractFile.read(t, 0, 64);
                            } catch (TskCoreException ex) {
                                Exceptions.printStackTrace(ex);
                            }

                            if (byteRead > 0) {
                                return new FileMaterials(abstractFile.getName(), abstractFile.getNameExtension(), fileSize, t);
                            }
                        } else {
                            int byteRead = 0;

                            try {
                                byteRead = abstractFile.read(t, 0, fileSize);
                            } catch (TskCoreException ex) {
                                Exceptions.printStackTrace(ex);
                            }

                            if (byteRead > 0) {
                                return new FileMaterials(abstractFile.getName(), abstractFile.getNameExtension(), fileSize, t);
                            }
                        }

                        try {
                            abstractFile.read(t, 0, fileSize);
                        } catch (TskCoreException ex) {
                            Exceptions.printStackTrace(ex);
                        }

                        return new FileMaterials(abstractFile.getName(), abstractFile.getNameExtension(), fileSize, t);
                    })
                    .collect(Collectors.toList());
        } catch (TskCoreException e) {
            e.printStackTrace();
        }
        updateExecutionTime(startTime, "startUp");
        System.out.println("STARTUP Ended!!");
    }

    private void updateExecutionTime(long startTime, String stepName) {
        long stopTime = System.nanoTime();
        long executionTime = stopTime - startTime;
        runTime += executionTime;
        System.out.println("Execution time of step " + stepName + " : " + executionTime / 1000000 + " miliseconds");
        System.out.println("Total execution time of step " + stepName + " in milisecs: " + runTime / 1000000 + " miliseconds");
    }

    @Override
    public ProcessResult process(Content content, DataSourceIngestModuleProgress dataSourceIngestModuleProgress) {
        long startTime = System.nanoTime();
        System.out.println("PROCESS Started!!");
        try {
            JavaRDD<FileMaterials> fileMaterialsJavaRDD = javaSparkContext.parallelize(fileMaterialsList);
            fileMaterialsJavaRDD.foreach(fileMaterials -> {
                System.out.println("is Email File: " + fileMaterials.getName());
                boolean isMbox = false;
                boolean isEMLFile = false;
                if (fileMaterials.getSize() > 64) {
                    isMbox = fileMaterials.isValidMimeTypeMbox();
                    isEMLFile = fileMaterials.isEMLFile();
                }
                boolean isPstFile = fileMaterials.isPstFile();
                boolean isVcardFile = fileMaterials.isVcardFile();

                System.out.println("Mbox: " + isMbox);
                System.out.println("EML: " + isEMLFile);
                System.out.println("PST: " + isPstFile);
                System.out.println("Vcard: " + isVcardFile);
            });

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return ProcessResult.ERROR;
        }
        updateExecutionTime(startTime, "process");
        System.out.println("PROCESS Ended!!");
        return ProcessResult.OK;
    }
}
