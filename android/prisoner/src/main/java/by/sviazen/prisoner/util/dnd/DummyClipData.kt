package by.sviazen.prisoner.util.dnd

import android.content.ClipData
import android.content.ClipDescription


class DummyClipData: ClipData(
    null,
    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
    null
)