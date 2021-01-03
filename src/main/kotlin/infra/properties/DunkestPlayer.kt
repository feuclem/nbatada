package infra.properties

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DunkestPlayer(
    val name: String,
    val surname: String,
    @JsonProperty("role_tag")
    val roleTag: String,
    val quotation: Double,
    @JsonProperty("avg_score")
    val averageScore: Double,
    @JsonProperty("lineup_tag")
    val lineupTag: String
)
