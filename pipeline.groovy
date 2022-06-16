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

def setFailType(fail_type, type){
    if (!fail_type.isEmpty()) {
        if (!fail_type.contains(type)) {
            fail_type += "," + type
    }
    } else {
        fail_type += type
    }
    return fail_type
}

def findFailType(manager){
    def fail_type = ""
    def previous_fail_type = ""
    if (manager.logContains(".*Error cloning remote repo.*")) {
        fail_type = setFailType(fail_type, "Git_Clone")
    }

    if (manager.logContains(".*NNC build \\(SCaffe\\) build failed.*")) {
        fail_type = setFailType(fail_type, "SCaffe TV failure")
    }

    if (manager.logContains(".*Error occurred while compiling NN models NPU compiler.*")) {
        fail_type = setFailType(fail_type, "Possible compile failure")
    }

    def fp = new FilePath(manager.build.workspace, 'fail_type')
    if (fp.exists()) {
        previous_fail_type = fp.readToString()
    }
    fail_type=setFailType(fail_type, previous_fail_type)
    fp.write(fail_type, null)
}