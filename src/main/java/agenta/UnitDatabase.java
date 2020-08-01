package agenta;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 Загружает и хранит список используемых в игре типов юнитов. В классе реализован шаблон
 "одиночка", так как, во-первых, нет необходимости хранить типы юнитов в нескольких
 местах одновременно, а во-вторых, доступ к типам юнитов требуется в целом ряде классов,
 а так они могут его легко получить через вызов метода get().
 */
public final class UnitDatabase
{
    private static final UnitDatabase instance = new UnitDatabase();

    public static UnitDatabase get()
    {
        return instance;
    }

    // debug
    public static void main(String[] args)
    {
        UnitDatabase ud = UnitDatabase.get();
        System.out.println("Index of knight = " + ud.indexOf("knight"));
        System.out.println("Index of Abracadabra = " + ud.indexOf("Abracadabra"));
        System.out.println("Unit types list:");
        for (int i = 0; i < 5; i++)
        {
            if (ud.typeOf(i) != null)
            {
                System.out.println("" + i + " - " + ud.nameOf(i));
            }
        }
    }

    private final List<UnitType> unitTypes = new ArrayList<>();
    private final List<String> unitNames = new ArrayList<>();

    private UnitDatabase()
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("types.ser"));
            for (int i = 0; i < 3; i++)
            {
                unitTypes.add((UnitType)ois.readObject());
                unitNames.add(unitTypes.get(i).getName().toLowerCase());
            }
            ois.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public int indexOf(String name)
    {
        return unitNames.indexOf(name.toLowerCase());
    }

    public int indexOf(UnitType type)
    {
        return indexOf(type.getName());
    }

    public String nameOf(int index)
    {
        if ((index >= 0) && (index < unitTypes.size()))
        {
            return unitTypes.get(index).getName();
        }
        return null;
    }

    public int size()
    {
        return unitTypes.size();
    }

    public UnitType typeOf(int index)
    {    // возвращаем не сам тип, а его копию
        if ((index >= 0) && (index < unitTypes.size()))
        {
            return new UnitType(unitTypes.get(index));
        }
        return null;
    }

    public UnitType typeOf(String name)
    {
        return typeOf(indexOf(name));
    }
}
