import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

import agenta.Command;
import agenta.Commander;
import agenta.UnitDatabase;
import agenta.UnitType;

/**
 <p>Простой командующий. Умеет считывать себя из текстового файла, задаёт команды только
 один раз.

 <p>Понимает три типа команд:<br>
 <юнит> attack [<юнит>] <авторитет><br>
 <юнит> stand <авторитет><br>
 <юнит> escape<br>

 <p>При запросе команд возвращает считанные из файла команды (каждую - один раз), после чего
 возвращает <code>null</code>. То есть, приказы отдаются только один раз - в начале работы.
 */

class SimpleCommander implements Commander
{
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Format: java SimpleCommander filename");
            System.exit(0);
        }
        SimpleCommander sc = new SimpleCommander(args[0]);
        UnitDatabase ud = UnitDatabase.get();

        for (int i = 0; i < ud.size(); i++)
            System.out.println(ud.nameOf(i) + sc.hasNewOrder(ud.typeOf(i)));
    }
    private Vector<String> commands = new Vector<String>();
    private Vector<String> types = new Vector<String>();
    private Vector<Boolean> flags = new Vector<Boolean>();

    public SimpleCommander(String fileName)
    {
        String s1 = "";

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int temp;
            StringBuilder sb = new StringBuilder(80);
            while ((temp = br.read()) != -1)
                sb.append((char)temp);
            br.close();
            s1 = sb.toString();

        }
        catch (FileNotFoundException e)
        {
            System.err.println("SimpleCommander - File not found: " + fileName);
            System.exit(0);
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(0);
        }
        s1 = s1.toLowerCase();

        StringTokenizer st = new StringTokenizer(s1);
        if (!st.hasMoreTokens())
        {
            System.err.println("File " + fileName + " is empty!");
            System.exit(0);
        }

        String s2;
        // флаг - надо ли читать авторитет из парсера
        boolean flag;
        UnitDatabase ud = UnitDatabase.get();
        Vector<String> orders = new Vector<String>(3);
        orders.add("attack");
        orders.add("stand");
        orders.add("escape");
        while (st.hasMoreTokens())
            try
            {
                flag = true;
                // получаем тип юнита
                s2 = st.nextToken();
                if (ud.indexOf(s2) == -1)
                    throw new Exception();
                types.add(s2);

                // получаем тип приказа
                s1 = st.nextToken();
                s2 = s2 + " " + s1;
                switch (orders.indexOf(s1))
                {
                case 0: // получаем тип атакуемого юнита
                    s1 = st.nextToken();
                    if (ud.indexOf(s1) != -1)
                        s2 = s2 + " " + s1;
                        // это поле может отсутствовать!
                    else
                        flag = false;
                    // никаких "break"! Надо идти дальше

                case 1: // получаем авторитет приказа (целое число от 1 до 4)
                    if (flag)
                        s1 = st.nextToken().substring(0, 1);
                    int temp = Integer.parseInt(s1);
                    if ((temp >= 1) && (temp <= 4))
                        s2 = s2 + " " + s1;
                    else
                        throw new Exception();
                    // снова идём дальше

                case 2: // конец  приказа
                    break;
                default:
                    throw new Exception();
                }
                commands.add(s2);
                flags.add(new Boolean("true"));
            }
            catch (Exception e)
            {
                System.err.println("SimpleCommander - Error in file " + fileName);
                System.exit(0);
            }
    }

    public void act()
    {
    }

    public String getOrder(UnitType ut)
    {
        int i = types.indexOf(ut.getName());
        if (flags.get(i).booleanValue())
        {
            flags.set(i, new Boolean("false"));
            return commands.get(i);
        }
        return null;
    }

    public boolean hasNewOrder(UnitType ut)
    {
        return flags.get(types.indexOf(ut.getName())).booleanValue();
    }

    public void obtain(Command com)
    {
    }

    public void submit(Commander comm, boolean subordinate)
    {
    }
}
