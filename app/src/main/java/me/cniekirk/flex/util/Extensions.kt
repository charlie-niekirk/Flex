package me.cniekirk.flex.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.analytics.FirebaseAnalytics
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.core.spans.*
import io.noties.markwon.editor.AbstractEditHandler
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorUtils
import io.noties.markwon.editor.PersistedSpans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import me.cniekirk.flex.R
import timber.log.Timber
import java.time.Duration
import kotlin.math.absoluteValue


private val imgurRegex = Regex("""(https?:)?/?/?(\w+\.)?imgur\.com/a/(\w+)""")
private val imgurGalleryRegex = Regex("""(https?:)?/?/?(\w+\.)?imgur\.com/(a|gallery)/(\S*)""")
private val imageRegex = Regex("""\b(https?://\S*?\.(?:png|jpe?g|gif?)(?:\?(?:(?:(?:[\w_-]+=[\w_-]+)(?:&[\w_-]+=[\w_-]+)*)|(?:[\w_-]+)))?)\b""")
private val videoRegex = Regex("""\b(https?://\S*?\.(?:mov|mp4|mpe?g|avi|gifv)(?:\?(?:(?:(?:[\w_-]+=[\w_-]+)(?:&[\w_-]+=[\w_-]+)*)|(?:[\w_-]+)))?)\b""")
private val wikipediaRegex = Regex("""(https?:)?/?/?(\S+\.)wikipedia.org/wiki/[\w+_-]+""")
private val youtubeRegex = Regex("""http(?:s?)://(?:www\.)?youtu(?:be\.com/watch\?v=|\.be/)([\w\-_]+)(\?t=[\w]+)?(&(amp;)?‌​[\w?‌​=]*)?""")
private val twitterRegex = Regex("""^https?://twitter\.com/(?:#!/)?(\w+)/status(es)?/(\d+)(\?\S+)?""")
private val urlRegex = Regex(
    """((?:(http|https|Http|Https)://(?:(?:[a-zA-Z0-9$\-_.+!*'(),;?&=]|(?:%[a-fA-F0-9]{2})){1,64}(?::(?:[a-zA-Z0-9$\-_.+!*'(),;?&=]|(?:%[a-fA-F0-9]{2})){1,25})?@)?)?((?:(?:[a-zA-Z0-9][a-zA-Z0-9\-_]{0,64}\.)+(?:(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])|(?:biz|b[abdefghijmnorstvwyz])|(?:cat|com|coop|c[acdfghiklmnoruvxyz])|d[ejkmoz]|(?:edu|e[cegrstu])|f[ijkmor]|(?:gov|g[abdefghilmnpqrstuwy])|h[kmnrtu]|(?:info|int|i[delmnoqrst])|(?:jobs|j[emop])|k[eghimnrwyz]|l[abcikrstuvy]|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])|(?:name|net|n[acefgilopruz])|(?:org|om)|(?:pro|p[aefghklmnrstwy])|qa|r[eouw]|s[abcdeghijklmnortuvyz]|(?:tel|travel|t[cdfghjklmnoprtvwz])|u[agkmsyz]|v[aceginu]|w[fs]|y[etu]|z[amw]))|(?:(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])))(?::\d{1,5})?)(/(?:(?:[a-zA-Z0-9;/?:@&=#~%\-.+!*',_])|(?:%[a-fA-F0-9]{2}))*)?"""
)

fun Int.condense(): String {
    val cdf = CompactDecimalFormat.getInstance(ULocale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
    return cdf.format(this)
}

fun Long.condense(): String {
    val cdf = CompactDecimalFormat.getInstance(ULocale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
    return cdf.format(this)
}

fun String.selfTextPreview(): String {
    return replace("&amp;#x200B;", "").
    replace("&#x200B;", "").
    replace("#", "").
    replace("\n", " ").trim(' ')
}

fun String.clean(): String {
    return replace("&amp;", "&")
}

private const val SECOND_MILLIS: Long = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val MONTH_MILLIS = 30 * DAY_MILLIS
private const val YEAR_MILLIS = 12 * MONTH_MILLIS

fun Long.getElapsedTime(adjust: Boolean = true): String {
    val now = System.currentTimeMillis()
    val diff = if (adjust) {
        now - (this * 1000)
    } else {
        (now - this).absoluteValue
    }
    return when {
        diff < MINUTE_MILLIS -> {
            "now"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "1m"
        }
        diff < 50 * MINUTE_MILLIS -> {
            "${diff / MINUTE_MILLIS}m"
        }
        diff < 120 * MINUTE_MILLIS -> {
            "1h"
        }
        diff < 24 * HOUR_MILLIS -> {
            "${diff / HOUR_MILLIS}h"
        }
        diff < 48 * HOUR_MILLIS -> {
            "1d"
        }
        diff < MONTH_MILLIS -> {
            "${diff / DAY_MILLIS}d"
        }
        diff < 2 * MONTH_MILLIS -> {
            "1M"
        }
        diff < YEAR_MILLIS -> {
            "${diff / MONTH_MILLIS}M"
        }
        diff < 2 * YEAR_MILLIS -> {
            "1Y"
        }
        else -> {
            "${diff / YEAR_MILLIS}Y"
        }
    }
}

fun String.processLink(): Link {
    return when {
        imageRegex.matches(this) -> {
            Link.ImageLink(this)
        }
        this.startsWith("https://v.redd.it") -> {
            Link.RedditVideo
        }
        this.startsWith("https://redgifs.com") -> {
            Link.RedGifLink
        }
        this.startsWith("https://gfycat.com") -> {
            Link.GfycatLink
        }
        this.startsWith("https://streamable.com") -> {
            Link.StreamableLink
        }
        videoRegex.matches(this) -> {
            Link.VideoLink(this)
        }
        this.contains("reddit.com/gallery") -> {
            Link.RedditGallery
        }
        imgurGalleryRegex.matches(this) -> {
            Link.ImgurGalleryLink(imgurGalleryRegex.matchEntire(this)?.groupValues?.get(4) ?: "")
        }
        twitterRegex.matches(this) -> {
            Link.TwitterLink(this)
        }
        youtubeRegex.matches(this) -> {
            Link.YoutubeLink(youtubeRegex.matchEntire(this)?.groupValues?.get(1) ?: "")
        }
        else -> {
            Link.ExternalLink
        }
    }
}

suspend fun String.processLinkInternal(block: suspend (Link) -> Unit) {
    when {
        imageRegex.matches(this) -> {
            block(Link.ImageLink(this))
        }
        this.startsWith("https://v.redd.it") -> {
            block(Link.RedditVideo)
        }
        this.startsWith("https://redgifs.com") -> {
            block(Link.RedGifLink)
        }
        this.startsWith("https://gfycat.com") -> {
            block(Link.GfycatLink)
        }
        this.startsWith("https://streamable.com") -> {
            block(Link.StreamableLink)
        }
        videoRegex.matches(this) -> {
            block(Link.VideoLink(this))
        }
        this.contains("reddit.com/gallery") -> {
            block(Link.RedditGallery)
        }
        imgurGalleryRegex.matches(this) -> {
            block(Link.ImgurGalleryLink(imgurGalleryRegex.matchEntire(this)?.groupValues?.get(4) ?: ""))
        }
        twitterRegex.matches(this) -> {
            block(Link.TwitterLink(twitterRegex.matchEntire(this)?.groupValues?.get(3) ?: ""))
        }
        else -> {
            block(Link.ExternalLink)
        }
    }
}

fun Int.getDepthColour(): Int {
    return when (this) {
        1 -> R.color.green
        2 -> R.color.blue
        3 -> R.color.indigo
        4 -> R.color.purple
        5 -> R.color.pink
        6 -> R.color.red
        else -> R.color.green
    }
}

fun Fragment.setCurrentScreen() {
    val bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, this@setCurrentScreen::class.java.simpleName)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, this@setCurrentScreen::class.java.simpleName)
    }
    FirebaseAnalytics.getInstance(requireContext()).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
}

fun Fragment.shareText(textToShare: String) {
    val share = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }, null)
    startActivity(share)
}

fun Fragment.shareMedia(mediaUri: Uri) {
    val share = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        type = requireContext().contentResolver.getType(mediaUri)
        putExtra(Intent.EXTRA_STREAM, mediaUri)
    }, null)
    startActivity(share)
}

fun Fragment.shareMedia(mediaUris: ArrayList<Uri>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "image/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUris)
    }
    startActivity(Intent.createChooser(intent, "Share Images"))
}

suspend fun Fragment.getUriFromBitmap(bmp: Bitmap): Uri =
    withContext(Dispatchers.IO) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val contentResolver = requireContext().contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let {
                resolver.openOutputStream(it)?.use { output ->
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, output)
                }
            }
        }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)

        imageUri!!
    }

suspend fun Fragment.loadImage(url: String, onLoaded: suspend (Bitmap) -> Unit) {
    val loader = ImageLoader(requireContext())
    val request = ImageRequest.Builder(requireContext())
        .data(url)
        .allowHardware(false)
        .build()

    when (val result = loader.execute(request)) {
        is SuccessResult -> {
            val bmp = (result.drawable as BitmapDrawable).bitmap
            onLoaded(bmp)
        }
        is ErrorResult -> {
            Timber.e(result.throwable)
        }
    }
}

val easterEggMap = mapOf(
    "tommyinnit" to R.string.easter_egg_tommyinnit,
    "ksi" to R.string.easter_egg_ksi,
    "spacexlounge" to R.string.easter_egg_spacex
)

fun Context.getEasterEggString(subreddit: String): String =
    getString(easterEggMap[subreddit.lowercase()] ?: R.string.default_empty_comments)

fun CharSequence.getUrls(): List<CharSequence>? {
    return if (urlRegex.containsMatchIn(this)) {
        val matches = urlRegex.findAll(this, 0)
        matches.map { this.subSequence(it.range.first, it.range.last + 1) }.toList()
    } else {
        null
    }
}

@ExperimentalCoroutinesApi
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

//fun ImageView.loadImage(imageUrl: String, over18: Boolean) {
//    if (over18) {
//        Glide.with(this)
//            .load(imageUrl)
//            .centerInside()
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 7)))
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .transition(withCrossFade())
//            .into(this)
//    } else {
//        Glide.with(this)
//            .load(imageUrl)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .centerInside()
//            .transition(withCrossFade())
//            .into(this)
//    }
//}

@ColorInt
fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
    val resolvedAttr = resolveThemeAttr(colorAttr)
    // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
    val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}

fun String.toYtThumb(): String =
    "https://img.youtube.com/vi/%s/0.jpg".format(this)

fun String.extractContentLinks(): List<ContentLink> {
    val imgur = imgurRegex.findAll(this).map { ContentLink.ImgurImageLink(it.value) }.distinct()
    val imgurGallery = imgurGalleryRegex.findAll(this).map { ContentLink.ImgurGalleryLink(it.groupValues[3]) }.distinct()
    val image = imageRegex.findAll(this).map { ContentLink.ImageLink(it.value) }.distinct()
    val video = videoRegex.findAll(this).map { ContentLink.VideoLink(it.value) }.distinct()
    val wikipedia = wikipediaRegex.findAll(this).map { ContentLink.WikipediaLink(it.value) }.distinct()
    val youtube = youtubeRegex.findAll(this).map { ContentLink.YoutubeLink(it.value) }.distinct()
    val regular = urlRegex.findAll(this).map { it.value }.distinct()

    val reg = regular
        .filterNot { (imgur + imgurGallery + image + video + wikipedia + youtube).map { link -> link.url }.contains(it) }.map { ContentLink.WebLink(it) }

    return (imgur + imgurGallery + image + video + wikipedia + youtube + reg).toList()
}

fun Context.createMarkdownEditor(markwon: Markwon? = null, linkOnClick: LinkSpanHandler.OnClick): MarkwonEditor {
    return MarkwonEditor.builder(markwon ?: Markwon.create(this))
        .useEditHandler(object : AbstractEditHandler<StrongEmphasisSpan>() {

            override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
                builder.persistSpan(StrongEmphasisSpan::class.java) { StrongEmphasisSpan() }
            }

            override fun handleMarkdownSpan(persistedSpans: PersistedSpans, editable: Editable,
                input: String, span: StrongEmphasisSpan, spanStart: Int, spanTextLength: Int) {
                val strongMatch = MarkwonEditorUtils.findDelimited(input, spanStart, "**", "__")
                if (strongMatch != null) {
                    editable.setSpan(
                        persistedSpans[StrongEmphasisSpan::class.java],
                        strongMatch.start(),
                        strongMatch.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            override fun markdownSpanType(): Class<StrongEmphasisSpan> {
                return StrongEmphasisSpan::class.java
            }

        })
        .useEditHandler(object : AbstractEditHandler<EmphasisSpan>() {
            override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
                builder.persistSpan(EmphasisSpan::class.java) { EmphasisSpan() }
            }

            override fun handleMarkdownSpan(
                persistedSpans: PersistedSpans,
                editable: Editable,
                input: String,
                span: EmphasisSpan,
                spanStart: Int,
                spanTextLength: Int
            ) {
                val emphasisMatch = MarkwonEditorUtils.findDelimited(input, spanStart, "*", "_")
                if (emphasisMatch != null) {
                    editable.setSpan(
                        persistedSpans[EmphasisSpan::class.java],
                        emphasisMatch.start(),
                        emphasisMatch.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            override fun markdownSpanType(): Class<EmphasisSpan> {
                return EmphasisSpan::class.java
            }

        })
        .useEditHandler(object : AbstractEditHandler<StrikethroughSpan>() {
            override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
                builder.persistSpan(StrikethroughSpan::class.java) { StrikethroughSpan() }
            }

            override fun handleMarkdownSpan(
                persistedSpans: PersistedSpans,
                editable: Editable,
                input: String,
                span: StrikethroughSpan,
                spanStart: Int,
                spanTextLength: Int
            ) {
                val emphasisMatch = MarkwonEditorUtils.findDelimited(input, spanStart, "~~")
                if (emphasisMatch != null) {
                    editable.setSpan(
                        persistedSpans[StrikethroughSpan::class.java],
                        emphasisMatch.start(),
                        emphasisMatch.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            override fun markdownSpanType(): Class<StrikethroughSpan> {
                return StrikethroughSpan::class.java
            }

        })
        .useEditHandler(object : AbstractEditHandler<CodeSpan>() {
            override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
                builder.persistSpan(CodeSpan::class.java) { CodeSpan(MarkwonTheme.create(this@createMarkdownEditor)) }
            }

            override fun handleMarkdownSpan(
                persistedSpans: PersistedSpans,
                editable: Editable,
                input: String,
                span: CodeSpan,
                spanStart: Int,
                spanTextLength: Int
            ) {
                val codeMatch = MarkwonEditorUtils.findDelimited(input, spanStart, "`")
                if (codeMatch != null) {
                    editable.setSpan(
                        persistedSpans[CodeSpan::class.java],
                        codeMatch.start(),
                        codeMatch.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            override fun markdownSpanType(): Class<CodeSpan> {
                return CodeSpan::class.java
            }

        })
        .useEditHandler(object : AbstractEditHandler<CodeBlockSpan>() {
            override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
                builder.persistSpan(CodeBlockSpan::class.java) { CodeBlockSpan(MarkwonTheme.create(this@createMarkdownEditor)) }
            }

            override fun handleMarkdownSpan(
                persistedSpans: PersistedSpans,
                editable: Editable,
                input: String,
                span: CodeBlockSpan,
                spanStart: Int,
                spanTextLength: Int
            ) {
                val codeMatch = MarkwonEditorUtils.findDelimited(input, spanStart, "```")
                if (codeMatch != null) {
                    editable.setSpan(
                        persistedSpans[CodeBlockSpan::class.java],
                        codeMatch.start(),
                        codeMatch.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            override fun markdownSpanType(): Class<CodeBlockSpan> {
                return CodeBlockSpan::class.java
            }

        })
        .useEditHandler(object : AbstractEditHandler<HeadingSpan>() {
            override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
                builder.persistSpan(Head1::class.java) {
                    Head1(MarkwonTheme.create(this@createMarkdownEditor))
                }.persistSpan(Head2::class.java) {
                    Head2(MarkwonTheme.create(this@createMarkdownEditor))
                }
            }

            override fun handleMarkdownSpan(
                persistedSpans: PersistedSpans,
                editable: Editable,
                input: String,
                span: HeadingSpan,
                spanStart: Int,
                spanTextLength: Int
            ) {
                val headingMatch = MarkwonEditorUtils.findDelimited(input, spanStart, "##")
                if (headingMatch != null) {
                    editable.setSpan(
                        persistedSpans[HeadingSpan::class.java],
                        headingMatch.start(),
                        headingMatch.end(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                val type = when (span.level) {
                    1 -> Head1::class.java
                    2 -> Head2::class.java
                    else -> null
                }

                type?.let {
                    val index = input.indexOf('\n', spanStart + spanTextLength)
                    val end = if (index < 0) input.length else index
                    editable.setSpan(
                        persistedSpans.get(type),
                        spanStart,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            override fun markdownSpanType(): Class<HeadingSpan> {
                return HeadingSpan::class.java
            }

            inner class Head1(theme: MarkwonTheme) :
                HeadingSpan(theme, 1) {}

            inner class Head2(theme: MarkwonTheme) :
                HeadingSpan(theme, 2) {}
        })
        .useEditHandler(LinkSpanHandler(linkOnClick))
        .build()
}

fun Duration.humanReadableFormat() =
    this.toString()
        .substring(2)
        .replace("(\\d[HMS])(?!$)", "$1 ")
        .lowercase()

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}