package me.cniekirk.flex.ui.text

import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import org.commonmark.node.Link
import java.util.regex.Pattern

class RedditLinkifyTextAddedListener : CorePlugin.OnTextAddedListener {

    private val pattern = Pattern.compile("""/?r/([^\s/]+)""", Pattern.MULTILINE)

    override fun onTextAdded(visitor: MarkwonVisitor, text: String, start: Int) {
        val matcher = pattern.matcher(text)

        while(matcher.find()) {
            val entire = matcher.group(0)
            val subreddit = matcher.group(1)
            val url = "flex://?subreddit=$subreddit"
            val index = start + matcher.start()
            setLink(visitor, url, index, index + entire!!.length)
        }
    }

    private fun setLink(visitor: MarkwonVisitor, destination: String, start: Int, end: Int) {
        val configuration = visitor.configuration()
        val renderProps = visitor.renderProps()

        CoreProps.LINK_DESTINATION.set(renderProps, destination)
        SpannableBuilder.setSpans(
            visitor.builder(),
            configuration.spansFactory().require(Link::class.java).getSpans(configuration, renderProps),
            start,
            end
        )
    }
}