package agenta;

public interface Commander
{
    void act(ActionListener actionListener);

    void obtain(Command com);
}
