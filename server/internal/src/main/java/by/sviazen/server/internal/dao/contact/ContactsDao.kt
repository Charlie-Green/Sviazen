package by.sviazen.server.internal.dao.contact

import by.sviazen.server.internal.entity.key.ContactKey
import by.sviazen.server.internal.entity.table.ContactEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.Repository
import javax.transaction.Transactional


@org.springframework.stereotype.Repository
interface ContactsDao: CrudRepository<ContactEntity, ContactKey> {

    @Transactional
    @Modifying
    @Query("delete from ContactEntity c where prisoner=:prisonerId")
    fun delete(prisonerId: Int)
}