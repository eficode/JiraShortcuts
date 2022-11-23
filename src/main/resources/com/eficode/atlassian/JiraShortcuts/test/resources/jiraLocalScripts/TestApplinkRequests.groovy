package com.eficode.atlassian.JiraShortcuts.test.resources.jiraLocalScripts

import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.sal.api.net.Request
import com.atlassian.sal.api.net.Response
import com.onresolve.scriptrunner.runner.customisers.WithPlugin
@WithPlugin("com.eficode.atlassian.JiraShortcuts")
import com.eficode.atlassian.JiraShortcuts.JiraShortcuts
import org.apache.groovy.json.internal.LazyMap
import org.apache.log4j.Level
import org.apache.log4j.Logger


Logger log = Logger.getLogger("create.bb.app.link")
log.setLevel(Level.ALL)

JiraShortcuts js = new JiraShortcuts()

ApplicationLink bitbucketLink = js.getApplicationLink("BITBUCKET_URL")

assert bitbucketLink : "Error finding Application link with url: BITBUCKET_URL"

Response whoAmIResponse = js.appLinkRequest(Request.MethodType.GET, "BITBUCKET_URL/plugins/servlet/applinks/whoami", null, null, bitbucketLink)

assert whoAmIResponse.statusCode == 200 : "WhoAmI request returned unexpected HTTP code:" + whoAmIResponse.statusCode
assert whoAmIResponse.responseBodyAsString == "BITBUCKET_USER" : "WhoAmI returned unexpected name:" + whoAmIResponse?.responseBodyAsString


def userResponse = js.appLinkGetJson("BITBUCKET_URL/plugins/servlet/applinks/whoami", null, null, bitbucketLink)

assert userResponse instanceof LazyMap
assert userResponse.name == "BITBUCKET_USER"