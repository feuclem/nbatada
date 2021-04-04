package infra

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import domain.Availability
import domain.Player
import domain.Position
import infra.properties.DunkestPlayer
import infra.properties.NonAmericanPlayer
import java.io.File
import java.text.Normalizer

object PlayersRepository {

    fun getBestPlayers(position: Position, averageScoreMax: Double): List<Player> {
        val objectMapper = jacksonObjectMapper()
        val dunkestPlayersFilePath = javaClass.getResource("../dunkestplayers.json")
        val dunkestPlayers: List<DunkestPlayer> = objectMapper.readValue(dunkestPlayersFilePath)

        return dunkestPlayers
            .filter { it.roleTag == position.name }
            .filter { it.averageScore <= averageScoreMax }
            .sortedBy { it.averageScore }
            .asReversed()
            .map {
                Player(
                    firstName = it.name,
                    lastName = it.surname,
                    nationality = "",
                    position = Position.valueOf(it.roleTag),
                    price = it.quotation,
                    averageScore = it.averageScore,
                    availability = Availability.valueOf(it.lineupTag ?: "NULL")
                )
            }.also {
                val playersFilePath = javaClass.getResource("../ratioed_centers.json")
                objectMapper.writeValue(File(playersFilePath.path), it)
            }
    }

    fun getBestRatioPlayer(position: Position?, price: Double): List<Player> {
        val objectMapper = jacksonObjectMapper()
        val dunkestPlayersFilePath = javaClass.getResource("../dunkestplayers.json")
        val dunkestPlayers: List<DunkestPlayer> = objectMapper.readValue(dunkestPlayersFilePath)

        if (position == null) {

        }

        return dunkestPlayers
            .let { listDunkestPlayer ->
                if (position != null) {
                    listDunkestPlayer.filter { it.roleTag == position.name }
                } else {
                    listDunkestPlayer
                }
            }
            .filter { it.quotation <= price }
            .filter { it.lineupTag != "INJURED" && it.lineupTag != "QUESTIONABLE" && it.lineupTag != "DISQUALIFIED" }
            .filter { it.lineupTag != "BENCH_PLAYER" }
            .sortedBy { it.averageScore / it.quotation }
            .asReversed()
            .map {
                Player(
                    firstName = it.name,
                    lastName = it.surname,
                    nationality = "",
                    position = Position.valueOf(it.roleTag),
                    price = it.quotation,
                    averageScore = it.averageScore,
                    availability = Availability.valueOf(it.lineupTag ?: "NULL")
                )
            }.also {
                val playersFilePath = when (position) {
                    Position.CENTER -> javaClass.getResource("../ratioed_centers.json")
                    Position.GUARD -> javaClass.getResource("../ratioed_guards.json")
                    Position.FORWARD -> javaClass.getResource("../ratioed_forwards.json")
                    else -> javaClass.getResource("../ratioed_players.json")
                   }
                objectMapper.writeValue(File(playersFilePath.path), it)
            }
    }

    fun getNonAmericanPlayers(): List<Player> {
        val objectMapper = jacksonObjectMapper()
        val nonAmericanPlayersFilePath = javaClass.getResource("../nonamericanplayers.json")
        val dunkestPlayersFilePath = javaClass.getResource("../dunkestplayers.json")

        val nonAmericanPlayers: List<NonAmericanPlayer> = objectMapper.readValue(nonAmericanPlayersFilePath)
        val dunkestPlayers: List<DunkestPlayer> = objectMapper.readValue(dunkestPlayersFilePath)

        return nonAmericanPlayers.mapNotNull { nonAmericanPlayer ->
            dunkestPlayers.find {
                it.name.unAccent().equals(nonAmericanPlayer.firstName.unAccent(), ignoreCase = true)
                        && it.surname.unAccent().equals(nonAmericanPlayer.lastName.unAccent(), ignoreCase = true)
            }?.let {
                Player(
                    firstName = nonAmericanPlayer.firstName,
                    lastName = nonAmericanPlayer.lastName,
                    nationality = nonAmericanPlayer.nationality,
                    position = Position.valueOf(it.roleTag),
                    price = it.quotation,
                    averageScore = it.averageScore,
                    availability = Availability.valueOf(it.lineupTag ?: "NULL")
                )
            }
        }.also {
            val playersFilePath = javaClass.getResource("../all_non_american_players.json")
            objectMapper.writeValue(File(playersFilePath.path), it)
        }
    }

    fun filterNonAmericanPlayers(position: Position?, price: Double): List<Player> {
        val objectMapper = jacksonObjectMapper()
        val nonAmericanPlayersFilePath = javaClass.getResource("../nonamericanplayers.json")
        val dunkestPlayersFilePath = javaClass.getResource("../dunkestplayers.json")

        val nonAmericanPlayers: List<NonAmericanPlayer> = objectMapper.readValue(nonAmericanPlayersFilePath)
        val dunkestPlayers = objectMapper.readValue<List<DunkestPlayer>>(dunkestPlayersFilePath).filter {
            it.quotation <= price
        }.let { listDunkestPlayer ->
            if(position != null) {
                listDunkestPlayer.filter { it.roleTag == position.name }
            } else {
                listDunkestPlayer
            }
        }

        return nonAmericanPlayers.mapNotNull { nonAmericanPlayer ->
            dunkestPlayers.find {
                it.name.unAccent().equals(nonAmericanPlayer.firstName.unAccent(), ignoreCase = true)
                        && it.surname.unAccent().equals(nonAmericanPlayer.lastName.unAccent(), ignoreCase = true)
            }?.let {
                Player(
                    firstName = nonAmericanPlayer.firstName,
                    lastName = nonAmericanPlayer.lastName,
                    nationality = nonAmericanPlayer.nationality,
                    position = Position.valueOf(it.roleTag),
                    price = it.quotation,
                    averageScore = it.averageScore,
                    availability = Availability.valueOf(it.lineupTag ?: "NULL")
                )
            }
        }.also {
            val playersFilePath = javaClass.getResource("../filtered_non_american_players.json")
            objectMapper.writeValue(File(playersFilePath.path), it.sortedBy(Player::averageScore).asReversed())
        }
    }


    private fun CharSequence.unAccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
    }
}