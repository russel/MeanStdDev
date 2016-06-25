#!/usr/bin/env groovy

import spock.lang.Specification
import spock.lang.Unroll

class scripttest_meanStdDev extends Specification {

  static final projectPath = new File(".").getCanonicalPath()
  static final scriptsPath = "${projectPath}/src/main/groovy"
  static final testScriptDirectory = "${projectPath}/testScripts"

  static final scriptPaths = [
      'meanStdDev_sequential',
      'meanStdDev_parallel',
      'meanStdDev_futures',
      'meanStdDev_dataflowVariables',
      'meanStdDev_dataflowOperators',
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
  def 'execute with no data on standard input, #scriptName'() {
    given:
     def process = ['sh', '-c', "${scriptPath} < ${testScriptDirectory}/emptyFile.txt"].execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == 'Mean = NaN, std.dev = NaN, df = -1\n'
    where:
     [scriptName, scriptPath] << scriptPaths
  }

  @Unroll
  def 'execution fails with non-existant file, #scriptName'() {
    given:
     def process = "${scriptPath} flobadob".execute()
    expect:
     process.waitFor() == 1
     process.err.text.startsWith 'Caught: java.io.FileNotFoundException: flobadob (No such file or directory)'
     process.text == ''
    where:
     [scriptName, scriptPath] << scriptPaths
  }

  @Unroll
  def 'execute on empty file, #scriptName'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/emptyFile.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == 'Mean = NaN, std.dev = NaN, df = -1\n'
    where:
     [scriptName, scriptPath] << scriptPaths
  }

  @Unroll
  def 'execute on file with one item, #scriptName'() {
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
  def 'execute on file with two items, #scriptName'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/twoItems.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == "Mean = 1.5, std.dev = ${sqrtHalf}, df = 1\n"
    where:
     [scriptName, scriptPath] << scriptPaths
  }

}
