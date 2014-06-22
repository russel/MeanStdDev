
abstract class Runner extends Script {
  def runner() {
    def file = System.in
    switch (args.size()) {
      case 0:
        break
      case 1:
        file = new File(args[0])
        break
      default:
        println 'Zero or one arguments only.'
        return
    }
    def (xb, sd, df) = meanStdDev(file.text.split().collect{Double.parseDouble(it)})
    println "Mean = ${xb}, std.dev = ${sd}, df = ${df}"
  }
}
