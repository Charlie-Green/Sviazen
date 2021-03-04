package by.sviazen.server.internal.dao.prisoner

import by.sviazen.server.internal.entity.table.PrisonerEntity
import by.sviazen.server.internal.entity.view.PrisonerView
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository


@org.springframework.stereotype.Repository
interface PrisonerDao: Repository<PrisonerEntity, Int> {

    @Query("select p from PrisonerView p where username=:username and pass=:passwordHash")
    fun get(
        username: String,
        passwordHash: ByteArray
    ): PrisonerView?

    @Query("select p from PrisonerEntity p where id=:id and pass=:passwordHash")
    fun get(
        id: Int,
        passwordHash: ByteArray
    ): PrisonerEntity?

    /** @return [PrisonerView] with only [PrisonerView.info] and
      * [PrisonerView.contactEntities] fields initialized. **/
    @Query("select p from PrisonerView p where p.id=:id")
    fun get(id: Int): PrisonerView

    @Query("select count(*) from PrisonerEntity p where username=:username")
    fun countByUsername(username: String): Int

    fun save(prisoner: PrisonerEntity)
}