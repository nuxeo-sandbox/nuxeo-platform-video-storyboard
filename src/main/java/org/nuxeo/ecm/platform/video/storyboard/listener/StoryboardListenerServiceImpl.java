package org.nuxeo.ecm.platform.video.storyboard.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Created by Michaël on 3/23/2016.
 */
public class StoryboardListenerServiceImpl extends DefaultComponent implements StoryboardListenerService{

    private static final Log log = LogFactory.getLog(StoryboardListenerServiceImpl.class);

    protected static final String CONFIG_EXT_POINT = "configuration";

    protected ConfigurationDescriptor config = null;

    /**
     * Component activated notification.
     * Called when the component is activated. All component dependencies are resolved at that moment.
     * Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
    }

    /**
     * Component deactivated notification.
     * Called before a component is unregistered.
     * Use this method to do cleanup if any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
    }

    /**
     * Application started notification.
     * Called after the application started.
     * You can do here any initialization that requires a working application
     * (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) {
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint,
                                     ComponentInstance contributor) {
        if (CONFIG_EXT_POINT.equals(extensionPoint)) {
            config = (ConfigurationDescriptor) contribution;
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint,
                                       ComponentInstance contributor) {
        // Logic to do when unregistering any contribution
    }


    @Override
    public String getDefaultChain() {
        return config.getDefaultListenerChainName();
    }
}
