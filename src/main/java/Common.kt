import java.nio.file.Path

object Common {

    val states = mapOf(
            "alabama" to "Alabama",
            "alaska" to "Alaska",
            "arizona" to "Arizona",
            "arkansas" to "Arkansas",
            "california" to "California",
            "colorado" to "Colorado",
            "connecticut" to "Connecticut",
            "delaware" to "Delaware",
            "district-of-columbia" to "District Of Columbia",
            "florida" to "Florida",
            "georgia" to "Georgia",
            "hawaii" to "Hawaii",
            "idaho" to "Idaho",
            "illinois" to "Illinois",
            "indiana" to "Indiana",
            "iowa" to "Iowa",
            "kansas" to "Kansas",
            "kentucky" to "Kentucky",
            "louisiana" to "Louisiana",
            "maine" to "Maine",
            "maryland" to "Maryland",
            "massachusetts" to "Massachusetts",
            "michigan" to "Michigan",
            "minnesota" to "Minnesota",
            "mississippi" to "Mississippi",
            "missouri" to "Missouri",
            "montana" to "Montana",
            "nebraska" to "Nebraska",
            "nevada" to "Nevada",
            "new-hampshire" to "New Hampshire",
            "new-jersey" to "New Jersey",
            "new-mexico" to "New Mexico",
            "new-york" to "New York",
            "north-carolina" to "North Carolina",
            "north-dakota" to "North Dakota",
            "ohio" to "Ohio",
            "oklahoma" to "Oklahoma",
            "oregon" to "Oregon",
            "pennsylvania" to "Pennsylvania",
            "rhode-island" to "Rhode Island",
            "south-carolina" to "South Carolina",
            "south-dakota" to "South Dakota",
            "tennessee" to "Tennessee",
            "texas" to "Texas",
            "utah" to "Utah",
            "vermont" to "Vermont",
            "virginia" to "Virginia",
            "washington" to "Washington",
            "west-virginia" to "West Virginia",
            "wisconsin" to "Wisconsin",
            "wyoming" to "Wyoming"
    )

    fun keyToName(key: String): String{
        return states[key]!!
    }

    fun nameToKey(name: String): String{
        for(s in states){
            if(s.value == name){
                return s.key
            }
        }

        return ""
    }
}
data class State(val key: String, val name: String)
data class Usa(val states: List<State>)
fun main() {
    val data = Usa(Common.states.map { State(it.key, it.value) })
    data.saveTo(Path.of("download","usa.json").toString())
}