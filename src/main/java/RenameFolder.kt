import java.nio.file.Paths

fun main() {

    for((key, label) in Common.states){
        val oldFile = Paths.get("download", "dmw", label).toFile()
        val newFile = Paths.get("download", "dmw", key).toFile()
        println("Rename $label to $key")
        oldFile.renameTo(newFile)
    }

}