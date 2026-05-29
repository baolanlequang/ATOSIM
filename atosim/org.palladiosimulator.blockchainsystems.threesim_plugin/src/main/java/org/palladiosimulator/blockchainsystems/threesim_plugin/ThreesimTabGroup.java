package org.palladiosimulator.blockchainsystems.threesim_plugin;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.palladiosimulator.blockchainsystems.plugin.ui.tabs.ArchitecturalModelsTab;
import org.palladiosimulator.blockchainsystems.plugin.ui.tabs.LoggingTab;
import org.palladiosimulator.blockchainsystems.plugin.ui.tabs.SimulationTerminationTab;
import org.palladiosimulator.blockchainsystems.plugin.ui.tabs.SimulationTypeTab;

public class ThreesimTabGroup extends AbstractLaunchConfigurationTabGroup {

    @Override
    public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
        setTabs(new org.eclipse.debug.ui.ILaunchConfigurationTab[]{
                new ArchitecturalModelsTab(),
                new SimulationTypeTab(),
                new SimulationTerminationTab(),
                new ThreesimTab(),
                new LoggingTab(),
                new CommonTab()
        });
    }
}
