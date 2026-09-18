package com.tpdoc.app.data.export

import com.tpdoc.app.data.room.Perusahaan

/**
 * Codec JSON pure untuk backup/restore. Parse ijo sebuah subset JSON yang cukup
 * untuk data Perusahaan. Unit-testable (nul dependency android).
 */
object JsonCodec {

    fun encode(perusahaans: List<Perusahaan>): String {
        if (perusahaans.isEmpty()) return "[]"
        val sb = StringBuilder()
        sb.append("[\n")
        perusahaans.forEachIndexed { i, p ->
            sb.append("  {\n")
            sb.append("    \"id\": ${p.id},\n")
            sb.append("    \"nama\": ").append(jsonString(p.nama)).append(",\n")
            sb.append("    \"npwp\": ").append(jsonString(p.npwp)).append(",\n")
            sb.append("    \"alamat\": ").append(jsonString(p.alamat)).append(",\n")
            sb.append("    \"negara\": ").append(jsonString(p.negara)).append(",\n")
            sb.append("    \"status\": ").append(jsonString(p.status)).append(",\n")
            sb.append("    \"parentId\": ").append(p.parentId ?: "null").append(",\n")
            sb.append("    \"tahunPajak\": ${p.tahunPajak},\n")
            sb.append("    \"logoPath\": ").append(p.logoPath?.let { jsonString(it) } ?: "null").append("\n")
            sb.append("  }")
            if (i < perusahaans.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("]")
        return sb.toString()
    }

    fun decode(text: String): List<Perusahaan> {
        val parser = Parser(text)
        val value = parser.parseValue()
        if (value !is ListValue) throw RuntimeException("Format JSON tidak valid: top-level wajib array")
        return value.items.mapNotNull { item ->
            val map = if (item is MapValue) (item as MapValue) else throw RuntimeException("Format JSON tidak valid: item wajib object")
            Perusahaan(
                id = map.longOr("id", 0L),
                nama = map.stringOr("nama", ""),
                npwp = map.stringOr("npwp", ""),
                alamat = map.stringOr("alamat", ""),
                negara = map.stringOr("negara", "Indonesia"),
                status = map.stringOr("status", Perusahaan.STATUS_INDUK),
                parentId = map.nullableLong("parentId"),
                tahunPajak = map.intOr("tahunPajak", 2024),
                logoPath = map.nullableString("logoPath"),
            )
        }
    }

    fun jsonString(s: String): String {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\""
    }

    // ---- Mini JSON parser ----

    private sealed class JsonValue
    private data class MapValue(val entries: List<Pair<String, JsonValue>>) : JsonValue() {
        fun longOr(key: String, def: Long): Long = entries.find { it.first == key }?.second?.let { (it as? NumValue)?.value?.toLong() ?: def } ?: def
        fun intOr(key: String, def: Int): Int = entries.find { it.first == key }?.second?.let { (it as? NumValue)?.value?.toInt() ?: def } ?: def
        fun stringOr(key: String, def: String): String = entries.find { it.first == key }?.second?.let { if (it is StrValue) (it as StrValue).value else def } ?: def
        fun nullableLong(key: String): Long? = entries.find { it.first == key }?.second?.let {
            when (it) {
                is NullValue -> null
                is NumValue -> (it as NumValue).value.toLong()
                else -> null
            }
        }
        fun nullableString(key: String): String? = entries.find { it.first == key }?.second?.let {
            when (it) {
                is NullValue -> null
                is StrValue -> (it as StrValue).value
                else -> null
            }
        }
    }

    private data class ListValue(val items: List<JsonValue>) : JsonValue()
    private data class StrValue(val value: String) : JsonValue()
    private data class NumValue(val value: Double) : JsonValue()
    private class NullValue : JsonValue()
    private data class BoolValue(val value: Boolean) : JsonValue()

    private class Parser(private val text: String) {
        private var pos = 0

        fun parseValue(): JsonValue {
            skipWs()
            if (pos >= text.length) throw RuntimeException("Unexpected end")
            return when (text[pos]) {
                '{' -> parseObject()
                '[' -> parseArray()
                '"' -> StrValue(parseString())
                't' -> { expect("true"); BoolValue(true) }
                'f' -> { expect("false"); BoolValue(false) }
                'n' -> { expect("null"); NullValue() }
                else -> NumValue(parseNumber())
            }
        }

        private fun parseObject(): JsonValue {
            pos++ // {
            val entries = ArrayList<Pair<String, JsonValue>>()
            skipWs()
            if (pos < text.length && text[pos] == '}') { pos++; return MapValue(entries) }
            while (pos < text.length) {
                skipWs()
                val key = parseString()
                skipWs()
                if (pos >= text.length || text[pos] != ':') throw RuntimeException("Expected ':'")
                pos++
                val value = parseValue()
                entries.add(key to value)
                skipWs()
                if (pos >= text.length) throw RuntimeException("Expected ',' or '}'")
                when (text[pos]) {
                    ',' -> pos++
                    '}' -> { pos++; break }
                    else -> throw RuntimeException("Expected ',' or '}'")
                }
            }
            return MapValue(entries)
        }

        private fun parseArray(): JsonValue {
            pos++ // [
            val items = ArrayList<JsonValue>()
            skipWs()
            if (pos < text.length && text[pos] == ']') { pos++; return ListValue(items) }
            while (pos < text.length) {
                items.add(parseValue())
                skipWs()
                if (pos >= text.length) throw RuntimeException("Expected ',' or ']'")
                when (text[pos]) {
                    ',' -> pos++
                    ']' -> { pos++; break }
                    else -> throw RuntimeException("Expected ',' or ']'")
                }
            }
            return ListValue(items)
        }

        private fun parseString(): String {
            if (pos >= text.length || text[pos] != '"') throw RuntimeException("Expected string")
            pos++
            val sb = StringBuilder()
            while (pos < text.length) {
                val c = text[pos]
                if (c == '"') { pos++; return sb.toString() }
                if (c == '\\') {
                    pos++
                    if (pos >= text.length) throw RuntimeException("Bad escape")
                    val e = text[pos]
                    when (e) {
                        '"' -> sb.append('"')
                        '\\' -> sb.append('\\')
                        '/' -> sb.append('/')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        't' -> sb.append('\t')
                        'b' -> sb.append('\b')
                        'f' -> sb.append('\u000C')
                        'u' -> {
                            if (pos + 4 >= text.length) throw RuntimeException("Bad unicode escape")
                            val hex = text.substring(pos + 1, pos + 5)
                            sb.appendCodePoint(hex.toInt(16))
                            pos += 4
                        }
                        else -> throw RuntimeException("Bad escape")
                    }
                    pos++
                } else {
                    sb.append(c)
                    pos++
                }
            }
            throw RuntimeException("Unterminated string")
        }

        private fun parseNumber(): Double {
            var end = pos
            while (end < text.length && (text[end].isDigit() || text[end] in "+-.eE")) end++
            if (end == pos) throw RuntimeException("Invalid number")
            val numText = text.substring(pos, end)
            pos = end
            return numText.toDoubleOrNull() ?: throw RuntimeException("Invalid number: $numText")
        }

        private fun expect(literal: String) {
            if (!text.substring(pos).startsWith(literal)) throw RuntimeException("Expected '$literal'")
            pos += literal.length
        }

        private fun skipWs() {
            while (pos < text.length && text[pos] in " \t\r\n") pos++
        }
    }
}