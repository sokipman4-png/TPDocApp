package com.tpdoc.app.data.export

/**
 * Spec share (pure, JVM-testable).
 *
 * Lessons learned Tahap 2.6 (BUG 2): "Calling startActivity() from outside of an
 * Activity context requires the FLAG_ACTIVITY_NEW_TASK flag." Penaljuran:
 *  - Al sudah intent share wajib mengandung FLAG_ACTIVITY_NEW_TASK | FLAG_GRANT_READ_URI_PERMISSION.
 *  - Chooser yang disampa ke context.startActivity() wajib juga mengandung FLAG_ACTIVITY_NEW_TASK
 *    (fallback saat context bukan Activity: package context/application).
 *
 * Nilai flag = android.content.Intent.FLAG_* (mirror konstanta yang digunakan SDK).
 */
data class ShareSpec(
    val uri: String,
    val mimeType: String,
    val title: String,
    val flags: Int,
)

object ShareIntent {

    /** android.content.Intent.FLAG_ACTIVITY_NEW_TASK (0x10000000). */
    const val FLAG_ACTIVITY_NEW_TASK: Int = 0x10000000

    /** android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION (0x00000001). */
    const val FLAG_GRANT_READ_URI_PERMISSION: Int = 0x00000001

    /** Flag mandatory untuk intent ACTION_SEND via startActivity (BUG 2 fixed). */
    const val FLAGS_SEND: Int = FLAG_ACTIVITY_NEW_TASK | FLAG_GRANT_READ_URI_PERMISSION

    fun build(uri: String, mimeType: String, title: String): ShareSpec =
        ShareSpec(uri = uri, mimeType = mimeType, title = title, flags = FLAGS_SEND)

    fun hasFlag(flags: Int, flag: Int): Boolean = (flags & flag) == flag

    fun containsNewTask(flags: Int): Boolean = hasFlag(flags, FLAG_ACTIVITY_NEW_TASK)

    fun containsReadUriPermission(flags: Int): Boolean = hasFlag(flags, FLAG_GRANT_READ_URI_PERMISSION)
}