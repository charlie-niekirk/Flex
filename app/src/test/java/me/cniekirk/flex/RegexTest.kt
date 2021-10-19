package me.cniekirk.flex

import me.cniekirk.flex.util.getUrls
import org.junit.Test
import org.junit.Assert

class RegexTest {

    private val urlsToExtract = listOf("google.com", "https://reddit.com/r/tommyinnit/something")
    private val input = "Hello there, we need to find google.com and lets see what https://reddit.com/r/tommyinnit/something has to offer"

    @Test
    fun `test extracting urls works correctly`() {
        val urls = input.getUrls()
        Assert.assertNotNull(urls)
        Assert.assertEquals(urls!![0].toString(), urlsToExtract[0])
        Assert.assertEquals(urls[1].toString(), urlsToExtract[1])
    }

}