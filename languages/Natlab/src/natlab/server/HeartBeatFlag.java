package natlab.server;

public class HeartBeatFlag
{
    private volatile boolean flag;

    public HeartBeatFlag()
    {
        flag = true;
    }

    public synchronized void set()
    {
        flag = true;
    }
    public synchronized void unset()
    {
        flag = false;
    }
    public synchronized boolean readFlag()
    {
        return flag;
    }
    public synchronized boolean readFlagAndUnset()
    {
        boolean f = flag;
        flag = false;
        return f;
    }
}