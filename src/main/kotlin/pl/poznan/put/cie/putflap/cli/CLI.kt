package pl.poznan.put.cie.putflap.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import jflap.file.XMLCodec
import pl.poznan.put.cie.putflap.report.Report
import java.io.File
import java.io.Serializable

object CLI : CliktCommand(
    name = "putflap",
    help = "an extension of JFLAP" +
            "\n\nTHIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE."
) {
    override fun run() = Unit

    private val subs = listOf(RandomCLI, RunCLI, TestCLI, WordCLI, ConvertCLI)
    fun init(args: Array<String>) = subcommands(subs).main(args)


    internal fun saveFile(result: Pair<Report, Serializable>, filename: String, json: Boolean) {
        val file = File("$filename${if (json) ".json" else ".jff"}")
        if (json) file.writeText(Report.getJSON(result.first))
        else XMLCodec().encode(result.second, file, null)
    }

    internal fun saveFile(report: Report, filename: String) {
        val file = File("$filename.json")
        file.writeText(Report.getJSON(report))
    }
}
