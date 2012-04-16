package de.jutzig.jabylon.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	private static BundleContext context;
	private static Activator activator;
	private List<IConfigurationElement> configSections;
	public static final String PLUGIN_ID = "de.jutzig.jabylon.ui";

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		activator = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		activator = null;
	}
	
	public static Activator getDefault()
	{
		return activator;
	}
	
	public List<IConfigurationElement> getConfigSections()
	{
		if(configSections==null)
		{
			configSections = new ArrayList<IConfigurationElement>();
			IConfigurationElement[] elements = RegistryFactory.getRegistry().getConfigurationElementsFor("de.jutzig.jabylon.ui.config");
			for (IConfigurationElement iConfigurationElement : elements) {
				configSections.add(iConfigurationElement);
			}
		}
		return configSections;
	}

	public static void error(String message, Throwable cause)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message));
	}
	
}
