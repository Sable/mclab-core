package natlab.tame.valueanalysis.components.constant;


public interface HasConstant {
    /**
     * returns the constant associated with this value, or null if it's not a constant
     * @return
     */
    public Constant getConstant();

}
