package me.cniekirk.flex.util

//import android.text.Editable
//import android.text.Spanned
//import android.text.style.ClickableSpan
//import android.view.View
//import io.noties.markwon.core.spans.LinkSpan
//import io.noties.markwon.editor.AbstractEditHandler
//import io.noties.markwon.editor.PersistedSpans
//
//
//class LinkSpanHandler(private val onClick: OnClick) : AbstractEditHandler<LinkSpan>() {
//
//    override fun configurePersistedSpans(builder: PersistedSpans.Builder) {
//        builder.persistSpan(EditLinkSpan::class.java) { EditLinkSpan(onClick) }
//    }
//
//    override fun handleMarkdownSpan(
//        persistedSpans: PersistedSpans,
//        editable: Editable,
//        input: String,
//        span: LinkSpan,
//        spanStart: Int,
//        spanTextLength: Int
//    ) {
//        val editLinkSpan = persistedSpans.get(EditLinkSpan::class.java)
//        editLinkSpan.link = span.link
//
//        var start = -1
//        var i = spanStart
//        val length = input.length
//        while (i < length) {
//            if (Character.isLetter(input[i])) {
//                start = i
//                break
//            }
//            i++
//        }
//
//        if (start > -1) {
//            editable.setSpan(
//                editLinkSpan,
//                start,
//                start + spanTextLength,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//    }
//
//    override fun markdownSpanType() =
//        LinkSpan::class.java
//
//    inner class EditLinkSpan(private val onClick: OnClick) : ClickableSpan() {
//        var link: String? = null
//        override fun onClick(widget: View) {
//            if (link != null) {
//                onClick.onClick(widget, link!!)
//            }
//        }
//
//    }
//
//    interface OnClick {
//        fun onClick(view: View, link: String)
//    }
//}