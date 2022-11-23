package com.eficode.atlassian.JiraShortcuts.test

import com.eficode.atlassian.jiraInstanceManager.JiraInstanceManagerRest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification

import java.util.regex.Matcher

/**
 * Presumes a JIRA and bitbucket is set up but not link
 * That JIRA has the latest version of JiraShortcuts installed
 *
 */

class ApplicationLinkSpec extends Specification{

    @Shared
    Logger log = LoggerFactory.getLogger(this.class)

    @Shared
    //String jiraBaseUrl = "http://jira.domain.se:8080"
    String jiraBaseUrl = "http://jira.auga.se:8081"

    @Shared
    //String bitbucketBaseUrl = "http://bitbucket.domain.se:7990"
    String bitbucketBaseUrl = "http://bitbucket.auga.se:7991"

    @Shared
    JiraInstanceManagerRest jiraRest = new JiraInstanceManagerRest("admin", "admin", jiraBaseUrl)

    @Shared
    String jiraScriptPath = "resources/jiraLocalScripts/"

    def setupSpec() {







    }

    ArrayList<String> deleteAllApplinks() {

        String deleteAllAppLinksScript = getClass().getResourceAsStream(jiraScriptPath + "DeleteAllAppLinks.groovy").text
        Map linkCleanup = jiraRest.executeLocalScriptFile(deleteAllAppLinksScript)

        assert  linkCleanup.errors == null  && linkCleanup.success

        ArrayList<String>deletedLinks = []
        ArrayList<String> logRows = linkCleanup.log as ArrayList<String>
        logRows.each {logRow ->

            String id = getFirstRegexGroup(/Deleting application link.*\((.*)\)/, logRow)

            if (id) {
                deletedLinks.add(id)
            }

        }

        return deletedLinks

    }

    String replacePlaceholders(String source) {

        String out = source.replace("BITBUCKET_URL", bitbucketBaseUrl)
        out = out.replace("BITBUCKET_USER", "admin")
        out = out.replace("BITBUCKET_PASSWORD", "admin")


        return out
    }

    String createBitbucketApplink() {

        String appLinkScript = getClass().getResourceAsStream(jiraScriptPath + "CrudBitbucketLink.groovy").text
        appLinkScript = replacePlaceholders(appLinkScript)

        Map linkCrud = jiraRest.executeLocalScriptFile(appLinkScript)
        assert linkCrud.errors == null  && linkCrud.success && linkCrud.log.last().endsWith("Script END")

        return getFirstRegexGroup(/Created link.*\((.*)\)/, linkCrud.log as ArrayList<String>)

    }

    String getFirstRegexGroup(String pattern, ArrayList<String> sourceStrings) {


        String firstMatch

        sourceStrings.each { sourceString ->

            String match = getFirstRegexGroup(pattern, sourceString)

            if (match && firstMatch == null)  {
                firstMatch =  match
            }

        }
        return firstMatch

    }

    String getFirstRegexGroup(String pattern, String sourceString) {

        Matcher matcher = sourceString =~ pattern

        return matcher.count && matcher[0].size() ? matcher[0][1] : null
    }

    def "Crud of App Links"() {

        setup:
        deleteAllApplinks()

        when:
        String newLinkId = createBitbucketApplink()

        then:
        newLinkId.length() > 30 && newLinkId.length() < 45

        when:
        ArrayList<String> deletedLinks = deleteAllApplinks()

        then:
        deletedLinks == [newLinkId]
    }


    def "Test Bitbucket Applink requests"() {

        setup:
        deleteAllApplinks()
        String bitbucketApplinkId = createBitbucketApplink()
        String testScript = getClass().getResourceAsStream(jiraScriptPath + "TestApplinkRequests.groovy").text
        testScript = replacePlaceholders(testScript)


        when:
        Map whoAmIResponse = jiraRest.executeLocalScriptFile(testScript)

        then:
        assert whoAmIResponse.errors == null  && whoAmIResponse.success


    }
}
