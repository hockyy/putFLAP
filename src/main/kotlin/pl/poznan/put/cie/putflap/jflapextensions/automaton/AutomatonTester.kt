package pl.poznan.put.cie.putflap.jflapextensions.automaton

import jflap.automata.*
import jflap.automata.fsa.FiniteStateAutomaton
import jflap.automata.graph.FSAEqualityChecker
import jflap.automata.mealy.MealyMachine
import jflap.automata.mealy.MealyTransition
import jflap.grammar.Grammar
import pl.poznan.put.cie.putflap.report.test.*

/**
 * Implements JFLAP tests
 */
object AutomatonTester {

    fun checkNondeterminism(automatons: Array<Automaton>): MultipleNondeterminismReport {
        val reports = Array(automatons.size) { checkNondeterminism(automatons[it]) }

        return MultipleNondeterminismReport(
            reports.all { it.deterministic },
            reports
        )
    }

    fun checkNondeterminism(automaton: Automaton): NondeterminismReport {
        val states = getNondeterministicStates(automaton)
        return NondeterminismReport(
            states.isEmpty(),
            states,
            checkLambdaTransitions(automaton)
        )
    }

    private fun getNondeterministicStates(automaton: Automaton): Array<State> {
        val detector = NondeterminismDetectorFactory.getDetector(automaton)
        return detector.getNondeterministicStates(automaton)
    }

    private fun checkLambdaTransitions(automaton: Automaton): LambdaTransitionsReport {
        val lambdaTransitions = getLambdaTransitions(automaton)
        return LambdaTransitionsReport(
            lambdaTransitions.isNotEmpty()
        )
    }

    private fun getLambdaTransitions(automaton: Automaton): Array<Transition> {
        val checker = LambdaCheckerFactory.getLambdaChecker(automaton)
        val transitions = automaton.transitions
        val lambdaTransitions = mutableListOf<Transition>()
        transitions.forEach { if (checker.isLambdaTransition(it)) lambdaTransitions.add(it) }

        return lambdaTransitions.toTypedArray()
    }

    fun checkEquivalenceOfManyFSAs(automatons: Array<FiniteStateAutomaton>): EquivalenceReport {
        return EquivalenceReport(
            areEquivalent(automatons)
        )
    }

    private fun checkEquivalenceOfTwoFSAs(a1: FiniteStateAutomaton, a2: FiniteStateAutomaton): EquivalenceReport {
        return EquivalenceReport(areEquivalent(a1, a2))
    }

    private fun areEquivalent(automatons: Array<FiniteStateAutomaton>): Boolean {
        var allEquivalent = true
        for (i in 1 until automatons.size) if (!areEquivalent(automatons[0], automatons[i])) {
            allEquivalent = false
            break
        }

        return allEquivalent
    }

    private fun areEquivalent(a1: FiniteStateAutomaton, a2: FiniteStateAutomaton): Boolean {
        return FSAEqualityChecker().equals(a1, a2)
    }

    fun retrieveAlphabets(automatons: Array<Automaton>): MultipleAlphabetReport {
        return MultipleAlphabetReport(
            Array(automatons.size) { retrieveAlphabet(automatons[it]) }
        )
    }

    fun retrieveAlphabet(automaton: Automaton): AlphabetReport {
        val inAlphabet = retrieveInAlphabet(automaton)
        val outAlphabet = when (automaton) {
            is MealyMachine -> {
                retrieveOutAlphabet(automaton)
            }
            else -> null
        }
        return AlphabetReport(
            inAlphabet.sorted().toTypedArray(),
            outAlphabet?.sorted()?.toTypedArray()
        )
    }

    private fun retrieveInAlphabet(automaton: Automaton): Array<String> {
        val inAlphabet = mutableSetOf<String>()
        automaton.transitions.forEach { inAlphabet.add(it.labelValue()) }
        return inAlphabet.toTypedArray()
    }

    private fun retrieveOutAlphabet(automaton: MealyMachine): Array<String> {
        val outAlphabet = mutableSetOf<String>()
        automaton.transitions.forEach { outAlphabet.add((it as MealyTransition).output) }
        return outAlphabet.toTypedArray()
    }

    private val grammarAlphabetRegex = Regex("[a-z]+")

    fun retrieveAlphabets(grammars: Array<Grammar>): MultipleAlphabetReport {
        return MultipleAlphabetReport(
            Array(grammars.size) { retrieveAlphabet(grammars[it]) }
        )
    }

    fun retrieveAlphabet(grammar: Grammar): AlphabetReport {
        val alphabet = retrieveGrammarAlphabet(grammar)
        return AlphabetReport(alphabet.sorted().toTypedArray())
    }

    private fun retrieveGrammarAlphabet(grammar: Grammar): Array<String> {
        val alphabet = mutableSetOf<String>()
        grammar.productions.forEach { production ->
            grammarAlphabetRegex.findAll(production.lhs).forEach { alphabet.add(it.value) }
            grammarAlphabetRegex.findAll(production.rhs).forEach { alphabet.add(it.value) }
        }
        return alphabet.toTypedArray()
    }
}