package agenta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Инициализатор из командной строки. Считывает файл с описанием расположения и
 * количества юнитов. Также получает и передаёт в <code>inputParameters</code> командиров.
 */
public class CommandLineInitiator
{
    private final InputParameters inputParameters;
    private final String initFile;
    private final Commander com1;
    private final Commander com2;

    public CommandLineInitiator(String initFile, Commander com1, Commander com2)
    {
        this.initFile = initFile;
        this.com1 = com1;
        this.com2 = com2;
        inputParameters = new InputParameters();
    }

    public void load()
    {
        String s1 = "", s2 = "";
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(initFile));
            int temp;
            StringBuilder sb = new StringBuilder(80);
            while ((temp = br.read()) != -1)
            {
                sb.append((char)temp);
            }
            br.close();
            s1 = sb.toString();

        }
        catch (FileNotFoundException e)
        {
            System.err.println("agenta.CommandLineInitiator - File not found: " + initFile);
            System.exit(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        s1 = s1.toLowerCase();

        StringTokenizer st = new StringTokenizer(s1);
        if (!st.hasMoreTokens())
        {
            System.err.println("File " + initFile + " is empty!");
            System.exit(0);
        }

        // here read & setup units - not done yet
        int player0, player1;
        while (st.hasMoreTokens())
        {
            try
            {
                // получаем тип юнита
                s1 = st.nextToken();
                // получаем количество юнитов у обоих игроков
                s2 = st.nextToken();
                player0 = Integer.parseInt(s2);
                s2 = st.nextToken();
                player1 = Integer.parseInt(s2);

                // добавляем юнит в параметры
                inputParameters.addUnit(s1, player0, player1);
            }
            catch (Exception e)
            {
                System.err.println("agenta.CommandLineInitiator - Error in file " + initFile);
            }
        }

        // добавляем командиров
        inputParameters.addCommander(com1);
        inputParameters.addCommander(com2);
    }

    public InputParameters getParameters()
    {
        return inputParameters;
    }
}
