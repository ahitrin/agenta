package agenta;

public interface Commander
{
    void act(ActionListener actionListener);

    void obtain(Command com);

    void submit(Commander comm, boolean subordinate);
}
