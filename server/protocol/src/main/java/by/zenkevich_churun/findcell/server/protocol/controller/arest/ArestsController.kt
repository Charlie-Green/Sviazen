package by.zenkevich_churun.findcell.server.protocol.controller.arest

import by.zenkevich_churun.findcell.serial.arest.abstr.ArestsDeserializer
import by.zenkevich_churun.findcell.serial.util.protocol.Base64Util
import by.zenkevich_churun.findcell.server.internal.repo.arest.ArestsRepository
import by.zenkevich_churun.findcell.server.protocol.di.ServerKoin
import by.zenkevich_churun.findcell.server.protocol.exc.IllegalServerParameterException
import by.zenkevich_churun.findcell.serial.arest.abstr.ArestsSerializer
import org.springframework.web.bind.annotation.*
import java.io.InputStream


@RestController
class ArestsController {

    private val repo by lazy {
        ServerKoin.instance().get(ArestsRepository::class)
    }


    @PostMapping("/arest/add")
    fun addArest(istream: InputStream) {

        val arest = ArestsDeserializer
            .forVersion(1)
            .deserializeOne(istream)

        val prisonerId = arest.prisonerId
        val passwordBase64 = arest.passwordBase64
        if(prisonerId == null || passwordBase64 == null) {
            println("Add Arest: credentials not specified")
            throw IllegalServerParameterException()
        }
        val passwordHash = Base64Util.decode(passwordBase64, "add arests")

        repo.addArest(arest, prisonerId, passwordHash)
    }


    @PostMapping("/arest/get")
    fun getArests(
        @RequestParam("v") version: Int,
        @RequestParam("id") prisonerId: Int,
        @RequestParam("pass") passwordBase64: String
    ): String {

        val passwordHash = Base64Util.decode(passwordBase64)

        val arests = try {
            repo.getArests(prisonerId, passwordHash)
        } catch(exc: IllegalArgumentException) {
            println(exc.message)
            throw IllegalServerParameterException()
        }

        return ArestsSerializer
            .forVersion(version)
            .serialize(arests)
    }
}