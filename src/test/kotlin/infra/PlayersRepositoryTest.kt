package infra

import domain.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayersRepositoryTest {

    @Test
    fun getNonAmericanPlayerTest() {
        val expected = 105

        val result = PlayersRepository.getNonAmericanPlayers()

        assertEquals(expected, result.size)
    }

    @Test
    fun filterNonAmericanPlayerTest() {
        val expected = 19

        val result = PlayersRepository.filterNonAmericanPlayers(Position.GUARD, 7.0)

        assertEquals(expected, result.size)
    }
}