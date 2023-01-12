package storage

abstract class StoragePreparer {
    abstract fun init()
    abstract fun close()
}