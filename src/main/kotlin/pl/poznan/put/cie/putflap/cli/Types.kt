package pl.poznan.put.cie.putflap.cli

abstract class Types {
    enum class RandomType {
        FSA, MOORE, MEALY, REGR
    }

    enum class TestType {
        NDET, EQ, AL
    }

    enum class ConvertType {
        DFA, MINI, GR, RE, PDA, FSA, JSON
    }
}