import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Permute{
    static List<Object[]> ret = new ArrayList<>();
    public static List<?> permute(java.util.List<?> arr){
       per(arr, 0);
       return ret;
    }
    static void per(java.util.List<?> arr, int k){
        for(int i = k; i < arr.size(); i++){
            java.util.Collections.swap(arr, i, k);
            per(arr, k+1);
            java.util.Collections.swap(arr, k, i);
        }
        if (k == arr.size() -1){
            System.out.println(java.util.Arrays.toString(arr.toArray()));
            ret.add(arr.toArray());
        }
    }
    public static void main(String[] args){

        List<String> a = new ArrayList<>();
        a.add("123");
        a.add("23233");
        a.add("543645656");
        a.add("767657");
        a.add("57");
        a.add("4");
        Permute.permute(a);
//        System.out.println(Permute.permute(a, 0));
        System.out.println(ret);
    }


    public static List<String> sortByLength(List<String> l){
        List<Integer> lengths = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        l.stream().forEach(e-> lengths.add(e.length()));
        Integer[] len =  lengths.toArray(new Integer[0]);
        Arrays.sort(len);
        for (Integer i: len){
            ret.addAll(l.stream().filter(e->e.toString().length()==i).collect(Collectors.toList()));
        }
        return ret;
    }
}