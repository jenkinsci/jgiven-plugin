package org.jenkinsci.plugins.jgiven;

import hudson.Extension;
import javaposse.jobdsl.dsl.Context;
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
        JgivenDslContext context = new JgivenDslContext();
        executeInContext(closure, context);
        JgivenReportGenerator jgivenReportGenerator = new JgivenReportGenerator(context.reportConfigs);
        jgivenReportGenerator.setJgivenResults(context.resultFiles);
        return jgivenReportGenerator;
    }

    public static class JgivenDslContext implements Context {
        private List<ReportConfig> reportConfigs = new ArrayList<ReportConfig>();
        private String resultFiles = "";

        public void html() {
            reportConfigs.add(new HtmlReportConfig());
        }

        public void html(Runnable closure) {
            HtmlReportContext context = new HtmlReportContext();
            executeInContext(closure, context);
            HtmlReportConfig reportConfig = new HtmlReportConfig();
            reportConfig.setCustomCssFile(context.customCss);
            reportConfig.setCustomJsFile(context.customJs);
            reportConfig.setTitle(context.title);
            reportConfigs.add(reportConfig);
        }

        public void results(String glob) {
            resultFiles = glob;
        }
    }

    public static class HtmlReportContext implements Context {
        private String customCss;
        private String customJs;
        private String title;

        public void customCss(String customCss) {
            this.customCss = customCss;
        }
        public void customJs(String customJs) {
            this.customJs = customJs;
        }
        public void title(String title) {
            this.title = title;
        }
    }
}
