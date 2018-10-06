package longmoneyoffshore.dlrtime.utils;

public class CompositeType<TOne,TTwo> {
    public TOne firstArg;
    public TTwo secondArg;

    public CompositeType (TOne t_one_arg, TTwo t_two_arg) {
        firstArg = t_one_arg;
        secondArg = t_two_arg;
    }
}
