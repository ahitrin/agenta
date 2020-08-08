package agenta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Загружает и хранит список используемых в игре типов юнитов.
 */
public final class UnitDatabase
{
    private final List<UnitType> unitTypes;
    private final List<String> unitNames;

    public UnitDatabase()
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
}
