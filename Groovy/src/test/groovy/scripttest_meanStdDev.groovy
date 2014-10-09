#! /usr/bin/env groovy

import spock.lang.Specification
import spock.lang.Unroll

class scripttest_meanStdDev extends Specification {

  static final projectPath = '/home/users/russel/Progs/Applications/MeanStdDev/Groovy'
  static final scriptsPath = "${projectPath}/src/main/groovy"
  static final testScriptDirectory = "${projectPath}/testScripts"

  static final scriptPaths = [
      'meanStdDev_sequential',
      'meanStdDev_parallel',
      'meanStdDev_promises',
      'meanStdDev_taskPromises',
      'meanStdDev_dataflowVariables',
      'meanStdDev_dataflowOperators',
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
  def 'script on no data on standard input'() {
    given:
     def process = ['sh', '-c', "${scriptPath} < ${testScriptDirectory}/emptyFile.txt"].execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == 'Mean = NaN, std.dev = NaN, df = -1\n'
    where:
     scriptPath << scriptPaths
  }

  @Unroll
  def 'script on non-existant file'() {
    given:
     def process = "${scriptPath} flobadob".execute()
    expect:
     process.waitFor() == 1
     process.err.text.startsWith 'Caught: java.io.FileNotFoundException: flobadob (No such file or directory)'
     process.text == ''
    where:
     scriptPath << scriptPaths
  }

  @Unroll
  def 'script on empty file'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/emptyFile.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == 'Mean = NaN, std.dev = NaN, df = -1\n'
    where:
     scriptPath << scriptPaths
  }

  @Unroll
  def 'script on file with one item'() {
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
  def 'script on file with two item'() {
    given:
     def process = "${scriptPath} ${testScriptDirectory}/twoItems.txt".execute()
    expect:
     process.waitFor() == 0
     process.err.text == ''
     process.text == "Mean = 1.5, std.dev = ${sqrtHalf}, df = 1\n"
    where:
     scriptPath << scriptPaths
  }

}
