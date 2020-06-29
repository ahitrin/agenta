package agenta;

import java.io.*;
import java.util.*;

/**
  Загружает и хранит список используемых в игре типов юнитов. В классе реализован шаблон
  "одиночка", так как, во-первых, нет необходимости хранить типы юнитов в нескольких
  местах одновременно, а во-вторых, доступ к типам юнитов требуется в целом ряде классов,
  а так они могут его легко получить через вызов метода get().
*/
public final class UnitDatabase {
    private static UnitDatabase instance = new UnitDatabase();
    private Vector<UnitType> unitTypes = new Vector<UnitType>();
    private Vector<String> unitNames = new Vector<String>();

    private UnitDatabase(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("types.ser"));
            for(int i = 0; i < 4; i++){
                unitTypes.add((UnitType)ois.readObject());
                unitNames.add(unitTypes.get(i).getName().toLowerCase());
            }
            ois.close();

        }catch(Exception e){
            System.err.println(e);
            System.exit(0);
        }
    }

    public static UnitDatabase get(){ return instance; }
    public int size(){ return unitTypes.size(); }

    public int indexOf(String name){ return unitNames.indexOf(name.toLowerCase()); }
    public int indexOf(UnitType type) { return indexOf(type.getName()); }

    public UnitType typeOf(String name){ return typeOf(indexOf(name)); }
    public UnitType typeOf(int index){    // возвращаем не сам тип, а его копию
        if((index >= 0) && (index < unitTypes.size()))
            return new UnitType(unitTypes.get(index));
        return null;
    }

    public String nameOf(int index){
        if((index >= 0) && (index < unitTypes.size())) return unitTypes.get(index).getName();
        return null;
    }

    // debug
    public static void main(String[] args){
        UnitDatabase ud = UnitDatabase.get();
        System.out.println("Index of knight = " + ud.indexOf("knight"));
        System.out.println("Index of Abracadabra = " + ud.indexOf("Abracadabra"));
        System.out.println("Unit types list:");
        for(int i = 0; i < 5; i++){
            if(ud.typeOf(i) != null)
                System.out.println("" + i + " - " + ud.nameOf(i));
        }
    }
}
