package infra

import domain.Availability
import domain.Player
import domain.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayersRepositoryTest {

    @Test
    fun getNonAmericanPlayerTest() {
        val expected = 104

        val result = PlayersRepository.getNonAmericanPlayers()

        assertEquals(expected, result.size)
    }

    @Test
    fun filterNonAmericanPlayerTest() {
        val expected = 19

        val result = PlayersRepository.filterNonAmericanPlayers(Position.FORWARD,6.0)

        assertEquals(expected, result.size)
    }

    @Test
    fun getBestPlayersTest() {
        val expected = listOf(
            Player(
                firstName = "NIKOLA",
                lastName = "JOKIC",
                nationality = "",
                position = Position.CENTER,
                price = 22.0,
                averageScore = 50.47,
                availability = Availability.REGULAR
            ),
            Player(
                firstName = "JOEL",
                lastName = "EMBIID",
                nationality = "",
                position = Position.CENTER,
                price = 19.8,
                averageScore = 42.75,
                availability = Availability.QUESTIONABLE
            ),
            Player(
                firstName = "KARL-ANTHONY",
                lastName = "TOWNS",
                nationality = "",
                position = Position.CENTER,
                price = 17.9,
                averageScore = 40.0,
                availability = Availability.REGULAR
            )
        )

        val result = PlayersRepository.getBestPlayers(Position.CENTER, 100.0)

        assertEquals(expected, result.subList(0, 3))
    }

    @Test
    fun getBestRatioPlayerTestCENTER() {
        PlayersRepository.getBestRatioPlayer(Position.CENTER, 100.0)
    }

    @Test
    fun getBestRatioPlayerTestFORWARD() {
        PlayersRepository.getBestRatioPlayer(Position.FORWARD, 100.0)
    }

    @Test
    fun getBestRatioPlayerTestGUARD() {
        PlayersRepository.getBestRatioPlayer(Position.GUARD, 100.0)
    }

    @Test
    fun getBestRatioPlayerTestALL() {
        PlayersRepository.getBestRatioPlayer(null, 100.0)
    }
}