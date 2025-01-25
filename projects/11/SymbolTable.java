import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public final Map<String,Object[]> classMap;
    public final Map<String,Object[]> subroutineMap;
    private int staticIndex, fieldIndex, argIndex, varIndex;

    public static enum Kind {
        STATIC, FIELD, ARG, VAR, NONE;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public static Kind getKind(String k){
            if (k.equals("static")) {
                return Kind.STATIC;
            }
            if (k.equals("field")) {
                return Kind.FIELD;
            }
            throw new RuntimeException("illegal string : " + k);
        }
    }

    public SymbolTable() {
        classMap = new HashMap<>();
        subroutineMap = new HashMap<>();
        staticIndex = 0;
        fieldIndex = 0;
        argIndex = 0;
        varIndex = 0;
    }

    public void reset(){
        subroutineMap.clear();
        argIndex = 0;
        varIndex = 0;
    }

    public void define(String name, String type, Kind kind){
        switch (kind) {
            case STATIC:
                classMap.put(name, new Object[]{type, kind, staticIndex++});
                break;
            case FIELD:
                classMap.put(name, new Object[]{type, kind, fieldIndex++});
                break;
            case ARG:
                subroutineMap.put(name, new Object[]{type, kind, argIndex++});
                break;
            case VAR:
                subroutineMap.put(name, new Object[]{type, kind, varIndex++});
                break;
            default:
                throw new IllegalArgumentException("Invalid kind: " + kind);
        }
    }

    public int varCount(Kind kind){
        return switch (kind) {
            case STATIC -> staticIndex;
            case FIELD -> fieldIndex;
            case ARG -> argIndex;
            case VAR -> varIndex;
            default -> throw new IllegalArgumentException("Invalid kind: " + kind);
        };
    }

    public Kind kindOf(String name){
        if(subroutineMap.containsKey(name)){
            return (Kind) subroutineMap.get(name)[1];
        }
        else if (classMap.containsKey(name)){
            return (Kind) classMap.get(name)[1];
        }
        return Kind.NONE;
    }

    public String typeOf(String name){
        if(subroutineMap.containsKey(name)){
            return subroutineMap.get(name)[0].toString();
        }
        if (classMap.containsKey(name)){
            return classMap.get(name)[0].toString();
        }
        else{
            return name;
        }
        //throw new IllegalArgumentException("Variable not found: " + name);
    }

    public int indexOf(String name){
        if(subroutineMap.containsKey(name)){
            return Integer.parseInt(subroutineMap.get(name)[2].toString());
        }
        if (classMap.containsKey(name)){
            return Integer.parseInt(classMap.get(name)[2].toString());
        }
        return 0;
        //throw new IllegalArgumentException("Variable not found: " + name);
    }

    public static String getSegment(Kind k){
        String seg = k.toString();
        if (k.toString().equals("static")) {
            seg = "static";
        }else if (k.toString().equals("var")) {
            seg = "local";
        }else if (k.toString().equals("field")) {
            seg = "this";
        }else if (k.toString().equals("arg")){
            seg = "argument";
        }
        return seg;
    }

    public void incArg (){
        argIndex++;
    }
}
