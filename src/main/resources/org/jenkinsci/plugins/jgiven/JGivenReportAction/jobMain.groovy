package org.jenkinsci.plugins.jgiven.JGivenReportAction

import lib.JenkinsTagLib

t = namespace(JenkinsTagLib)

table(style: "margin-top: 1em; margin-left: 1em;") {
    t.summary(icon: 'clipboard.png') {
        span(_('latest.jgiven.reports'))

        ul(style: 'list-style-type: none; margin: 0;') {
            my.reports.each { report ->
                li {
                    a(href: my.getReportUrl(report), report.name)
                }
            }
        }
    }
}
