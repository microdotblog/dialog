package com.dialogapp.dialog.ui.util

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.text.style.QuoteSpan
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.common.PostClickedListener
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import timber.log.Timber
import java.util.*


class HtmlTextHelper(private val glide: GlideRequests,
                     private val postClickedListener: PostClickedListener,
                     private val contentHtml: String?) {

    fun setHtmlContent(textView: TextView) {
        if (contentHtml != null) {
            val document = Jsoup.parse(contentHtml)
            val images = document.select("img")

            val imageGetter: GlideImageGetter?
            val spannedText: SpannableString
            if (!images.isEmpty()) {
                imageGetter = GlideImageGetter(glide, textView, ImageSize.LARGE, parseImages(images))
                spannedText = getHtmlString(document.html(), imageGetter)
            } else {
                spannedText = getHtmlString(document.html(), null)
            }

            val typedValue = TypedValue()
            textView.context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            setSpans(spannedText, typedValue.data)

            textView.text = trimTrailingWhitespace(spannedText)
            textView.movementMethod = LinkMovementMethod.getInstance()
        } else {
            textView.text = ""
        }
    }

    private fun getHtmlString(html: String, imageGetter: GlideImageGetter?): SpannableString {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(html, FROM_HTML_MODE_LEGACY, imageGetter, null))
        } else {
            SpannableString(Html.fromHtml(html, imageGetter, null))
        }
    }

    private fun setSpans(text: SpannableString, color: Int) {
        for (span in text.getSpans(0, text.length, ImageSpan::class.java)) {
            val flags = text.getSpanFlags(span)
            val start = text.getSpanStart(span)
            val end = text.getSpanEnd(span)

            text.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    postClickedListener.onImageClicked(span.source)
                }
            }, start, end, flags)
        }

        for (span in text.getSpans(0, text.length, URLSpan::class.java)) {
            val start = text.getSpanStart(span)
            val end = text.getSpanEnd(span)
            val flags = text.getSpanFlags(span)

            text.setSpan(object : ClickableSpan() {
                override fun onClick(view: View) {
                    if (text[start] == '@')
                        postClickedListener.onLinkClicked(true, text.subSequence(start + 1, end).toString())
                    else
                        postClickedListener.onLinkClicked(false, span.url)
                }
            }, start, end, flags)

            if (text[start] == '@') {
                text.removeSpan(span)
                text.setSpan(object : URLSpan(span.url) {
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }, start, end, 0)
            }
            text.removeSpan(span)
        }

        val quoteSpan = CustomQuoteSpan(color, 4, 8)
        for (span in text.getSpans(0, text.length, QuoteSpan::class.java)) {
            val start = text.getSpanStart(span)
            val end = text.getSpanEnd(span)
            text.removeSpan(span)
            text.setSpan(quoteSpan, start, end, 0)
        }
    }

    private fun parseImages(images: Elements): Queue<ImageType> {
        val imageTypes = LinkedList<ImageType>()
        for (img in images) {
            Timber.i(img.outerHtml())
            var width = -1
            var height = -1
            if (img.hasAttr("width") && img.hasAttr("height")) {
                try {
                    width = Integer.parseInt(img.attributes().get("width"))
                    height = Integer.parseInt(img.attributes().get("height"))
                } catch (e: NumberFormatException) {
                }
            }

            if (img.hasClass("wp-smiley") || img.hasClass("emoji") ||
                    img.hasClass("mini_thumbnail") || (width in 1..100) && (height in 1..100)
                    || !img.hasAttr("src"))
                imageTypes.add(ImageType.EMOJI)
            else {
                imageTypes.add(ImageType.IMAGE)
            }

            // Add para tags to ensure that there is a gap between each image span
            img.wrap("<p></p>")
        }
        return imageTypes
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