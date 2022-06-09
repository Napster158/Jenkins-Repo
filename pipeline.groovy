import hudson.FilePath
import hudson.model.*

def findClonedRepos(manager){
    def lines = manager.build.logFile.readLines()
    def result = lines.findAll{it.contains("github")}
    manager.listener.logger.println(result.toString());
    manager.listener.logger.println("---------------------");

    def fp = new FilePath(manager.build.workspace, 'result')
    fp.write(result.toString(), null)
}
