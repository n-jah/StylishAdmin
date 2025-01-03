package com.example.stylishadmin.utils

import android.net.Uri

object UriUtils {
    fun isUrl(input: String): Boolean {
        val uri = Uri.parse(input)
        return uri.scheme == "http" || uri.scheme == "https"
    }

    fun isUri(input: String): Boolean {
        val uri = Uri.parse(input)
        return uri.scheme == "content" || uri.scheme == "file"
    }
}
