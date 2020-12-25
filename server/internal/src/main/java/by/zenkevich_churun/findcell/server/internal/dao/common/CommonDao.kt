package by.zenkevich_churun.findcell.server.internal.dao.common

import by.zenkevich_churun.findcell.protocol.prisoner.entity.Prisoner
import by.zenkevich_churun.findcell.server.internal.dao.internal.DatabaseConnection
import by.zenkevich_churun.findcell.server.internal.entity.PrisonerEntity
import by.zenkevich_churun.findcell.server.internal.util.ServerInternalUtil.optionalResult


class CommonDao(
    private val connection: DatabaseConnection) {

    private val queryValidateCredentials = ValidateCredentialsQuery()


    /** Checks if the given combination
      * if [Prisoner.id] and [Prisoner.passwordHash] is valid.
      * If it is, the specified [PrisonerEntity] is returned.
      * Otherwise an [IllegalArgumentException] is thrown. **/
    fun validateCredentials(
        id: Int,
        passwordHash: ByteArray
    ): PrisonerEntity {

        val q = queryValidateCredentials
            .getTypedQuery(entityMan, id, passwordHash)

        return q.optionalResult
            ?: throw IllegalArgumentException("User ID or password hash are invalid")
    }


    private val entityMan
        get() = connection.entityMan
}