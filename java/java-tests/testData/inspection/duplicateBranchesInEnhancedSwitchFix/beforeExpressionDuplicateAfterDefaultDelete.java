// "Delete redundant 'switch' result expression" "GENERIC_ERROR_OR_WARNING"
class C {
    void foo(int n) {
        String s = switch (n) {
            case 2 -> bar("B");
            default ->
                /*comment 1*/
                bar("A");
            case 1 ->
                /*comment 1*/
                bar("A");<caret>
        };
    }
    String bar(String s){return s;}
}