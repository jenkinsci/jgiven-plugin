package org.jenkinsci.plugins.jgiven;

import javaposse.jobdsl.dsl.Context;

public class HtmlReportContext implements Context {
    String customCss;
    String customJs;
    String title;

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
