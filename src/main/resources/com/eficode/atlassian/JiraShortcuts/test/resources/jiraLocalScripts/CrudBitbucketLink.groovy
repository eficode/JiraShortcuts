package com.eficode.atlassian.JiraShortcuts.test.resources.jiraLocalScripts

import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType
import com.onresolve.scriptrunner.runner.customisers.WithPlugin

@WithPlugin("com.eficode.atlassian.JiraShortcuts")
import com.eficode.atlassian.JiraShortcuts.JiraShortcuts
import org.apache.log4j.Level
import org.apache.log4j.Logger


Logger log = Logger.getLogger("create.bb.app.link")
log.setLevel(Level.ALL)


JiraShortcuts jc = new JiraShortcuts()

ApplicationLink link  = jc.createApplicationLink(BitbucketApplicationType, "Bitbucket", true, "BITBUCKET_URL", "BITBUCKET_USER", "BITBUCKET_PASSWORD")

log.info("Created link:" + link.toString())

log.info("Script END")
