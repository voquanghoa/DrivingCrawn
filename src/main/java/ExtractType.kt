import com.google.gson.Gson
import java.nio.file.Path

fun main() {
    val extractedRoot = "extracted"
    //extractedRoot.createFolderIfNotExist()

    fun extract(type: String){
        val folder = Path.of(extractedRoot, type).createFolderIfNotExist()
        val summaryJson = "download/dmw/summary.json".readFile()
        val summary = Gson().fromJson<Summary>(summaryJson, Summary::class.java)

        val data = ExtractedData()
        for(state in summary.summaries){
            val stateId = Common.nameToKey(state.state)
            val stateName = state.state
            val count = state.summaries.first { it.type == type }.count

            data.states.add(ExtractedState(stateId, stateName, count))
            "download/dmw/$stateId/$type".copyChildrenTo("$folder/$stateId", ".json")
        }

        data.saveTo("$folder/states.json")
    }

    extract("Car")
    extract("CDL")
    extract("Motorcycle")
}