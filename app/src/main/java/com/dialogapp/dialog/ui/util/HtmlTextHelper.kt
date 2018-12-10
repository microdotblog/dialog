package com.dialogapp.dialog.ui.util

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.dialogapp.dialog.GlideRequests
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import timber.log.Timber
import java.util.*


class HtmlTextHelper(private val glide: GlideRequests, private val contentHtml: String?) {

    fun setHtmlContent(textView: TextView, setSpans: Boolean = false) {
        if (contentHtml != null) {
            val document = Jsoup.parse(contentHtml)
            val images = document.select("img")

            val imageGetter: GlideImageGetter?
            val spannedText: SpannableString
            if (!images.isEmpty()) {
                imageGetter = GlideImageGetter(glide, textView, parseImages(images))
                spannedText = getHtmlString(imageGetter)
            } else {
                spannedText = getHtmlString(null)
            }

            if (setSpans) setSpans(spannedText)

            textView.text = trimTrailingWhitespace(spannedText)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } else {
            textView.text = ""
        }
    }

    private fun getHtmlString(imageGetter: GlideImageGetter?): SpannableString {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(contentHtml, FROM_HTML_MODE_LEGACY, imageGetter, null))
        } else {
            SpannableString(Html.fromHtml(contentHtml, imageGetter, null))
        }
    }

    private fun setSpans(text: SpannableString) {
//        for (span in text.getSpans(0, text.length, ImageSpan::class.java)) {
//            val flags = text.getSpanFlags(span)
//            val start = text.getSpanStart(span)
//            val end = text.getSpanEnd(span)
//
//            text.setSpan(object : ClickableSpan() {
//                override fun onClick(widget: View) {
//                    postOptionClickedListener.onImageClicked(span.source)
//                }
//            }, start, end, flags)
//        }
//
//        for (span in text.getSpans(0, text.length, URLSpan::class.java)) {
//            val start = text.getSpanStart(span)
//            val end = text.getSpanEnd(span)
//            val flags = text.getSpanFlags(span)
//
//            text.setSpan(object : ClickableSpan() {
//                override fun onClick(view: View) {
//                    if (text[start] == '@')
//                        postOptionClickedListener.onLinkClicked(true, text.subSequence(start + 1, end).toString())
//                    else
//                        postOptionClickedListener.onLinkClicked(false, span.url)
//                }
//            }, start, end, flags)
//
//            if (text[start] == '@') {
//                text.removeSpan(span)
//                text.setSpan(object : URLSpan(span.url) {
//                    override fun updateDrawState(ds: TextPaint) {
//                        super.updateDrawState(ds)
//                        ds.isUnderlineText = false
//                    }
//                }, start, end, 0)
//            }
//            text.removeSpan(span)
//        }
//
//        val quoteSpan = CustomQuoteSpan(primaryColor, 4, 8)
//        for (span in text.getSpans(0, text.length, QuoteSpan::class.java)) {
//            val start = text.getSpanStart(span)
//            val end = text.getSpanEnd(span)
//            text.removeSpan(span)
//            text.setSpan(quoteSpan, start, end, 0)
//        }
    }

    private fun parseImages(images: Elements): Queue<Boolean> {
        val emojiList = LinkedList<Boolean>()
        for (img in images) {
            Timber.i(img.outerHtml())
            var width = -1
            var height = -1
            var hasAttribs = false
            if (img.hasAttr("width") && img.hasAttr("height")) {
                try {
                    width = Integer.parseInt(img.attributes().get("width"))
                    height = Integer.parseInt(img.attributes().get("height"))
                    hasAttribs = true
                } catch (e: NumberFormatException) {
                    hasAttribs = false
                }
            }

            if (img.hasClass("wp-smiley") || img.hasClass("mini_thumbnail") ||
                    (hasAttribs && (width in 1..100) && (height in 1..100)))
                emojiList.add(true)
            else
                emojiList.add(false)

            // Add para tags to ensure that there is a gap between each image span
            img.wrap("<p></p>")
        }
        return emojiList
    }

    /**
     * Remove unwanted newlines added by Html.fromHtml
     */
    private fun trimTrailingWhitespace(source: CharSequence?): CharSequence {
        if (source == null)
            return ""

        var i = source.length

        // loop back to the first non-whitespace character
        while (--i >= 0 && Character.isWhitespace(source[i])) {
        }

        return source.subSequence(0, i + 1)
    }
}