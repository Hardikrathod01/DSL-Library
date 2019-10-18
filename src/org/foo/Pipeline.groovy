package org.foo
/*
 * Copyright 2019 General Electric Company. All rights reserved.
 */

import javaposse.jobdsl.dsl.*
import hudson.model.FreeStyleProject;
import hudson.model.*
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.UserRemoteConfig;
import hudson.triggers.SCMTrigger;
import hudson.util.Secret;
import javaposse.jobdsl.plugin.*;
import jenkins.model.Jenkins;
import jenkins.model.JenkinsLocationConfiguration;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import jenkins.model.JenkinsLocationConfiguration;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl;
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl.DescriptorImpl;
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.BlanketWhitelist;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition;
import hudson.plugins.git.extensions.*
import hudson.plugins.git.extensions.impl.*
import hudson.tasks.LogRotator

/**
 * The class is the entry point for creating a pipeline Jenkins job.
 */
class Pipeline {
    /**
     * <p>
     *     This function is the entry point for create pipeline Jenkins job.
     * </p>
     *
     * @param dslFactory the current context of the script.
     * @param config contains the configurations required for creating the
     *               Jenkins job.
     */
    void create(def dslFactory, Map config) {
        def baseJobFolderName
        // Check the branch, and set the folder name
        if ("master" == "master") {
            baseJobFolderName = config.job.baseJobFolderName
        } else {
            if (config.job.baseJobFolderName) {
                baseJobFolderName = config.job.baseJobFolderName + config.global.constants.xcicd + "master"
            } else {
                baseJobFolderName = config.global.constants.xcicd.substring(1) + "master" 
            }

        }
        // Constructing the jenkins job name
        String fullJobName = (baseJobFolderName ? baseJobFolderName + "/" : "") + "${config.job.jobName}"
        // Creating the jenkins pipeline job
        def workflowJob = new WorkflowJob(Jenkins.instance, fullJobName);
        // Setting the pipeline definition
        setPipelineDefinition(dslFactory, theJob, config)
       
    }

    /**
     * <p>
     *     This function will add Jenkins step of definition to the pipeline
     *     job.
     * </p>
     * @param dslFactory the current context of the script.
     * @param job the instance of the Jenkins job created.
     * @param config contains the configurations required for creating the
     *               Jenkins job.
     */
    void setPipelineDefinition(dslFactory, job, config) {
        if (config.job.scriptName) {
            String scriptPath = config.global.paths.pipelineScripts + config.job.scriptName
            job.with {
                definition {
                    cps {
                        script(dslFactory.readFileFromWorkspace(scriptPath))
                        sandbox()
                    }
                }
            }
        }
        if (config.job.pipeline_scm) {
            job.with {
                definition {
                    cpsScm {
                        config.job.pipeline_scm.each {
                            it.each { item ->
                                if (item.key == "git") {
                                    scm {
                                        git {
                                            remote {
                                                String urlGit
                                                if (item.value.repoURL) {
                                                    urlGit = item.value.repoURL
                                                } else {
                                                    urlGit = item.value.gitServer + "/${item.value.group}/${item.value.repo}.git"
                                                }
                                                url(urlGit)
                                                credentials(config.global.environment.gitCredentials)
                                            }
                                            branch("refs/heads/" + "${item.value.branch}")
                                            extensions {
                                                localBranch(item.value.branch)
                                                wipeOutWorkspace()
                                            }
                                        }
                                    }
                                }
                                if (item.key == "perforceP4") {
                                    scm {
                                        perforceP4(config.global.environment.perforceCredentials) {
                                            configure{ proj ->
                                                if(item.value.workspaceType == "Streams") {
                                                    proj.remove( proj / workspace)
                                                    proj / workspace(class: 'org.jenkinsci.plugins.p4.workspace.StreamWorkspaceImpl') {
                                                        charset item.value.workspaceProperties.charset
                                                        pinHost item.value.workspaceProperties.pinHost
                                                        streamName item.value.workspaceProperties.streamName
                                                        format item.value.workspaceProperties.format
                                                    }
                                                }
                                                if(item.value.populate == "SyncOnly") {
                                                    proj.remove( proj / populate)
                                                    proj / populate(class: 'org.jenkinsci.plugins.p4.populate.SyncOnlyImpl') {
                                                        have item.value.populateProperties.have
                                                        modtime item.value.populateProperties.modtime
                                                        quiet item.value.populateProperties.quiet
                                                        revert item.value.populateProperties.revert
                                                        force item.value.populateProperties.force
                                                        parallel{
                                                            enable item.value.populateProperties.parallel.enable
                                                            threads item.value.populateProperties.parallel.threads
                                                            minfiles item.value.populateProperties.parallel.minfiles
                                                            minbytes item.value.populateProperties.parallel.minbytes
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                                if (item.key == "scriptPath") {
                                    scriptPath(item.value)
                                }
                                if (item.key == "lightweight") {
                                    lightweight(item.value)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
