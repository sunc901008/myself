package com.lucene;



import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.Operations;

import java.io.IOException;

public class MyAutomatonQuery extends MyMultiTermQuery {
    protected final Automaton automaton;
    protected final CompiledAutomaton compiled;
    protected final Term term;
    public MyAutomatonQuery(final Term term, Automaton automaton) {
        this(term, automaton, Operations.DEFAULT_MAX_DETERMINIZED_STATES);
    }
    public MyAutomatonQuery(final Term term, Automaton automaton, int maxDeterminizedStates) {
        this(term, automaton, maxDeterminizedStates, false);
    }
    public MyAutomatonQuery(final Term term, Automaton automaton, int maxDeterminizedStates, boolean isBinary) {
        super(term.field());
        this.term = term;
        this.automaton = automaton;
        this.compiled = new CompiledAutomaton(automaton, null, true, maxDeterminizedStates, isBinary);
    }

    @Override
    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        return compiled.getTermsEnum(terms);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + compiled.hashCode();
        result = prime * result + ((term == null) ? 0 : term.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyAutomatonQuery other = (MyAutomatonQuery) obj;
        if (!compiled.equals(other.compiled))
            return false;
        if (term == null) {
            if (other.term != null)
                return false;
        } else if (!term.equals(other.term))
            return false;
        return true;
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!term.field().equals(field)) {
            buffer.append(term.field());
            buffer.append(":");
        }
        buffer.append(getClass().getSimpleName());
        buffer.append(" {");
        buffer.append('\n');
        buffer.append(automaton.toString());
        buffer.append("}");
        return buffer.toString();
    }

    public Automaton getAutomaton() {
        return automaton;
    }
}

