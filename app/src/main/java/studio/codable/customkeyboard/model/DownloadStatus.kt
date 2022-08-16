package studio.codable.customkeyboard.model

import java.io.File

sealed class DownloadStatus {

    sealed class Success : DownloadStatus() {
        abstract val result: File

        data class FromCache(override val result: File) : Success()
        data class Downloaded(override val result: File) : Success()
    }

    sealed class Fail : DownloadStatus() {
        open val throwable: Throwable? = null

        object NetworkError : Fail()
        data class IOError(
            override val throwable: Throwable
        ) : Fail()
    }
}