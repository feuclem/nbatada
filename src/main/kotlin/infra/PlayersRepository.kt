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
                    availability = Availability.valueOf(it.lineupTag)
                )
            }
        }.also {
            val playersFilePath = javaClass.getResource("../players.json")
            objectMapper.writeValue(File(playersFilePath.path), it)
        }
    }

    fun filterNonAmericanPlayers(position: Position, price: Double): List<Player> {
        val objectMapper = jacksonObjectMapper()
        val nonAmericanPlayersFilePath = javaClass.getResource("../nonamericanplayers.json")
        val dunkestPlayersFilePath = javaClass.getResource("../dunkestplayers.json")

        val nonAmericanPlayers: List<NonAmericanPlayer> = objectMapper.readValue(nonAmericanPlayersFilePath)
        val dunkestPlayers = objectMapper.readValue<List<DunkestPlayer>>(dunkestPlayersFilePath).filter {
            it.quotation <= price && it.roleTag == position.name
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
                    availability = Availability.valueOf(it.lineupTag)
                )
            }
        }.also {
            val playersFilePath = javaClass.getResource("../filtered_players.json")
            objectMapper.writeValue(File(playersFilePath.path), it.sortedBy(Player::averageScore).asReversed())
        }
    }


    private fun CharSequence.unAccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
    }
}