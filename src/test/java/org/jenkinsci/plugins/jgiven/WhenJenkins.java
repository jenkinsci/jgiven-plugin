package org.jenkinsci.plugins.jgiven;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import hudson.model.AbstractBuild;

import java.util.concurrent.ExecutionException;

public class WhenJenkins<SELF extends WhenJenkins<SELF>> extends JenkinsStage<SELF> {
    @ProvidedScenarioState
    private AbstractBuild<?, ?> build;

    public SELF the_project_is_built() throws ExecutionException, InterruptedException {
        build = project.scheduleBuild2(0).get();
        return self();
    }
}
