package org.sleuthkit.autopsy.thunderbirdparser;

import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.ingest.DataSourceIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleFactory;
import org.sleuthkit.autopsy.ingest.IngestModuleFactoryAdapter;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;

@ServiceProvider(service = IngestModuleFactory.class)
public class SparkDataSourceModuleFactory extends IngestModuleFactoryAdapter {

    static String getModuleName() {
        return "Distributed Identification of E-mail Files (DIEF)";
    }

    @Override
    public String getModuleDisplayName() {
        return getModuleName();
    }

    @Override
    public boolean isDataSourceIngestModuleFactory() {
        return true;
    }

    @Override
    public String getModuleDescription() {
        return "Distributed Identification of E-mail Files (DIEF)";
    }

    @Override
    public String getModuleVersionNumber() {
        return "DIEF Module Version Number 1";
    }

    @Override
    public DataSourceIngestModule createDataSourceIngestModule(IngestModuleIngestJobSettings settings) {
        return new SparkDataSourceModule();
    }
}
