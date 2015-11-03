package org.jenkinsci.plugins.jgiven;

import com.tngtech.jgiven.report.ReportGenerator;
import groovy.lang.Closure;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.init.Initializer;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static hudson.init.InitMilestone.PLUGINS_STARTED;

public class JgivenReportGenerator extends Recorder implements SimpleBuildStep {
    public static final String REPORTS_DIR = "jgiven-reports";
    private List<ReportConfig> reportConfigs;

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @DataBoundConstructor
    public JgivenReportGenerator(List<ReportConfig> reportConfigs) {
        this.reportConfigs = (reportConfigs != null && !reportConfigs.isEmpty()) ? new ArrayList<ReportConfig>(reportConfigs) : Collections.<ReportConfig>singletonList(new HtmlReportConfig());
    }

    public JgivenReportGenerator(Closure<?> configClosure) {
        JgivenDslContext context = new JgivenDslContext();
        executeInContext(configClosure, context);
        setJgivenResults(context.resultFiles);
        reportConfigs = context.reportConfigs;
    }

    private static void executeInContext(Closure<?> configClosure, Object context) {
        configClosure.setDelegate(context);
        configClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
        configClosure.call();
    }

    private String jgivenResults;

    public List<ReportConfig> getReportConfigs() {
        return Collections.unmodifiableList(reportConfigs);
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println(Messages.JgivenReportGenerator_generating_reports());
        File reportRootDir = reportRootDir(run);
        File jgivenJsons = new File(reportRootDir, "json");
        int numFiles = workspace.copyRecursiveTo(jgivenResults, new FilePath(jgivenJsons));
        if (numFiles > 0) {
            listener.getLogger().println(Messages.JgivenReportGenerator_results_found(numFiles));
            for (ReportConfig reportConfig : reportConfigs) {
                listener.getLogger().println(Messages.JgivenReportGenerator_generating_report(reportConfig.getReportName()));
                generateReport(reportRootDir, jgivenJsons, reportConfig, workspace);
            }
            run.addAction(new JgivenReportAction(run, reportConfigs));
        } else {
            listener.getLogger().println(Messages._JgivenReportGenerator_no_reports());
        }
    }

    private void generateReport(File reportRootDir, File JgivenJsons, ReportConfig reportConfig, FilePath workspace) throws IOException, InterruptedException {
        try {
            reportConfig.reportGenerator(JgivenJsons, reportRootDir, workspace).generate();
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File reportRootDir(Run<?, ?> run) {
        return new File(run.getRootDir(), REPORTS_DIR);
    }

    public String getJgivenResults() {
        return jgivenResults;
    }

    @DataBoundSetter
    public void setJgivenResults(String jgivenResults) {
        this.jgivenResults = jgivenResultsFromString(jgivenResults);
    }

    private static String jgivenResultsFromString(String jgivenResults) {
        return StringUtils.isBlank(jgivenResults) ? "**/json/*.json" : jgivenResults;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.JgivenReportGenerator_display_name();
        }

        public FormValidation doCheckJgivenResults(
                @AncestorInPath AbstractProject project,
                @QueryParameter String value) throws IOException {
            if (project == null) {
                return FormValidation.ok();
            }
            return FilePath.validateFileMask(project.getSomeWorkspace(), jgivenResultsFromString(value));
        }
    }

    public static abstract class ReportConfig extends AbstractDescribableImpl<ReportConfig> {
        private ReportGenerator.Format format;

        public ReportGenerator.Format getFormat() {
            return format;
        }

        ReportConfig(ReportGenerator.Format format) {
            this.format = format;
        }

        public String getReportDirectory() {
            return getFormat().name().toLowerCase(Locale.ENGLISH);
        }

        public String getReportUrl() {
            return getReportDirectory();
        }

        abstract String getReportName();

        public ReportGenerator reportGenerator(File sourceDir, File reportRootDir, FilePath workspace) throws IOException, InterruptedException {
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.setSourceDirectory(sourceDir);
            reportGenerator.setFormat(getFormat());
            reportGenerator.setTargetDirectory(new File(reportRootDir, getReportDirectory()));
            return reportGenerator;
        }
    }

    public static class HtmlReportConfig extends ReportConfig {
        private String customCssFile;
        private String customJsFile;
        private String title;

        @DataBoundConstructor
        public HtmlReportConfig() {
            super(ReportGenerator.Format.HTML);
        }

        public HtmlReportConfig(Closure<?> closure) {
            this();
            HtmlReportContext context = new HtmlReportContext();
            executeInContext(closure, context);
            this.setCustomCssFile(context.customCss);
            this.setCustomJsFile(context.customJs);
            this.setTitle(context.title);
        }

        public String getReportName() {
            return Messages.JgivenReport_html_name();
        }

        public String getReportUrl() {
            return String.format("%s/index.html", getReportDirectory());
        }

        public String getCustomCssFile() {
            return customCssFile;
        }

        @DataBoundSetter
        public void setCustomCssFile(String customCssFile) {
            this.customCssFile = customCssFile;
        }

        public String getCustomJsFile() {
            return customJsFile;
        }

        @DataBoundSetter
        public void setCustomJsFile(String customJsFile) {
            this.customJsFile = customJsFile;
        }

        public String getTitle() {
            return title;
        }

        @DataBoundSetter
        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public ReportGenerator reportGenerator(File sourceDir, File reportRootDir, FilePath workspace) throws IOException, InterruptedException {
            ReportGenerator reportGenerator = super.reportGenerator(sourceDir, reportRootDir, workspace);
            if (StringUtils.isNotBlank(customCssFile)) {
                reportGenerator.getConfig().setCustomCssFile(copyFileToMaster(workspace, customCssFile));
            }
            if (StringUtils.isNotBlank(customJsFile)) {
                reportGenerator.getConfig().setCustomJsFile(copyFileToMaster(workspace, customJsFile));
            }
            if (StringUtils.isNotBlank(title)) {
                reportGenerator.getConfig().setTitle(title);
            }
            return reportGenerator;
        }

        private File copyFileToMaster(FilePath workspace, String file) throws IOException, InterruptedException {
            File tmpFile = File.createTempFile("file", null);
            workspace.child(file).copyTo(new FilePath(tmpFile));
            return tmpFile;
        }

        @Extension
        public static class DescriptorImpl extends Descriptor<ReportConfig> {
            @Override
            public String getDisplayName() {
                return Messages.JgivenReport_html_name();
            }

            public FormValidation doCheckCustomCssFile(@QueryParameter String value) {
                return validateFileExists(value);
            }

            public FormValidation doCheckCustomJsFile(@QueryParameter String value) {
                return validateFileExists(value);
            }

            private FormValidation validateFileExists(@QueryParameter String value) {
                if (StringUtils.isEmpty(value)) {
                    return FormValidation.ok();
                }
                File file = new File(value);
                return file.exists() ? FormValidation.ok() : FormValidation.error(Messages.JgivenReportGenerator_custom_file_does_not_exist());
            }
        }
    }

    public static class TextReportConfig extends ReportConfig {
        @DataBoundConstructor
        public TextReportConfig() {
            super(ReportGenerator.Format.TEXT);
        }

        @Override
        public String getReportName() {
            return Messages.JgivenReport_text_name();
        }

        @Extension
        public static class DescriptorImpl extends Descriptor<ReportConfig> {
            @Override
            public String getDisplayName() {
                return Messages.JgivenReport_text_name();
            }
        }
    }

    public static class AsciiDocReportConfig extends ReportConfig {
        @DataBoundConstructor
        public AsciiDocReportConfig() {
            super(ReportGenerator.Format.ASCIIDOC);
        }

        public String getReportName() {
            return Messages.JgivenReport_asciidoc_name();
        }

        @Extension
        public static class DescriptorImpl extends Descriptor<ReportConfig> {
            @Override
            public String getDisplayName() {
                return Messages.JgivenReport_asciidoc_name();
            }
        }
    }

    @Initializer(before = PLUGINS_STARTED)
    public static void addAliases() {
        Items.XSTREAM2.addCompatibilityAlias("org.jenkinsci.plugins.jgiven.JgivenReportGenerator$Html5ReportConfig", HtmlReportConfig.class);
    }
}
