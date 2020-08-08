package agenta;

import java.util.List;
import java.util.stream.Collectors;

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

    private final List<UnitType> unitTypes;
    private final List<String> unitNames;

    private UnitDatabase()
    {
        unitTypes = DefaultUnits.build();
        unitNames = unitTypes.stream()
                .map(UnitType::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
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
            return new UnitTypeImpl(unitTypes.get(index));
        }
        return null;
    }

    public UnitType typeOf(String name)
    {
        return typeOf(indexOf(name));
    }
}
