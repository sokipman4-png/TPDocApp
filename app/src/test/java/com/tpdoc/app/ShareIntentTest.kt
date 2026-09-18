package com.tpdoc.app

import com.tpdoc.app.data.export.ShareIntent
import org.junit.Assert.*
import org.junit.Test

/**
 * Test share intent (BUG 2 fix Tahap 2.6):
 * de intent moet FLAG_ACTIVITY_NEW_TASK + FLAG_GRANT_READ_URI_PERMISSION bevatten,
 * anders gooit startActivity() de "outside of an Activity context" fout.
 */
class ShareIntentTest {

    @Test
    fun `share intent bevat FLAG_ACTIVITY_NEW_TASK`() {
        val spec = ShareIntent.build("file:///tmp/tpdoc.csv", "text/csv", "Share CSV")
        assertTrue(ShareIntent.containsNewTask(spec.flags))
        assertEquals(ShareIntent.FLAG_ACTIVITY_NEW_TASK, 0x10000000)
    }

    @Test
    fun `share intent bevat FLAG_GRANT_READ_URI_PERMISSION`() {
        val spec = ShareIntent.build("file:///tmp/tpdoc.csv", "text/csv", "Share CSV")
        assertTrue(ShareIntent.containsReadUriPermission(spec.flags))
        assertEquals(ShareIntent.FLAG_GRANT_READ_URI_PERMISSION, 0x00000001)
    }

    @Test
    fun `share intent flags = beide vereiste flags`() {
        val spec = ShareIntent.build("file:///tmp/tpdoc.csv", "text/csv", "Share CSV")
        assertEquals(
            ShareIntent.FLAGS_SEND,
            spec.flags,
        )
        assertEquals(0x10000001, spec.flags)
    }

    @Test
    fun `uri mime en title worden doorgegeven`() {
        val spec = ShareIntent.build("file:///x/tp.csv", "text/csv", "Share Profil PT X")
        assertEquals("file:///x/tp.csv", spec.uri)
        assertEquals("text/csv", spec.mimeType)
        assertEquals("Share Profil PT X", spec.title)
    }

    @Test
    fun `hasFlag false bij ontbrekende flag`() {
        assertFalse(ShareIntent.hasFlag(ShareIntent.FLAG_GRANT_READ_URI_PERMISSION, ShareIntent.FLAG_ACTIVITY_NEW_TASK))
        assertFalse(ShareIntent.hasFlag(0, ShareIntent.FLAG_ACTIVITY_NEW_TASK))
    }
}