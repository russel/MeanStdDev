#!/usr/bin/env groovy

import spock.lang.Specification
import spock.lang.Unroll

class scripttest_runningMeanStdDev extends Specification {

  static final projectPath = new File(".").getCanonicalPath()
  static final scriptsPath = "${projectPath}/src/main/groovy"
  static final testScriptDirectory = "${projectPath}/testScripts"

  static final scriptPaths = [
      'runningMeanStdDev_dataflowOperators'
      ].collect{[it, "${scriptsPath}/${it}.groovy"]}

  static final sqrtHalf = 0.7071067811865476

  @Unroll
  def 'script exists and is executable, #scriptName'() {
    given:
      def file = new File(scriptPath)
    expect:
      file.exists()
      file.canExecute()
    where:
      [scriptName, scriptPath] << scriptPaths
  }

  @Unroll
  def 'execute on no data, #scriptName'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/emptyFile.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == ''
    where:
     [scriptName, scriptPath] << scriptPaths
  }

  @Unroll
  def 'execute on one item, #scriptName'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/singleItem.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == 'Mean = 1.0, std.dev = NaN, df = 0\n'
    where:
     [scriptName, scriptPath] << scriptPaths
  }

  @Unroll
  def 'execute on two items, #scriptName'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/twoItems.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == "Mean = 1.0, std.dev = NaN, df = 0\nMean = 1.5, std.dev = ${sqrtHalf}, df = 1\n"
    where:
     [scriptName, scriptPath] << scriptPaths
  }

}
