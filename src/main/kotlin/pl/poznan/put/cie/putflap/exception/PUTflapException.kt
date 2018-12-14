package pl.poznan.put.cie.putflap.exception

/**
 * Exceptions used in PUTflap. Currently not fully developed
 */
abstract class PUTflapException(message: String) : Exception(message)

class IncompatibleAutomatonException(message: String = "") : PUTflapException(
    "Given automaton cannot be used in current context. $message"
)

class TooManyNonterminalsException : PUTflapException(
    "Maximal supported number of nonterminals is 25"
)

class InvalidActionException(message: String = "") : PUTflapException(message)