package org.palladiosimulator.blockchainsystems.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public abstract class PluginLaunch extends LaunchConfigurationDelegate {

    @Override
    public void launch(
            ILaunchConfiguration configuration,
            String arg1,
            ILaunch arg2,
            IProgressMonitor progressMonitor
    ) throws CoreException {
        launchSimulationJob(configuration);
    }

    protected abstract void launchSimulationJob(ILaunchConfiguration configuration);
}
