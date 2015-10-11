package org.jenkinsci.plugins.jgiven;

import groovy.lang.Closure;
import hudson.Extension;
import javaposse.jobdsl.dsl.Context;
import javaposse.jobdsl.dsl.Preconditions;
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;
import org.jenkinsci.plugins.jgiven.JgivenReportGenerator.HtmlReportConfig;
import org.jenkinsci.plugins.jgiven.JgivenReportGenerator.ReportConfig;

import java.util.ArrayList;
import java.util.List;

@Extension(optional = true)
public class JgivenDslContextExtension extends ContextExtensionPoint {
    @DslExtensionMethod(context = PublisherContext.class)
    public Object jgivenReports(Runnable closure) {
        Preconditions.checkArgument(closure instanceof Closure, "runnable must be a Groovy closure");
        return new JgivenReportGenerator((Closure<?>) closure);
    }
}
