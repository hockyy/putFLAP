package pl.poznan.put.cie.putflap.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.int
import jflap.automata.Automaton
import jflap.automata.fsa.FiniteStateAutomaton
import jflap.automata.mealy.MealyMachine
import jflap.automata.pda.PushdownAutomaton
import jflap.file.XMLCodec
import pl.poznan.put.cie.putflap.generator.WordGenerator
import pl.poznan.put.cie.putflap.report.WordsReport
import java.io.File

internal object WordCLI : CliktCommand(name = "word", help = "generate valid word for given automaton") {

    val multiple by option("-m", "--multiple", help = "number of words to generate [default=1]")
        .int()
        .default(1)
        .validate { require(it > 0) { "number of structures must be greater than zero" } }

    private val json by option("-j", "--json", help = "write answer as json")
        .flag(default = false)

    private val automatonFile by argument(help = "name of file with automaton")

    override fun run() {
        val automaton = XMLCodec().decode(File(automatonFile), null) as Automaton
        val report: WordsReport = when(automaton) {
            is FiniteStateAutomaton -> WordGenerator.randomWords(automaton, multiple)
            is MealyMachine /* also Moore */ -> WordGenerator.randomWords(automaton, multiple)
            is PushdownAutomaton -> WordGenerator.randomWords(automaton, multiple)
            else -> TODO("implement word generation for all automatons")
        }

        if (json) CLI.saveFile(report, "words")
        else for (word in report.words) echo(word)
    }
}