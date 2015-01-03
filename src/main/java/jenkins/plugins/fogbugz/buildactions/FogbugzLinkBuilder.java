package jenkins.plugins.fogbugz.buildactions;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;

import java.io.Serializable;
import java.util.logging.Level;

import lombok.extern.java.Log;

import org.kohsuke.stapler.DataBoundConstructor;

import com.google.common.base.Splitter;

/**
 * Adds a FogbugzLinkAction to the current build.
 * Gets CASE_ID from parameters or branch name.
 */
@Log
public class FogbugzLinkBuilder extends Builder implements Serializable {
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @DataBoundConstructor
    public FogbugzLinkBuilder() {}

    @Override
    public final boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener) {
        try {
            String givenCaseIds = build.getEnvVars().get("CASE_ID");

            Iterable<String> caseIds = Splitter.on(',').trimResults().omitEmptyStrings().split(givenCaseIds);
            for (String id : caseIds) {
                int caseId = Integer.parseInt(id);
                // If we finally get a caseId, use that to create and attach a LinkAction.
                // Else: just bail out.
                if (caseId != 0) {
                    // Attach an Action to the Build.
                    build.getActions().add(new FogbugzLinkAction(caseId));
                }
            }
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Could not resolve $CASE_ID, skipping build action creation.", e);
        }
        return true;
    }
}