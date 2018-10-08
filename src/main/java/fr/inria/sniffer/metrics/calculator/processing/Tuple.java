package fr.inria.sniffer.metrics.calculator.processing;

/**
 * Created by sarra on 18/07/17.
 */
public class Tuple<X, Y, Z> {
    public final X introduced;
    public final Y refactored;
    public final Z deleted;

    public Tuple(X introduced, Y refactored, Z deleted) {
        this.introduced = introduced;
        this.refactored = refactored;
        this.deleted = deleted;
    }
}
