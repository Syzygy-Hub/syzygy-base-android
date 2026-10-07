package com.syzygyhub.base.storage

// import com.syzygyhub.core.di.Container  // available via JitPack
// import com.syzygy.services.persistence.EncryptedStorageProvider

// TODO: Implement StorageModule
// Resolve from DI container:
//   val storageProvider = appModule.container.resolve(EncryptedStorageProvider::class)
//
// EncryptedStorageProvider wraps Android EncryptedSharedPreferences.
// Use StorageKey<T> typed keys to read/write values:
//   val myKey = StorageKey<String>("my.key")
//   storageProvider.set("value", myKey) { it }
//   val value = storageProvider.get(myKey) { it }
