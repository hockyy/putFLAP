package pl.poznan.put.cie.putflap.generator

import jflap.automata.Automaton
import jflap.automata.State
import jflap.automata.fsa.FiniteStateAutomaton
import jflap.automata.mealy.MealyMachine
import jflap.automata.pda.PDATransition
import jflap.automata.pda.PushdownAutomaton
import pl.poznan.put.cie.putflap.jflapextensions.automaton.labelValue
import pl.poznan.put.cie.putflap.report.WordsReport
import java.util.*

/**
 * Implements generation of random, valid words for automatons
 */
object WordGenerator {
    private const val p = .5
    private const val LAMBDA = ""

    /**
     * Generates [n] random, valid words for specified [automaton] and returns [WordsReport]. The number of generated words may be equal or smaller than [n].
     */
    fun words(automaton: Automaton, n: Int): WordsReport {
        val words = randomMultiple(automaton, n)

        return WordsReport(
            n,
            words.size,
            words
        )
    }

    /**
     * Generates [n] random, valid words for specified [automaton]. The number of generated words may be equal or smaller than [n]
     */
    private fun randomMultiple(automaton: Automaton, n: Int): Array<String> {
        val words = mutableSetOf<String>()
        var sinceLastNew = 0
        val maxSinceLastNew = getMaxSinceLastNew(automaton.transitions.size)
        while (words.size < n && sinceLastNew < maxSinceLastNew) {
            val word = when (automaton) {
                is FiniteStateAutomaton -> randomSingle(automaton)
                is MealyMachine /* also Moore */ -> randomSingle(automaton)
                is PushdownAutomaton -> randomSingle(automaton)
                else -> TODO("implement word generation for all automatons")
            }

            if (words.add(word)) sinceLastNew = 0
            else sinceLastNew++
        }

        return words.toTypedArray()
    }

    private fun randomSingle(automaton: Automaton): String {
        var word = ""
        var currentState = getStartState(automaton)

        var on = true
        while (on) {
            // select transition
            val transition = automaton.getTransitionsToState(currentState).random()

            // update word
            word = transition.labelValue() + word

            // update current state
            currentState = transition.fromState

            // check end condition
            if (automaton.initialState == currentState)
                if (automaton.getTransitionsToState(currentState).isEmpty()) on = false
                else if (Math.random() > p) on = false
        }

        return word
    }

    private fun randomSingle(automaton: PushdownAutomaton): String {
        var word = ""
        var currentState = getStartState(automaton)
        val stack = LinkedList<String>()
        stack.push("Z")

        var on = true
        while (on) {
            // select transition
            var transition: PDATransition
            do {
                transition = automaton.getTransitionsToState(currentState).random() as PDATransition
            } while (run {
                    when {
                        transition.stringToPush == LAMBDA -> false
                        transition.stringToPush.length > 1 -> {
                            var w = ""
                            for (i in 0 until transition.stringToPush.length) {
                                w += stack[i]
                            }
                            transition.stringToPush != w
                        }
                        else -> transition.stringToPush != stack.first
                    }
                })

            // update stack
            if (transition.stringToPush != LAMBDA) for (i in 0 until transition.stringToPush.length) stack.pop()
            if (transition.stringToPop != LAMBDA) stack.push(transition.stringToPop)

            // update word
            word = transition.labelValue() + word

            // update current state
            currentState = transition.fromState

            // check end condition
            if (automaton.initialState == currentState && stack.contains("Z") && stack.size == 1) on = false
        }

        return word
    }

    private fun getStartState(automaton: Automaton): State {
        var startState: State
        do {
            startState = automaton.states.random()
        } while (
            when (automaton) {
                is FiniteStateAutomaton -> !automaton.finalStates.contains(startState)
                is MealyMachine /* also Moore */ -> automaton.getTransitionsToState(startState).isEmpty()
                is PushdownAutomaton -> !automaton.finalStates.contains(startState)
                else -> TODO("implement word generator for all automatons")
            }
        )

        return startState
    }

    private fun getMaxSinceLastNew(n: Int): Int = n * 50
}