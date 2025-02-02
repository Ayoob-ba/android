package com.tonapps.wallet.data.account.source

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getStringOrNull
import com.tonapps.blockchain.ton.contract.walletVersion
import com.tonapps.extensions.toByteArray
import com.tonapps.extensions.toParcel
import com.tonapps.sqlite.withTransaction
import com.tonapps.wallet.data.account.Wallet
import com.tonapps.wallet.data.account.entities.WalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.ton.api.pub.PublicKeyEd25519

internal class DatabaseSource(
    context: Context,
    private val scope: CoroutineScope
): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private companion object {
        private const val DATABASE_NAME = "account"
        private const val DATABASE_VERSION = 3

        private const val WALLET_TABLE_NAME = "wallet"
        private const val WALLET_TABLE_ID_COLUMN = "id"
        private const val WALLET_TABLE_ID_PUBLIC_KEY_COLUMN = "public_key"
        private const val WALLET_TABLE_TYPE_COLUMN = "type"
        private const val WALLET_TABLE_VERSION_COLUMN = "version"
        private const val WALLET_TABLE_LABEL_COLUMN = "label"
        private const val WALLET_TABLE_LEDGER_DEVICE_ID_COLUMN = "ledger_device_id"
        private const val WALLET_TABLE_LEDGER_ACCOUNT_INDEX_COLUMN = "ledger_account_index"

        private const val WALLET_TABLE_KEYSTONE_XFP_COLUMN = "keystone_xfp"
        private const val WALLET_TABLE_KEYSTONE_PATH_COLUMN = "keystone_path"

        private val walletFields = arrayOf(
            WALLET_TABLE_ID_COLUMN,
            WALLET_TABLE_ID_PUBLIC_KEY_COLUMN,
            WALLET_TABLE_TYPE_COLUMN,
            WALLET_TABLE_VERSION_COLUMN,
            WALLET_TABLE_LABEL_COLUMN,
            WALLET_TABLE_LEDGER_DEVICE_ID_COLUMN,
            WALLET_TABLE_LEDGER_ACCOUNT_INDEX_COLUMN,
            WALLET_TABLE_KEYSTONE_XFP_COLUMN,
            WALLET_TABLE_KEYSTONE_PATH_COLUMN
        ).joinToString(",")

        private fun WalletEntity.toValues(): ContentValues {
            val values = ContentValues()
            values.put(WALLET_TABLE_ID_COLUMN, id)
            values.put(WALLET_TABLE_ID_PUBLIC_KEY_COLUMN, publicKey.key.toByteArray())
            values.put(WALLET_TABLE_TYPE_COLUMN, type.id)
            values.put(WALLET_TABLE_VERSION_COLUMN, version.id)
            values.put(WALLET_TABLE_LABEL_COLUMN, label.toByteArray())
            ledger?.let {
                values.put(WALLET_TABLE_LEDGER_DEVICE_ID_COLUMN, it.deviceId)
                values.put(WALLET_TABLE_LEDGER_ACCOUNT_INDEX_COLUMN, it.accountIndex)
            }
            keystone?.let {
                values.put(WALLET_TABLE_KEYSTONE_XFP_COLUMN, it.xfp)
                values.put(WALLET_TABLE_KEYSTONE_PATH_COLUMN, it.path)
            }
            return values
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $WALLET_TABLE_NAME (" +
                "$WALLET_TABLE_ID_COLUMN TEXT PRIMARY KEY," +
                "$WALLET_TABLE_ID_PUBLIC_KEY_COLUMN BLOB," +
                "$WALLET_TABLE_TYPE_COLUMN INTEGER," +
                "$WALLET_TABLE_VERSION_COLUMN TEXT," +
                "$WALLET_TABLE_LABEL_COLUMN BLOB," +
                "$WALLET_TABLE_LEDGER_DEVICE_ID_COLUMN TEXT," +
                "$WALLET_TABLE_LEDGER_ACCOUNT_INDEX_COLUMN INTEGER," +
                "$WALLET_TABLE_KEYSTONE_XFP_COLUMN TEXT," +
                "$WALLET_TABLE_KEYSTONE_PATH_COLUMN TEXT" +
                ");")

        val walletIndexPrefix = "idx_$WALLET_TABLE_NAME"
        db.execSQL("CREATE UNIQUE INDEX ${walletIndexPrefix}_${WALLET_TABLE_ID_COLUMN} ON $WALLET_TABLE_NAME($WALLET_TABLE_ID_COLUMN);")
        db.execSQL("CREATE INDEX ${walletIndexPrefix}_${WALLET_TABLE_TYPE_COLUMN} ON $WALLET_TABLE_NAME($WALLET_TABLE_TYPE_COLUMN);")
        db.execSQL("CREATE INDEX ${walletIndexPrefix}_${WALLET_TABLE_VERSION_COLUMN} ON $WALLET_TABLE_NAME($WALLET_TABLE_VERSION_COLUMN);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (1 >= oldVersion && newVersion == 2) {
            db.execSQL("ALTER TABLE $WALLET_TABLE_NAME ADD COLUMN $WALLET_TABLE_LEDGER_DEVICE_ID_COLUMN TEXT;")
            db.execSQL("ALTER TABLE $WALLET_TABLE_NAME ADD COLUMN $WALLET_TABLE_LEDGER_ACCOUNT_INDEX_COLUMN INTEGER;")
        }

        if (2 >= oldVersion && newVersion == 3) {
            db.execSQL("ALTER TABLE $WALLET_TABLE_NAME ADD COLUMN $WALLET_TABLE_KEYSTONE_XFP_COLUMN TEXT;")
            db.execSQL("ALTER TABLE $WALLET_TABLE_NAME ADD COLUMN $WALLET_TABLE_KEYSTONE_PATH_COLUMN TEXT;")
        }
    }

    suspend fun clearAccounts() = withContext(scope.coroutineContext) {
        writableDatabase.delete(WALLET_TABLE_NAME, null, null)
    }

    suspend fun deleteAccount(id: String) = withContext(scope.coroutineContext) {
        val count = writableDatabase.delete(WALLET_TABLE_NAME, "$WALLET_TABLE_ID_COLUMN = ?", arrayOf(id))
        if (count == 0) {
            throw IllegalStateException("Account with id $id not found")
        }
    }

    suspend fun editAccount(id: String, label: Wallet.Label) = withContext(scope.coroutineContext) {
        val values = ContentValues()
        values.put(WALLET_TABLE_LABEL_COLUMN, label.toByteArray())
        val count = writableDatabase.update(WALLET_TABLE_NAME, values, "$WALLET_TABLE_ID_COLUMN = ?", arrayOf(id))
        if (count == 0) {
            throw IllegalStateException("Account with id $id not found")
        }
    }

    suspend fun insertAccounts(wallets: List<WalletEntity>) = withContext(scope.coroutineContext) {
        writableDatabase.withTransaction {
            for (wallet in wallets) {
                writableDatabase.insertOrThrow(WALLET_TABLE_NAME, null, wallet.toValues())
            }
        }
    }

    suspend fun getAccounts(): List<WalletEntity> = withContext(scope.coroutineContext) {
        val query = "SELECT $walletFields FROM $WALLET_TABLE_NAME LIMIT 1000;"
        val cursor = readableDatabase.rawQuery(query, null)
        readAccounts(cursor)
    }

    suspend fun getAccount(id: String): WalletEntity? = withContext(scope.coroutineContext) {
        if (id.isNotBlank()) {
            val query = "SELECT $walletFields FROM $WALLET_TABLE_NAME WHERE $WALLET_TABLE_ID_COLUMN = ?;"
            val cursor = readableDatabase.rawQuery(query, arrayOf(id))
            readAccounts(cursor).firstOrNull()
        } else {
            null
        }
    }

    suspend fun getFirstAccountId(): String? = withContext(scope.coroutineContext) {
        val query = "SELECT $WALLET_TABLE_ID_COLUMN FROM $WALLET_TABLE_NAME LIMIT 1;"
        val cursor = readableDatabase.rawQuery(query, null)
        val idIndex = cursor.getColumnIndex(WALLET_TABLE_ID_COLUMN)
        val id = if (cursor.moveToFirst()) {
            cursor.getString(idIndex)
        } else {
            null
        }
        cursor.close()
        id
    }

    private fun readAccounts(cursor: Cursor): List<WalletEntity> {
        val idIndex = cursor.getColumnIndex(WALLET_TABLE_ID_COLUMN)
        val publicKeyIndex = cursor.getColumnIndex(WALLET_TABLE_ID_PUBLIC_KEY_COLUMN)
        val typeIndex = cursor.getColumnIndex(WALLET_TABLE_TYPE_COLUMN)
        val versionIndex = cursor.getColumnIndex(WALLET_TABLE_VERSION_COLUMN)
        val labelIndex = cursor.getColumnIndex(WALLET_TABLE_LABEL_COLUMN)
        val ledgerDeviceIdIndex = cursor.getColumnIndex(WALLET_TABLE_LEDGER_DEVICE_ID_COLUMN)
        val ledgerAccountIndexIndex = cursor.getColumnIndex(WALLET_TABLE_LEDGER_ACCOUNT_INDEX_COLUMN)
        val keystoneXfpIndex = cursor.getColumnIndex(WALLET_TABLE_KEYSTONE_XFP_COLUMN)
        val keystonePathIndex = cursor.getColumnIndex(WALLET_TABLE_KEYSTONE_PATH_COLUMN)
        val accounts = mutableListOf<WalletEntity>()
        while (cursor.moveToNext()) {
            val wallet = WalletEntity(
                id = cursor.getString(idIndex),
                publicKey = PublicKeyEd25519(cursor.getBlob(publicKeyIndex)),
                type = Wallet.typeOf(cursor.getInt(typeIndex)),
                version = walletVersion(cursor.getInt(versionIndex)),
                label = cursor.getBlob(labelIndex).toParcel<Wallet.Label>()!!
            )
            if (wallet.type == Wallet.Type.Ledger) {
                accounts.add(
                    wallet.copy(
                        ledger = WalletEntity.Ledger(
                            deviceId = cursor.getString(ledgerDeviceIdIndex),
                            accountIndex = cursor.getInt(ledgerAccountIndexIndex)
                        )
                    )
                )
            } else if (wallet.type == Wallet.Type.Keystone) {
                accounts.add(
                    wallet.copy(
                        keystone = WalletEntity.Keystone(
                            xfp = cursor.getStringOrNull(keystoneXfpIndex) ?: "",
                            path = cursor.getStringOrNull(keystonePathIndex) ?: ""
                        )
                    )
                )
            } else {
                accounts.add(wallet)
            }
        }
        cursor.close()
        return accounts
    }
}