package io.fourth_finger.sound_sculptor

import android.content.Context
import java.io.*

private const val N_BACKUPS = 2

class FileUtil {

    companion object {

        private val fileLock = Any()

        /**
         * Returns a [List] of strings appending a number 0 through [N_BACKUPS] to [fileName]
         */
        private fun getFileNames(fileName: String): List<String> {
            val list = mutableListOf<String>()
            for (i in 0..N_BACKUPS) {
                list.add(fileName + i.toString())
            }
            return list
        }

        /**
         * Tries to save a file with the given [fileName].
         * The file saved may not have that name, but can loaded with [load].
         * [saveFileVerificationNumber] is used for verifying the file during [load].
         */
        fun <T : Serializable> save(
            t: T,
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
        ) {
            synchronized(fileLock) {
                val fileName = fileName.replace("/", "")
                val fileNames = getFileNames(fileName)
                var file = File(context.filesDir, fileNames[fileNames.size - 1])
                if (file.exists()) {
                    file.delete()
                }
                for (i in ((fileNames.size - 2) downTo (0))) {
                    val file2 = File(context.filesDir, fileNames[i])
                    file2.renameTo(file)
                    file = File(context.filesDir, fileNames[i])
                }
                context.openFileOutput(fileNames[0], Context.MODE_PRIVATE)
                    .use { fileOutputStream ->
                        ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                            objectOutputStream.writeObject(t)
                            objectOutputStream.writeLong(saveFileVerificationNumber)
                        }
                    }
            }
        }

        /**
         * Tries to save a file with the given [fileName].
         * The file saved may not have that name, but can be used with [load].
         * [saveFileVerificationNumber] is used for verifying the file during [load].
         */
        fun <T : Serializable> saveList(
            t: List<T>,
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long,
        ) {
            synchronized(fileLock) {
                val fileNames = getFileNames(fileName)
                var file = File(context.filesDir, fileNames[fileNames.size - 1])
                if (file.exists()) {
                    file.delete()
                }
                for (i in ((fileNames.size - 2) downTo (0))) {
                    val file2 = File(context.filesDir, fileNames[i])
                    file2.renameTo(file)
                    file = File(context.filesDir, fileNames[i])
                }
                context.openFileOutput(fileNames[0], Context.MODE_PRIVATE).use { fileOutputStream ->
                    ObjectOutputStream(fileOutputStream).use { objectOutputStream ->
                        objectOutputStream.writeInt(t.size)
                        for (t2 in t) {
                            objectOutputStream.writeObject(t2)
                        }
                        objectOutputStream.writeLong(saveFileVerificationNumber)
                    }
                }
            }
        }

        /**
         * Tries to load [fileSave].
         * [saveFileVerificationNumber] is the number passed to [save]
         * and is used to verify the file.
         */
        private fun <T : Serializable> attemptLoadFile(
            context: Context,
            fileSave: String,
            saveFileVerificationNumber: Long
        ): T? {
            var longEOF = 0L
            var t: T? = null
            val file = File(context.filesDir, fileSave)
            if (file.exists()) {
                try {
                    context.openFileInput(fileSave).use { fileInputStream ->
                        ObjectInputStream(fileInputStream).use { objectInputStream ->
                            @Suppress("UNCHECKED_CAST")
                            t = objectInputStream.readObject() as T
                            longEOF = objectInputStream.readLong()
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            if (longEOF != saveFileVerificationNumber) {
                return null
            }
            return t
        }

        /**
         * Tries to load [fileSave].
         * [saveFileVerificationNumber] is the number passed to [save]
         * and is used to verify the file.
         */
        private fun <T : Serializable> attemptLoadListFile(
            context: Context,
            fileSave: String,
            saveFileVerificationNumber: Long
        ): List<T>? {
            var longEOF = 0L
            val t = mutableListOf<T>()
            val file = File(context.filesDir, fileSave)
            if (file.exists()) {
                try {
                    context.openFileInput(fileSave).use { fileInputStream ->
                        ObjectInputStream(fileInputStream).use { objectInputStream ->
                            val n = objectInputStream.readInt()
                            for (i in 0..n) {
                                @Suppress("UNCHECKED_CAST")
                                t.add(objectInputStream.readObject() as T)
                            }
                            longEOF = objectInputStream.readLong()
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            if (longEOF != saveFileVerificationNumber) {
                return null
            }
            return t
        }

        /**
         * Tries to load [fileName].
         * [saveFileVerificationNumber] is the number passed to [save]
         * and is used to verify the file.
         */
        fun <T : Serializable> load(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ): T? {
            synchronized(fileLock) {
                val fileNames = getFileNames(fileName)
                var t: T? = null
                var i = 0
                while (i < fileNames.size) {
                    t = attemptLoadFile(context, fileNames[i], saveFileVerificationNumber)
                    i++
                    if (t != null) {
                        break
                    }
                }
                return t
            }
        }

        /**
         * Tries to load [fileName].
         * [saveFileVerificationNumber] is the number passed to [save]
         * and is used to verify the file.
         */
        fun <T : Serializable> loadList(
            context: Context,
            fileName: String,
            saveFileVerificationNumber: Long
        ): List<T>? {
            synchronized(fileLock) {
                val fileNames = getFileNames(fileName)
                var t: List<T>? = null
                var i = 0
                while (i < fileNames.size) {
                    t = attemptLoadListFile(context, fileNames[i], saveFileVerificationNumber)
                    i++
                    if (t != null) {
                        break
                    }
                }
                return t
            }
        }

        fun delete(
            context: Context,
            fileName: String,
        ) {
            synchronized(fileLock) {
                val fileNames = getFileNames(fileName)
                for (fn in fileNames) {
                    val file = File(context.filesDir, fn)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
        }

    }

}