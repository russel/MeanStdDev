#! /usr/bin/env groovy

import spock.lang.Specification
import spock.lang.Unroll

//import meanStdDev_parallel
//import meanStdDev_dataflowVariables
class scripttest_runningMeanStdDev extends Specification {

  static final projectPath = '/home/users/russel/Progs/Applications/MeanStdDev/Groovy'
  static final scriptsPath = "${projectPath}/src/main/groovy"
  static final testScriptDirectory = "${projectPath}/testScripts"

  static final scriptPaths = [
      'runningMeanStdDev_dataflowOperators'
      ].collect{"${scriptsPath}/${it}.groovy"}

  static final sqrtHalf = 0.7071067811865476

  @Unroll
  def 'script exists and is executable'() {
    given:
      def file = new File(scriptPath)
    expect:
      file.exists()
      file.canExecute()
    where:
      scriptPath << scriptPaths
  }

  @Unroll
  def 'script on no data'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/emptyFile.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == ''
    where:
     scriptPath << scriptPaths
  }

  @Unroll
  def 'script on one item'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/singleItem.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == 'Mean = 1.0, std.dev = NaN, df = 0\n'
    where:
     scriptPath << scriptPaths
  }

  @Unroll
  def 'script on two item'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/twoItems.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == "Mean = 1.0, std.dev = NaN, df = 0\nMean = 1.5, std.dev = ${sqrtHalf}, df = 1\n"
    where:
     scriptPath << scriptPaths
  }

}
