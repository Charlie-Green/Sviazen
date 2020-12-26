package by.zenkevich_churun.findcell.contract.prisoner.encode

import by.zenkevich_churun.findcell.contract.prisoner.entity.Contact
import by.zenkevich_churun.findcell.contract.prisoner.entity.Prisoner
import by.zenkevich_churun.findcell.contract.prisoner.internal.PrisonerContract1
import by.zenkevich_churun.findcell.contract.prisoner.util.ProtocolUtil


/** Converts a Prisoner into JSON format. **/
internal class PrisonerEncoder1: PrisonerEncoder {

    override fun encode(prisoner: Prisoner): String {
        val sb = StringBuilder(
            "{" +
                "\"${PrisonerContract1.KEY_PRISONER_ID  }\": \"${prisoner.id}\",\n" +
                "\"${PrisonerContract1.KEY_PRISONER_NAME}\": \"${prisoner.name}\",\n" +
                "\"${PrisonerContract1.KEY_PRISONER_INFO}\": \"${prisoner.info}\""
        )

        for(c in prisoner.contacts) {
            sb.append(",\n")
            sb.append( encodeContact(c) )
        }

        prisoner.username?.also { username ->
            val key = PrisonerContract1.KEY_PRISONER_USERNAME
            sb.append(",\n\"$key\": \"$username\"")
        }

        // Binary password hash is converted to Base64
        // to be transmitted in a text format.
        prisoner.passwordHash?.also { hash ->
            val base64 = ProtocolUtil.encodeBase64(hash)
            val key = PrisonerContract1.KEY_PRISONER_PASSWORD_HASH
            sb.append(",\n\"$key\": \"$base64\"")
        }

        return sb.toString()
    }


    private fun encodeContact(c: Contact): String {
        return "\"${c.type}\": \"${c.data}\""
    }
}