/*
 * Copyright 2019 General Electric Company. All rights reserved.
 */

import groovy.json.JsonSlurperClassic

import static groovy.io.FileType.FILES

/**
 * The file is the entry point to the framework. This script will traverse
 * through the files and folders and execute the .groovy files for creating the
 * Jenkins Jobs.
 */
// Get the seed job workspace
String workspace = SEED_JOB.getWorkspace()
JsonSlurperClassic slurper = new JsonSlurperClassic()
String jobsFolder = workspace + "/jobs/seeds"
String globalConfig = workspace + "/src/org/foo/baseConfig.cfg"
File globalConfigFile = new File(globalConfig)
String globalConfigContent = globalConfigFile.getText()

new File(jobsFolder).eachFileRecurse(FILES) {
    if (it.name.endsWith('.cfg')) {
        // Object for storing parsed json response
        String finalJson = "{ \"global\":" + globalConfigContent + ", \"job\":" + it.getText() + "}"
        def result = slurper.parseText(finalJson)
        String sourceFile = readFileFromWorkspace(result.global.paths.jobTemplates + result.job.jobType + ".groovy")
        Class UtilityClass = new GroovyClassLoader(getClass().getClassLoader()).parseClass(sourceFile)
        GroovyObject groovyObject = (GroovyObject) UtilityClass.getDeclaredConstructor().newInstance()
        groovyObject.create(this, result)
    }
}
