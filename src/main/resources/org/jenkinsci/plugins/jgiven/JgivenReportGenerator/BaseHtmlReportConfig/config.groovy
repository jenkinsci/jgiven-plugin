package org.jenkinsci.plugins.jgiven.JgivenReportGenerator.BaseHtmlReportConfig

import lib.FormTagLib

f = namespace(FormTagLib)

f.entry(field: 'customCssFile', title: _('custom.css.file')) {
    f.textbox()
}