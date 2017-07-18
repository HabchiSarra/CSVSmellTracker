package developers;

/**
 * Created by sarra on 18/07/17.
 */
public class Tuple<X,Y> {
    public final X introduced;
    public final Y refactored;
    public Tuple(X introduced, Y refactored) {
        this.introduced = introduced;
        this.refactored = refactored;
    }
}
